package io.github.jacorycyjin.smartlibrary.backend.service;

import io.github.jacorycyjin.smartlibrary.backend.dto.ResourceDTO;

import java.util.List;

/**
 * 偶遇推荐服务
 * 
 * @author Jacory
 * @date 2025/04/06
 */
public interface SerendipityService {

    /**
     * 智能随机推荐
     * - 如果用户已登录且有浏览历史：70%从相关分类推荐 + 30%完全随机
     * - 如果用户未登录或无浏览历史：完全随机推荐
     * 
     * @param userId 用户ID（可为null）
     * @param limit 推荐数量
     * @return 推荐资源列表
     */
    List<ResourceDTO> smartRecommend(String userId, Integer limit);
}
