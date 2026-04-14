package io.github.jacorycyjin.smartlibrary.backend.controller;

import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;
import io.github.jacorycyjin.smartlibrary.backend.common.response.Result;
import io.github.jacorycyjin.smartlibrary.backend.common.util.UserContext;
import io.github.jacorycyjin.smartlibrary.backend.service.NotificationService;
import io.github.jacorycyjin.smartlibrary.backend.vo.NotificationVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知控制器
 * 
 * @author jacorycyjin
 * @date 2025/01/15
 */
@RestController
@RequestMapping("/notification")
public class NotificationController {
    
    @Resource
    private NotificationService notificationService;
    
    /**
     * 获取用户通知列表
     */
    @GetMapping("/list")
    public Result<List<NotificationVO>> getNotifications(@RequestParam(defaultValue = "20") Integer limit) {
        String userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.fail(ApiCode.UNAUTHORIZED.getCode(), "未登录");
        }
        
        List<NotificationVO> notifications = notificationService.getUserNotifications(userId, limit);
        return Result.success(notifications);
    }
    
    /**
     * 获取未读通知数量
     */
    @GetMapping("/unread-count")
    public Result<Map<String, Integer>> getUnreadCount() {
        String userId = UserContext.getCurrentUserId();
        if (userId == null) {
            Map<String, Integer> result = new HashMap<>();
            result.put("count", 0);
            return Result.success(result);
        }
        
        int count = notificationService.getUnreadCount(userId);
        Map<String, Integer> result = new HashMap<>();
        result.put("count", count);
        return Result.success(result);
    }
    
    /**
     * 标记通知为已读
     */
    @PostMapping("/read/{notificationId}")
    public Result<Void> markAsRead(@PathVariable String notificationId) {
        String userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.fail(ApiCode.UNAUTHORIZED.getCode(), "未登录");
        }
        
        notificationService.markAsRead(notificationId, userId);
        return Result.success();
    }
    
    /**
     * 标记全部为已读
     */
    @PostMapping("/read-all")
    public Result<Void> markAllAsRead() {
        String userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.fail(ApiCode.UNAUTHORIZED.getCode(), "未登录");
        }
        
        notificationService.markAllAsRead(userId);
        return Result.success();
    }
    
    /**
     * 删除通知
     */
    @DeleteMapping("/{notificationId}")
    public Result<Void> deleteNotification(@PathVariable String notificationId) {
        String userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.fail(ApiCode.UNAUTHORIZED.getCode(), "未登录");
        }
        
        notificationService.deleteNotification(notificationId, userId);
        return Result.success();
    }
}
