package io.github.jacorycyjin.smartlibrary.backend.service;

import io.github.jacorycyjin.smartlibrary.backend.vo.AdminStatsVO;

import java.util.List;
import java.util.Map;

/**
 * 管理员统计服务接口
 * 
 * @author Kiro
 * @date 2026/04/05
 */
public interface AdminStatsService {
    
    /**
     * 获取统计数据
     * 
     * @return 统计数据
     */
    AdminStatsVO getStats();
    
    /**
     * 获取浏览量排行榜
     * 
     * @param limit 限制数量
     * @return 排行榜数据
     */
    List<Map<String, Object>> getViewRanking(int limit);
    
    /**
     * 获取收藏量排行榜
     * 
     * @param limit 限制数量
     * @return 排行榜数据
     */
    List<Map<String, Object>> getFavoriteRanking(int limit);
    
    /**
     * 获取评论量排行榜
     * 
     * @param limit 限制数量
     * @return 排行榜数据
     */
    List<Map<String, Object>> getCommentRanking(int limit);
    
    /**
     * 获取评分排行榜
     * 
     * @param limit 限制数量
     * @return 排行榜数据
     */
    List<Map<String, Object>> getRatingRanking(int limit);
}
