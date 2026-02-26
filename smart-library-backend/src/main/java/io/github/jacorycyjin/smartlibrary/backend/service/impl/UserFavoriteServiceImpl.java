package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import io.github.jacorycyjin.smartlibrary.backend.dto.UserFavoriteDTO;
import io.github.jacorycyjin.smartlibrary.backend.entity.UserFavorite;
import io.github.jacorycyjin.smartlibrary.backend.mapper.UserFavoriteMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.ResourceMapper;
import io.github.jacorycyjin.smartlibrary.backend.service.UserFavoriteService;
import io.github.jacorycyjin.smartlibrary.backend.common.exception.BusinessException;
import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户收藏服务实现
 * 
 * @author Jacory
 * @date 2026/02/26
 */
@Service
public class UserFavoriteServiceImpl implements UserFavoriteService {
    
    @jakarta.annotation.Resource
    private UserFavoriteMapper userFavoriteMapper;
    
    @jakarta.annotation.Resource
    private ResourceMapper resourceMapper;
    
    /**
     * 添加收藏
     * 
     * @param userId 用户ID
     * @param resourceId 资源ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addFavorite(String userId, String resourceId) {
        // 检查是否已收藏
        int count = userFavoriteMapper.checkFavorite(userId, resourceId);
        if (count > 0) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "已经收藏过该资源");
        }
        
        // 创建收藏记录
        UserFavorite favorite = UserFavorite.builder()
                .userId(userId)
                .resourceId(resourceId)
                .ctime(LocalDateTime.now())
                .build();
        
        int result = userFavoriteMapper.insertFavorite(favorite);
        
        // 更新资源的收藏数（star_count）
        if (result > 0) {
            resourceMapper.incrementStarCount(resourceId);
        }
        
        return result > 0;
    }
    
    /**
     * 取消收藏
     * 
     * @param userId 用户ID
     * @param resourceId 资源ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean removeFavorite(String userId, String resourceId) {
        // 检查是否已收藏
        int count = userFavoriteMapper.checkFavorite(userId, resourceId);
        if (count == 0) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "未收藏该资源");
        }
        
        int result = userFavoriteMapper.deleteFavorite(userId, resourceId);
        
        // 更新资源的收藏数（star_count）
        if (result > 0) {
            resourceMapper.decrementStarCount(resourceId);
        }
        
        return result > 0;
    }
    
    /**
     * 检查是否已收藏
     * 
     * @param userId 用户ID
     * @param resourceId 资源ID
     * @return 是否已收藏
     */
    @Override
    public Boolean isFavorited(String userId, String resourceId) {
        int count = userFavoriteMapper.checkFavorite(userId, resourceId);
        return count > 0;
    }
    
    /**
     * 获取用户收藏列表
     * 
     * @param userId 用户ID
     * @param limit 查询数量
     * @param offset 偏移量
     * @return 收藏列表
     */
    @Override
    public List<UserFavoriteDTO> getUserFavorites(String userId, Integer limit, Integer offset) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("limit", limit);
        params.put("offset", offset);
        
        List<UserFavorite> favorites = userFavoriteMapper.selectFavoritesByUserId(params);
        return favorites.stream()
                .map(UserFavoriteDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * 统计用户收藏数量
     * 
     * @param userId 用户ID
     * @return 收藏数量
     */
    @Override
    public Integer countUserFavorites(String userId) {
        return userFavoriteMapper.countFavoritesByUserId(userId);
    }
}
