package io.github.jacorycyjin.smartlibrary.backend.mapper;

import io.github.jacorycyjin.smartlibrary.backend.entity.UserFavorite;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 用户收藏 Mapper
 * 
 * @author Jacory
 * @date 2026/02/26
 */
@Mapper
public interface UserFavoriteMapper {
    
    /**
     * 添加收藏
     * 
     * @param favorite 收藏实体
     * @return 影响行数
     */
    int insertFavorite(UserFavorite favorite);
    
    /**
     * 取消收藏（物理删除）
     * 
     * @param userId 用户ID
     * @param resourceId 资源ID
     * @return 影响行数
     */
    int deleteFavorite(String userId, String resourceId);
    
    /**
     * 检查是否已收藏
     * 
     * @param userId 用户ID
     * @param resourceId 资源ID
     * @return 收藏记录数（0或1）
     */
    int checkFavorite(String userId, String resourceId);
    
    /**
     * 查询用户收藏列表
     * 
     * @param params 查询参数（userId, limit, offset）
     * @return 收藏列表
     */
    List<UserFavorite> selectFavoritesByUserId(Map<String, Object> params);
    
    /**
     * 统计用户收藏数量
     * 
     * @param userId 用户ID
     * @return 收藏数量
     */
    int countFavoritesByUserId(String userId);
}
