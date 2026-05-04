package io.github.jacorycyjin.smartlibrary.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 作者信息爬取任务实体
 * 
 * @author Kiro
 * @date 2026/05/04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorCrawlTask {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 作者ID
     */
    private String authorId;
    
    /**
     * 作者姓名
     */
    private String authorName;
    
    /**
     * 豆瓣作者页面URL
     */
    private String doubanAuthorUrl;
    
    /**
     * 状态: 0-待处理 / 1-处理中 / 2-已完成 / 3-失败 / 4-无资源
     */
    private Integer status;
    
    /**
     * 错误信息
     */
    private String errorMsg;
    
    /**
     * 重试次数
     */
    private Integer retryCount;
    
    /**
     * 创建时间
     */
    private LocalDateTime ctime;
    
    /**
     * 更新时间
     */
    private LocalDateTime mtime;
}
