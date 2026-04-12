package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageDTO;
import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;
import io.github.jacorycyjin.smartlibrary.backend.common.exception.BusinessException;
import io.github.jacorycyjin.smartlibrary.backend.common.util.UUIDUtil;
import io.github.jacorycyjin.smartlibrary.backend.entity.Category;
import io.github.jacorycyjin.smartlibrary.backend.entity.Resource;
import io.github.jacorycyjin.smartlibrary.backend.mapper.AuthorMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.CategoryMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.ResourceMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.TagMapper;
import io.github.jacorycyjin.smartlibrary.backend.service.AdminResourceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员资源管理服务实现
 * 
 * @author Kiro
 * @date 2026/04/05
 */
@Service
public class AdminResourceServiceImpl implements AdminResourceService {

    @jakarta.annotation.Resource
    private ResourceMapper resourceMapper;

    @jakarta.annotation.Resource
    private CategoryMapper categoryMapper;

    @jakarta.annotation.Resource
    private TagMapper tagMapper;

    @jakarta.annotation.Resource
    private AuthorMapper authorMapper;

    @Override
    public PageDTO<Resource> getResourceList(Map<String, Object> params) {
        // 获取分页参数并转换类型
        Integer pageNum = 1;
        Integer pageSize = 10;
        
        if (params.get("page") != null) {
            Object pageObj = params.get("page");
            pageNum = pageObj instanceof Integer ? (Integer) pageObj : Integer.parseInt(pageObj.toString());
        }
        
        if (params.get("pageSize") != null) {
            Object pageSizeObj = params.get("pageSize");
            pageSize = pageSizeObj instanceof Integer ? (Integer) pageSizeObj : Integer.parseInt(pageSizeObj.toString());
        }
        
        // 设置分页参数
        PageHelper.startPage(pageNum, pageSize);

        // 查询资源列表
        params.put("deleted", 0);
        List<Resource> resources = resourceMapper.searchResources(params);
        PageInfo<Resource> pageInfo = new PageInfo<>(resources);

        return new PageDTO<>(pageNum, (int) pageInfo.getTotal(), pageSize, pageInfo.getList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteResource(String resourceId) {
        Resource resource = resourceMapper.findByResourceId(resourceId);
        if (resource == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "资源不存在");
        }

        resource.setDeleted(1);
        resourceMapper.updateByResourceId(resource);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restoreResource(String resourceId) {
        Resource resource = resourceMapper.findByResourceId(resourceId);
        if (resource == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "资源不存在");
        }

        resource.setDeleted(0);
        resourceMapper.updateByResourceId(resource);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createResource(io.github.jacorycyjin.smartlibrary.backend.form.ResourceForm form) {
        // 生成资源ID
        String resourceId = UUIDUtil.generateUUID();

        // 构建资源实体
        Resource resource = Resource.builder()
                .resourceId(resourceId)
                .type(form.getType())
                .title(form.getTitle())
                .coverUrl(form.getCoverUrl())
                .summary(form.getSummary())
                .pubDate(form.getPubDate())
                .isbn(form.getIsbn())
                .publisher(form.getPublisher())
                .price(form.getPrice())
                .pageCount(form.getPageCount())
                .doi(form.getDoi())
                .journal(form.getJournal())
                .sourceOrigin(form.getSourceOrigin() != null ? form.getSourceOrigin() : 99) // 默认手动录入
                .sourceUrl(form.getSourceUrl())
                .viewCount(0)
                .commentCount(0)
                .starCount(0)
                .hasGraph(0)
                .deleted(0)
                .build();

        // 插入资源
        resourceMapper.insert(resource);

        // 关联分类
        if (form.getCategoryIds() != null && !form.getCategoryIds().isEmpty()) {
            for (String categoryId : form.getCategoryIds()) {
                Map<String, Object> relParams = new HashMap<>();
                relParams.put("resourceId", resourceId);
                relParams.put("categoryId", categoryId);
                // 这里需要调用关联表的插入方法，暂时省略
            }
        }

        // 关联标签
        if (form.getTagIds() != null && !form.getTagIds().isEmpty()) {
            for (String tagId : form.getTagIds()) {
                Map<String, Object> relParams = new HashMap<>();
                relParams.put("resourceId", resourceId);
                relParams.put("tagId", tagId);
                // 这里需要调用关联表的插入方法，暂时省略
            }
        }

        // 关联作者
        if (form.getAuthorIds() != null && !form.getAuthorIds().isEmpty()) {
            int sort = 1;
            for (String authorId : form.getAuthorIds()) {
                Map<String, Object> relParams = new HashMap<>();
                relParams.put("resourceId", resourceId);
                relParams.put("authorId", authorId);
                relParams.put("sort", sort++);
                // 这里需要调用关联表的插入方法，暂时省略
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateResource(io.github.jacorycyjin.smartlibrary.backend.form.ResourceForm form) {
        Resource resource = resourceMapper.findByResourceId(form.getResourceId());
        if (resource == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "资源不存在");
        }

        // 更新基本信息
        resource.setType(form.getType());
        resource.setTitle(form.getTitle());
        resource.setCoverUrl(form.getCoverUrl());
        resource.setSummary(form.getSummary());
        resource.setPubDate(form.getPubDate());
        resource.setIsbn(form.getIsbn());
        resource.setPublisher(form.getPublisher());
        resource.setPrice(form.getPrice());
        resource.setPageCount(form.getPageCount());
        resource.setDoi(form.getDoi());
        resource.setJournal(form.getJournal());
        resource.setSourceUrl(form.getSourceUrl());

        resourceMapper.updateByResourceId(resource);

        // 更新关联关系（需要先删除旧关联，再插入新关联）
        // 这里暂时省略关联表的更新逻辑
    }

    @Override
    public Map<String, Object> getResourceDetail(String resourceId) {
        Resource resource = resourceMapper.findByResourceId(resourceId);
        if (resource == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "资源不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("resource", resource);

        // 查询关联的分类
        List<Category> categories = categoryMapper.selectCategoriesByResourceId(resourceId);
        result.put("categories", categories);

        // 查询关联的标签
        List<Map<String, Object>> tags = tagMapper.selectTagsByResourceIds(List.of(resourceId));
        result.put("tags", tags);

        // 查询关联的作者
        List<Map<String, Object>> authors = authorMapper.selectAuthorsByResourceId(resourceId);
        result.put("authors", authors);

        return result;
    }

    @Override
    public List<Map<String, Object>> getAllCategories() {
        Map<String, Object> params = new HashMap<>();
        params.put("deleted", 0);
        List<Category> categories = categoryMapper.searchCategories(params);
        
        // 转换为 Map 列表
        return categories.stream()
                .map(category -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("categoryId", category.getCategoryId());
                    map.put("name", category.getName());
                    map.put("parentId", category.getParentId());
                    map.put("level", category.getLevel());
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getAllTags() {
        return tagMapper.selectAll();
    }

    @Override
    public List<Map<String, Object>> searchAuthors(String keyword) {
        // 这里需要实现作者搜索逻辑
        // 暂时返回空列表
        return new java.util.ArrayList<>();
    }
}
