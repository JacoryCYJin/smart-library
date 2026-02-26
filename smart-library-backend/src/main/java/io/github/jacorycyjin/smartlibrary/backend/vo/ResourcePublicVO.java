package io.github.jacorycyjin.smartlibrary.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 资源公开信息 VO（列表页用）
 * 
 * @author Jacory
 * @date 2025/01/19
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@SuperBuilder
public class ResourcePublicVO {

    /**
     * 资源业务ID
     */
    private String resourceId;

    /**
     * 资源类型（1=图书，2=文献）
     */
    private Integer type;

    /**
     * 标题/书名
     */
    private String title;

    /**
     * 副标题
     */
    private String subTitle;

    /**
     * 作者名称快照（冗余字段，用于快速展示，多个作者用逗号分隔，已按 sort 排序）
     */
    private String authorName;

    /**
     * 封面URL
     */
    private String coverUrl;

    /**
     * 出版/发表日期
     */
    private LocalDate pubDate;

    /**
     * 出版社（仅图书）
     */
    private String publisher;

    /**
     * 期刊名称（仅文献）
     */
    private String journal;

    /**
     * 原站评分
     */
    private BigDecimal sourceScore;

    /**
     * 总浏览量
     */
    private Integer viewCount;

    /**
     * 总评论数
     */
    private Integer commentCount;

    /**
     * 总收藏数
     */
    private Integer starCount;

    /**
     * 极简分类列表（仅一级分类名称）
     */
    private List<String> categoryNames;

    /**
     * 标签列表
     */
    private List<TagVO> tags;

    /**
     * 从 DTO 转换为 VO
     */
    public static ResourcePublicVO fromDTO(io.github.jacorycyjin.smartlibrary.backend.dto.ResourceDTO dto) {
        if (dto == null) {
            return null;
        }
        
        // 提取分类名称
        List<String> categoryNames = dto.getCategories() != null 
            ? dto.getCategories().stream()
                .map(c -> c.getName())
                .toList()
            : null;
        
        // 转换标签
        List<TagVO> tags = dto.getTags() != null
            ? dto.getTags().stream()
                .map(TagVO::fromDTO)
                .toList()
            : null;
        
        return ResourcePublicVO.builder()
                .resourceId(dto.getResourceId())
                .type(dto.getType())
                .title(dto.getTitle())
                .subTitle(dto.getSubTitle())
                .authorName(dto.getAuthorName())
                .coverUrl(dto.getCoverUrl())
                .pubDate(dto.getPubDate())
                .publisher(dto.getPublisher())
                .journal(dto.getJournal())
                .sourceScore(dto.getSourceScore())
                .viewCount(dto.getViewCount())
                .commentCount(dto.getCommentCount())
                .starCount(dto.getStarCount())
                .categoryNames(categoryNames)
                .tags(tags)
                .build();
    }
}
