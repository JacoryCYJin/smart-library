package io.github.jacorycyjin.smartlibrary.backend.controller;

import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;
import io.github.jacorycyjin.smartlibrary.backend.common.response.Result;
import io.github.jacorycyjin.smartlibrary.backend.common.util.UserContext;
import io.github.jacorycyjin.smartlibrary.backend.service.BookmarkService;
import io.github.jacorycyjin.smartlibrary.backend.vo.BookmarkVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 书签控制器
 * 
 * @author Jacory
 * @date 2025/03/14
 */
@Slf4j
@RestController
@RequestMapping("/bookmark")
public class BookmarkController {

    @Resource
    private BookmarkService bookmarkService;

    /**
     * 随机获取一个书签（漂流书签触发）
     * 
     * @return 书签VO
     */
    @GetMapping("/random")
    public Result<BookmarkVO> getRandomBookmark() {
        // 从 ThreadLocal 获取当前用户ID（未登录时为 null）
        String userId = UserContext.getCurrentUserId();
        
        log.info("随机获取书签: userId={}", userId);
        
        BookmarkVO bookmark = bookmarkService.getRandomBookmark(userId);
        
        if (bookmark == null) {
            return Result.fail(ApiCode.RESOURCE_NOT_FOUND.getCode(), "暂无可用书签");
        }
        
        return Result.success(bookmark);
    }

    /**
     * 记录书签点击（用户点击书签跳转时调用）
     * 
     * @param bookmarkId 书签ID
     * @return 成功响应
     */
    @PostMapping("/click/{bookmarkId}")
    public Result<Void> recordClick(@PathVariable String bookmarkId) {
        log.info("记录书签点击: bookmarkId={}", bookmarkId);
        
        bookmarkService.recordClick(bookmarkId);
        
        return Result.success();
    }
}
