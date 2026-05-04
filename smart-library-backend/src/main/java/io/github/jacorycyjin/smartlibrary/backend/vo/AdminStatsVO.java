package io.github.jacorycyjin.smartlibrary.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 管理员统计数据 VO
 * 
 * @author Kiro
 * @date 2026/04/04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsVO {
    
    /**
     * 资源总数
     */
    private Long resourceCount;
    
    /**
     * 用户总数
     */
    private Long userCount;
    
    /**
     * 评论总数
     */
    private Long commentCount;
    
    /**
     * 收藏总数
     */
    private Long favoriteCount;
    
    /**
     * 今日新增资源数
     */
    private Long todayResourceCount;
    
    /**
     * 今日新增用户数
     */
    private Long todayUserCount;
    
    /**
     * 今日新增评论数
     */
    private Long todayCommentCount;
    
    /**
     * 今日新增收藏数
     */
    private Long todayFavoriteCount;
    
    /**
     * 分类分布（Top 10）
     */
    private List<Map<String, Object>> categoryDistribution;
    
    /**
     * AI 图谱统计
     */
    private Map<String, Object> graphStats;
    
    /**
     * 爬虫任务统计
     */
    private Map<String, Object> crawlerStats;
}
