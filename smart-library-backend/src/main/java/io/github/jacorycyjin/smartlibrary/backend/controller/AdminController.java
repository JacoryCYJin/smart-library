package io.github.jacorycyjin.smartlibrary.backend.controller;

import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageDTO;
import io.github.jacorycyjin.smartlibrary.backend.common.response.Result;
import io.github.jacorycyjin.smartlibrary.backend.service.AdminService;
import io.github.jacorycyjin.smartlibrary.backend.vo.AdminStatsVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员控制器
 * 
 * @author Kiro
 * @date 2026/04/04
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private AdminService adminService;

    /**
     * 获取统计数据
     */
    @GetMapping("/stats")
    public Result<AdminStatsVO> getStats() {
        return Result.success(adminService.getStats());
    }

    /**
     * 获取用户列表
     */
    @PostMapping("/users")
    public Result<PageDTO> getUserList(@RequestBody Map<String, Object> params) {
        return Result.success(adminService.getUserList(params));
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/users/{userId}/status")
    public Result<Void> updateUserStatus(
            @PathVariable String userId,
            @RequestParam Integer status) {
        adminService.updateUserStatus(userId, status);
        return Result.success();
    }

    /**
     * 更新用户角色
     */
    @PutMapping("/users/{userId}/role")
    public Result<Void> updateUserRole(
            @PathVariable String userId,
            @RequestParam Integer role) {
        adminService.updateUserRole(userId, role);
        return Result.success();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/users/{userId}")
    public Result<Void> deleteUser(@PathVariable String userId) {
        adminService.deleteUser(userId);
        return Result.success();
    }

    /**
     * 获取资源列表
     */
    @PostMapping("/resources")
    public Result<PageDTO> getResourceList(@RequestBody Map<String, Object> params) {
        return Result.success(adminService.getResourceList(params));
    }

    /**
     * 删除资源
     */
    @DeleteMapping("/resources/{resourceId}")
    public Result<Void> deleteResource(@PathVariable String resourceId) {
        adminService.deleteResource(resourceId);
        return Result.success();
    }

    /**
     * 恢复资源
     */
    @PutMapping("/resources/{resourceId}/restore")
    public Result<Void> restoreResource(@PathVariable String resourceId) {
        adminService.restoreResource(resourceId);
        return Result.success();
    }

    /**
     * 添加资源
     */
    @PostMapping("/resources/create")
    public Result<Void> createResource(@RequestBody io.github.jacorycyjin.smartlibrary.backend.form.ResourceForm form) {
        adminService.createResource(form);
        return Result.success();
    }

    /**
     * 更新资源
     */
    @PutMapping("/resources/{resourceId}")
    public Result<Void> updateResource(
            @PathVariable String resourceId,
            @RequestBody io.github.jacorycyjin.smartlibrary.backend.form.ResourceForm form) {
        form.setResourceId(resourceId);
        adminService.updateResource(form);
        return Result.success();
    }

    /**
     * 获取资源详情（用于编辑）
     */
    @GetMapping("/resources/{resourceId}")
    public Result<Map<String, Object>> getResourceDetail(@PathVariable String resourceId) {
        return Result.success(adminService.getResourceDetail(resourceId));
    }

    /**
     * 获取所有分类列表
     */
    @GetMapping("/categories")
    public Result<java.util.List<Map<String, Object>>> getAllCategories() {
        return Result.success(adminService.getAllCategories());
    }

    /**
     * 获取所有标签列表
     */
    @GetMapping("/tags")
    public Result<java.util.List<Map<String, Object>>> getAllTags() {
        return Result.success(adminService.getAllTags());
    }

    /**
     * 搜索作者
     */
    @GetMapping("/authors/search")
    public Result<java.util.List<Map<String, Object>>> searchAuthors(@RequestParam String keyword) {
        return Result.success(adminService.searchAuthors(keyword));
    }

    /**
     * 获取评论列表
     */
    @PostMapping("/comments")
    public Result<PageDTO> getCommentList(@RequestBody Map<String, Object> params) {
        return Result.success(adminService.getCommentList(params));
    }

    /**
     * 审核评论
     */
    @PutMapping("/comments/{commentId}/audit")
    public Result<Void> auditComment(
            @PathVariable String commentId,
            @RequestParam Integer auditStatus,
            @RequestParam(required = false) String rejectionReason) {
        adminService.auditComment(commentId, auditStatus, rejectionReason);
        return Result.success();
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/comments/{commentId}")
    public Result<Void> deleteComment(@PathVariable String commentId) {
        adminService.deleteComment(commentId);
        return Result.success();
    }

    /**
     * 恢复评论
     */
    @PutMapping("/comments/{commentId}/restore")
    public Result<Void> restoreComment(@PathVariable String commentId) {
        adminService.restoreComment(commentId);
        return Result.success();
    }

    /**
     * 获取分类树（带统计）
     */
    @GetMapping("/categories/tree")
    public Result<java.util.List<Map<String, Object>>> getCategoryTree() {
        return Result.success(adminService.getCategoryTree());
    }

    /**
     * 添加分类
     */
    @PostMapping("/categories/create")
    public Result<Void> createCategory(@RequestBody io.github.jacorycyjin.smartlibrary.backend.form.CategoryForm form) {
        adminService.createCategory(form);
        return Result.success();
    }

    /**
     * 更新分类
     */
    @PutMapping("/categories/{categoryId}")
    public Result<Void> updateCategory(
            @PathVariable String categoryId,
            @RequestBody io.github.jacorycyjin.smartlibrary.backend.form.CategoryForm form) {
        form.setCategoryId(categoryId);
        adminService.updateCategory(form);
        return Result.success();
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/categories/{categoryId}")
    public Result<Void> deleteCategory(@PathVariable String categoryId) {
        adminService.deleteCategory(categoryId);
        return Result.success();
    }
}
