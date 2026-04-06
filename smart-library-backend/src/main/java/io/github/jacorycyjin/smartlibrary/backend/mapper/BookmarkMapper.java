package io.github.jacorycyjin.smartlibrary.backend.mapper;

import io.github.jacorycyjin.smartlibrary.backend.entity.Bookmark;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 书签 Mapper
 * 
 * @author Jacory
 * @date 2025/04/06
 */
@Mapper
public interface BookmarkMapper {
    
    /**
     * 随机获取一个书签（关联资源表）
     * 
     * @return 书签数据（包含书名、封面等）
     */
    Map<String, Object> selectRandomBookmark();
    
    /**
     * 根据ID查询书签
     * 
     * @param bookmarkId 书签ID
     * @return 书签实体
     */
    Bookmark selectByBookmarkId(@Param("bookmarkId") String bookmarkId);
    
    /**
     * 增加点击次数
     * 
     * @param bookmarkId 书签ID
     * @return 影响行数
     */
    int incrementClickCount(@Param("bookmarkId") String bookmarkId);
}
