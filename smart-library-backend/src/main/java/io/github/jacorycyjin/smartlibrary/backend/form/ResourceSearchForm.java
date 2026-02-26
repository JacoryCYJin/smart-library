package io.github.jacorycyjin.smartlibrary.backend.form;

import io.github.jacorycyjin.smartlibrary.backend.common.form.PageQueryForm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 资源搜索表单
 * 
 * @author Jacory
 * @date 2025/01/19
 */
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ResourceSearchForm extends PageQueryForm {

    /**
     * 资源类型（1=图书，2=文献）
     * 不传则查询所有类型
     */
    private Integer type;

    /**
     * 关键词（搜索标题、作者、简介）
     */
    private String keyword;

    /**
     * 分类ID列表
     */
    private List<String> categoryIds;

    /**
     * 标签ID列表
     */
    private List<String> tagIds;

    /**
     * 作者名称
     */
    private String authorName;

    /**
     * 出版社（仅图书）
     */
    private String publisher;

    /**
     * 期刊名称（仅文献）
     */
    private String journal;

    /**
     * 排序字段（view_count, comment_count, star_count, pub_date, ctime）
     * 默认降序排列
     */
    private String sortBy;
}
