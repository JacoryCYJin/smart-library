package io.github.jacorycyjin.smartlibrary.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 资源详情 VO（详情页用）
 * 
 * @author Jacory
 * @date 2025/01/19
 */
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class ResourceDetailVO extends ResourcePublicVO {

    // ========== 图书特有字段 ==========
    
    /**
     * ISBN（仅图书）
     */
    private String isbn;

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
     * 资源文件列表（支持多种格式）
     */
    private List<ResourceFileVO> files;

    // ========== 通用详情字段 ==========
    
    /**
     * 简介/摘要
     */
    private String summary;

    /**
     * 数据来源：1=豆瓣读书，2=Z-Library，99=手动录入
     */
    private Integer sourceOrigin;

    /**
     * 原站链接
     */
    private String sourceUrl;

    /**
     * NLP情感分析评分
     */
    private BigDecimal sentimentScore;

    /**
     * 完整分类层级路径
     * 例如: [["计算机", "编程语言", "Java"], ["技术", "后端开发"]]
     */
    private List<List<CategoryVO>> categoryPaths;

    /**
     * 创建时间
     */
    private LocalDateTime ctime;

    /**
     * 更新时间
     */
    private LocalDateTime mtime;
}
