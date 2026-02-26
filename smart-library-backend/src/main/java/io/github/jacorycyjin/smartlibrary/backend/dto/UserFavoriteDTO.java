package io.github.jacorycyjin.smartlibrary.backend.dto;

import io.github.jacorycyjin.smartlibrary.backend.entity.UserFavorite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户收藏 DTO
 * 
 * @author Jacory
 * @date 2025/01/19
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class UserFavoriteDTO {

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
     * 从实体类转换为 DTO
     */
    public static UserFavoriteDTO fromEntity(UserFavorite favorite) {
        if (favorite == null) {
            return null;
        }
        return UserFavoriteDTO.builder()
                .userId(favorite.getUserId())
                .resourceId(favorite.getResourceId())
                .ctime(favorite.getCtime())
                .build();
    }
}
