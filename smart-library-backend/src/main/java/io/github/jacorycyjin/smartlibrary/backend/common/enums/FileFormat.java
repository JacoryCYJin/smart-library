package io.github.jacorycyjin.smartlibrary.backend.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 资源文件格式枚举
 * 
 * @author Jacory
 * @date 2026/02/27
 */
@AllArgsConstructor
@Getter
public enum FileFormat {

    PDF(1, "PDF"),
    EPUB(2, "EPUB"),
    MOBI(3, "MOBI");

    private final Integer code;
    private final String description;

    /**
     * 根据 code 获取枚举
     */
    public static FileFormat fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (FileFormat format : values()) {
            if (format.code.equals(code)) {
                return format;
            }
        }
        return null;
    }
}
