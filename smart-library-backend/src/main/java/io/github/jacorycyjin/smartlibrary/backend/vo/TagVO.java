package io.github.jacorycyjin.smartlibrary.backend.vo;

import io.github.jacorycyjin.smartlibrary.backend.dto.TagDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 图书标签 VO
 * 
 * @author Jacory
 * @date 2025/12/28
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class TagVO {

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
     * 从 DTO 转换为 VO
     */
    public static TagVO fromDTO(TagDTO dto) {
        if (dto == null) {
            return null;
        }
        return TagVO.builder()
                .tagId(dto.getTagId())
                .name(dto.getName())
                .type(dto.getType())
                .ctime(dto.getCtime())
                .mtime(dto.getMtime())
                .build();
    }
}
