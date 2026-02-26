package io.github.jacorycyjin.smartlibrary.backend.dto;

import io.github.jacorycyjin.smartlibrary.backend.entity.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 图书标签 DTO
 * 
 * @author Jacory
 * @date 2025/12/28
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class TagDTO {

    /**
     * 标签业务ID
     */
    private String tagId;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 标签类型：0=NLP，1=人工，2=爬虫
     */
    private Integer type;

    /**
     * 创建时间
     */
    private LocalDateTime ctime;

    /**
     * 更新时间
     */
    private LocalDateTime mtime;

    /**
     * 从实体类转换为 DTO
     */
    public static TagDTO fromEntity(Tag tag) {
        if (tag == null) {
            return null;
        }
        return TagDTO.builder()
                .tagId(tag.getTagId())
                .name(tag.getName())
                .type(tag.getType())
                .ctime(tag.getCtime())
                .mtime(tag.getMtime())
                .build();
    }

    /**
     * 从 Map 转换为 DTO（用于 MyBatis 查询结果）
     * 
     * @param map MyBatis 查询结果
     * @return TagDTO
     */
    public static TagDTO fromMap(java.util.Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        
        TagDTO.TagDTOBuilder builder = TagDTO.builder()
                .tagId((String) map.get("tagId"))
                .type((Integer) map.get("tagType"));
        
        // 处理 name 字段（可能是 name 或 tagName）
        if (map.containsKey("tagName")) {
            builder.name((String) map.get("tagName"));
        } else if (map.containsKey("name")) {
            builder.name((String) map.get("name"));
        }
        
        // 处理时间字段（如果存在）
        if (map.containsKey("ctime") && map.get("ctime") != null) {
            Object ctime = map.get("ctime");
            if (ctime instanceof java.sql.Timestamp) {
                builder.ctime(((java.sql.Timestamp) ctime).toLocalDateTime());
            } else if (ctime instanceof LocalDateTime) {
                builder.ctime((LocalDateTime) ctime);
            }
        }
        
        if (map.containsKey("mtime") && map.get("mtime") != null) {
            Object mtime = map.get("mtime");
            if (mtime instanceof java.sql.Timestamp) {
                builder.mtime(((java.sql.Timestamp) mtime).toLocalDateTime());
            } else if (mtime instanceof LocalDateTime) {
                builder.mtime((LocalDateTime) mtime);
            }
        }
        
        return builder.build();
    }
}
