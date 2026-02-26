package io.github.jacorycyjin.smartlibrary.backend.controller;

import io.github.jacorycyjin.smartlibrary.backend.common.response.Result;
import io.github.jacorycyjin.smartlibrary.backend.common.util.UserContext;
import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;
import io.github.jacorycyjin.smartlibrary.backend.common.exception.BusinessException;
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
     * 检查用户是否登录
     */
    private void checkLogin() {
        String userId = UserContext.getCurrentUserId();
        if (userId == null || userId.isEmpty()) {
            throw new BusinessException(ApiCode.UNAUTHORIZED.getCode(), "请先登录");
        }
    }
    
    /**
     * 添加收藏
     * 
     * @param resourceId 资源ID
     * @return 是否成功
     */
    @PostMapping("/add/{resourceId}")
    public Result<Boolean> addFavorite(@PathVariable String resourceId) {
        checkLogin();
        String userId = UserContext.getCurrentUserId();
        Boolean success = userFavoriteService.addFavorite(userId, resourceId);
        return Result.success(success);
    }
    
    /**
     * 取消收藏
     * 
     * @param resourceId 资源ID
     * @return 是否成功
     */
    @DeleteMapping("/remove/{resourceId}")
    public Result<Boolean> removeFavorite(@PathVariable String resourceId) {
        checkLogin();
        String userId = UserContext.getCurrentUserId();
        Boolean success = userFavoriteService.removeFavorite(userId, resourceId);
        return Result.success(success);
    }
    
    /**
     * 检查是否已收藏
     * 
     * @param resourceId 资源ID
     * @return 是否已收藏
     */
    @GetMapping("/check/{resourceId}")
    public Result<Boolean> checkFavorite(@PathVariable String resourceId) {
        // 未登录时返回 false
        String userId = UserContext.getCurrentUserId();
        if (userId == null || userId.isEmpty()) {
            return Result.success(false);
        }
        Boolean isFavorited = userFavoriteService.isFavorited(userId, resourceId);
        return Result.success(isFavorited);
    }
    
    /**
     * 获取用户收藏列表
     * 
     * @param limit 查询数量（默认20）
     * @param offset 偏移量（默认0）
     * @return 收藏列表
     */
    @GetMapping("/list")
    public Result<List<UserFavoriteVO>> getUserFavorites(
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam(defaultValue = "0") Integer offset) {
        checkLogin();
        String userId = UserContext.getCurrentUserId();
        List<UserFavoriteDTO> dtos = userFavoriteService.getUserFavorites(userId, limit, offset);
        List<UserFavoriteVO> vos = dtos.stream()
                .map(UserFavoriteVO::fromDTO)
                .collect(Collectors.toList());
        return Result.success(vos);
    }
    
    /**
     * 统计用户收藏数量
     * 
     * @return 收藏数量
     */
    @GetMapping("/count")
    public Result<Integer> countUserFavorites() {
        checkLogin();
        String userId = UserContext.getCurrentUserId();
        Integer count = userFavoriteService.countUserFavorites(userId);
        return Result.success(count);
    }
}
