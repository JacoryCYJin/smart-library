package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageDTO;
import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;
import io.github.jacorycyjin.smartlibrary.backend.common.exception.BusinessException;
import io.github.jacorycyjin.smartlibrary.backend.entity.Resource;
import io.github.jacorycyjin.smartlibrary.backend.entity.ResourceCharacterGraph;
import io.github.jacorycyjin.smartlibrary.backend.mapper.ResourceCharacterGraphMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.ResourceMapper;
import io.github.jacorycyjin.smartlibrary.backend.service.AdminGraphService;
import io.github.jacorycyjin.smartlibrary.backend.service.CharacterGraphService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员 AI 图谱管理服务实现
 * 
 * @author Kiro
 * @date 2026/04/05
 */
@Service
public class AdminGraphServiceImpl implements AdminGraphService {

    @jakarta.annotation.Resource
    private ResourceCharacterGraphMapper graphMapper;

    @jakarta.annotation.Resource
    private ResourceMapper resourceMapper;

    @jakarta.annotation.Resource
    private CharacterGraphService characterGraphService;

    @Override
    public PageDTO getGraphList(Map<String, Object> params) {
        int total = graphMapper.countGraphs(params);
        List<ResourceCharacterGraph> graphs = graphMapper.searchGraphs(params);

        // 转换为 Map 列表，并关联资源信息
        List<Map<String, Object>> graphList = new ArrayList<>();
        for (ResourceCharacterGraph graph : graphs) {
            Map<String, Object> graphMap = new HashMap<>();
            graphMap.put("graphId", graph.getGraphId());
            graphMap.put("resourceId", graph.getResourceId());
            graphMap.put("generateStatus", graph.getGenerateStatus());
            graphMap.put("errorMessage", graph.getErrorMessage());
            graphMap.put("aiModel", graph.getAiModel());
            graphMap.put("tokenUsage", graph.getTokenUsage());
            graphMap.put("generateTime", graph.getGenerateTime());
            graphMap.put("ctime", graph.getCtime());
            
            // 解析 JSON 获取节点数和边数
            int nodeCount = 0;
            int edgeCount = 0;
            if (graph.getGraphJson() != null && !graph.getGraphJson().isEmpty()) {
                try {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(graph.getGraphJson());
                    if (root.has("nodes") && root.get("nodes").isArray()) {
                        nodeCount = root.get("nodes").size();
                    }
                    if (root.has("edges") && root.get("edges").isArray()) {
                        edgeCount = root.get("edges").size();
                    }
                } catch (Exception e) {
                    // 解析失败忽略
                }
            }
            graphMap.put("nodeCount", nodeCount);
            graphMap.put("edgeCount", edgeCount);
            
            // 查询资源标题
            Map<String, Object> resourceParams = new HashMap<>();
            resourceParams.put("resourceId", graph.getResourceId());
            List<Resource> resources = resourceMapper.searchResources(resourceParams);
            if (!resources.isEmpty()) {
                graphMap.put("resourceTitle", resources.get(0).getTitle());
            }
            
            graphList.add(graphMap);
        }

        PageDTO<Map<String, Object>> result = new PageDTO<>();
        result.setTotalCount(total);
        result.setList(graphList);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void triggerGraphGeneration(String resourceId, Boolean forceGenerate) {
        // 检查资源是否存在
        Map<String, Object> params = new HashMap<>();
        params.put("resourceId", resourceId);
        List<Resource> resources = resourceMapper.searchResources(params);
        if (resources.isEmpty()) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "资源不存在");
        }

        // 检查是否已有图谱
        ResourceCharacterGraph existingGraph = graphMapper.selectByResourceId(resourceId);
        if (existingGraph != null && (forceGenerate == null || !forceGenerate)) {
            // 非强制模式：已有图谱时抛出异常
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "该资源已有图谱，请使用重试功能");
        }

        // 调用 AI 服务生成图谱（异步）
        try {
            if (forceGenerate != null && forceGenerate) {
                // 强制生成模式：跳过体裁判定，允许覆盖已有数据
                characterGraphService.generateAndSaveGraphForce(resourceId);
            } else {
                // 普通模式：AI 智能判断
                characterGraphService.generateAndSaveGraph(resourceId);
            }
        } catch (Exception e) {
            throw new BusinessException(ApiCode.SERVER_ERROR.getCode(), "触发图谱生成失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void retryGraphGeneration(String graphId) {
        ResourceCharacterGraph graph = graphMapper.selectByGraphId(graphId);
        if (graph == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "图谱不存在");
        }

        // 只有失败状态才能重试
        if (graph.getGenerateStatus() != 3) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "只有失败状态的图谱才能重试");
        }

        // 重置状态为待生成
        graph.setGenerateStatus(0);
        graph.setErrorMessage(null);
        graphMapper.update(graph);

        // 调用 AI 服务重新生成
        try {
            characterGraphService.generateAndSaveGraph(graph.getResourceId());
        } catch (Exception e) {
            throw new BusinessException(ApiCode.SERVER_ERROR.getCode(), "重试图谱生成失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteGraph(String graphId) {
        ResourceCharacterGraph graph = graphMapper.selectByGraphId(graphId);
        if (graph == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "图谱不存在");
        }

        graphMapper.deleteById(graphId);
    }

    @Override
    public Map<String, Object> getGraphDetail(String graphId) {
        ResourceCharacterGraph graph = graphMapper.selectByGraphId(graphId);
        if (graph == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "图谱不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("graphId", graph.getGraphId());
        result.put("resourceId", graph.getResourceId());
        result.put("graphJson", graph.getGraphJson());
        result.put("generateStatus", graph.getGenerateStatus());
        result.put("errorMessage", graph.getErrorMessage());
        result.put("aiModel", graph.getAiModel());
        result.put("tokenUsage", graph.getTokenUsage());
        result.put("generateTime", graph.getGenerateTime());

        return result;
    }
}
