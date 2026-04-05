package io.github.jacorycyjin.smartlibrary.backend.form;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 分类表单（添加/编辑）
 * 
 * @author Kiro
 * @date 2026/04/05
 */
@Data
public class CategoryForm {

    /**
     * 分类ID（编辑时必填）
     */
    private String categoryId;

    /**
     * 父分类ID（顶级分类为 null 或 "0"）
     */
    private String parentId;

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    private String name;

    /**
     * 层级（1-3）
     */
    @NotNull(message = "层级不能为空")
    private Integer level;

    /**
     * 排序权重
     */
    private Integer sortOrder;
}
