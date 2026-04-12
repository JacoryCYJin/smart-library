package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageDTO;
import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;
import io.github.jacorycyjin.smartlibrary.backend.common.exception.BusinessException;
import io.github.jacorycyjin.smartlibrary.backend.entity.Comment;
import io.github.jacorycyjin.smartlibrary.backend.mapper.CommentMapper;
import io.github.jacorycyjin.smartlibrary.backend.service.AdminCommentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 管理员评论管理服务实现
 * 
 * @author Kiro
 * @date 2026/04/05
 */
@Service
public class AdminCommentServiceImpl implements AdminCommentService {

    @Resource
    private CommentMapper commentMapper;

    @Override
    public PageDTO<Comment> getCommentList(Map<String, Object> params) {
        // 获取分页参数并转换类型
        Integer pageNum = 1;
        Integer pageSize = 10;
        
        if (params.get("page") != null) {
            Object pageObj = params.get("page");
            pageNum = pageObj instanceof Integer ? (Integer) pageObj : Integer.parseInt(pageObj.toString());
        }
        
        if (params.get("pageSize") != null) {
            Object pageSizeObj = params.get("pageSize");
            pageSize = pageSizeObj instanceof Integer ? (Integer) pageSizeObj : Integer.parseInt(pageSizeObj.toString());
        }
        
        // 设置分页参数
        PageHelper.startPage(pageNum, pageSize);

        // 查询评论列表
        params.put("deleted", 0);
        List<Comment> comments = commentMapper.searchComments(params);
        PageInfo<Comment> pageInfo = new PageInfo<>(comments);

        return new PageDTO<>(pageNum, (int) pageInfo.getTotal(), pageSize, pageInfo.getList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditComment(String commentId, Integer auditStatus, String rejectionReason) {
        Comment comment = commentMapper.findByCommentId(commentId);
        if (comment == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "评论不存在");
        }

        comment.setAuditStatus(auditStatus);
        if (auditStatus == 2 && rejectionReason != null) {
            comment.setRejectionReason(rejectionReason);
        }
        commentMapper.updateByCommentId(comment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(String commentId) {
        Comment comment = commentMapper.findByCommentId(commentId);
        if (comment == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "评论不存在");
        }

        comment.setDeleted(1);
        commentMapper.updateByCommentId(comment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restoreComment(String commentId) {
        Comment comment = commentMapper.findByCommentId(commentId);
        if (comment == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "评论不存在");
        }

        comment.setDeleted(0);
        commentMapper.updateByCommentId(comment);
    }
}
