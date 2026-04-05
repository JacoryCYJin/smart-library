package io.github.jacorycyjin.smartlibrary.backend.service;

import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageDTO;
import io.github.jacorycyjin.smartlibrary.backend.vo.AdminStatsVO;

import java.util.Map;

/**
 * 管理员服务接口
 * 
 * @author Kiro
 * @date 2026/04/04
 */
public interface AdminService {

    /**
     * 获取统计数据
     */
    AdminStatsVO getStats();

    /**
     * 获取用户列表
     */
    PageDTO getUserList(Map<String, Object> params);

    /**
     * 更新用户状态
     */
    void updateUserStatus(String userId, Integer status);

    /**
     * 更新用户角色
     */
    void updateUserRole(String userId, Integer role);

    /**
     * 删除用户
     */
    void deleteUser(String userId);

    /**
     * 获取资源列表
     */
    PageDTO getResourceList(Map<String, Object> params);

    /**
     * 删除资源
     */
    void deleteResource(String resourceId);

    /**
     * 恢复资源
     */
    void restoreResource(String resourceId);

    /**
     * 获取评论列表
     */
    PageDTO getCommentList(Map<String, Object> params);

    /**
     * 审核评论
     */
    void auditComment(String commentId, Integer auditStatus, String rejectionReason);

    /**
     * 删除评论
     */
    void deleteComment(String commentId);

    /**
     * 恢复评论
     */
    void restoreComment(String commentId);

    /**
     * 添加资源
     */
    void createResource(io.github.jacorycyjin.smartlibrary.backend.form.ResourceForm form);

    /**
     * 更新资源
     */
    void updateResource(io.github.jacorycyjin.smartlibrary.backend.form.ResourceForm form);

    /**
     * 获取资源详情（用于编辑）
     */
    Map<String, Object> getResourceDetail(String resourceId);

    /**
     * 获取所有分类列表
     */
    java.util.List<Map<String, Object>> getAllCategories();

    /**
     * 获取所有标签列表
     */
    java.util.List<Map<String, Object>> getAllTags();

    /**
     * 搜索作者
     */
    java.util.List<Map<String, Object>> searchAuthors(String keyword);

    /**
     * 获取分类树（带统计）
     */
    java.util.List<Map<String, Object>> getCategoryTree();

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
