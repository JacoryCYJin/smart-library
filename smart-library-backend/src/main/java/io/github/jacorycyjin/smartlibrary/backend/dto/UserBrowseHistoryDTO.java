package io.github.jacorycyjin.smartlibrary.backend.dto;

import io.github.jacorycyjin.smartlibrary.backend.entity.UserBrowseHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户浏览历史 DTO
 * 
 * @author Jacory
 * @date 2025/12/28
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class UserBrowseHistoryDTO {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 资源ID
     */
    private String resourceId;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 创建时间
     */
    private LocalDateTime ctime;

    /**
     * 最后浏览时间
     */
    private LocalDateTime mtime;

    /**
     * 从实体类转换为 DTO
     */
    public static UserBrowseHistoryDTO fromEntity(UserBrowseHistory history) {
        if (history == null) {
            return null;
        }
        return UserBrowseHistoryDTO.builder()
                .userId(history.getUserId())
                .resourceId(history.getResourceId())
                .viewCount(history.getViewCount())
                .ctime(history.getCtime())
                .mtime(history.getMtime())
                .build();
    }
}
