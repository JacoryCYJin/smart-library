package io.github.jacorycyjin.smartlibrary.backend.controller;

import io.github.jacorycyjin.smartlibrary.backend.common.response.Result;
import io.github.jacorycyjin.smartlibrary.backend.common.util.UserContext;
import io.github.jacorycyjin.smartlibrary.backend.dto.ResourceDTO;
import io.github.jacorycyjin.smartlibrary.backend.service.HybridRecommendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 偶遇推荐 Controller
 * 使用混合推荐策略（协同过滤 + 智能随机）
 * 
 * @author Jacory
 * @date 2025/04/06
 */
@Slf4j
@RestController
@RequestMapping("/serendipity")
public class SerendipityController {

    private final HybridRecommendService hybridRecommendService;

    public SerendipityController(HybridRecommendService hybridRecommendService) {
        this.hybridRecommendService = hybridRecommendService;
    }

    /**
     * 智能推荐（混合策略）
     * 优先级：协同过滤 → 分类推荐 → 热门推荐
     * 
     * @param limit 推荐数量（默认12）
     * @return 推荐资源列表
     */
    @GetMapping("/recommend")
    public Result<List<ResourceDTO>> recommend(@RequestParam(defaultValue = "12") Integer limit) {
        // 尝试获取当前用户ID（如果已登录）
        String userId = null;
        try {
            userId = UserContext.getCurrentUserId();
        } catch (Exception e) {
            // 用户未登录，userId 保持为 null
            log.debug("用户未登录，将使用热门推荐");
        }

        List<ResourceDTO> resources = hybridRecommendService.getHomeRecommendations(userId, limit);
        return Result.success(resources);
    }
}
