package io.github.jacorycyjin.smartlibrary.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 资源聚合链接实体
 *
 * @author jcy
 * @date 2026/03/06
 */
@Data
public class ResourceLink {
    private Long id;
    private String linkId;
    private String resourceId;
    
    private Integer linkType;
    private Integer platform;
    private String url;
    private String title;
    private String description;
    private String coverUrl;  // 视频封面URL（B站/YouTube）
    
    private Integer sortOrder;
    private Integer clickCount;
    private Integer status;
    
    private LocalDateTime ctime;
    private LocalDateTime mtime;
    private Integer deleted;
}
