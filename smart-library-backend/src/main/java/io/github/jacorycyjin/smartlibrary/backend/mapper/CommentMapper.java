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

    /**
     * 检查用户是否已对该资源评论过
     * 
     * @param userId 用户ID
     * @param resourceId 资源ID
     * @return 评论实体（如果存在）
     */
    Comment selectByUserAndResource(@Param("userId") String userId, @Param("resourceId") String resourceId);

    /**
     * 根据评论ID查询评论
     * 
     * @param commentId 评论业务ID
     * @return 评论信息
     */
    Comment findByCommentId(@Param("commentId") String commentId);

    /**
     * 根据评论ID更新评论
     * 
     * @param comment 评论信息
     * @return 影响行数
     */
    int updateByCommentId(Comment comment);

    /**
     * 统计评论数量
     * 
     * @param params 查询参数
     * @return 评论数量
     */
    Long countByParams(java.util.Map<String, Object> params);

    /**
     * 管理员查询评论列表
     * 
     * @param params 查询参数
     * @return 评论列表
     */
    List<Comment> searchComments(java.util.Map<String, Object> params);
}
