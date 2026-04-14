package io.github.jacorycyjin.smartlibrary.backend.mapper;

import io.github.jacorycyjin.smartlibrary.backend.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 公告Mapper
 * 
 * @author jacorycyjin
 * @date 2025/01/15
 */
@Mapper
public interface AnnouncementMapper {
    
    /**
     * 插入公告
     */
    int insert(Announcement announcement);
    
    /**
     * 根据ID查询公告
     */
    Announcement selectById(@Param("announcementId") String announcementId);
    
    /**
     * 动态查询公告列表
     */
    List<Announcement> search(Map<String, Object> params);
    
    /**
     * 统计公告数量
     */
    int count(Map<String, Object> params);
    
    /**
     * 更新公告
     */
    int update(Announcement announcement);
    
    /**
     * 逻辑删除公告
     */
    int deleteById(@Param("announcementId") String announcementId);
    
    /**
     * 增加查看次数
     */
    int incrementViewCount(@Param("announcementId") String announcementId);
}
