package io.github.jacorycyjin.smartlibrary.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

/**
 * 资源-分类关联实体类
 * 
 * @author Jacory
 * @date 2025/01/19
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class ResourceCategoryRel {

    /**
     * 主键ID，自增
     */
    private Long id;

    /**
     * 资源ID
     */
    private String resourceId;

    /**
     * 分类ID
     */
    private String categoryId;
}
