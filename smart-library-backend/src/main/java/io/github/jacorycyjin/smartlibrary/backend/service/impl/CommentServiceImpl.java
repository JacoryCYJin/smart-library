package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageDTO;
import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;
import io.github.jacorycyjin.smartlibrary.backend.common.exception.BusinessException;
import io.github.jacorycyjin.smartlibrary.backend.common.util.UUIDUtil;
import io.github.jacorycyjin.smartlibrary.backend.dto.CommentDTO;
import io.github.jacorycyjin.smartlibrary.backend.entity.Comment;
import io.github.jacorycyjin.smartlibrary.backend.entity.User;
import io.github.jacorycyjin.smartlibrary.backend.form.CommentCreateForm;
import io.github.jacorycyjin.smartlibrary.backend.mapper.CommentMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.ResourceMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.UserMapper;
import io.github.jacorycyjin.smartlibrary.backend.service.CommentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评论服务实现
 * 
 * @author Jacory
 * @date 2025/01/21
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private ResourceMapper resourceMapper;

    /**
     * 获取资源的评论列表（分页）
     * 
     * @param resourceId 资源业务ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    @Override
    public PageDTO<CommentDTO> getCommentsByResourceId(String resourceId, Integer pageNum, Integer pageSize) {
        // 设置默认值
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        // 分页查询评论（只查询已通过审核的）
        PageHelper.startPage(pageNum, pageSize);
        List<Comment> comments = commentMapper.selectByResourceId(resourceId);
        PageInfo<Comment> pageInfo = new PageInfo<>(comments);

        // 批量查询用户信息
        List<String> userIds = comments.stream()
                .map(Comment::getUserId)
                .distinct()
                .toList();
        
        Map<String, User> userMap = userMapper.selectByUserIds(userIds).stream()
                .collect(Collectors.toMap(User::getUserId, u -> u));

        // 转换为 DTO（包含用户信息）
        List<CommentDTO> commentDTOs = comments.stream()
                .map(comment -> {
                    CommentDTO dto = CommentDTO.fromEntity(comment);
                    User user = userMap.get(comment.getUserId());
                    if (user != null) {
                        dto.setUsername(user.getUsername());
                        dto.setAvatarUrl(user.getAvatarUrl());
                    }
                    return dto;
                })
                .toList();

        return new PageDTO<>(pageNum, (int) pageInfo.getTotal(), pageSize, commentDTOs);
    }

    /**
     * 创建评论
     * 
     * @param userId 用户业务ID
     * @param form 评论创建表单
     * @return 评论ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createComment(String userId, CommentCreateForm form) {
        // 检查资源是否存在（使用通用查询方法）
        Map<String, Object> params = new HashMap<>();
        params.put("resourceId", form.getResourceId());
        params.put("limit", 1);
        List<io.github.jacorycyjin.smartlibrary.backend.entity.Resource> resources = resourceMapper.searchResources(params);
        
        if (resources == null || resources.isEmpty()) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "资源不存在");
        }

        // 创建评论
        Comment comment = Comment.builder()
                .commentId(UUIDUtil.generateUUID())
                .userId(userId)
                .resourceId(form.getResourceId())
                .content(form.getContent())
                .score(form.getScore())
                .likeCount(0)
                .auditStatus(1) // 默认通过审核
                .ctime(LocalDateTime.now())
                .deleted(0)
                .build();

        commentMapper.insert(comment);

        // 增加资源的评论数
        resourceMapper.incrementCommentCount(form.getResourceId());

        return comment.getCommentId();
    }

    /**
     * 删除评论
     * 
     * @param userId 用户业务ID
     * @param commentId 评论ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(String userId, String commentId) {
        Comment comment = commentMapper.selectByCommentId(commentId);
        if (comment == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "评论不存在");
        }

        // 只能删除自己的评论
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(ApiCode.FORBIDDEN.getCode(), "无权删除他人评论");
        }

        // 逻辑删除
        commentMapper.deleteByCommentId(commentId);

        // 减少资源的评论数
        resourceMapper.decrementCommentCount(comment.getResourceId());
    }
}
