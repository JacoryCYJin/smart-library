package io.github.jacorycyjin.smartlibrary.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 书签实体类（用于Matter.js物理引擎掉落）
 * 
 * @author Jacory
 * @date 2025/01/19
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class Bookmark {

    /**
     * 主键ID，自增
     */
    private Long id;

    /**
     * 书签业务ID
     */
    private String bookmarkId;

    /**
     * 点击跳转目标资源ID
     */
    private String resourceId;

    /**
     * 金句内容
     */
    private String content;

    /**
     * 作者/出处
     */
    private String authorNote;

    /**
     * 引流次数
     */
    private Integer clickCount;

    /**
     * 状态：1=上架，0=下架
     */
    private Integer status;

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
