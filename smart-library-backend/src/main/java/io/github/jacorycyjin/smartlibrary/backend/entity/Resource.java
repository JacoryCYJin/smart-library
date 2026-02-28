package io.github.jacorycyjin.smartlibrary.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 资源实体类（合并图书和文献）
 * 
 * @author Jacory
 * @date 2025/01/19
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class Resource {

    /**
     * 主键ID，自增
     */
    private Long id;

    /**
     * 资源业务ID
     */
    private String resourceId;

    /**
     * 资源类型：1=图书，2=文献期刊
     */
    private Integer type;

    // ========== 通用基础信息 ==========

    /**
     * 标题/书名
     */
    private String title;

    /**
     * 作者姓名快照（用于列表展示）
     */
    private String authorName;

    /**
     * 译者姓名快照（用于列表展示）
     */
    private String translatorName;

    /**
     * 封面图片URL
     */
    private String coverUrl;

    /**
     * 简介/摘要（NLP分析文本源）
     */
    private String summary;

    /**
     * 出版/发表日期
     */
    private LocalDate pubDate;

    // ========== 图书特有字段 (Type=1) ==========

    /**
     * ISBN号
     */
    private String isbn;

    /**
     * 出版社
     */
    private String publisher;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 页数
     */
    private Integer pageCount;

    // ========== 文献特有字段 (Type=2) ==========

    /**
     * DOI唯一标识
     */
    private String doi;

    /**
     * 期刊/会议名称
     */
    private String journal;

    // ========== 数据来源与算法支持 ==========

    /**
     * 数据来源：1=豆瓣读书，2=Z-Library，99=手动录入
     */
    private Integer sourceOrigin;

    /**
     * 原站链接
     */
    private String sourceUrl;

    /**
     * 原站评分（如豆瓣评分）
     */
    private BigDecimal sourceScore;

    /**
     * NLP情感分析得分（0-1）
     */
    private BigDecimal sentimentScore;

    // ========== 统计字段（冗余优化） ==========

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

    // ========== 系统字段 ==========

    /**
     * 创建时间
     */
    private LocalDateTime ctime;

    /**
     * 更新时间
     */
    private LocalDateTime mtime;

    /**
     * 是否删除：0=未删除，1=已删除
     */
    private Integer deleted;
}
