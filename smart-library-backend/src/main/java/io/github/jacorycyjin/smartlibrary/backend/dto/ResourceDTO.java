package io.github.jacorycyjin.smartlibrary.backend.dto;

import io.github.jacorycyjin.smartlibrary.backend.entity.Resource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 资源 DTO
 * 
 * @author Jacory
 * @date 2025/01/19
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class ResourceDTO {

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
     * 简介/摘要
     */
    private String summary;

    /**
     * 出版/发表日期
     */
    private LocalDate pubDate;

    // ========== 图书特有字段 ==========
    
    /**
     * ISBN（仅图书）
     */
    private String isbn;

    /**
     * 出版社（仅图书）
     */
    private String publisher;

    /**
     * 价格（仅图书）
     */
    private BigDecimal price;

    /**
     * 页数（仅图书）
     */
    private Integer pageCount;

    // ========== 文献特有字段 ==========
    
    /**
     * DOI标识（仅文献）
     */
    private String doi;

    /**
     * 期刊名称（仅文献）
     */
    private String journal;

    /**
     * 资源文件列表（支持多种格式）
     */
    private List<ResourceFileDTO> files;

    // ========== 通用详情字段 ==========
    
    /**
     * 数据来源
     */
    private String sourceOrigin;

    /**
     * 原站链接
     */
    private String sourceUrl;

    /**
     * 原站评分
     */
    private BigDecimal sourceScore;

    /**
     * NLP情感分析评分
     */
    private BigDecimal sentimentScore;

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
     * 分类列表
     */
    private List<CategoryDTO> categories;

    /**
     * 标签列表
     */
    private List<TagDTO> tags;

    /**
     * 创建时间
     */
    private LocalDateTime ctime;

    /**
     * 更新时间
     */
    private LocalDateTime mtime;

    /**
     * 从实体类转换为 DTO（基础字段）
     */
    public static ResourceDTO fromEntity(Resource entity) {
        if (entity == null) {
            return null;
        }
        return ResourceDTO.builder()
                .resourceId(entity.getResourceId())
                .type(entity.getType())
                .title(entity.getTitle())
                .subTitle(entity.getSubTitle())
                .authorName(entity.getAuthorName())
                .coverUrl(entity.getCoverUrl())
                .summary(entity.getSummary())
                .pubDate(entity.getPubDate())
                .isbn(entity.getIsbn())
                .publisher(entity.getPublisher())
                .price(entity.getPrice())
                .pageCount(entity.getPageCount())
                .doi(entity.getDoi())
                .journal(entity.getJournal())
                .sourceOrigin(entity.getSourceOrigin())
                .sourceUrl(entity.getSourceUrl())
                .sourceScore(entity.getSourceScore())
                .sentimentScore(entity.getSentimentScore())
                .viewCount(entity.getViewCount())
                .commentCount(entity.getCommentCount())
                .starCount(entity.getStarCount())
                .ctime(entity.getCtime())
                .mtime(entity.getMtime())
                .build();
    }
}
