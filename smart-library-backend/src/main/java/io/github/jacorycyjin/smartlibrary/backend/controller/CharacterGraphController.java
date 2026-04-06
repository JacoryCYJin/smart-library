package io.github.jacorycyjin.smartlibrary.backend.controller;

import io.github.jacorycyjin.smartlibrary.backend.common.response.Result;
import io.github.jacorycyjin.smartlibrary.backend.dto.CharacterGraphDTO;
import io.github.jacorycyjin.smartlibrary.backend.service.CharacterGraphService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * AI 人物关系图谱 Controller
 *
 * @author jcy
 * @date 2026/03/10
 */
@Slf4j
@RestController
@RequestMapping("/graph")
public class CharacterGraphController {
    
    @jakarta.annotation.Resource
    private CharacterGraphService characterGraphService;
    
    /**
     * 为指定资源生成人物关系图谱
     *
     * @param resourceId 资源ID
     * @return 图谱ID
     */
    @PostMapping("/generate/{resourceId}")
    public Result<String> generateGraph(@PathVariable String resourceId) {
        log.info("收到生成图谱请求，资源ID: {}", resourceId);
        String graphId = characterGraphService.generateAndSaveGraph(resourceId);
        return Result.success(graphId);
    }
    
    /**
     * 获取资源的人物关系图谱
     * 如果不存在则自动触发生成
     *
     * @param resourceId 资源ID
     * @return 图谱数据
     */
    @GetMapping("/{resourceId}")
    public Result<CharacterGraphDTO> getOrGenerateGraph(@PathVariable String resourceId) {
        log.info("获取图谱，资源ID: {}", resourceId);
        
        try {
            // 1. 先尝试从数据库获取已有图谱
            CharacterGraphDTO existingGraph = characterGraphService.getGraphByResourceId(resourceId);
            if (existingGraph != null) {
                log.info("返回已有图谱，资源ID: {}", resourceId);
                return Result.success(existingGraph);
            }
            
            // 2. 如果不存在，自动触发生成（异步）
            log.info("图谱不存在，自动触发生成，资源ID: {}", resourceId);
            characterGraphService.generateAndSaveGraph(resourceId);
            
            // 3. 返回 null，前端显示加载状态并轮询
            return Result.success(null);
            
        } catch (Exception e) {
            log.error("获取图谱失败，资源ID: {}", resourceId, e);
            return Result.fail(500, "获取图谱失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试接口：直接根据文本生成图谱
     *
     * @param description 书籍简介
     * @return 图谱数据
     */
    @PostMapping("/test")
    public Result<CharacterGraphDTO> testGenerate(@RequestBody String description) {
        log.info("收到测试生成图谱请求，文本长度: {}", description.length());
        CharacterGraphDTO graph = characterGraphService.generateCharacterGraph(description);
        return Result.success(graph);
    }
}
