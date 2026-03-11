package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jacorycyjin.smartlibrary.backend.common.constant.AIPromptConstants;
import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;
import io.github.jacorycyjin.smartlibrary.backend.common.enums.GraphGenerateStatus;
import io.github.jacorycyjin.smartlibrary.backend.common.exception.BusinessException;
import io.github.jacorycyjin.smartlibrary.backend.common.util.UUIDUtil;
import io.github.jacorycyjin.smartlibrary.backend.config.SiliconFlowConfig;
import io.github.jacorycyjin.smartlibrary.backend.dto.CharacterGraphDTO;
import io.github.jacorycyjin.smartlibrary.backend.entity.Resource;
import io.github.jacorycyjin.smartlibrary.backend.entity.ResourceCharacterGraph;
import io.github.jacorycyjin.smartlibrary.backend.mapper.ResourceMapper;
import io.github.jacorycyjin.smartlibrary.backend.service.AIService;
import io.github.jacorycyjin.smartlibrary.backend.service.CharacterGraphService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * AI 人物关系图谱服务实现
 *
 * @author jcy
 * @date 2026/03/10
 */
@Slf4j
@Service
public class CharacterGraphServiceImpl implements CharacterGraphService {
    
    @jakarta.annotation.Resource
    private SiliconFlowConfig siliconFlowConfig;
    
    @jakarta.annotation.Resource
    private ResourceMapper resourceMapper;
    
    @jakarta.annotation.Resource
    private ObjectMapper objectMapper;
    
    @jakarta.annotation.Resource
    private AIService aiService;
    
    @Override
    public CharacterGraphDTO generateCharacterGraph(String bookDescription) {
        if (bookDescription == null || bookDescription.trim().isEmpty()) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "书籍简介不能为空");
        }
        
        try {
            // 使用通用 AI 服务生成图谱
            // 注意：这里的 bookDescription 实际上应该是书名，参数名有误导性
            // 但为了保持接口兼容性，暂时保留
            String userPrompt = String.format(
                AIPromptConstants.CHARACTER_GRAPH_USER_PROMPT_TEMPLATE,
                bookDescription,
                "未知作者"  // 如果只传入书名，作者信息缺失
            );
            
            CharacterGraphDTO graphDTO = aiService.chatJson(
                AIPromptConstants.CHARACTER_GRAPH_SYSTEM_PROMPT,
                userPrompt,
                CharacterGraphDTO.class
            );
            
            // 验证数据完整性
            if (graphDTO.getNodes() == null || graphDTO.getEdges() == null) {
                throw new BusinessException(ApiCode.SERVER_ERROR.getCode(), "图谱数据格式错误");
            }
            
            log.info("图谱生成成功，节点数: {}, 边数: {}", graphDTO.getNodes().size(), graphDTO.getEdges().size());
            
            return graphDTO;
            
        } catch (Exception e) {
            log.error("生成人物关系图谱失败", e);
            throw new BusinessException(ApiCode.SERVER_ERROR.getCode(), "生成图谱失败: " + e.getMessage());
        }
    }
    
    @Override
    public String generateAndSaveGraph(String resourceId) {
        // 1. 查询资源信息
        Resource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "资源不存在");
        }
        
        // 2. 检查是否已有图谱（包括空图谱）
        ResourceCharacterGraph existingGraph = resourceMapper.selectGraphByResourceId(resourceId);
        if (existingGraph != null) {
            // 如果已经生成过（无论成功还是空），直接返回
            if (existingGraph.getGenerateStatus() == GraphGenerateStatus.SUCCESS.getCode()) {
                log.info("资源 {} 已存在图谱记录，直接返回（避免重复调用 LLM）", resourceId);
                return existingGraph.getGraphId();
            }
            // 如果之前失败了，可以重新生成
            if (existingGraph.getGenerateStatus() == GraphGenerateStatus.FAILED.getCode()) {
                log.info("资源 {} 之前生成失败，尝试重新生成", resourceId);
            }
        }
        
        // 3. 创建图谱记录（状态：生成中）
        String graphId = UUIDUtil.generateUUID();
        ResourceCharacterGraph graph = new ResourceCharacterGraph();
        graph.setGraphId(graphId);
        graph.setResourceId(resourceId);
        graph.setGenerateStatus(GraphGenerateStatus.GENERATING.getCode());
        graph.setAiModel(siliconFlowConfig.getModel());
        
        if (existingGraph == null) {
            resourceMapper.insertGraph(graph);
        } else {
            graph.setGraphId(existingGraph.getGraphId());
            resourceMapper.updateGraph(graph);
            graphId = existingGraph.getGraphId();
        }
        
        // 4. 生成图谱（基于书名和作者）
        try {
            long startTime = System.currentTimeMillis();
            
            // 使用书名和作者生成图谱
            String userPrompt = String.format(
                AIPromptConstants.CHARACTER_GRAPH_USER_PROMPT_TEMPLATE,
                resource.getTitle(),
                resource.getAuthorName() != null ? resource.getAuthorName() : "未知作者"
            );
            
            CharacterGraphDTO graphDTO = aiService.chatJson(
                AIPromptConstants.CHARACTER_GRAPH_SYSTEM_PROMPT,
                userPrompt,
                CharacterGraphDTO.class
            );
            
            long endTime = System.currentTimeMillis();
            int generateTime = (int) (endTime - startTime);
            
            // 5. 检查是否为空数组（兜底规则触发）
            if (graphDTO.getNodes() == null || graphDTO.getNodes().isEmpty()) {
                log.info("资源 {} 不适合生成人物图谱（非叙事类或知识库未收录），保存空图谱记录", resourceId);
                
                // 保存空图谱记录（避免下次重复调用 LLM）
                String emptyGraphJson = objectMapper.writeValueAsString(graphDTO);
                graph.setGraphJson(emptyGraphJson);
                graph.setGenerateStatus(GraphGenerateStatus.SUCCESS.getCode());
                graph.setGenerateTime(generateTime);
                
                resourceMapper.updateGraph(graph);
                
                // 不更新 has_graph 字段（保持为 0）
                log.info("资源 {} 已标记为不适合生成图谱（空图谱已保存）", resourceId);
                
                // 返回 graphId（表示已处理，但图谱为空）
                return graphId;
            }
            
            // 6. 更新图谱数据（非空图谱）
            String graphJson = objectMapper.writeValueAsString(graphDTO);
            graph.setGraphJson(graphJson);
            graph.setGenerateStatus(GraphGenerateStatus.SUCCESS.getCode());
            graph.setGenerateTime(generateTime);
            
            resourceMapper.updateGraph(graph);
            
            // 7. 更新资源的 has_graph 字段
            resourceMapper.updateHasGraph(resourceId, 1);
            
            log.info("资源 {} 图谱生成成功，图谱ID: {}, 节点数: {}, 边数: {}", 
                resourceId, graphId, graphDTO.getNodes().size(), graphDTO.getEdges().size());
            
            return graphId;
            
        } catch (Exception e) {
            log.error("资源 {} 图谱生成失败", resourceId, e);
            
            // 更新失败状态
            graph.setGenerateStatus(GraphGenerateStatus.FAILED.getCode());
            graph.setErrorMessage(e.getMessage());
            resourceMapper.updateGraph(graph);
            
            throw new BusinessException(ApiCode.SERVER_ERROR.getCode(), "图谱生成失败: " + e.getMessage());
        }
    }
    
    @Override
    public CharacterGraphDTO getGraphByResourceId(String resourceId) {
        try {
            // 从数据库查询图谱记录
            ResourceCharacterGraph graph = resourceMapper.selectGraphByResourceId(resourceId);
            
            // 如果不存在或生成失败，返回 null
            if (graph == null || graph.getGenerateStatus() != GraphGenerateStatus.SUCCESS.getCode()) {
                return null;
            }
            
            // 解析 JSON 数据
            String graphJson = graph.getGraphJson();
            if (graphJson == null || graphJson.trim().isEmpty()) {
                return null;
            }
            
            CharacterGraphDTO graphDTO = objectMapper.readValue(graphJson, CharacterGraphDTO.class);
            log.info("成功获取图谱数据，资源ID: {}, 节点数: {}, 边数: {}", 
                resourceId, graphDTO.getNodes().size(), graphDTO.getEdges().size());
            
            return graphDTO;
            
        } catch (Exception e) {
            log.error("查询图谱数据失败，资源ID: {}", resourceId, e);
            return null;
        }
    }
}
