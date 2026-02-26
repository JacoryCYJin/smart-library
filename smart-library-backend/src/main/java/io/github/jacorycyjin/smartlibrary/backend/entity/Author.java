package io.github.jacorycyjin.smartlibrary.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 作者信息实体类
 * 
 * @author Jacory
 * @date 2025/12/28
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class Author {

    /**
     * 主键ID，自增
     */
    private Long id;

    /**
     * 作者业务ID
     */
    private String authorId;

    /**
     * 作者姓名
     */
    private String name;

    /**
     * 原名
     */
    private String originalName;

    /**
     * 国籍
     */
    private String country;

    /**
     * 头像URL
     */
    private String photoUrl;

    /**
     * 生平简介
     */
    private String description;

    /**
     * 来源
     */
    private String sourceOrigin;

    /**
     * 来源链接
     */
    private String sourceUrl;

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
