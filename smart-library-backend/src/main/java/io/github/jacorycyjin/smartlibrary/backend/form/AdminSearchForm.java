package io.github.jacorycyjin.smartlibrary.backend.form;

import lombok.Data;

/**
 * 管理员搜索表单
 * 
 * @author Jacory
 * @date 2026/04/05
 */
@Data
public class AdminSearchForm {

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

    /**
     * 资源ID（用于链接查询）
     */
    private String resourceId;

    /**
     * 生成状态（用于图谱查询）
     */
    private Integer generateStatus;
}
