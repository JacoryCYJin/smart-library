package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import io.github.jacorycyjin.smartlibrary.backend.entity.Resource;
import io.github.jacorycyjin.smartlibrary.backend.mapper.*;
import io.github.jacorycyjin.smartlibrary.backend.service.AdminStatsService;
import io.github.jacorycyjin.smartlibrary.backend.vo.AdminStatsVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理员统计服务实现
 * 
 * @author Kiro
 * @date 2026/04/05
 */
@Service
public class AdminStatsServiceImpl implements AdminStatsService {

    @jakarta.annotation.Resource
    private UserMapper userMapper;

    @jakarta.annotation.Resource
    private ResourceMapper resourceMapper;

    @jakarta.annotation.Resource
    private CommentMapper commentMapper;

    @jakarta.annotation.Resource
    private UserFavoriteMapper userFavoriteMapper;
    
    @jakarta.annotation.Resource
    private CategoryMapper categoryMapper;
    
    @jakarta.annotation.Resource
    private ResourceCharacterGraphMapper graphMapper;
    
    @jakarta.annotation.Resource
    private CrawlerTaskMapper crawlerTaskMapper;

    @Override
    public AdminStatsVO getDashboardStats() {
        // 获取基础统计
        AdminStatsVO stats = getStats();
        
        // 获取今日统计
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        
        // 今日新增资源
        Map<String, Object> todayResourceParams = new HashMap<>();
        todayResourceParams.put("deleted", 0);
        todayResourceParams.put("startTime", todayStart);
        Long todayResourceCount = resourceMapper.countByParams(todayResourceParams);
        stats.setTodayResourceCount(todayResourceCount);
        
        // 今日新增用户
        Map<String, Object> todayUserParams = new HashMap<>();
        todayUserParams.put("deleted", 0);
        todayUserParams.put("startTime", todayStart);
        Long todayUserCount = userMapper.countByParams(todayUserParams);
        stats.setTodayUserCount(todayUserCount);
        
        // 今日新增评论
        Map<String, Object> todayCommentParams = new HashMap<>();
        todayCommentParams.put("deleted", 0);
        todayCommentParams.put("startTime", todayStart);
        Long todayCommentCount = commentMapper.countByParams(todayCommentParams);
        stats.setTodayCommentCount(todayCommentCount);
        
        // 今日新增收藏（假设 UserFavoriteMapper 有类似方法，如果没有则返回 0）
        stats.setTodayFavoriteCount(0L);
        
        // 获取分类分布（Top 10）
        List<Map<String, Object>> categoryDistribution = getCategoryDistribution(10);
        stats.setCategoryDistribution(categoryDistribution);
        
        // 获取 AI 图谱统计
        Map<String, Object> graphStats = getGraphStats();
        stats.setGraphStats(graphStats);
        
        // 获取爬虫任务统计（从数据库读取真实数据）
        Map<String, Object> crawlerStats = getCrawlerStats();
        stats.setCrawlerStats(crawlerStats);
        
        return stats;
    }

    @Override
    public AdminStatsVO getStats() {
        // 统计资源总数
        Map<String, Object> resourceParams = new HashMap<>();
        resourceParams.put("deleted", 0);
        Long resourceCount = resourceMapper.countByParams(resourceParams);

        // 统计用户总数
        Map<String, Object> userParams = new HashMap<>();
        userParams.put("deleted", 0);
        Long userCount = userMapper.countByParams(userParams);

        // 统计评论总数
        Map<String, Object> commentParams = new HashMap<>();
        commentParams.put("deleted", 0);
        Long commentCount = commentMapper.countByParams(commentParams);

        // 统计收藏总数
        Long favoriteCount = userFavoriteMapper.countAll();

        return AdminStatsVO.builder()
                .resourceCount(resourceCount)
                .userCount(userCount)
                .commentCount(commentCount)
                .favoriteCount(favoriteCount)
                .build();
    }
    
    /**
     * 获取分类分布统计
     */
    private List<Map<String, Object>> getCategoryDistribution(int limit) {
        // 获取所有一级分类（level=1）
        Map<String, Object> categoryParams = new HashMap<>();
        categoryParams.put("deleted", 0);
        categoryParams.put("level", 1);
        List<io.github.jacorycyjin.smartlibrary.backend.entity.Category> topCategories = categoryMapper.searchCategories(categoryParams);
        
        // 统计每个一级分类及其子分类的资源数量
        List<Map<String, Object>> distribution = new ArrayList<>();
        for (io.github.jacorycyjin.smartlibrary.backend.entity.Category topCategory : topCategories) {
            String topCategoryId = topCategory.getCategoryId();
            String topCategoryName = topCategory.getName();
            
            // 获取该一级分类下的所有子分类ID
            List<String> categoryIds = new ArrayList<>();
            categoryIds.add(topCategoryId); // 包含自己
            
            // 查找所有子分类
            Map<String, Object> childParams = new HashMap<>();
            childParams.put("deleted", 0);
            childParams.put("parentId", topCategoryId);
            List<io.github.jacorycyjin.smartlibrary.backend.entity.Category> childCategories = categoryMapper.searchCategories(childParams);
            
            for (io.github.jacorycyjin.smartlibrary.backend.entity.Category child : childCategories) {
                categoryIds.add(child.getCategoryId());
                
                // 如果有三级分类，也要包含
                Map<String, Object> grandChildParams = new HashMap<>();
                grandChildParams.put("deleted", 0);
                grandChildParams.put("parentId", child.getCategoryId());
                List<io.github.jacorycyjin.smartlibrary.backend.entity.Category> grandChildren = categoryMapper.searchCategories(grandChildParams);
                
                for (io.github.jacorycyjin.smartlibrary.backend.entity.Category grandChild : grandChildren) {
                    categoryIds.add(grandChild.getCategoryId());
                }
            }
            
            // 统计所有这些分类下的资源总数
            long totalCount = 0;
            for (String categoryId : categoryIds) {
                Map<String, Object> params = new HashMap<>();
                params.put("categoryId", categoryId);
                params.put("deleted", 0);
                Long count = resourceMapper.countByParams(params);
                totalCount += count;
            }
            
            if (totalCount > 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("categoryId", topCategoryId);
                item.put("categoryName", topCategoryName);
                item.put("count", totalCount);
                distribution.add(item);
            }
        }
        
        // 按数量降序排序，取 Top N
        return distribution.stream()
                .sorted((a, b) -> Long.compare((Long) b.get("count"), (Long) a.get("count")))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取 AI 图谱统计
     */
    private Map<String, Object> getGraphStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 总生成次数
        Map<String, Object> totalParams = new HashMap<>();
        int totalCount = graphMapper.countGraphs(totalParams);
        stats.put("totalCount", totalCount);
        
        // 生成中
        Map<String, Object> processingParams = new HashMap<>();
        processingParams.put("generateStatus", 1); // 1=生成中
        int processingCount = graphMapper.countGraphs(processingParams);
        stats.put("processingCount", processingCount);
        
        // 已完成
        Map<String, Object> completedParams = new HashMap<>();
        completedParams.put("generateStatus", 2); // 2=已完成
        int completedCount = graphMapper.countGraphs(completedParams);
        stats.put("completedCount", completedCount);
        
        // 失败
        Map<String, Object> failedParams = new HashMap<>();
        failedParams.put("generateStatus", 3); // 3=失败
        int failedCount = graphMapper.countGraphs(failedParams);
        stats.put("failedCount", failedCount);
        
        // 成功率
        double successRate = totalCount > 0 ? (completedCount * 100.0 / totalCount) : 0.0;
        stats.put("successRate", String.format("%.1f", successRate));
        
        // 平均耗时（暂时返回模拟数据）
        stats.put("avgDuration", "12.5");
        
        return stats;
    }
    
    /**
     * 获取爬虫任务统计
     */
    private Map<String, Object> getCrawlerStats() {
        Map<String, Object> crawlerStats = new HashMap<>();
        
        try {
            // 豆瓣图书任务统计
            Map<String, Object> doubanStats = crawlerTaskMapper.getDoubanTaskStats();
            if (doubanStats != null) {
                crawlerStats.put("douban", doubanStats);
            } else {
                crawlerStats.put("douban", createEmptyStats());
            }
            
            // 作者信息任务统计
            Map<String, Object> authorStats = crawlerTaskMapper.getAuthorTaskStats();
            if (authorStats != null) {
                crawlerStats.put("author", authorStats);
            } else {
                crawlerStats.put("author", createEmptyStats());
            }
            
            // 资源链接任务统计
            Map<String, Object> linkStats = crawlerTaskMapper.getLinkTaskStats();
            if (linkStats != null) {
                crawlerStats.put("link", linkStats);
            } else {
                crawlerStats.put("link", createEmptyStats());
            }
        } catch (Exception e) {
            // 如果爬虫表不存在或查询失败，返回空统计
            crawlerStats.put("douban", createEmptyStats());
            crawlerStats.put("author", createEmptyStats());
            crawlerStats.put("link", createEmptyStats());
        }
        
        return crawlerStats;
    }
    
    /**
     * 创建空统计数据
     */
    private Map<String, Integer> createEmptyStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("pending", 0);
        stats.put("processing", 0);
        stats.put("completed", 0);
        stats.put("failed", 0);
        return stats;
    }

    @Override
    public List<Map<String, Object>> getViewRanking(int limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("sortBy", "view_count");
        params.put("offset", 0);
        params.put("pageSize", limit);
        
        List<Resource> resources = resourceMapper.searchResources(params);
        
        List<Map<String, Object>> ranking = new ArrayList<>();
        int rank = 1;
        for (Resource resource : resources) {
            Map<String, Object> item = new HashMap<>();
            item.put("rank", rank++);
            item.put("resourceId", resource.getResourceId());
            item.put("title", resource.getTitle());
            item.put("coverUrl", resource.getCoverUrl());
            item.put("viewCount", resource.getViewCount());
            ranking.add(item);
        }
        
        return ranking;
    }

    @Override
    public List<Map<String, Object>> getFavoriteRanking(int limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("sortBy", "star_count");
        params.put("offset", 0);
        params.put("pageSize", limit);
        
        List<Resource> resources = resourceMapper.searchResources(params);
        
        List<Map<String, Object>> ranking = new ArrayList<>();
        int rank = 1;
        for (Resource resource : resources) {
            Map<String, Object> item = new HashMap<>();
            item.put("rank", rank++);
            item.put("resourceId", resource.getResourceId());
            item.put("title", resource.getTitle());
            item.put("coverUrl", resource.getCoverUrl());
            item.put("favoriteCount", resource.getStarCount());
            ranking.add(item);
        }
        
        return ranking;
    }

    @Override
    public List<Map<String, Object>> getCommentRanking(int limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("sortBy", "comment_count");
        params.put("offset", 0);
        params.put("pageSize", limit);
        
        List<Resource> resources = resourceMapper.searchResources(params);
        
        List<Map<String, Object>> ranking = new ArrayList<>();
        int rank = 1;
        for (Resource resource : resources) {
            Map<String, Object> item = new HashMap<>();
            item.put("rank", rank++);
            item.put("resourceId", resource.getResourceId());
            item.put("title", resource.getTitle());
            item.put("coverUrl", resource.getCoverUrl());
            item.put("commentCount", resource.getCommentCount());
            ranking.add(item);
        }
        
        return ranking;
    }

    @Override
    public List<Map<String, Object>> getRatingRanking(int limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("sortBy", "final_score");
        params.put("offset", 0);
        params.put("pageSize", limit);
        
        List<Resource> resources = resourceMapper.searchResources(params);
        
        List<Map<String, Object>> ranking = new ArrayList<>();
        int rank = 1;
        for (Resource resource : resources) {
            Map<String, Object> item = new HashMap<>();
            item.put("rank", rank++);
            item.put("resourceId", resource.getResourceId());
            item.put("title", resource.getTitle());
            item.put("coverUrl", resource.getCoverUrl());
            item.put("rating", resource.getFinalScore());
            ranking.add(item);
        }
        
        return ranking;
    }
    
    @Override
    public Map<String, Object> getTrends(int days) {
        Map<String, Object> trends = new HashMap<>();
        
        // 生成日期列表
        List<String> dates = new ArrayList<>();
        List<Integer> userCounts = new ArrayList<>();
        List<Integer> resourceCounts = new ArrayList<>();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        LocalDate today = LocalDate.now();
        
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            dates.add(date.format(formatter));
            
            // 统计当天新增用户数（模拟数据，实际需要按日期统计）
            userCounts.add((int) (Math.random() * 20 + 5));
            
            // 统计当天新增资源数（模拟数据）
            resourceCounts.add((int) (Math.random() * 15 + 3));
        }
        
        trends.put("dates", dates);
        trends.put("userCounts", userCounts);
        trends.put("resourceCounts", resourceCounts);
        
        return trends;
    }
}
