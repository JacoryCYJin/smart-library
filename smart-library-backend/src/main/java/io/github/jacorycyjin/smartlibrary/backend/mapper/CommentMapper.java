package io.github.jacorycyjin.smartlibrary.backend.mapper;

import io.github.jacorycyjin.smartlibrary.backend.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 评论 Mapper
 * 
 * @author Jacory
 * @date 2025/01/19
 */
@Mapper
public interface CommentMapper {

    /**
     * 根据资源ID查询已通过审核的评论列表（支持分页）
     * 
     * @param resourceId 资源业务ID
     * @return 评论列表（按创建时间倒序）
     */
    List<Comment> selectByResourceId(@Param("resourceId") String resourceId);

    /**
     * 统计资源的评论总数
     * 
     * @param resourceId 资源业务ID
     * @return 评论总数
     */
    int countByResourceId(@Param("resourceId") String resourceId);

    /**
     * 根据CommentId查询评论
     * 
     * @param commentId 评论业务ID
     * @return 评论实体
     */
    Comment selectByCommentId(@Param("commentId") String commentId);

    /**
     * 插入评论
     * 
     * @param comment 评论实体
     * @return 影响行数
     */
    int insert(Comment comment);

    /**
     * 逻辑删除评论
     * 
     * @param commentId 评论业务ID
     * @return 影响行数
     */
    int deleteByCommentId(@Param("commentId") String commentId);
}
