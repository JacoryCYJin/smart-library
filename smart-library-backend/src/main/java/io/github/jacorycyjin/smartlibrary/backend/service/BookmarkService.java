package io.github.jacorycyjin.smartlibrary.backend.service;

import java.util.Map;

/**
 * 书签服务接口
 * 
 * @author Jacory
 * @date 2025/04/06
 */
public interface BookmarkService {
    
    /**
     * 随机获取一个书签（包含书名、作者、封面等信息）
     * 
     * @return 书签数据
     */
    Map<String, Object> getRandomBookmark();
    
    /**
     * 记录书签点击
     * 
     * @param bookmarkId 书签ID
     */
    void recordClick(String bookmarkId);
}
