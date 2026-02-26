package io.github.jacorycyjin.smartlibrary.backend.service;

import io.github.jacorycyjin.smartlibrary.backend.dto.UserFavoriteDTO;

import java.util.List;

/**
 * 用户收藏服务接口
 * 
 * @author Jacory
 * @date 2026/02/26
 */
public interface UserFavoriteService {
    
    /**
     * 添加收藏
     * 
     * @param userId 用户ID
     * @param resourceId 资源ID
     * @return 是否成功
     */
    Boolean addFavorite(String userId, String resourceId);
    
    /**
     * 取消收藏
     * 
     * @param userId 用户ID
     * @param resourceId 资源ID
     * @return 是否成功
     */
    Boolean removeFavorite(String userId, String resourceId);
    
    /**
     * 检查是否已收藏
     * 
     * @param userId 用户ID
     * @param resourceId 资源ID
     * @return 是否已收藏
     */
    Boolean isFavorited(String userId, String resourceId);
    
    /**
     * 获取用户收藏列表
     * 
     * @param userId 用户ID
     * @param limit 查询数量
     * @param offset 偏移量
     * @return 收藏列表
     */
    List<UserFavoriteDTO> getUserFavorites(String userId, Integer limit, Integer offset);
    
    /**
     * 统计用户收藏数量
     * 
     * @param userId 用户ID
     * @return 收藏数量
     */
    Integer countUserFavorites(String userId);
}
