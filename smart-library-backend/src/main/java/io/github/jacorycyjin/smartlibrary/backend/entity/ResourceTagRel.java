package io.github.jacorycyjin.smartlibrary.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;

/**
 * 资源-标签关联实体类
 * 
 * @author Jacory
 * @date 2025/01/19
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class ResourceTagRel {

    /**
     * 主键ID，自增
     */
    private Long id;

    /**
     * 资源ID
     */
    private String resourceId;

    /**
     * 标签ID
     */
    private String tagId;

    /**
     * TF-IDF权重值（核心算法字段）
     */
    private BigDecimal weight;
}
