package io.github.jacorycyjin.smartlibrary.backend.vo;

import io.github.jacorycyjin.smartlibrary.backend.dto.UserFavoriteDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户收藏 VO
 * 
 * @author Jacory
 * @date 2025/01/19
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class UserFavoriteVO {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 资源ID
     */
    private String resourceId;

    /**
     * 创建时间
     */
    private LocalDateTime ctime;

    /**
     * 从 DTO 转换为 VO
     */
    public static UserFavoriteVO fromDTO(UserFavoriteDTO dto) {
        if (dto == null) {
            return null;
        }
        return UserFavoriteVO.builder()
                .userId(dto.getUserId())
                .resourceId(dto.getResourceId())
                .ctime(dto.getCtime())
                .build();
    }
}
