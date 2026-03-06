package io.github.jacorycyjin.smartlibrary.backend.common.enums;

/**
 * AI图谱生成状态枚举
 *
 * @author jcy
 * @date 2026/03/06
 */
public enum GraphGenerateStatus {
    NOT_GENERATED(0, "未生成"),
    GENERATING(1, "生成中"),
    SUCCESS(2, "生成成功"),
    FAILED(-1, "生成失败");

    private final Integer code;
    private final String description;

    GraphGenerateStatus(Integer code, String description) {
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
    public static GraphGenerateStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (GraphGenerateStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
