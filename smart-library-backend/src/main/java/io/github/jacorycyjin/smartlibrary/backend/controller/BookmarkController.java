package io.github.jacorycyjin.smartlibrary.backend.controller;

import io.github.jacorycyjin.smartlibrary.backend.common.response.Result;
import io.github.jacorycyjin.smartlibrary.backend.service.BookmarkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 书签 Controller
 * 
 * @author Jacory
 * @date 2025/04/06
 */
@Slf4j
@RestController
@RequestMapping("/bookmark")
public class BookmarkController {
    
    private final BookmarkService bookmarkService;
    
    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }
    
    /**
     * 随机获取一个书签
     * 
     * @return 书签数据（包含书名、作者、封面等）
     */
    @GetMapping("/random")
    public Result<Map<String, Object>> getRandomBookmark() {
        Map<String, Object> bookmark = bookmarkService.getRandomBookmark();
        return Result.success(bookmark);
    }
    
    /**
     * 记录书签点击
     * 
     * @param bookmarkId 书签ID
     * @return 成功标识
     */
    @PostMapping("/click/{bookmarkId}")
    public Result<Void> recordClick(@PathVariable String bookmarkId) {
        bookmarkService.recordClick(bookmarkId);
        return Result.success();
    }
}
