package io.github.jacorycyjin.smartlibrary.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系统公告实体类
 * 
 * @author jacorycyjin
 * @date 2025/01/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Announcement {
    private Long id;
    private String announcementId;
    private String title;
    private String content;
    private Integer type; // 1-系统更新 / 2-功能上线 / 3-维护通知 / 4-活动公告
    private Integer priority; // 0-普通 / 1-重要 / 2-紧急
    
    private String publisherId;
    private String publisherName;
    
    private Integer status; // 0-草稿 / 1-已发布 / 2-已撤回
    private LocalDateTime publishTime;
    
    private Integer viewCount;
    
    private LocalDateTime ctime;
    private LocalDateTime mtime;
    private Integer deleted;
}
