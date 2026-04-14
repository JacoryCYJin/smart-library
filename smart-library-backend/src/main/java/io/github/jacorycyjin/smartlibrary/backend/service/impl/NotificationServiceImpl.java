package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import io.github.jacorycyjin.smartlibrary.backend.entity.UserNotification;
import io.github.jacorycyjin.smartlibrary.backend.mapper.UserNotificationMapper;
import io.github.jacorycyjin.smartlibrary.backend.service.NotificationService;
import io.github.jacorycyjin.smartlibrary.backend.vo.NotificationVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 通知服务实现
 * 
 * @author jacorycyjin
 * @date 2025/01/15
 */
@Service
public class NotificationServiceImpl implements NotificationService {
    
    @Resource
    private UserNotificationMapper notificationMapper;
    
    @Override
    public List<NotificationVO> getUserNotifications(String userId, Integer limit) {
        List<UserNotification> notifications = notificationMapper.selectByUserId(userId, limit);
        return notifications.stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public int getUnreadCount(String userId) {
        return notificationMapper.countUnread(userId);
    }
    
    @Override
    public void markAsRead(String notificationId, String userId) {
        notificationMapper.markAsRead(notificationId, userId);
    }
    
    @Override
    public void markAllAsRead(String userId) {
        notificationMapper.markAllAsRead(userId);
    }
    
    @Override
    public void deleteNotification(String notificationId, String userId) {
        notificationMapper.deleteById(notificationId, userId);
    }
    
    /**
     * Entity转VO
     */
    private NotificationVO toVO(UserNotification entity) {
        return NotificationVO.builder()
                .notificationId(entity.getNotificationId())
                .type(entity.getType())
                .title(entity.getTitle())
                .content(entity.getContent())
                .linkUrl(entity.getLinkUrl())
                .isRead(entity.getIsRead())
                .ctime(entity.getCtime())
                .build();
    }
}
