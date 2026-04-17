package io.github.jacorycyjin.smartlibrary.backend.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 全局搜索表单
 * 
 * @author Jacory
 * @date 2025/01/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalSearchForm {

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 图书页码（从 1 开始）
     */
    private Integer bookPageNum = 1;

    /**
     * 图书每页数量
     */
    private Integer bookPageSize = 20;

    /**
     * 作者页码（从 1 开始）
     */
    private Integer authorPageNum = 1;

    /**
     * 作者每页数量
     */
    private Integer authorPageSize = 20;
}
