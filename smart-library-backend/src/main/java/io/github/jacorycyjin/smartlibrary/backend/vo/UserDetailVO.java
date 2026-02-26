package io.github.jacorycyjin.smartlibrary.backend.vo;

import io.github.jacorycyjin.smartlibrary.backend.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 用户详细信息 VO（用于个人中心和后台管理）
 * 继承自 UserPublicVO，包含隐私字段
 * 
 * @author Jacory
 * @date 2025/12/30
 */
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class UserDetailVO extends UserPublicVO {

    /**
     * 用户手机号
     */
    private String phone;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户状态：1=正常，0=禁用
     */
    private Integer status;

    /**
     * 用户角色
     */
    private Integer role;

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

    /**
     * 从 DTO 转换为 VO
     */
    public static UserDetailVO fromDTO(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        return UserDetailVO.builder()
                // 继承自 UserPublicVO 的字段
                .userId(dto.getUserId())
                .username(dto.getUsername())
                .avatarUrl(dto.getAvatarUrl())
                .bio(dto.getBio())
                .ctime(dto.getCtime())
                // UserDetailVO 特有的字段
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .status(dto.getStatus())
                .role(dto.getRole())
                .mtime(dto.getMtime())
                .deleted(dto.getDeleted())
                .token(dto.getToken())
                .build();
    }
}
