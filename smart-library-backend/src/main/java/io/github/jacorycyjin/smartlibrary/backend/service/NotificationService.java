package io.github.jacorycyjin.smartlibrary.backend.service;

import io.github.jacorycyjin.smartlibrary.backend.vo.NotificationVO;

import java.util.List;

/**
 * 通知服务接口
 * 
 * @author jacorycyjin
 * @date 2025/01/15
 */
public interface NotificationService {
    
    /**
     * 获取用户通知列表
     */
    List<NotificationVO> getUserNotifications(String userId, Integer limit);
    
    /**
     * 获取未读通知数量
     */
    int getUnreadCount(String userId);
    
    /**
     * 标记通知为已读
     */
    void markAsRead(String notificationId, String userId);
    
    /**
     * 标记全部为已读
     */
    void markAllAsRead(String userId);
    
    /**
     * 删除通知
     */
    void deleteNotification(String notificationId, String userId);
}
