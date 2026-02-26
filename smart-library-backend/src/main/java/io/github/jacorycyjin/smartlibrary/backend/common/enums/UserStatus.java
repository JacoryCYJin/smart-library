package io.github.jacorycyjin.smartlibrary.backend.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Jacory
 * @date 2025/12/13
 */
@AllArgsConstructor
@Getter
public enum UserStatus {
    ACTIVE(0, "正常"),
    INACTIVE(1, "禁用");

    private final Integer code;
    private final String status;
}
