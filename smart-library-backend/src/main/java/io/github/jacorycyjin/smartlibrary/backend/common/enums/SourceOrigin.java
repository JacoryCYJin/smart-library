package io.github.jacorycyjin.smartlibrary.backend.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 资源来源枚举
 * 
 * @author Jacory
 * @date 2025/02/27
 */
@AllArgsConstructor
@Getter
public enum SourceOrigin {

    DOUBAN(1, "豆瓣读书"),
    ZLIBRARY(2, "Z-Library"),
    MANUAL(99, "手动录入");

    private final Integer code;
    private final String description;

    /**
     * 根据 code 获取枚举
     * 
     * @param code 来源代码
     * @return 对应的枚举值，未找到返回 null
     */
    public static SourceOrigin fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (SourceOrigin origin : values()) {
            if (origin.code.equals(code)) {
                return origin;
            }
        }
        return null;
    }

    /**
     * 判断是否为豆瓣来源
     */
    public boolean isDouban() {
        return this == DOUBAN;
    }

    /**
     * 判断是否为 Z-Library 来源
     */
    public boolean isZLibrary() {
        return this == ZLIBRARY;
    }

    /**
     * 判断是否为手动录入
     */
    public boolean isManual() {
        return this == MANUAL;
    }
}
