package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import io.github.jacorycyjin.smartlibrary.backend.dto.UserFavoriteDTO;
import io.github.jacorycyjin.smartlibrary.backend.entity.UserFavorite;
import io.github.jacorycyjin.smartlibrary.backend.mapper.UserFavoriteMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.ResourceMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.CategoryMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.TagMapper;
import io.github.jacorycyjin.smartlibrary.backend.service.UserFavoriteService;
import io.github.jacorycyjin.smartlibrary.backend.common.exception.BusinessException;
import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;
import io.github.jacorycyjin.smartlibrary.backend.vo.ResourcePublicVO;
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
    
    @jakarta.annotation.Resource
    private CategoryMapper categoryMapper;
    
    @jakarta.annotation.Resource
    private TagMapper tagMapper;
    
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
     * @return 收藏的资源列表
     */
    @Override
    public List<ResourcePublicVO> getUserFavorites(String userId, Integer limit, Integer offset) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("limit", limit);
        params.put("offset", offset);
        
        List<UserFavorite> favorites = userFavoriteMapper.selectFavoritesByUserId(params);
        
        if (favorites == null || favorites.isEmpty()) {
            return List.of();
        }
        
        // 提取资源ID列表
        List<String> resourceIds = favorites.stream()
                .map(UserFavorite::getResourceId)
                .collect(Collectors.toList());
        
        // 批量查询资源信息
        Map<String, Object> resourceParams = new HashMap<>();
        resourceParams.put("resourceIds", resourceIds);
        List<io.github.jacorycyjin.smartlibrary.backend.entity.Resource> resources = resourceMapper.searchResources(resourceParams);
        
        // 转换为 VO
        return resources.stream()
                .map(resource -> {
                    io.github.jacorycyjin.smartlibrary.backend.dto.ResourceDTO dto = 
                        io.github.jacorycyjin.smartlibrary.backend.converter.ResourceConverter.toDTO(
                            resource, 
                            categoryMapper, 
                            tagMapper
                        );
                    return io.github.jacorycyjin.smartlibrary.backend.vo.ResourcePublicVO.fromDTO(dto);
                })
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
