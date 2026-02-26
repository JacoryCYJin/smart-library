package io.github.jacorycyjin.smartlibrary.backend.dto;

import io.github.jacorycyjin.smartlibrary.backend.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 图书评论与评分 DTO
 * 
 * @author Jacory
 * @date 2025/01/19
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class CommentDTO {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名（关联查询）
     */
    private String username;

    /**
     * 用户头像（关联查询）
     */
    private String avatarUrl;

    /**
     * 资源ID
     */
    private String resourceId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评分（1-5）
     */
    private BigDecimal score;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 审核状态：0=待审，1=通过，2=拒绝
     */
    private Integer auditStatus;

    /**
     * 拒绝理由
     */
    private String rejectionReason;

    /**
     * 创建时间
     */
    private LocalDateTime ctime;

    /**
     * 更新时间
     */
    private LocalDateTime mtime;

    /**
     * 从实体类转换为 DTO
     */
    public static CommentDTO fromEntity(Comment comment) {
        if (comment == null) {
            return null;
        }
        return CommentDTO.builder()
                .userId(comment.getUserId())
                .resourceId(comment.getResourceId())
                .content(comment.getContent())
                .score(comment.getScore())
                .likeCount(comment.getLikeCount())
                .auditStatus(comment.getAuditStatus())
                .rejectionReason(comment.getRejectionReason())
                .ctime(comment.getCtime())
                .mtime(comment.getMtime())
                .build();
    }
}
