package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import io.github.jacorycyjin.smartlibrary.backend.mapper.BookmarkMapper;
import io.github.jacorycyjin.smartlibrary.backend.service.BookmarkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 书签服务实现
 * 
 * @author Jacory
 * @date 2025/04/06
 */
@Slf4j
@Service
public class BookmarkServiceImpl implements BookmarkService {
    
    private final BookmarkMapper bookmarkMapper;
    
    public BookmarkServiceImpl(BookmarkMapper bookmarkMapper) {
        this.bookmarkMapper = bookmarkMapper;
    }
    
    @Override
    public Map<String, Object> getRandomBookmark() {
        return bookmarkMapper.selectRandomBookmark();
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
