package io.github.jacorycyjin.smartlibrary.backend.common.enums;

/**
 * 资源链接平台枚举
 * 用于标识链接来源平台
 *
 * @author jcy
 * @date 2026/03/06
 */
public enum LinkPlatform {
    DOUBAN(1, "豆瓣", "https://book.douban.com"),
    ZLIBRARY(2, "ZLibrary", "https://zh.zlibrary.org"),
    BILIBILI(3, "哔哩哔哩", "https://www.bilibili.com"),
    YOUTUBE(4, "YouTube", "https://www.youtube.com"),
    OTHER(99, "其他", "");

    private final Integer code;
    private final String name;
    private final String domain;

    LinkPlatform(Integer code, String name, String domain) {
        this.code = code;
        this.name = name;
        this.domain = domain;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDomain() {
        return domain;
    }

    /**
     * 根据 code 获取枚举
     */
    public static LinkPlatform fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (LinkPlatform platform : values()) {
            if (platform.code.equals(code)) {
                return platform;
            }
        }
        return null;
    }

    /**
     * 根据 URL 自动识别平台
     */
    public static LinkPlatform detectFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return OTHER;
        }
        
        String lowerUrl = url.toLowerCase();
        if (lowerUrl.contains("douban.com")) {
            return DOUBAN;
        } else if (lowerUrl.contains("zlibrary") || lowerUrl.contains("z-lib")) {
            return ZLIBRARY;
        } else if (lowerUrl.contains("bilibili.com") || lowerUrl.contains("b23.tv")) {
            return BILIBILI;
        } else if (lowerUrl.contains("youtube.com") || lowerUrl.contains("youtu.be")) {
            return YOUTUBE;
        }
        
        return OTHER;
    }
}
