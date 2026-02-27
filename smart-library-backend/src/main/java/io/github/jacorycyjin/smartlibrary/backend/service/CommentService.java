package io.github.jacorycyjin.smartlibrary.backend.service;

import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageDTO;
import io.github.jacorycyjin.smartlibrary.backend.dto.CommentDTO;
import io.github.jacorycyjin.smartlibrary.backend.form.CommentCreateForm;

/**
 * 评论服务接口
 * 
 * @author Jacory
 * @date 2025/01/21
 */
public interface CommentService {

    /**
     * 获取资源的评论列表（分页）
     * 
     * @param resourceId 资源ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 评论列表
     */
    PageDTO<CommentDTO> getCommentsByResourceId(String resourceId, Integer pageNum, Integer pageSize);

    /**
     * 创建评论
     * 
     * @param userId 用户ID
     * @param form 评论表单
     * @return 评论ID
     */
    String createComment(String userId, CommentCreateForm form);

    /**
     * 删除评论
     * 
     * @param userId 用户ID
     * @param commentId 评论ID
     */
    void deleteComment(String userId, String commentId);
}
