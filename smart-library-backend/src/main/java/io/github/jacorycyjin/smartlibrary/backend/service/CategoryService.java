package io.github.jacorycyjin.smartlibrary.backend.service;

import io.github.jacorycyjin.smartlibrary.backend.dto.CategoryDTO;

import java.util.List;

/**
 * 分类服务接口
 * 
 * @author Jacory
 * @date 2025/01/20
 */
public interface CategoryService {

    /**
     * 获取所有分类列表
     * 
     * @return 分类DTO列表
     */
    List<CategoryDTO> getAllCategories();

    /**
     * 展开分类ID列表：将父分类展开为包含所有子分类的ID列表
     * 
     * @param categoryIds 原始分类ID列表
     * @return 展开后的分类ID列表（包含所有子分类）
     */
    List<String> expandCategoryIds(List<String> categoryIds);
}