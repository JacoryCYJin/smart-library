package io.github.jacorycyjin.smartlibrary.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户通知实体类
 * 
 * @author jacorycyjin
 * @date 2025/01/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNotification {
    private Long id;
    private String notificationId;
    private String userId;
    
    private Integer type; // 1-系统公告 / 2-评论回复 / 3-收藏提醒 / 4-系统消息
    private String title;
    private String content;
    private String linkUrl;
    
    private String relatedId;
    
    private Integer isRead; // 0-未读 / 1-已读
    private LocalDateTime readTime;
    
    private LocalDateTime ctime;
    private Integer deleted;
}
