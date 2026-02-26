package io.github.jacorycyjin.smartlibrary.backend.vo;

import io.github.jacorycyjin.smartlibrary.backend.dto.CategoryDTO;
import io.github.jacorycyjin.smartlibrary.backend.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 图书分类 VO
 * 
 * @author Jacory
 * @date 2025/12/28
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class CategoryVO {

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
     * 从 DTO 转换为 VO
     */
    public static CategoryVO fromDTO(CategoryDTO dto) {
        if (dto == null) {
            return null;
        }
        return CategoryVO.builder()
                .categoryId(dto.getCategoryId())
                .parentId(dto.getParentId())
                .name(dto.getName())
                .level(dto.getLevel())
                .sortOrder(dto.getSortOrder())
                .ctime(dto.getCtime())
                .mtime(dto.getMtime())
                .build();
    }

    /**
     * 从实体类转换为 VO
     */
    public static CategoryVO fromEntity(Category entity) {
        if (entity == null) {
            return null;
        }
        return CategoryVO.builder()
                .categoryId(entity.getCategoryId())
                .parentId(entity.getParentId())
                .name(entity.getName())
                .level(entity.getLevel())
                .sortOrder(entity.getSortOrder())
                .ctime(entity.getCtime())
                .mtime(entity.getMtime())
                .build();
    }
}
