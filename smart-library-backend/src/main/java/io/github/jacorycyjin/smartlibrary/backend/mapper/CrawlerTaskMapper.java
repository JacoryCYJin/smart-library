package io.github.jacorycyjin.smartlibrary.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 爬虫任务 Mapper
 * 
 * @author Kiro
 * @date 2026/05/04
 */
@Mapper
public interface CrawlerTaskMapper {
    
    /**
     * 获取豆瓣任务统计（按状态分组）
     * 
     * @return 统计数据 {pending: 0, processing: 0, completed: 0, failed: 0}
     */
    Map<String, Object> getDoubanTaskStats();
    
    /**
     * 获取作者任务统计（按状态分组）
     * 
     * @return 统计数据 {pending: 0, processing: 0, completed: 0, failed: 0}
     */
    Map<String, Object> getAuthorTaskStats();
    
    /**
     * 获取资源链接任务统计（按整体状态分组）
     * 
     * @return 统计数据 {pending: 0, processing: 0, completed: 0, failed: 0}
     */
    Map<String, Object> getLinkTaskStats();
}
