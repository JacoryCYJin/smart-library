package io.github.jacorycyjin.smartlibrary.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI情感走向图实体
 *
 * @author jcy
 * @date 2026/04/06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceEmotionArc {
    private Long id;
    private String arcId;
    private String resourceId;
    
    private String arcJson;
    
    private Integer generateStatus;
    private String errorMessage;
    
    private String aiModel;
    private Integer tokenUsage;
    private Integer generateTime;
    
    private LocalDateTime ctime;
    private LocalDateTime mtime;
    private Integer deleted;
}
