package io.github.jacorycyjin.smartlibrary.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 推荐结果缓存实体类
 * 
 * @author Jacory
 * @date 2025/01/19
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class RecommendResult {

    /**
     * 主键ID，自增
     */
    private Long id;

    /**
     * 目标用户
     */
    private String userId;

    /**
     * 推荐资源
     */
    private String resourceId;

    /**
     * 推荐匹配度
     */
    private BigDecimal score;

    /**
     * 推荐理由（如：看了《三体》/ 内容相似）
     */
    private String reason;

    /**
     * 创建时间
     */
    private LocalDateTime ctime;
}
