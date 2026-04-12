package io.github.jacorycyjin.smartlibrary.backend.service;

import io.github.jacorycyjin.smartlibrary.backend.dto.ResourceDTO;
import java.util.List;

/**
 * 推荐服务接口
 * 
 * @author Jacory
 * @date 2025/04/11
 */
public interface RecommendService {

    /**
     * 获取用户推荐列表
     * 
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐资源列表
     */
    List<ResourceDTO> getRecommendations(String userId, Integer limit);

    /**
     * 获取推荐系统统计信息
     * 
     * @return 统计信息（推荐覆盖用户数等）
     */
    Integer getRecommendationCoverage();
}
