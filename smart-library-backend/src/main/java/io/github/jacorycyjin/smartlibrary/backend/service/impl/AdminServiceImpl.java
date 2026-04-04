package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageDTO;
import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;
import io.github.jacorycyjin.smartlibrary.backend.common.exception.BusinessException;
import io.github.jacorycyjin.smartlibrary.backend.common.util.PageUtils;
import io.github.jacorycyjin.smartlibrary.backend.entity.Comment;
import io.github.jacorycyjin.smartlibrary.backend.entity.Resource;
import io.github.jacorycyjin.smartlibrary.backend.entity.User;
import io.github.jacorycyjin.smartlibrary.backend.mapper.CommentMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.ResourceMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.UserFavoriteMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.UserMapper;
import io.github.jacorycyjin.smartlibrary.backend.service.AdminService;
import io.github.jacorycyjin.smartlibrary.backend.vo.AdminStatsVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员服务实现
 * 
 * @author Kiro
 * @date 2026/04/04
 */
@Service
public class AdminServiceImpl implements AdminService {

    @jakarta.annotation.Resource
    private UserMapper userMapper;

    @jakarta.annotation.Resource
    private ResourceMapper resourceMapper;

    @jakarta.annotation.Resource
    private CommentMapper commentMapper;

    @jakarta.annotation.Resource
    private UserFavoriteMapper userFavoriteMapper;

    @Override
    public AdminStatsVO getStats() {
        // 统计资源总数
        Map<String, Object> resourceParams = new HashMap<>();
        resourceParams.put("deleted", 0);
        Long resourceCount = resourceMapper.countByParams(resourceParams);

        // 统计用户总数
        Map<String, Object> userParams = new HashMap<>();
        userParams.put("deleted", 0);
        Long userCount = userMapper.countByParams(userParams);

        // 统计评论总数
        Map<String, Object> commentParams = new HashMap<>();
        commentParams.put("deleted", 0);
        Long commentCount = commentMapper.countByParams(commentParams);

        // 统计收藏总数
        Long favoriteCount = userFavoriteMapper.countAll();

        return AdminStatsVO.builder()
                .resourceCount(resourceCount)
                .userCount(userCount)
                .commentCount(commentCount)
                .favoriteCount(favoriteCount)
                .build();
    }

    @Override
    public PageDTO getUserList(Map<String, Object> params) {
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

        // 查询用户列表
        params.put("deleted", 0);
        List<User> users = userMapper.searchUsers(params);
        PageInfo<User> pageInfo = new PageInfo<>(users);

        return new PageDTO<>(pageNum, (int) pageInfo.getTotal(), pageSize, pageInfo.getList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(String userId, Integer status) {
        User user = userMapper.findByUserId(userId);
        if (user == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "用户不存在");
        }

        user.setStatus(status);
        userMapper.updateByUserId(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserRole(String userId, Integer role) {
        User user = userMapper.findByUserId(userId);
        if (user == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "用户不存在");
        }

        user.setRole(role);
        userMapper.updateByUserId(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(String userId) {
        User user = userMapper.findByUserId(userId);
        if (user == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "用户不存在");
        }

        user.setDeleted(1);
        userMapper.updateByUserId(user);
    }

    @Override
    public PageDTO getResourceList(Map<String, Object> params) {
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

        // 查询资源列表
        params.put("deleted", 0);
        List<Resource> resources = resourceMapper.searchResources(params);
        PageInfo<Resource> pageInfo = new PageInfo<>(resources);

        return new PageDTO<>(pageNum, (int) pageInfo.getTotal(), pageSize, pageInfo.getList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteResource(String resourceId) {
        Resource resource = resourceMapper.findByResourceId(resourceId);
        if (resource == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "资源不存在");
        }

        resource.setDeleted(1);
        resourceMapper.updateByResourceId(resource);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restoreResource(String resourceId) {
        Resource resource = resourceMapper.findByResourceId(resourceId);
        if (resource == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "资源不存在");
        }

        resource.setDeleted(0);
        resourceMapper.updateByResourceId(resource);
    }

    @Override
    public PageDTO getCommentList(Map<String, Object> params) {
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
