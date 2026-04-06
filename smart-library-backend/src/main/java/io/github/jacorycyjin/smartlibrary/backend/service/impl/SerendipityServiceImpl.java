package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import io.github.jacorycyjin.smartlibrary.backend.converter.ResourceConverter;
import io.github.jacorycyjin.smartlibrary.backend.dto.ResourceDTO;
import io.github.jacorycyjin.smartlibrary.backend.entity.Resource;
import io.github.jacorycyjin.smartlibrary.backend.mapper.CategoryMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.ResourceMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.TagMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.UserBrowseHistoryMapper;
import io.github.jacorycyjin.smartlibrary.backend.service.SerendipityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 偶遇推荐服务实现
 * 
 * @author Jacory
 * @date 2025/04/06
 */
@Slf4j
@Service
public class SerendipityServiceImpl implements SerendipityService {

    private final ResourceMapper resourceMapper;
    private final UserBrowseHistoryMapper browseHistoryMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;

    public SerendipityServiceImpl(ResourceMapper resourceMapper,
                                  UserBrowseHistoryMapper browseHistoryMapper,
                                  CategoryMapper categoryMapper,
                                  TagMapper tagMapper) {
        this.resourceMapper = resourceMapper;
        this.browseHistoryMapper = browseHistoryMapper;
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
    }

    @Override
    public List<ResourceDTO> smartRecommend(String userId, Integer limit) {
        List<Resource> resources = new ArrayList<>();

        // 如果用户已登录，尝试获取用户浏览过的分类
        if (userId != null && !userId.isEmpty()) {
            List<String> categoryIds = browseHistoryMapper.selectCategoryIdsByUserId(userId, 5);
            
            // 如果用户有浏览历史，进行智能推荐
            if (categoryIds != null && !categoryIds.isEmpty()) {
                log.info("用户 {} 有浏览历史，从分类 {} 中推荐", userId, categoryIds);
                
                // 70%从相关分类中推荐
                int relatedCount = (int) (limit * 0.7);
                List<Resource> relatedResources = resourceMapper.randomRecommendByCategories(categoryIds, relatedCount);
                resources.addAll(relatedResources);
                
                // 30%完全随机推荐
                int randomCount = limit - relatedCount;
                List<Resource> randomResources = resourceMapper.randomRecommend(randomCount);
                resources.addAll(randomResources);
                
                log.info("推荐结果：相关分类 {} 本，随机 {} 本", relatedResources.size(), randomResources.size());
            } else {
                // 用户无浏览历史，完全随机推荐
                log.info("用户 {} 无浏览历史，完全随机推荐", userId);
                resources = resourceMapper.randomRecommend(limit);
            }
        } else {
            // 用户未登录，完全随机推荐
            log.info("用户未登录，完全随机推荐");
            resources = resourceMapper.randomRecommend(limit);
        }

        // 转换为 DTO（使用 ResourceConverter 的静态方法）
        return resources.stream()
                .map(resource -> ResourceConverter.toDTO(resource, categoryMapper, tagMapper))
                .collect(Collectors.toList());
    }
}
