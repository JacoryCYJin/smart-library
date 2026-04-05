package io.github.jacorycyjin.smartlibrary.backend.form;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 资源表单（添加/编辑）
 * 
 * @author Kiro
 * @date 2026/04/05
 */
@Data
public class ResourceForm {

    /**
     * 资源ID（编辑时必填）
     */
    private String resourceId;

    /**
     * 资源类型：1=图书，2=文献
     */
    @NotNull(message = "资源类型不能为空")
    private Integer type;

    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    private String title;

    /**
     * 封面URL
     */
    private String coverUrl;

    /**
     * 简介
     */
    private String summary;

    /**
     * 出版/发表日期
     */
    private LocalDate pubDate;

    // ========== 图书特有字段 ==========

    /**
     * ISBN号
     */
    private String isbn;

    /**
     * 出版社
     */
    private String publisher;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 页数
     */
    private Integer pageCount;

    // ========== 文献特有字段 ==========

    /**
     * DOI
     */
    private String doi;

    /**
     * 期刊名称
     */
    private String journal;

    // ========== 关联数据 ==========

    /**
     * 分类ID列表
     */
    private List<String> categoryIds;

    /**
     * 标签ID列表
     */
    private List<String> tagIds;

    /**
     * 作者ID列表
     */
    private List<String> authorIds;

    /**
     * 数据来源
     */
    private Integer sourceOrigin;

    /**
     * 原站链接
     */
    private String sourceUrl;
}
