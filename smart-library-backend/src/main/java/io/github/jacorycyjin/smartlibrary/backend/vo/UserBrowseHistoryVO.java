package io.github.jacorycyjin.smartlibrary.backend.vo;

import io.github.jacorycyjin.smartlibrary.backend.dto.UserBrowseHistoryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户浏览历史 VO
 * 
 * @author Jacory
 * @date 2025/12/28
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class UserBrowseHistoryVO {

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
     * 从 DTO 转换为 VO
     */
    public static UserBrowseHistoryVO fromDTO(UserBrowseHistoryDTO dto) {
        if (dto == null) {
            return null;
        }
        return UserBrowseHistoryVO.builder()
                .userId(dto.getUserId())
                .resourceId(dto.getResourceId())
                .viewCount(dto.getViewCount())
                .ctime(dto.getCtime())
                .mtime(dto.getMtime())
                .build();
    }
}
