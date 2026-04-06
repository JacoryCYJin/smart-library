package io.github.jacorycyjin.smartlibrary.backend.controller;

import io.github.jacorycyjin.smartlibrary.backend.common.response.Result;
import io.github.jacorycyjin.smartlibrary.backend.common.util.UserContext;
import io.github.jacorycyjin.smartlibrary.backend.dto.ResourceDTO;
import io.github.jacorycyjin.smartlibrary.backend.service.SerendipityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 偶遇推荐 Controller
 * 
 * @author Jacory
 * @date 2025/04/06
 */
@Slf4j
@RestController
@RequestMapping("/serendipity")
public class SerendipityController {

    private final SerendipityService serendipityService;

    public SerendipityController(SerendipityService serendipityService) {
        this.serendipityService = serendipityService;
    }

    /**
     * 智能随机推荐
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
            log.debug("用户未登录，将进行完全随机推荐");
        }

        List<ResourceDTO> resources = serendipityService.smartRecommend(userId, limit);
        return Result.success(resources);
    }
}
