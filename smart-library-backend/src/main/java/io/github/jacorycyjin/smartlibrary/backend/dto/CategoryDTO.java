package io.github.jacorycyjin.smartlibrary.backend.dto;

import io.github.jacorycyjin.smartlibrary.backend.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 图书分类 DTO
 * 
 * @author Jacory
 * @date 2025/12/28
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class CategoryDTO {

    /**
     * 分类业务ID
     */
    private String categoryId;

    /**
     * 父分类ID
     */
    private String parentId;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 层级
     */
    private Integer level;

    /**
     * 排序权重
     */
    private Integer sortOrder;

    /**
     * 创建时间
     */
    private LocalDateTime ctime;

    /**
     * 更新时间
     */
    private LocalDateTime mtime;

    /**
     * 从实体类转换为 DTO
     */
    public static CategoryDTO fromEntity(Category category) {
        if (category == null) {
            return null;
        }
        return CategoryDTO.builder()
                .categoryId(category.getCategoryId())
                .parentId(category.getParentId())
                .name(category.getName())
                .level(category.getLevel())
                .sortOrder(category.getSortOrder())
                .ctime(category.getCtime())
                .mtime(category.getMtime())
                .build();
    }
}
