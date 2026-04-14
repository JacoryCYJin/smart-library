package io.github.jacorycyjin.smartlibrary.backend.mapper;

import io.github.jacorycyjin.smartlibrary.backend.entity.UserNotification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户通知Mapper
 * 
 * @author jacorycyjin
 * @date 2025/01/15
 */
@Mapper
public interface UserNotificationMapper {
    
    /**
     * 批量插入通知
     */
    int batchInsert(@Param("list") List<UserNotification> notifications);
    
    /**
     * 查询用户通知列表
     */
    List<UserNotification> selectByUserId(@Param("userId") String userId, 
                                          @Param("limit") Integer limit);
    
    /**
     * 查询未读通知数量
     */
    int countUnread(@Param("userId") String userId);
    
    /**
     * 标记为已读
     */
    int markAsRead(@Param("notificationId") String notificationId, 
                   @Param("userId") String userId);
    
    /**
     * 标记全部为已读
     */
    int markAllAsRead(@Param("userId") String userId);
    
    /**
     * 删除通知
     */
    int deleteById(@Param("notificationId") String notificationId, 
                   @Param("userId") String userId);
}
