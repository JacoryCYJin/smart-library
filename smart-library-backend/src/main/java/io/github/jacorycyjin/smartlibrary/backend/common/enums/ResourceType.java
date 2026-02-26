package io.github.jacorycyjin.smartlibrary.backend.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 资源类型枚举
 * 
 * @author Jacory
 * @date 2025/01/19
 */
@AllArgsConstructor
@Getter
public enum ResourceType {

    BOOK(1, "图书"),
    LITERATURE(2, "文献期刊");

    private final Integer code;
    private final String description;

    /**
     * 根据 code 获取枚举
     */
    public static ResourceType fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ResourceType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 判断是否为图书
     */
    public boolean isBook() {
        return this == BOOK;
    }

    /**
     * 判断是否为文献
     */
    public boolean isLiterature() {
        return this == LITERATURE;
    }
}
