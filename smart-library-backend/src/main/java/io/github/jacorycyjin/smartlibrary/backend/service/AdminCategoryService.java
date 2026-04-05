package io.github.jacorycyjin.smartlibrary.backend.service;

import java.util.List;
import java.util.Map;

/**
 * 管理员分类管理服务接口
 * 
 * @author Kiro
 * @date 2026/04/05
 */
public interface AdminCategoryService {

    /**
     * 获取分类树（带统计）
     */
    List<Map<String, Object>> getCategoryTree();

    /**
     * 添加分类
     */
    void createCategory(io.github.jacorycyjin.smartlibrary.backend.form.CategoryForm form);

    /**
     * 更新分类
     */
    void updateCategory(io.github.jacorycyjin.smartlibrary.backend.form.CategoryForm form);

    /**
     * 删除分类
     */
    void deleteCategory(String categoryId);
}
