package io.github.jacorycyjin.smartlibrary.backend.vo;

import io.github.jacorycyjin.smartlibrary.backend.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 用户公开信息 VO（用于评论区、用户主页、关注列表等公开场景）
 * 
 * @author Jacory
 * @date 2025/12/30
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@SuperBuilder
public class UserPublicVO {

    /**
     * 用户业务ID
     */
    private String userId;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 用户简介
     */
    private String bio;

    /**
     * 注册时间/加入时间
     */
    private LocalDateTime ctime;

    /**
     * 从 DTO 转换为 VO
     */
    public static UserPublicVO fromDTO(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        return UserPublicVO.builder()
                .userId(dto.getUserId())
                .username(dto.getUsername())
                .avatarUrl(dto.getAvatarUrl())
                .bio(dto.getBio())
                .ctime(dto.getCtime())
                .build();
    }
}
