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
     * 获取完整的数据看板统计数据
     * 
     * @return 统计数据
     */
    AdminStatsVO getDashboardStats();
    
    /**
     * 获取基础统计数据（兼容旧接口）
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
    
    /**
     * 获取趋势数据（用户和资源增长）
     * 
     * @param days 天数
     * @return 趋势数据
     */
    Map<String, Object> getTrends(int days);
}
