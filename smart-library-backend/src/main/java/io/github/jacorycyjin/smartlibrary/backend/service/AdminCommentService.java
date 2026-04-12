package io.github.jacorycyjin.smartlibrary.backend.service;

import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageDTO;
import io.github.jacorycyjin.smartlibrary.backend.entity.Comment;

import java.util.Map;

/**
 * 管理员评论管理服务接口
 * 
 * @author Kiro
 * @date 2026/04/05
 */
public interface AdminCommentService {

    /**
     * 获取评论列表
     */
    PageDTO<Comment> getCommentList(Map<String, Object> params);

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
