package io.github.jacorycyjin.smartlibrary.backend.controller;

import io.github.jacorycyjin.smartlibrary.backend.common.annotation.RequireLogin;
import io.github.jacorycyjin.smartlibrary.backend.common.response.Result;
import io.github.jacorycyjin.smartlibrary.backend.common.util.UserContext;
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
     * @param resourceId 资源ID
     * @return 是否成功
     */
    @PostMapping("/add/{resourceId}")
    @RequireLogin
    public Result<Boolean> addFavorite(@PathVariable String resourceId) {
        String userId = UserContext.getCurrentUserId();
        Boolean success = userFavoriteService.addFavorite(userId, resourceId);
        return Result.success(success);
    }
    
    /**
     * 取消收藏（需要登录）
     * 
     * @param resourceId 资源ID
     * @return 是否成功
     */
    @DeleteMapping("/remove/{resourceId}")
    @RequireLogin
    public Result<Boolean> removeFavorite(@PathVariable String resourceId) {
        String userId = UserContext.getCurrentUserId();
        Boolean success = userFavoriteService.removeFavorite(userId, resourceId);
        return Result.success(success);
    }
    
    /**
     * 检查是否已收藏（无需登录）
     * 
     * @param resourceId 资源ID
     * @return 是否已收藏
     */
    @GetMapping("/check/{resourceId}")
    public Result<Boolean> checkFavorite(@PathVariable String resourceId) {
        String userId = UserContext.getCurrentUserId();
        
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
     * @param limit 查询数量（默认20）
     * @param offset 偏移量（默认0）
     * @return 收藏列表
     */
    @GetMapping("/list")
    @RequireLogin
    public Result<List<UserFavoriteVO>> getUserFavorites(
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam(defaultValue = "0") Integer offset) {
        String userId = UserContext.getCurrentUserId();
        List<UserFavoriteDTO> dtos = userFavoriteService.getUserFavorites(userId, limit, offset);
        List<UserFavoriteVO> vos = dtos.stream()
                .map(UserFavoriteVO::fromDTO)
                .collect(Collectors.toList());
        return Result.success(vos);
    }
    
    /**
     * 统计用户收藏数量（需要登录）
     * 
     * @return 收藏数量
     */
    @GetMapping("/count")
    @RequireLogin
    public Result<Integer> countUserFavorites() {
        String userId = UserContext.getCurrentUserId();
        Integer count = userFavoriteService.countUserFavorites(userId);
        return Result.success(count);
    }
}
