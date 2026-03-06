package io.github.jacorycyjin.smartlibrary.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI人物关系图谱实体
 *
 * @author jcy
 * @date 2026/03/06
 */
@Data
public class ResourceCharacterGraph {
    private Long id;
    private String graphId;
    private String resourceId;
    
    private String graphJson;
    
    private Integer generateStatus;
    private String errorMessage;
    
    private String aiModel;
    private Integer tokenUsage;
    private Integer generateTime;
    
    private LocalDateTime ctime;
    private LocalDateTime mtime;
    private Integer deleted;
}
