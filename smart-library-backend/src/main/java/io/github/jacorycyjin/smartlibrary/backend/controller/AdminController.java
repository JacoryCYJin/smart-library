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
}
