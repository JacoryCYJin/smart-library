package io.github.jacorycyjin.smartlibrary.backend.controller;

import io.github.jacorycyjin.smartlibrary.backend.common.response.Result;
import io.github.jacorycyjin.smartlibrary.backend.dto.ResourceDTO;
import io.github.jacorycyjin.smartlibrary.backend.service.RecommendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 推荐系统 Controller
 * 
 * @author Jacory
 * @date 2025/04/11
 */
@Slf4j
@RestController
@RequestMapping("/recommend")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    /**
     * 获取当前用户的推荐列表
     * 
     * @param userId 当前用户ID（从请求头获取，可选）
     * @param limit 推荐数量（默认10）
     * @return 推荐资源列表
     */
    @GetMapping("/list")
    public Result<List<ResourceDTO>> getRecommendations(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        log.info("获取推荐列表: userId={}, limit={}", userId, limit);
        
        List<ResourceDTO> recommendations = recommendService.getRecommendations(userId, limit);
        
        return Result.success(recommendations);
    }

    /**
     * 获取推荐系统统计信息
     * 
     * @return 推荐覆盖用户数
     */
    @GetMapping("/stats")
    public Result<Integer> getStats() {
        Integer coverage = recommendService.getRecommendationCoverage();
        return Result.success(coverage);
    }
}
