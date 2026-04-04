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
}
