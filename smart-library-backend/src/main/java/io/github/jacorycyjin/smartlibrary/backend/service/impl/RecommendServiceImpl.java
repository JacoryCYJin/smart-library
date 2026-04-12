package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import io.github.jacorycyjin.smartlibrary.backend.dto.ResourceDTO;
import io.github.jacorycyjin.smartlibrary.backend.entity.RecommendResult;
import io.github.jacorycyjin.smartlibrary.backend.mapper.RecommendResultMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.ResourceMapper;
import io.github.jacorycyjin.smartlibrary.backend.service.RecommendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 推荐服务实现类
 * 
 * @author Jacory
 * @date 2025/04/11
 */
@Slf4j
@Service
public class RecommendServiceImpl implements RecommendService {

    @Autowired
    private RecommendResultMapper recommendResultMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Override
    public List<ResourceDTO> getRecommendations(String userId, Integer limit) {
        log.info("获取用户推荐列表: userId={}, limit={}", userId, limit);

        // 1. 从推荐结果表查询
        List<RecommendResult> results = recommendResultMapper.selectByUserId(userId, limit);

        if (results == null || results.isEmpty()) {
            log.warn("用户 {} 没有推荐结果", userId);
            return new ArrayList<>();
        }

        // 2. 提取资源ID列表
        List<String> resourceIds = results.stream()
                .map(RecommendResult::getResourceId)
                .collect(Collectors.toList());

        // 3. 批量查询资源详情（返回 Resource 实体）
        List<io.github.jacorycyjin.smartlibrary.backend.entity.Resource> resources = 
                resourceMapper.selectByResourceIds(resourceIds);

        // 4. 转换为 ResourceDTO
        List<ResourceDTO> resourceDTOs = resources.stream()
                .map(ResourceDTO::fromEntity)
                .collect(Collectors.toList());

        log.info("成功获取 {} 条推荐", resourceDTOs.size());
        return resourceDTOs;
    }

    @Override
    public Integer getRecommendationCoverage() {
        Integer count = recommendResultMapper.countUsersWithRecommendations();
        log.info("推荐覆盖用户数: {}", count);
        return count;
    }
}
