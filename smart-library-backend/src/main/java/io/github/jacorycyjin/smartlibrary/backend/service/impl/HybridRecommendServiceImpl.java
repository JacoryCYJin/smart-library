package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import io.github.jacorycyjin.smartlibrary.backend.dto.ResourceDTO;
import io.github.jacorycyjin.smartlibrary.backend.entity.Resource;
import io.github.jacorycyjin.smartlibrary.backend.entity.UserBrowseHistory;
import io.github.jacorycyjin.smartlibrary.backend.mapper.ResourceCategoryRelMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.ResourceMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.UserBrowseHistoryMapper;
import io.github.jacorycyjin.smartlibrary.backend.service.HybridRecommendService;
import io.github.jacorycyjin.smartlibrary.backend.service.RecommendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 混合推荐服务实现类
 * 
 * @author Jacory
 * @date 2025/04/11
 */
@Slf4j
@Service
public class HybridRecommendServiceImpl implements HybridRecommendService {

    @Autowired
    private RecommendService recommendService;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired(required = false)
    private UserBrowseHistoryMapper browseHistoryMapper;

    @Autowired
    private ResourceCategoryRelMapper categoryRelMapper;

    @Override
    public List<ResourceDTO> getHomeRecommendations(String userId, Integer limit) {
        log.info("获取首页推荐: userId={}, limit={}", userId, limit);

        // 策略 1: 协同过滤推荐（已登录用户）
        if (userId != null && !userId.isEmpty()) {
            try {
                List<ResourceDTO> cfRecommendations = recommendService.getRecommendations(userId, limit);
                if (!cfRecommendations.isEmpty()) {
                    log.info("使用协同过滤推荐: {} 条", cfRecommendations.size());
                    return cfRecommendations;
                }
            } catch (Exception e) {
                log.warn("协同过滤推荐失败，降级到其他策略: {}", e.getMessage());
            }

            // 策略 2: 基于浏览历史的分类推荐
            try {
                List<ResourceDTO> categoryRecommendations = getRecommendationsByBrowseHistory(userId, limit);
                if (!categoryRecommendations.isEmpty()) {
                    log.info("使用基于浏览历史的分类推荐: {} 条", categoryRecommendations.size());
                    return categoryRecommendations;
                }
            } catch (Exception e) {
                log.warn("分类推荐失败: {}", e.getMessage());
            }
        }

        // 策略 3: 热门图书推荐（降级策略）
        log.info("使用热门图书推荐（降级）");
        return getHotRecommendations(limit);
    }

    @Override
    public List<ResourceDTO> getRelatedRecommendations(String resourceId, String userId, Integer limit) {
        log.info("获取相关推荐: resourceId={}, userId={}, limit={}", resourceId, userId, limit);

        // 策略: 同分类热门图书
        // 注: 基于物品的协同过滤由 Python 端离线计算，通过 RecommendService 查询
        return getSameCategoryRecommendations(resourceId, limit);
    }

    @Override
    public List<ResourceDTO> getPersonalRecommendations(String userId, Integer limit) {
        log.info("获取个人推荐: userId={}, limit={}", userId, limit);

        // 纯协同过滤推荐
        List<ResourceDTO> recommendations = recommendService.getRecommendations(userId, limit);

        // 如果没有推荐，返回空列表（不降级）
        if (recommendations.isEmpty()) {
            log.warn("用户 {} 暂无个性化推荐", userId);
        }

        return recommendations;
    }

    /**
     * 基于浏览历史的分类推荐
     * 从用户浏览过的图书的分类中随机推荐
     */
    private List<ResourceDTO> getRecommendationsByBrowseHistory(String userId, Integer limit) {
        if (browseHistoryMapper == null) {
            return new ArrayList<>();
        }

        // 1. 获取用户浏览历史（最近10条）
        List<UserBrowseHistory> histories = browseHistoryMapper.selectByUserId(userId, 10, 0);

        if (histories.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 提取浏览过的资源ID
        List<String> browsedResourceIds = histories.stream()
                .map(UserBrowseHistory::getResourceId)
                .collect(Collectors.toList());

        // 3. 查询这些资源的分类
        List<String> categoryIds = getCategoryIdsByResourceIds(browsedResourceIds);

        if (categoryIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 4. 从这些分类中随机推荐（排除已浏览的）
        List<Resource> resources = resourceMapper.randomRecommendByCategories(categoryIds, limit * 2);

        // 5. 过滤掉已浏览的资源
        List<Resource> filteredResources = resources.stream()
                .filter(r -> !browsedResourceIds.contains(r.getResourceId()))
                .limit(limit)
                .collect(Collectors.toList());

        return filteredResources.stream()
                .map(ResourceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 热门图书推荐（按浏览量排序）
     */
    private List<ResourceDTO> getHotRecommendations(Integer limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("sortBy", "view_count");
        params.put("limit", limit);

        List<Resource> resources = resourceMapper.searchResources(params);

        return resources.stream()
                .map(ResourceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 同分类推荐
     */
    private List<ResourceDTO> getSameCategoryRecommendations(String resourceId, Integer limit) {
        // 1. 查询当前资源的分类
        List<String> categoryIds = getCategoryIdsByResourceIds(Collections.singletonList(resourceId));

        if (categoryIds.isEmpty()) {
            // 降级：返回热门图书
            return getHotRecommendations(limit);
        }

        // 2. 从同分类中随机推荐（排除当前资源）
        List<Resource> resources = resourceMapper.randomRecommendByCategories(categoryIds, limit * 2);

        List<Resource> filteredResources = resources.stream()
                .filter(r -> !r.getResourceId().equals(resourceId))
                .limit(limit)
                .collect(Collectors.toList());

        return filteredResources.stream()
                .map(ResourceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 根据资源ID列表查询分类ID
     */
    private List<String> getCategoryIdsByResourceIds(List<String> resourceIds) {
        if (resourceIds == null || resourceIds.isEmpty()) {
            return new ArrayList<>();
        }
        return categoryRelMapper.selectCategoryIdsByResourceIds(resourceIds);
    }
}
