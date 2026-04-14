package io.github.jacorycyjin.smartlibrary.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 公告VO
 * 
 * @author jacorycyjin
 * @date 2025/01/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementVO {
    private String announcementId;
    private String title;
    private String content;
    private Integer type;
    private Integer priority;
    private Integer status;
    private String publisherName;
    private LocalDateTime publishTime;
    private Integer viewCount;
}
