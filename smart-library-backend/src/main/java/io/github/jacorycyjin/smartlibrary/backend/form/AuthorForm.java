package io.github.jacorycyjin.smartlibrary.backend.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 作者表单
 * 
 * @author Jacory
 * @date 2026/04/05
 */
@Data
public class AuthorForm {

    /**
     * 作者ID（编辑时必填）
     */
    private String authorId;

    /**
     * 作者姓名
     */
    @NotBlank(message = "作者姓名不能为空")
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
     * 数据来源：1=豆瓣读书，2=Z-Library，99=手动录入
     */
    private Integer sourceOrigin;

    /**
     * 来源链接
     */
    private String sourceUrl;
}
