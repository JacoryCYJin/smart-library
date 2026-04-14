package io.github.jacorycyjin.smartlibrary.backend.form;

import lombok.Data;

/**
 * 公告搜索表单
 * 
 * @author jacorycyjin
 * @date 2025/01/15
 */
@Data
public class AnnouncementSearchForm {

    /**
     * 搜索关键词（标题或内容）
     */
    private String keyword;

    /**
     * 公告类型（1-系统更新, 2-功能上线, 3-维护通知, 4-活动公告）
     */
    private Integer type;

    /**
     * 优先级（0-普通, 1-重要, 2-紧急）
     */
    private Integer priority;

    /**
     * 状态（0-草稿, 1-已发布）
     */
    private Integer status;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;
}
