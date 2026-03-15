package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import io.github.jacorycyjin.smartlibrary.backend.entity.Bookmark;
import io.github.jacorycyjin.smartlibrary.backend.mapper.BookmarkMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.ResourceMapper;
import io.github.jacorycyjin.smartlibrary.backend.service.BookmarkService;
import io.github.jacorycyjin.smartlibrary.backend.vo.BookmarkVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 书签服务实现类
 * 
 * @author Jacory
 * @date 2025/03/14
 */
@Slf4j
@Service
public class BookmarkServiceImpl implements BookmarkService {

    @jakarta.annotation.Resource
    private BookmarkMapper bookmarkMapper;

    @jakarta.annotation.Resource
    private ResourceMapper resourceMapper;

    @Override
    public BookmarkVO getRandomBookmark(String userId) {
        // 随机获取一个书签
        Bookmark bookmark = bookmarkMapper.selectRandomBookmark(userId);
        
        if (bookmark == null) {
            log.info("没有可用的书签数据");
            return null;
        }

        // 查询关联的资源信息
        io.github.jacorycyjin.smartlibrary.backend.entity.Resource resource = 
            resourceMapper.selectById(bookmark.getResourceId());

        // 构建 VO
        return BookmarkVO.builder()
                .bookmarkId(bookmark.getBookmarkId())
                .resourceId(bookmark.getResourceId())
                .content(bookmark.getContent())
                .authorName(bookmark.getAuthorName())
                .bookTitle(bookmark.getBookTitle())
                .resourceCoverUrl(resource != null ? resource.getCoverUrl() : "")
                .build();
    }

    @Override
    public void recordClick(String bookmarkId) {
        int rows = bookmarkMapper.incrementClickCount(bookmarkId);
        if (rows > 0) {
            log.info("书签点击记录成功: bookmarkId={}", bookmarkId);
        } else {
            log.warn("书签点击记录失败: bookmarkId={}", bookmarkId);
        }
    }
}
