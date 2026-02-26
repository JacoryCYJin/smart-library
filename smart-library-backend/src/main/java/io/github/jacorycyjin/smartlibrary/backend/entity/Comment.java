package io.github.jacorycyjin.smartlibrary.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 图书评论与评分实体类
 * 
 * @author Jacory
 * @date 2025/12/28
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class Comment {

    /**
     * 主键ID，自增
     */
    private Long id;

    /**
     * 用户ID
     */
    private String userId;

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
     * 是否删除：0=未删除，1=已删除
     */
    private Integer deleted;
}
