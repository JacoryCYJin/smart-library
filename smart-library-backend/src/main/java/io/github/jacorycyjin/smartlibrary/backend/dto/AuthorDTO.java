package io.github.jacorycyjin.smartlibrary.backend.dto;

import io.github.jacorycyjin.smartlibrary.backend.entity.Author;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 作者信息DTO
 * 
 * @author Jacory
 * @date 2025/12/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthorDTO {

    /**
     * 作者业务ID
     */
    private String authorId;

    /**
     * 作者姓名
     */
    private String name;

    /**
     * 原名
     */
    private String originalName;

    /**
     * 国籍
     */
    private String country;

    /**
     * 头像URL
     */
    private String photoUrl;

    /**
     * 生平简介
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime ctime;

    /**
     * 更新时间
     */
    private LocalDateTime mtime;

    /**
     * 排序权重（1为一作，2为二作...）
     */
    private Integer sort;

    /**
     * 从实体类转换为 DTO
     */
    public static AuthorDTO fromEntity(Author author) {
        if (author == null) {
            return null;
        }
        return AuthorDTO.builder()
                .authorId(author.getAuthorId())
                .name(author.getName())
                .originalName(author.getOriginalName())
                .country(author.getCountry())
                .photoUrl(author.getPhotoUrl())
                .description(author.getDescription())
                .ctime(author.getCtime())
                .mtime(author.getMtime())
                .build();
    }

    /**
     * 从 Map 转换为 DTO（用于 MyBatis 查询结果）
     */
    public static AuthorDTO fromMap(Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        return AuthorDTO.builder()
                .authorId((String) map.get("authorId"))
                .name((String) map.get("name"))
                .originalName((String) map.get("originalName"))
                .country((String) map.get("country"))
                .photoUrl((String) map.get("photoUrl"))
                .description((String) map.get("description"))
                .sort((Integer) map.get("sort"))
                .build();
    }
}
