package io.github.jacorycyjin.smartlibrary.backend.vo;

import io.github.jacorycyjin.smartlibrary.backend.dto.AuthorDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 作者详情 VO
 * 
 * @author Jacory
 * @date 2025/12/28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class AuthorDetailVO extends AuthorPublicVO {

    /**
     * 作品列表
     */
    private List<ResourcePublicVO> works;

    /**
     * 作品数量
     */
    private Integer worksCount;

    /**
     * 从 DTO 转换为 VO
     */
    public static AuthorDetailVO fromDTO(AuthorDTO dto) {
        if (dto == null) {
            return null;
        }
        return AuthorDetailVO.builder()
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
