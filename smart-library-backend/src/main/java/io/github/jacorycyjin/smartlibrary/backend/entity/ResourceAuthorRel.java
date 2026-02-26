package io.github.jacorycyjin.smartlibrary.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

/**
 * 资源-作者关联实体类
 * 
 * @author Jacory
 * @date 2025/01/19
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class ResourceAuthorRel {

    /**
     * 主键ID，自增
     */
    private Long id;

    /**
     * 资源ID
     */
    private String resourceId;

    /**
     * 作者ID
     */
    private String authorId;

    /**
     * 角色（作者/译者/编者）
     */
    private String role;

    /**
     * 排序权重：1为一作，2为二作...
     */
    private Integer sort;
}
