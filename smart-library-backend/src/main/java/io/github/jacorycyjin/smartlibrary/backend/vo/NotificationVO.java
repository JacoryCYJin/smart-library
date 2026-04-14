package io.github.jacorycyjin.smartlibrary.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户通知VO
 * 
 * @author jacorycyjin
 * @date 2025/01/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationVO {
    private String notificationId;
    private Integer type;
    private String title;
    private String content;
    private String linkUrl;
    private Integer isRead;
    private LocalDateTime ctime;
}
