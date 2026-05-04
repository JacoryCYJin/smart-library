package io.github.jacorycyjin.smartlibrary.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 豆瓣图书爬取任务实体
 * 
 * @author Kiro
 * @date 2026/05/04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoubanCrawlTask {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 分类ID
     */
    private String categoryId;
    
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 状态: 0-待处理 / 1-处理中 / 2-已完成 / 3-失败
     */
    private Integer status;
    
    /**
     * 进度（已爬取数量）
     */
    private Integer progress;
    
    /**
     * 目标数量
     */
    private Integer target;
    
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
