package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import io.github.jacorycyjin.smartlibrary.backend.entity.Resource;
import io.github.jacorycyjin.smartlibrary.backend.mapper.CommentMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.ResourceMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.UserFavoriteMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.UserMapper;
import io.github.jacorycyjin.smartlibrary.backend.service.AdminStatsService;
import io.github.jacorycyjin.smartlibrary.backend.vo.AdminStatsVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
