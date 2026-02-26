package io.github.jacorycyjin.smartlibrary.backend.dto;

import io.github.jacorycyjin.smartlibrary.backend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Jacory
 * @date 2025/12/11
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class UserDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 用户UUID，全球唯一标识
     */
    private String userId;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 用户手机号
     */
    private String phone;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 用户角色
     */
    private Integer role;

    /**
     * 用户简介
     */
    private String bio;

    /**
     * 用户状态：1=正常，0=禁用
     */
    private Integer status;

    /**
     * 注册时间
     */
    private LocalDateTime ctime;

    /**
     * 更新时间
     */
    private LocalDateTime mtime;

    /**
     * 是否删除：0=未删除，1=已删除
     */
    private Integer deleted;

    /**
     * JWT token（仅登录时返回）
     */
    private String token;

    public static UserDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }
        return UserDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .password(user.getPassword())
                .phone(user.getPhone())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .bio(user.getBio())
                .status(user.getStatus())
                .ctime(user.getCtime())
                .mtime(user.getMtime())
                .deleted(user.getDeleted())
                .build();
    }
}
