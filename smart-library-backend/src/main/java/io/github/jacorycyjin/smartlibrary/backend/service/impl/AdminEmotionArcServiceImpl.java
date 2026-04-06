package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageDTO;
import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;
import io.github.jacorycyjin.smartlibrary.backend.common.exception.BusinessException;
import io.github.jacorycyjin.smartlibrary.backend.entity.Resource;
import io.github.jacorycyjin.smartlibrary.backend.entity.ResourceEmotionArc;
import io.github.jacorycyjin.smartlibrary.backend.mapper.ResourceEmotionArcMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.ResourceMapper;
import io.github.jacorycyjin.smartlibrary.backend.service.AdminEmotionArcService;
import io.github.jacorycyjin.smartlibrary.backend.service.EmotionArcService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员 AI 情感走向管理服务实现
 *
 * @author jcy
 * @date 2026/04/06
 */
@Service
public class AdminEmotionArcServiceImpl implements AdminEmotionArcService {

    @jakarta.annotation.Resource
    private ResourceEmotionArcMapper emotionArcMapper;

    @jakarta.annotation.Resource
    private ResourceMapper resourceMapper;

    @jakarta.annotation.Resource
    private EmotionArcService emotionArcService;

    @Override
    public PageDTO<Map<String, Object>> getEmotionArcList(Map<String, Object> params) {
        // 统计总数
        int totalCount = emotionArcMapper.countEmotionArcs(params);
        
        // 查询列表
        List<ResourceEmotionArc> arcList = emotionArcMapper.searchEmotionArcs(params);
        
        // 转换为 Map 格式
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (ResourceEmotionArc arc : arcList) {
            Map<String, Object> arcMap = new HashMap<>();
            arcMap.put("arcId", arc.getArcId());
            arcMap.put("resourceId", arc.getResourceId());
            arcMap.put("generateStatus", arc.getGenerateStatus());
            arcMap.put("errorMessage", arc.getErrorMessage());
            arcMap.put("aiModel", arc.getAiModel());
            arcMap.put("tokenUsage", arc.getTokenUsage());
            arcMap.put("generateTime", arc.getGenerateTime());
            arcMap.put("ctime", arc.getCtime());
            
            // 解析章节数
            int chapterCount = 0;
            if (arc.getArcJson() != null && !arc.getArcJson().isEmpty()) {
                try {
                    com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    com.fasterxml.jackson.databind.JsonNode rootNode = objectMapper.readTree(arc.getArcJson());
                    if (rootNode.has("chapters") && rootNode.get("chapters").isArray()) {
                        chapterCount = rootNode.get("chapters").size();
                    }
                } catch (Exception e) {
                    // 解析失败，章节数为 0
                }
            }
            arcMap.put("chapterCount", chapterCount);
            
            // 查询资源标题
            Map<String, Object> resourceParams = new HashMap<>();
            resourceParams.put("resourceId", arc.getResourceId());
            List<Resource> resources = resourceMapper.searchResources(resourceParams);
            if (!resources.isEmpty()) {
                arcMap.put("resourceTitle", resources.get(0).getTitle());
            }
            
            resultList.add(arcMap);
        }
        
        PageDTO<Map<String, Object>> result = new PageDTO<>();
        result.setTotalCount(totalCount);
        result.setList(resultList);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void triggerEmotionArcGeneration(String resourceId, Boolean forceGenerate) {
        // 检查资源是否存在
        Map<String, Object> params = new HashMap<>();
        params.put("resourceId", resourceId);
        List<Resource> resources = resourceMapper.searchResources(params);
        if (resources.isEmpty()) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "资源不存在");
        }

        // 检查是否已有情感走向
        ResourceEmotionArc existingArc = emotionArcMapper.selectByResourceId(resourceId);
        if (existingArc != null) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "该资源已有情感走向，请使用重试功能");
        }

        // 调用 AI 服务生成情感走向（异步）
        try {
            if (forceGenerate != null && forceGenerate) {
                // 强制生成模式：跳过体裁判定
                emotionArcService.generateAndSaveEmotionArcForce(resourceId);
            } else {
                // 普通模式：AI 智能判断
                emotionArcService.generateAndSaveEmotionArc(resourceId);
            }
        } catch (Exception e) {
            throw new BusinessException(ApiCode.SERVER_ERROR.getCode(), "触发情感走向生成失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void retryEmotionArcGeneration(String arcId) {
        // 根据 arcId 查询情感走向
        ResourceEmotionArc arc = emotionArcMapper.selectByArcId(arcId);
        if (arc == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "情感走向不存在");
        }
        
        // 重置状态为生成中
        emotionArcMapper.updateGenerateStatus(arc.getResourceId(), 1, null);
        
        // 调用 AI 服务重新生成
        try {
            emotionArcService.generateAndSaveEmotionArc(arc.getResourceId());
        } catch (Exception e) {
            throw new BusinessException(ApiCode.SERVER_ERROR.getCode(), "重新生成失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEmotionArc(String arcId) {
        // 根据 arcId 查询情感走向
        ResourceEmotionArc arc = emotionArcMapper.selectByArcId(arcId);
        if (arc == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "情感走向不存在");
        }
        
        // 逻辑删除
        int rows = emotionArcMapper.deleteByArcId(arcId);
        if (rows == 0) {
            throw new BusinessException(ApiCode.SERVER_ERROR.getCode(), "删除失败");
        }
    }

    @Override
    public Map<String, Object> getEmotionArcDetail(String arcId) {
        // 根据 arcId 查询情感走向
        ResourceEmotionArc arc = emotionArcMapper.selectByArcId(arcId);
        if (arc == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "情感走向不存在");
        }
        
        // 转换为 Map 格式
        Map<String, Object> result = new HashMap<>();
        result.put("arcId", arc.getArcId());
        result.put("resourceId", arc.getResourceId());
        result.put("arcJson", arc.getArcJson());
        result.put("generateStatus", arc.getGenerateStatus());
        result.put("errorMessage", arc.getErrorMessage());
        result.put("aiModel", arc.getAiModel());
        result.put("tokenUsage", arc.getTokenUsage());
        result.put("generateTime", arc.getGenerateTime());
        result.put("ctime", arc.getCtime());
        result.put("mtime", arc.getMtime());
        
        // 查询资源信息
        Map<String, Object> resourceParams = new HashMap<>();
        resourceParams.put("resourceId", arc.getResourceId());
        List<Resource> resources = resourceMapper.searchResources(resourceParams);
        if (!resources.isEmpty()) {
            Resource resource = resources.get(0);
            result.put("resourceTitle", resource.getTitle());
            result.put("resourceCoverUrl", resource.getCoverUrl());
        }
        
        return result;
    }
}
