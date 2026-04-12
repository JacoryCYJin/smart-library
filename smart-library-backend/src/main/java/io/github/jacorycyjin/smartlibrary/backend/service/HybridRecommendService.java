package io.github.jacorycyjin.smartlibrary.backend.service;

import io.github.jacorycyjin.smartlibrary.backend.dto.ResourceDTO;
import java.util.List;

/**
 * 混合推荐服务接口
 * 整合多种推荐策略，提供统一的推荐入口
 * 
 * @author Jacory
 * @date 2025/04/11
 */
public interface HybridRecommendService {

    /**
     * 获取首页推荐（混合策略）
     * 策略优先级：
     * 1. 协同过滤推荐（如果有）
     * 2. 基于浏览历史的分类推荐
     * 3. 热门图书推荐（降级）
     * 
     * @param userId 用户ID（可为空，未登录用户）
     * @param limit 推荐数量
     * @return 推荐资源列表
     */
    List<ResourceDTO> getHomeRecommendations(String userId, Integer limit);

    /**
     * 获取图书详情页的相关推荐
     * 策略：
     * 1. 基于当前图书的协同过滤（看过这本书的人还看了）
     * 2. 同分类热门图书（降级）
     * 
     * @param resourceId 当前图书ID
     * @param userId 用户ID（可为空）
     * @param limit 推荐数量
     * @return 推荐资源列表
     */
    List<ResourceDTO> getRelatedRecommendations(String resourceId, String userId, Integer limit);

    /**
     * 获取个人中心推荐（纯协同过滤）
     * 
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐资源列表
     */
    List<ResourceDTO> getPersonalRecommendations(String userId, Integer limit);
}
