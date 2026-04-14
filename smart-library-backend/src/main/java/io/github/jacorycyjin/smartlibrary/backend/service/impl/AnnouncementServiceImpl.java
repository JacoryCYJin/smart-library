package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;
import io.github.jacorycyjin.smartlibrary.backend.common.exception.BusinessException;
import io.github.jacorycyjin.smartlibrary.backend.common.util.UUIDUtil;
import io.github.jacorycyjin.smartlibrary.backend.entity.Announcement;
import io.github.jacorycyjin.smartlibrary.backend.entity.User;
import io.github.jacorycyjin.smartlibrary.backend.entity.UserNotification;
import io.github.jacorycyjin.smartlibrary.backend.form.AnnouncementForm;
import io.github.jacorycyjin.smartlibrary.backend.mapper.AnnouncementMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.UserMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.UserNotificationMapper;
import io.github.jacorycyjin.smartlibrary.backend.service.AnnouncementService;
import io.github.jacorycyjin.smartlibrary.backend.vo.AnnouncementVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 公告服务实现
 * 
 * @author jacorycyjin
 * @date 2025/01/15
 */
@Service
public class AnnouncementServiceImpl implements AnnouncementService {
    
    @Resource
    private AnnouncementMapper announcementMapper;
    
    @Resource
    private UserNotificationMapper notificationMapper;
    
    @Resource
    private UserMapper userMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createAnnouncement(AnnouncementForm form, String publisherId, String publisherName) {
        String announcementId = UUIDUtil.generateUUID();
        
        Announcement announcement = Announcement.builder()
                .announcementId(announcementId)
                .title(form.getTitle())
                .content(form.getContent())
                .type(form.getType())
                .priority(form.getPriority() != null ? form.getPriority() : 0)
                .publisherId(publisherId)
                .publisherName(publisherName)
                .status(form.getStatus() != null ? form.getStatus() : 0)
                .publishTime(form.getStatus() != null && form.getStatus() == 1 ? LocalDateTime.now() : null)
                .viewCount(0)
                .build();
        
        announcementMapper.insert(announcement);
        
        // 如果是已发布状态，给所有用户创建通知
        if (form.getStatus() != null && form.getStatus() == 1) {
            createNotificationsForAllUsers(announcement);
        }
        
        return announcementId;
    }
    
    @Override
    public List<AnnouncementVO> getAnnouncementList(Integer status, Integer type) {
        Map<String, Object> params = new HashMap<>();
        params.put("status", status);
        params.put("type", type);
        
        List<Announcement> announcements = announcementMapper.search(params);
        return announcements.stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public AnnouncementVO getAnnouncementDetail(String announcementId) {
        Announcement announcement = announcementMapper.selectById(announcementId);
        if (announcement == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), ApiCode.RESOURCE_NOT_FOUND.getMessage());
        }
        
        // 增加查看次数
        announcementMapper.incrementViewCount(announcementId);
        
        return toVO(announcement);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAnnouncement(String announcementId, AnnouncementForm form) {
        Announcement announcement = announcementMapper.selectById(announcementId);
        if (announcement == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), ApiCode.RESOURCE_NOT_FOUND.getMessage());
        }
        
        Announcement updateEntity = Announcement.builder()
                .announcementId(announcementId)
                .title(form.getTitle())
                .content(form.getContent())
                .type(form.getType())
                .priority(form.getPriority())
                .status(form.getStatus())
                .build();
        
        // 如果从草稿变为发布，设置发布时间并创建通知
        if (announcement.getStatus() == 0 && form.getStatus() != null && form.getStatus() == 1) {
            updateEntity.setPublishTime(LocalDateTime.now());
            announcementMapper.update(updateEntity);
            createNotificationsForAllUsers(announcementMapper.selectById(announcementId));
        } else {
            announcementMapper.update(updateEntity);
        }
    }
    
    @Override
    public void deleteAnnouncement(String announcementId) {
        announcementMapper.deleteById(announcementId);
    }
    
    /**
     * 为所有用户创建通知
     */
    private void createNotificationsForAllUsers(Announcement announcement) {
        // 查询所有用户
        Map<String, Object> params = new HashMap<>();
        params.put("status", 0); // 只给正常用户发送
        List<User> users = userMapper.searchUsers(params);
        
        if (users.isEmpty()) {
            return;
        }
        
        // 批量创建通知
        List<UserNotification> notifications = users.stream()
                .map(user -> UserNotification.builder()
                        .notificationId(UUIDUtil.generateUUID())
                        .userId(user.getUserId())
                        .type(1) // 1-系统公告
                        .title(announcement.getTitle())
                        .content(announcement.getContent().length() > 100 
                                ? announcement.getContent().substring(0, 100) + "..." 
                                : announcement.getContent())
                        .linkUrl("/announcement/" + announcement.getAnnouncementId())
                        .relatedId(announcement.getAnnouncementId())
                        .isRead(0)
                        .build())
                .collect(Collectors.toList());
        
        notificationMapper.batchInsert(notifications);
    }
    
    /**
     * Entity转VO
     */
    private AnnouncementVO toVO(Announcement entity) {
        return AnnouncementVO.builder()
                .announcementId(entity.getAnnouncementId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .type(entity.getType())
                .priority(entity.getPriority())
                .publisherName(entity.getPublisherName())
                .publishTime(entity.getPublishTime())
                .viewCount(entity.getViewCount())
                .build();
    }
}
