package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jacorycyjin.smartlibrary.backend.common.constant.AIPromptConstants;
import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;
import io.github.jacorycyjin.smartlibrary.backend.common.exception.BusinessException;
import io.github.jacorycyjin.smartlibrary.backend.common.util.UUIDUtil;
import io.github.jacorycyjin.smartlibrary.backend.entity.Resource;
import io.github.jacorycyjin.smartlibrary.backend.entity.ResourceEmotionArc;
import io.github.jacorycyjin.smartlibrary.backend.mapper.ResourceEmotionArcMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.ResourceMapper;
import io.github.jacorycyjin.smartlibrary.backend.service.AIService;
import io.github.jacorycyjin.smartlibrary.backend.service.EmotionArcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 情感走向生成服务实现
 *
 * @author jcy
 * @date 2026/04/06
 */
@Service
public class EmotionArcServiceImpl implements EmotionArcService {

    private static final Logger log = LoggerFactory.getLogger(EmotionArcServiceImpl.class);

    @jakarta.annotation.Resource
    private ResourceEmotionArcMapper emotionArcMapper;

    @jakarta.annotation.Resource
    private ResourceMapper resourceMapper;

    @jakarta.annotation.Resource
    private AIService aiService;

    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void generateAndSaveEmotionArc(String resourceId) {
        generateEmotionArcInternal(resourceId, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String generateAndSaveEmotionArcSync(String resourceId) {
        return generateEmotionArcInternal(resourceId, false);
    }

    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void generateAndSaveEmotionArcForce(String resourceId) {
        generateEmotionArcInternal(resourceId, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String generateAndSaveEmotionArcForceSync(String resourceId) {
        return generateEmotionArcInternal(resourceId, true);
    }

    /**
     * 内部生成逻辑（严格参考人物图谱实现）
     * 
     * @return 情感走向ID
     */
    private String generateEmotionArcInternal(String resourceId, boolean forceMode) {
        try {
            // 1. 查询资源信息
            Map<String, Object> params = new HashMap<>();
            params.put("resourceId", resourceId);
            List<Resource> resources = resourceMapper.searchResources(params);
            if (resources.isEmpty()) {
                throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "资源不存在");
            }
            Resource resource = resources.get(0);

            // 2. 检查是否已有情感走向记录
            ResourceEmotionArc existingArc = emotionArcMapper.selectByResourceId(resourceId);
            if (existingArc != null && !forceMode) {
                // 非强制模式：如果已经生成成功，直接返回（避免重复调用 LLM）
                if (existingArc.getGenerateStatus() == 2) {
                    log.info("资源 {} 已存在情感走向记录（status=2），直接返回（避免重复调用 LLM）", resourceId);
                    return existingArc.getArcId();
                }
                // 如果是生成中状态，也跳过（避免重复生成）
                if (existingArc.getGenerateStatus() == 1) {
                    log.info("资源 {} 正在生成中（status=1），跳过重复生成", resourceId);
                    return existingArc.getArcId();
                }
                // 如果之前失败了（status=3），继续重新生成
                if (existingArc.getGenerateStatus() == 3) {
                    log.info("资源 {} 之前生成失败（status=3），尝试重新生成", resourceId);
                }
            } else if (existingArc != null && forceMode) {
                // 强制模式：允许覆盖已有数据
                log.info("资源 {} 强制重新生成情感走向（覆盖已有数据）", resourceId);
            } else {
                // 首次生成
                log.info("资源 {} 首次生成情感走向", resourceId);
            }

            // 3. 创建情感走向记录（状态：生成中）
            String arcId = existingArc != null ? existingArc.getArcId() : UUIDUtil.generateUUID();
            ResourceEmotionArc arc = ResourceEmotionArc.builder()
                    .arcId(arcId)
                    .resourceId(resourceId)
                    .generateStatus(1) // 生成中
                    .build();
            
            if (existingArc == null) {
                emotionArcMapper.insert(arc);
            } else {
                emotionArcMapper.updateGenerateStatus(resourceId, 1, null);
            }

            // 4. 构建 AI Prompt
            long startTime = System.currentTimeMillis();
            String systemPrompt = forceMode
                    ? AIPromptConstants.EMOTION_ARC_FORCE_SYSTEM_PROMPT
                    : AIPromptConstants.EMOTION_ARC_SYSTEM_PROMPT;
            String userPrompt = String.format(
                    AIPromptConstants.EMOTION_ARC_USER_PROMPT_TEMPLATE,
                    resource.getTitle(),
                    resource.getAuthorName() != null ? resource.getAuthorName() : "未知作者"
            );

            // 5. 调用 AI 服务生成情感走向
            String rawArcJson = aiService.chat(systemPrompt, userPrompt);
            
            long endTime = System.currentTimeMillis();
            int generateTime = (int) (endTime - startTime);

            // 6. 清理 JSON 字符串（移除 Markdown 代码块标记）
            String arcJson = cleanJsonString(rawArcJson);
            log.info("清理后的 JSON 长度: {} 字符", arcJson.length());

            // 7. 检查是否为空数组（兜底规则触发）
            // 解析 JSON 检查 chapters 是否为空
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(arcJson);
                JsonNode chaptersNode = rootNode.get("chapters");
                
                if (chaptersNode == null || !chaptersNode.isArray() || chaptersNode.size() == 0) {
                    log.info("资源 {} 不适合生成情感走向（非叙事类或知识库未收录），保存空记录", resourceId);
                    
                    // 保存空记录（避免下次重复调用 LLM）
                    emotionArcMapper.updateArcData(
                            resourceId,
                            arcJson,
                            2, // 生成成功（但为空）
                            null,
                            "gpt-4",
                            0,
                            generateTime
                    );
                    
                    log.info("资源 {} 已标记为不适合生成情感走向（空记录已保存）", resourceId);
                    return arcId;
                }
            } catch (Exception parseEx) {
                log.warn("解析情感走向 JSON 失败，继续保存: {}", parseEx.getMessage());
            }

            // 8. 更新情感走向数据（非空）
            emotionArcMapper.updateArcData(
                    resourceId,
                    arcJson,
                    2, // 生成成功
                    null,
                    "gpt-4",
                    1000,
                    generateTime
            );

            log.info("情感走向生成成功: resourceId={}, arcId={}", resourceId, arcId);
            return arcId;

        } catch (Exception e) {
            log.error("情感走向生成失败: resourceId={}, error={}", resourceId, e.getMessage(), e);
            // 更新状态为失败（截断错误信息，避免超过数据库字段长度）
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.length() > 500) {
                errorMessage = errorMessage.substring(0, 500) + "...";
            }
            emotionArcMapper.updateGenerateStatus(resourceId, 3, errorMessage);
            throw new BusinessException(ApiCode.SERVER_ERROR.getCode(), "情感走向生成失败: " + e.getMessage());
        }
    }

    /**
     * 清理 JSON 字符串（移除 Markdown 代码块标记）
     */
    private String cleanJsonString(String rawJson) {
        if (rawJson == null || rawJson.trim().isEmpty()) {
            return rawJson;
        }
        
        String cleaned = rawJson.trim();
        
        // 移除 Markdown 代码块标记
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7); // 移除 ```json
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3); // 移除 ```
        }
        
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3); // 移除结尾的 ```
        }
        
        return cleaned.trim();
    }

    @Override
    public Integer getEmotionArcStatus(String resourceId) {
        ResourceEmotionArc arc = emotionArcMapper.selectByResourceId(resourceId);
        return arc != null ? arc.getGenerateStatus() : 0;
    }

    @Override
    public String getEmotionArcJson(String resourceId) {
        ResourceEmotionArc arc = emotionArcMapper.selectByResourceId(resourceId);
        if (arc == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "情感走向不存在");
        }
        if (arc.getGenerateStatus() != 2) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "情感走向尚未生成完成");
        }
        return arc.getArcJson();
    }

    @Override
    public Map<String, Object> getEmotionArcByResourceId(String resourceId) {
        try {
            // 从数据库查询情感走向记录
            ResourceEmotionArc arc = emotionArcMapper.selectByResourceId(resourceId);
            
            // 如果不存在或生成失败，返回 null（与人物关系图谱逻辑一致）
            if (arc == null || arc.getGenerateStatus() != 2) {
                return null;
            }
            
            // 解析 JSON 数据
            String arcJson = arc.getArcJson();
            if (arcJson == null || arcJson.trim().isEmpty()) {
                return null;
            }
            
            // 解析章节数
            int chapterCount = 0;
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(arcJson);
            if (rootNode.has("chapters") && rootNode.get("chapters").isArray()) {
                chapterCount = rootNode.get("chapters").size();
            }
            
            // 构建返回数据
            Map<String, Object> result = new HashMap<>();
            result.put("chapterCount", chapterCount);
            @SuppressWarnings("unchecked")
            Map<String, Object> dataMap = objectMapper.readValue(arcJson, Map.class);
            result.put("data", dataMap);
            
            log.info("成功获取情感走向数据，资源ID: {}, 章节数: {}", resourceId, chapterCount);
            return result;
            
        } catch (Exception e) {
            log.error("查询情感走向数据失败，资源ID: {}", resourceId, e);
            return null;
        }
    }
}
