package io.github.jacorycyjin.smartlibrary.backend.controller;

import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageDTO;
import io.github.jacorycyjin.smartlibrary.backend.common.response.Result;
import io.github.jacorycyjin.smartlibrary.backend.service.AdminStatsService;
import io.github.jacorycyjin.smartlibrary.backend.service.AdminAuthorService;
import io.github.jacorycyjin.smartlibrary.backend.service.AdminLinkService;
import io.github.jacorycyjin.smartlibrary.backend.service.AdminGraphService;
import io.github.jacorycyjin.smartlibrary.backend.service.AdminUserService;
import io.github.jacorycyjin.smartlibrary.backend.service.AdminResourceService;
import io.github.jacorycyjin.smartlibrary.backend.service.AdminCommentService;
import io.github.jacorycyjin.smartlibrary.backend.service.AdminCategoryService;
import io.github.jacorycyjin.smartlibrary.backend.vo.AdminStatsVO;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    @Resource
    private AdminStatsService adminStatsService;

    @Resource
    private AdminAuthorService adminAuthorService;

    @Resource
    private AdminLinkService adminLinkService;

    @Resource
    private AdminGraphService adminGraphService;

    @Resource
    private io.github.jacorycyjin.smartlibrary.backend.service.AdminEmotionArcService adminEmotionArcService;

    @Resource
    private AdminUserService adminUserService;

    @Resource
    private AdminResourceService adminResourceService;

    @Resource
    private AdminCommentService adminCommentService;

    @Resource
    private AdminCategoryService adminCategoryService;

    /**
     * 获取统计数据
     */
    @GetMapping("/stats")
    public Result<AdminStatsVO> getStats() {
        return Result.success(adminStatsService.getStats());
    }

    /**
     * 获取用户列表
     */
    @PostMapping("/users")
    public Result<PageDTO> getUserList(@RequestBody Map<String, Object> params) {
        return Result.success(adminUserService.getUserList(params));
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/users/{userId}/status")
    public Result<Void> updateUserStatus(
            @PathVariable String userId,
            @RequestParam Integer status) {
        adminUserService.updateUserStatus(userId, status);
        return Result.success();
    }

    /**
     * 更新用户角色
     */
    @PutMapping("/users/{userId}/role")
    public Result<Void> updateUserRole(
            @PathVariable String userId,
            @RequestParam Integer role) {
        adminUserService.updateUserRole(userId, role);
        return Result.success();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/users/{userId}")
    public Result<Void> deleteUser(@PathVariable String userId) {
        adminUserService.deleteUser(userId);
        return Result.success();
    }

    /**
     * 获取资源列表
     */
    @PostMapping("/resources")
    public Result<PageDTO> getResourceList(@RequestBody Map<String, Object> params) {
        return Result.success(adminResourceService.getResourceList(params));
    }

    /**
     * 删除资源
     */
    @DeleteMapping("/resources/{resourceId}")
    public Result<Void> deleteResource(@PathVariable String resourceId) {
        adminResourceService.deleteResource(resourceId);
        return Result.success();
    }

    /**
     * 恢复资源
     */
    @PutMapping("/resources/{resourceId}/restore")
    public Result<Void> restoreResource(@PathVariable String resourceId) {
        adminResourceService.restoreResource(resourceId);
        return Result.success();
    }

    /**
     * 添加资源
     */
    @PostMapping("/resources/create")
    public Result<Void> createResource(@RequestBody io.github.jacorycyjin.smartlibrary.backend.form.ResourceForm form) {
        adminResourceService.createResource(form);
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
        adminResourceService.updateResource(form);
        return Result.success();
    }

    /**
     * 获取资源详情（用于编辑）
     */
    @GetMapping("/resources/{resourceId}")
    public Result<Map<String, Object>> getResourceDetail(@PathVariable String resourceId) {
        return Result.success(adminResourceService.getResourceDetail(resourceId));
    }

    /**
     * 获取所有分类列表
     */
    @GetMapping("/categories")
    public Result<java.util.List<Map<String, Object>>> getAllCategories() {
        return Result.success(adminResourceService.getAllCategories());
    }

    /**
     * 获取所有标签列表
     */
    @GetMapping("/tags")
    public Result<java.util.List<Map<String, Object>>> getAllTags() {
        return Result.success(adminResourceService.getAllTags());
    }

    /**
     * 搜索作者
     */
    @GetMapping("/authors/search")
    public Result<java.util.List<Map<String, Object>>> searchAuthors(@RequestParam String keyword) {
        return Result.success(adminResourceService.searchAuthors(keyword));
    }

    /**
     * 获取评论列表
     */
    @PostMapping("/comments")
    public Result<PageDTO> getCommentList(@RequestBody Map<String, Object> params) {
        return Result.success(adminCommentService.getCommentList(params));
    }

    /**
     * 审核评论
     */
    @PutMapping("/comments/{commentId}/audit")
    public Result<Void> auditComment(
            @PathVariable String commentId,
            @RequestParam Integer auditStatus,
            @RequestParam(required = false) String rejectionReason) {
        adminCommentService.auditComment(commentId, auditStatus, rejectionReason);
        return Result.success();
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/comments/{commentId}")
    public Result<Void> deleteComment(@PathVariable String commentId) {
        adminCommentService.deleteComment(commentId);
        return Result.success();
    }

    /**
     * 恢复评论
     */
    @PutMapping("/comments/{commentId}/restore")
    public Result<Void> restoreComment(@PathVariable String commentId) {
        adminCommentService.restoreComment(commentId);
        return Result.success();
    }

    /**
     * 获取分类树（带统计）
     */
    @GetMapping("/categories/tree")
    public Result<java.util.List<Map<String, Object>>> getCategoryTree() {
        return Result.success(adminCategoryService.getCategoryTree());
    }

    /**
     * 添加分类
     */
    @PostMapping("/categories/create")
    public Result<Void> createCategory(@RequestBody io.github.jacorycyjin.smartlibrary.backend.form.CategoryForm form) {
        adminCategoryService.createCategory(form);
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
        adminCategoryService.updateCategory(form);
        return Result.success();
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/categories/{categoryId}")
    public Result<Void> deleteCategory(@PathVariable String categoryId) {
        adminCategoryService.deleteCategory(categoryId);
        return Result.success();
    }

    // ==================== 作者管理 ====================

    /**
     * 获取作者列表
     */
    @PostMapping("/authors/list")
    public Result<PageDTO> getAuthorList(@RequestBody io.github.jacorycyjin.smartlibrary.backend.form.AdminSearchForm form) {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", form.getKeyword());
        params.put("offset", (form.getPageNum() - 1) * form.getPageSize());
        params.put("pageSize", form.getPageSize());
        return Result.success(adminAuthorService.getAuthorList(params));
    }

    /**
     * 添加作者
     */
    @PostMapping("/authors/create")
    public Result<Void> createAuthor(@RequestBody io.github.jacorycyjin.smartlibrary.backend.form.AuthorForm form) {
        adminAuthorService.createAuthor(form);
        return Result.success();
    }

    /**
     * 更新作者
     */
    @PutMapping("/authors/{authorId}")
    public Result<Void> updateAuthor(
            @PathVariable String authorId,
            @RequestBody io.github.jacorycyjin.smartlibrary.backend.form.AuthorForm form) {
        form.setAuthorId(authorId);
        adminAuthorService.updateAuthor(form);
        return Result.success();
    }

    /**
     * 删除作者
     */
    @DeleteMapping("/authors/{authorId}")
    public Result<Void> deleteAuthor(@PathVariable String authorId) {
        adminAuthorService.deleteAuthor(authorId);
        return Result.success();
    }

    /**
     * 获取作者详情
     */
    @GetMapping("/authors/{authorId}")
    public Result<Map<String, Object>> getAuthorDetail(@PathVariable String authorId) {
        return Result.success(adminAuthorService.getAuthorDetail(authorId));
    }

    // ==================== 资源链接管理 ====================

    /**
     * 获取资源链接列表
     */
    @PostMapping("/links/list")
    public Result<PageDTO> getLinkList(@RequestBody io.github.jacorycyjin.smartlibrary.backend.form.AdminSearchForm form) {
        Map<String, Object> params = new HashMap<>();
        params.put("resourceId", form.getResourceId());
        params.put("offset", (form.getPageNum() - 1) * form.getPageSize());
        params.put("pageSize", form.getPageSize());
        return Result.success(adminLinkService.getLinkList(params));
    }

    /**
     * 添加资源链接
     */
    @PostMapping("/links/create")
    public Result<Void> createLink(@RequestBody io.github.jacorycyjin.smartlibrary.backend.form.ResourceLinkForm form) {
        adminLinkService.createLink(form);
        return Result.success();
    }

    /**
     * 更新资源链接
     */
    @PutMapping("/links/{linkId}")
    public Result<Void> updateLink(
            @PathVariable String linkId,
            @RequestBody io.github.jacorycyjin.smartlibrary.backend.form.ResourceLinkForm form) {
        form.setLinkId(linkId);
        adminLinkService.updateLink(form);
        return Result.success();
    }

    /**
     * 删除资源链接
     */
    @DeleteMapping("/links/{linkId}")
    public Result<Void> deleteLink(@PathVariable String linkId) {
        adminLinkService.deleteLink(linkId);
        return Result.success();
    }

    /**
     * 获取资源链接详情
     */
    @GetMapping("/links/{linkId}")
    public Result<Map<String, Object>> getLinkDetail(@PathVariable String linkId) {
        return Result.success(adminLinkService.getLinkDetail(linkId));
    }

    // ==================== AI 图谱管理 ====================

    /**
     * 获取 AI 图谱列表
     */
    @PostMapping("/graphs/list")
    public Result<PageDTO> getGraphList(@RequestBody io.github.jacorycyjin.smartlibrary.backend.form.AdminSearchForm form) {
        Map<String, Object> params = new HashMap<>();
        params.put("generateStatus", form.getGenerateStatus());
        params.put("offset", (form.getPageNum() - 1) * form.getPageSize());
        params.put("pageSize", form.getPageSize());
        return Result.success(adminGraphService.getGraphList(params));
    }

    /**
     * 手动触发图谱生成
     */
    @PostMapping("/graphs/trigger/{resourceId}")
    public Result<Void> triggerGraphGeneration(
            @PathVariable String resourceId,
            @RequestParam(defaultValue = "false") Boolean forceGenerate) {
        log.info("触发图谱生成: resourceId={}, forceGenerate={}", resourceId, forceGenerate);
        adminGraphService.triggerGraphGeneration(resourceId, forceGenerate);
        return Result.success();
    }

    /**
     * 重试失败的图谱生成
     */
    @PostMapping("/graphs/retry/{graphId}")
    public Result<Void> retryGraphGeneration(@PathVariable String graphId) {
        adminGraphService.retryGraphGeneration(graphId);
        return Result.success();
    }

    /**
     * 删除图谱
     */
    @DeleteMapping("/graphs/{graphId}")
    public Result<Void> deleteGraph(@PathVariable String graphId) {
        adminGraphService.deleteGraph(graphId);
        return Result.success();
    }

    /**
     * 获取图谱详情
     */
    @GetMapping("/graphs/{graphId}")
    public Result<Map<String, Object>> getGraphDetail(@PathVariable String graphId) {
        return Result.success(adminGraphService.getGraphDetail(graphId));
    }

    // ==================== AI 情感走向管理 ====================

    /**
     * 获取 AI 情感走向列表
     */
    @PostMapping("/emotion-arcs/list")
    public Result<PageDTO> getEmotionArcList(@RequestBody io.github.jacorycyjin.smartlibrary.backend.form.AdminSearchForm form) {
        Map<String, Object> params = new HashMap<>();
        params.put("generateStatus", form.getGenerateStatus());
        params.put("offset", (form.getPageNum() - 1) * form.getPageSize());
        params.put("pageSize", form.getPageSize());
        return Result.success(adminEmotionArcService.getEmotionArcList(params));
    }

    /**
     * 手动触发情感走向生成
     */
    @PostMapping("/emotion-arcs/trigger/{resourceId}")
    public Result<Void> triggerEmotionArcGeneration(
            @PathVariable String resourceId,
            @RequestParam(defaultValue = "false") Boolean forceGenerate) {
        log.info("触发情感走向生成: resourceId={}, forceGenerate={}", resourceId, forceGenerate);
        adminEmotionArcService.triggerEmotionArcGeneration(resourceId, forceGenerate);
        return Result.success();
    }

    /**
     * 重试失败的情感走向生成
     */
    @PostMapping("/emotion-arcs/retry/{arcId}")
    public Result<Void> retryEmotionArcGeneration(@PathVariable String arcId) {
        adminEmotionArcService.retryEmotionArcGeneration(arcId);
        return Result.success();
    }

    /**
     * 删除情感走向
     */
    @DeleteMapping("/emotion-arcs/{arcId}")
    public Result<Void> deleteEmotionArc(@PathVariable String arcId) {
        adminEmotionArcService.deleteEmotionArc(arcId);
        return Result.success();
    }

    /**
     * 获取情感走向详情
     */
    @GetMapping("/emotion-arcs/{arcId}")
    public Result<Map<String, Object>> getEmotionArcDetail(@PathVariable String arcId) {
        return Result.success(adminEmotionArcService.getEmotionArcDetail(arcId));
    }

    // ==================== 排行榜统计 ====================

    /**
     * 获取浏览量排行榜
     */
    @GetMapping("/ranking/views")
    public Result<java.util.List<Map<String, Object>>> getViewRanking(
            @RequestParam(defaultValue = "10") int limit) {
        return Result.success(adminStatsService.getViewRanking(limit));
    }

    /**
     * 获取收藏量排行榜
     */
    @GetMapping("/ranking/favorites")
    public Result<java.util.List<Map<String, Object>>> getFavoriteRanking(
            @RequestParam(defaultValue = "10") int limit) {
        return Result.success(adminStatsService.getFavoriteRanking(limit));
    }

    /**
     * 获取评论量排行榜
     */
    @GetMapping("/ranking/comments")
    public Result<java.util.List<Map<String, Object>>> getCommentRanking(
            @RequestParam(defaultValue = "10") int limit) {
        return Result.success(adminStatsService.getCommentRanking(limit));
    }

    /**
     * 获取评分排行榜
     */
    @GetMapping("/ranking/ratings")
    public Result<java.util.List<Map<String, Object>>> getRatingRanking(
            @RequestParam(defaultValue = "10") int limit) {
        return Result.success(adminStatsService.getRatingRanking(limit));
    }
}
