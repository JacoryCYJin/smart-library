package io.github.jacorycyjin.smartlibrary.backend.service;

import io.github.jacorycyjin.smartlibrary.backend.vo.BookmarkVO;

/**
 * 书签服务接口
 * 
 * @author Jacory
 * @date 2025/03/14
 */
public interface BookmarkService {

    /**
     * 随机获取一个书签（排除用户已交互的资源）
     * 
     * @param userId 用户ID（可为空）
     * @return 书签VO，如果没有可用书签则返回null
     */
    BookmarkVO getRandomBookmark(String userId);

    /**
     * 记录书签点击（用户点击书签跳转时调用）
     * 
     * @param bookmarkId 书签ID
     */
    void recordClick(String bookmarkId);
}
