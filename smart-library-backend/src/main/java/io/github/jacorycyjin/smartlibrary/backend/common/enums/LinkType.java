package io.github.jacorycyjin.smartlibrary.backend.common.enums;

/**
 * 资源链接类型枚举（页面类型）
 * 
 * @author jcy
 * @date 2026/03/06
 */
public enum LinkType {
    INFO_PAGE(1, "书籍页"),
    DOWNLOAD_PAGE(2, "下载页"),
    REVIEW_PAGE(3, "解读页");

    private final Integer code;
    private final String description;

    LinkType(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据 code 获取枚举
     */
    public static LinkType fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (LinkType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
