package io.github.jacorycyjin.smartlibrary.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 书签实体
 * 
 * @author Jacory
 * @date 2025/04/06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bookmark {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 书签ID（UUID）
     */
    private String bookmarkId;
    
    /**
     * 资源ID
     */
    private String resourceId;
    
    /**
     * 书签内容（金句）
     */
    private String content;
    
    /**
     * 作者备注（来源）
     */
    private String authorNote;
    
    /**
     * 点击次数
     */
    private Integer clickCount;
    
    /**
     * 创建时间
     */
    private LocalDateTime ctime;
    
    /**
     * 更新时间
     */
    private LocalDateTime mtime;
}
