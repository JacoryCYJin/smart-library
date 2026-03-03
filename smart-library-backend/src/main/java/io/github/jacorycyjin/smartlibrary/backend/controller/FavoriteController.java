package io.github.jacorycyjin.smartlibrary.backend.controller;

import io.github.jacorycyjin.smartlibrary.backend.common.annotation.CurrentUserId;
import io.github.jacorycyjin.smartlibrary.backend.common.annotation.RequireLogin;
import io.github.jacorycyjin.smartlibrary.backend.common.response.Result;
import io.github.jacorycyjin.smartlibrary.backend.dto.UserFavoriteDTO;
import io.github.jacorycyjin.smartlibrary.backend.service.UserFavoriteService;
import io.github.jacorycyjin.smartlibrary.backend.vo.UserFavoriteVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户收藏控制器
 * 
 * @author Jacory
 * @date 2026/02/26
 */
@RestController
@RequestMapping("/favorite")
public class FavoriteController {
    
    @jakarta.annotation.Resource
    private UserFavoriteService userFavoriteService;
    
    /**
     * 添加收藏（需要登录）
     * 
     * @param userId 当前用户ID（自动注入）
     * @param resourceId 资源ID
     * @return 是否成功
     */
    @PostMapping("/add/{resourceId}")
    @RequireLogin
    public Result<Boolean> addFavorite(
            @CurrentUserId String userId,
            @PathVariable String resourceId) {
        Boolean success = userFavoriteService.addFavorite(userId, resourceId);
        return Result.success(success);
    }
    
    /**
     * 取消收藏（需要登录）
     * 
     * @param userId 当前用户ID（自动注入）
     * @param resourceId 资源ID
     * @return 是否成功
     */
    @DeleteMapping("/remove/{resourceId}")
    @RequireLogin
    public Result<Boolean> removeFavorite(
            @CurrentUserId String userId,
            @PathVariable String resourceId) {
        Boolean success = userFavoriteService.removeFavorite(userId, resourceId);
        return Result.success(success);
    }
    
    /**
     * 检查是否已收藏（无需登录，但需要用户ID）
     * 
     * @param userId 当前用户ID（自动注入，未登录时为 null）
     * @param resourceId 资源ID
     * @return 是否已收藏
     */
    @GetMapping("/check/{resourceId}")
    public Result<Boolean> checkFavorite(
            @CurrentUserId String userId,
            @PathVariable String resourceId) {
        // 未登录时返回 false
        if (userId == null) {
            return Result.success(false);
        }
        
        Boolean isFavorited = userFavoriteService.isFavorited(userId, resourceId);
        return Result.success(isFavorited);
    }
    
    /**
     * 获取用户收藏列表（需要登录）
     * 
     * @param userId 当前用户ID（自动注入）
     * @param limit 查询数量（默认20）
     * @param offset 偏移量（默认0）
     * @return 收藏列表
     */
    @GetMapping("/list")
    @RequireLogin
    public Result<List<UserFavoriteVO>> getUserFavorites(
            @CurrentUserId String userId,
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam(defaultValue = "0") Integer offset) {
        List<UserFavoriteDTO> dtos = userFavoriteService.getUserFavorites(userId, limit, offset);
        List<UserFavoriteVO> vos = dtos.stream()
                .map(UserFavoriteVO::fromDTO)
                .collect(Collectors.toList());
        return Result.success(vos);
    }
    
    /**
     * 统计用户收藏数量（需要登录）
     * 
     * @param userId 当前用户ID（自动注入）
     * @return 收藏数量
     */
    @GetMapping("/count")
    @RequireLogin
    public Result<Integer> countUserFavorites(@CurrentUserId String userId) {
        Integer count = userFavoriteService.countUserFavorites(userId);
        return Result.success(count);
    }
}
