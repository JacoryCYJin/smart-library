package io.github.jacorycyjin.smartlibrary.backend.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 公告表单
 * 
 * @author jacorycyjin
 * @date 2025/01/15
 */
@Data
public class AnnouncementForm {
    @NotBlank(message = "标题不能为空")
    private String title;
    
    @NotBlank(message = "内容不能为空")
    private String content;
    
    @NotNull(message = "类型不能为空")
    private Integer type; // 1-系统更新 / 2-功能上线 / 3-维护通知 / 4-活动公告
    
    private Integer priority; // 0-普通 / 1-重要 / 2-紧急
    
    private Integer status; // 0-草稿 / 1-已发布
}
