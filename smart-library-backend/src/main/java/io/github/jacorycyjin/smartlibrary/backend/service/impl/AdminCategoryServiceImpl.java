package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;
import io.github.jacorycyjin.smartlibrary.backend.common.exception.BusinessException;
import io.github.jacorycyjin.smartlibrary.backend.common.util.UUIDUtil;
import io.github.jacorycyjin.smartlibrary.backend.entity.Category;
import io.github.jacorycyjin.smartlibrary.backend.mapper.CategoryMapper;
import io.github.jacorycyjin.smartlibrary.backend.service.AdminCategoryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员分类管理服务实现
 * 
 * @author Kiro
 * @date 2026/04/05
 */
@Service
public class AdminCategoryServiceImpl implements AdminCategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public List<Map<String, Object>> getCategoryTree() {
        // 查询所有分类
        Map<String, Object> params = new HashMap<>();
        params.put("deleted", 0);
        List<Category> categories = categoryMapper.searchCategories(params);
        
        // 构建分类 Map（用于快速查找）
        Map<String, Category> categoryMap = categories.stream()
                .collect(java.util.stream.Collectors.toMap(
                    Category::getCategoryId,
                    c -> c
                ));
        
        // 统计每个分类的资源数量
        Map<String, Long> resourceCountMap = new HashMap<>();
        for (Category category : categories) {
            Long count = categoryMapper.countResourcesByCategoryId(category.getCategoryId());
            resourceCountMap.put(category.getCategoryId(), count);
        }
        
        // 构建树形结构
        List<Map<String, Object>> tree = new ArrayList<>();
        for (Category category : categories) {
            if (category.getParentId() == null || category.getParentId().isEmpty()) {
                // 一级分类
                Map<String, Object> node = buildCategoryNode(category, categoryMap, resourceCountMap, categories);
                tree.add(node);
            }
        }
        
        return tree;
    }
    
    /**
     * 构建分类节点（递归）
     */
    private Map<String, Object> buildCategoryNode(
            Category category,
            Map<String, Category> categoryMap,
            Map<String, Long> resourceCountMap,
            List<Category> allCategories) {
        
        Map<String, Object> node = new HashMap<>();
        node.put("categoryId", category.getCategoryId());
        node.put("categoryName", category.getName());
        node.put("parentId", category.getParentId());
        node.put("level", category.getLevel());
        node.put("sortOrder", category.getSortOrder());
        
        // 当前分类的资源数量
        Long resourceCount = resourceCountMap.getOrDefault(category.getCategoryId(), 0L);
        node.put("resourceCount", resourceCount);
        
        // 查找子分类
        List<Map<String, Object>> children = new ArrayList<>();
        for (Category child : allCategories) {
            if (category.getCategoryId().equals(child.getParentId())) {
                Map<String, Object> childNode = buildCategoryNode(child, categoryMap, resourceCountMap, allCategories);
                children.add(childNode);
                // 累加子分类的资源数量
                resourceCount += (Long) childNode.get("resourceCount");
            }
        }
        
        if (!children.isEmpty()) {
            node.put("children", children);
        }
        
        // 更新总资源数量（包含子分类）
        node.put("resourceCount", resourceCount);
        
        return node;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createCategory(io.github.jacorycyjin.smartlibrary.backend.form.CategoryForm form) {
        // 生成分类ID
        String categoryId = UUIDUtil.generateUUID();

        // 构建分类实体
        Category category = Category.builder()
                .categoryId(categoryId)
                .parentId(form.getParentId())
                .name(form.getName())
                .level(form.getLevel())
                .sortOrder(form.getSortOrder() != null ? form.getSortOrder() : 0)
                .deleted(0)
                .build();

        // 插入分类
        categoryMapper.insert(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(io.github.jacorycyjin.smartlibrary.backend.form.CategoryForm form) {
        Category category = categoryMapper.findByCategoryId(form.getCategoryId());
        if (category == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "分类不存在");
        }

        // 更新基本信息
        category.setName(form.getName());
        category.setParentId(form.getParentId());
        category.setLevel(form.getLevel());
        category.setSortOrder(form.getSortOrder());

        categoryMapper.update(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(String categoryId) {
        Category category = categoryMapper.findByCategoryId(categoryId);
        if (category == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "分类不存在");
        }

        // 检查是否有子分类
        Map<String, Object> params = new HashMap<>();
        params.put("parentId", categoryId);
        params.put("deleted", 0);
        List<Category> children = categoryMapper.searchCategories(params);
        if (!children.isEmpty()) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "该分类下有子分类，无法删除");
        }

        // 检查是否有关联资源
        Long resourceCount = categoryMapper.countResourcesByCategoryId(categoryId);
        if (resourceCount > 0) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), 
                "该分类下有 " + resourceCount + " 个资源，无法删除");
        }

        // 软删除
        category.setDeleted(1);
        categoryMapper.update(category);
    }
}
