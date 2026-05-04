package io.github.jacorycyjin.smartlibrary.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 资源链接爬取任务实体
 * 
 * @author Kiro
 * @date 2026/05/04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceLinkCrawlTask {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 资源ID
     */
    private String resourceId;
    
    /**
     * ISBN
     */
    private String isbn;
    
    /**
     * 图书标题
     */
    private String title;
    
    /**
     * 书籍页链接数量
     */
    private Integer infoPageCount;
    
    /**
     * 书籍页链接列表（JSON）
     */
    private String infoPageJson;
    
    /**
     * 书籍页爬取状态: 0-待处理 / 1-处理中 / 2-已完成 / 3-失败 / 4-无资源
     */
    private Integer infoPageStatus;
    
    /**
     * 下载页链接数量
     */
    private Integer downloadPageCount;
    
    /**
     * 下载页链接列表（JSON）
     */
    private String downloadPageJson;
    
    /**
     * 下载页爬取状态: 0-待处理 / 1-处理中 / 2-已完成 / 3-失败 / 4-无资源
     */
    private Integer downloadPageStatus;
    
    /**
     * 解读页链接数量
     */
    private Integer reviewPageCount;
    
    /**
     * 解读页链接列表（JSON）
     */
    private String reviewPageJson;
    
    /**
     * 解读页爬取状态: 0-待处理 / 1-处理中 / 2-已完成 / 3-失败 / 4-无资源
     */
    private Integer reviewPageStatus;
    
    /**
     * 整体状态: 0-待处理 / 1-部分完成 / 2-全部完成 / 3-全部失败
     */
    private Integer overallStatus;
    
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
