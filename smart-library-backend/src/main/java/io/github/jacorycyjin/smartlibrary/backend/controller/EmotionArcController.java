package io.github.jacorycyjin.smartlibrary.backend.controller;

import io.github.jacorycyjin.smartlibrary.backend.common.response.Result;
import io.github.jacorycyjin.smartlibrary.backend.service.EmotionArcService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 情感走向控制器
 *
 * @author jcy
 * @date 2026/04/06
 */
@RestController
@RequestMapping("/emotion-arc")
public class EmotionArcController {

    private static final Logger log = LoggerFactory.getLogger(EmotionArcController.class);

    @Resource
    private EmotionArcService emotionArcService;

    /**
     * 获取资源的情感走向
     * 如果不存在则自动触发生成
     *
     * @param resourceId 资源ID
     * @return 情感走向数据
     */
    @GetMapping("/{resourceId}")
    public Result<Map<String, Object>> getEmotionArc(@PathVariable String resourceId) {
        log.info("获取情感走向，资源ID: {}", resourceId);
        
        try {
            // 1. 先尝试从数据库获取已有情感走向
            Map<String, Object> existingArc = emotionArcService.getEmotionArcByResourceId(resourceId);
            if (existingArc != null) {
                log.info("返回已有情感走向，资源ID: {}", resourceId);
                return Result.success(existingArc);
            }
            
            // 2. 如果不存在，自动触发生成（异步）
            log.info("情感走向不存在，自动触发生成，资源ID: {}", resourceId);
            emotionArcService.generateAndSaveEmotionArc(resourceId);
            
            // 3. 返回 null，前端显示加载状态并轮询
            return Result.success(null);
            
        } catch (Exception e) {
            log.error("获取情感走向失败，资源ID: {}", resourceId, e);
            return Result.fail(500, "获取情感走向失败: " + e.getMessage());
        }
    }

    /**
     * 手动触发情感走向生成（仅供管理员使用）
     * 注意：普通用户无法访问此接口
     *
     * @param resourceId 资源ID
     * @return 情感走向ID
     */
    @PostMapping("/generate/{resourceId}")
    public Result<String> generateEmotionArc(@PathVariable String resourceId) {
        log.info("收到生成情感走向请求，资源ID: {}", resourceId);
        String arcId = emotionArcService.generateAndSaveEmotionArcSync(resourceId);
        return Result.success(arcId);
    }
}
