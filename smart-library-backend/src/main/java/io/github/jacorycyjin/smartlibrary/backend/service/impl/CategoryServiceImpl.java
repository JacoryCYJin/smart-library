package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import io.github.jacorycyjin.smartlibrary.backend.dto.CategoryDTO;
import io.github.jacorycyjin.smartlibrary.backend.entity.Category;
import io.github.jacorycyjin.smartlibrary.backend.mapper.CategoryMapper;
import io.github.jacorycyjin.smartlibrary.backend.service.CategoryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分类服务实现
 * 
 * @author Jacory
 * @date 2025/01/20
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    /**
     * 获取所有分类列表
     * 
     * @return 分类DTO列表
     */
    @Override
    public List<CategoryDTO> getAllCategories() {
        // 使用通用查询方法
        Map<String, Object> params = new HashMap<>();
        // 不传参数，查询所有未删除的分类
        List<Category> categories = categoryMapper.searchCategories(params);
        
        // 使用 DTO 自带的 fromEntity 方法转换
        return categories.stream()
                .map(CategoryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 展开分类ID列表：将父分类展开为包含所有子分类的ID列表
     * 
     * @param categoryIds 原始分类ID列表
     * @return 展开后的分类ID列表（包含所有子分类）
     */
    @Override
    public List<String> expandCategoryIds(List<String> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return null;
        }
        
        // 对每个分类ID，查询其所有子分类
        return categoryIds.stream()
                .flatMap(categoryId -> categoryMapper.selectDescendantIds(categoryId).stream())
                .distinct()
                .toList();
    }
}
