package io.github.jacorycyjin.smartlibrary.backend.vo;

import io.github.jacorycyjin.smartlibrary.backend.dto.AuthorDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


/**
 * 作者信息 VO
 * 
 * @author Jacory
 * @date 2025/12/28
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@SuperBuilder
public class AuthorPublicVO {

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
     * 排序权重（1为一作，2为二作...）
     */
    private Integer sort;

    /**
     * 从 DTO 转换为 VO
     */
    public static AuthorPublicVO fromDTO(AuthorDTO dto) {
        if (dto == null) {
            return null;
        }
        return AuthorPublicVO.builder()
                .authorId(dto.getAuthorId())
                .name(dto.getName())
                .originalName(dto.getOriginalName())
                .country(dto.getCountry())
                .photoUrl(dto.getPhotoUrl())
                .description(dto.getDescription())
                .sort(dto.getSort())
                .build();
    }
}
