package io.github.jacorycyjin.smartlibrary.backend.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Jacory
 * @date 2025/12/13
 */
@AllArgsConstructor
@Getter
public enum UserRole {
    USER(0, "用户"),
    ADMIN(1, "管理员");

    private final Integer code;
    private final String role;
}
