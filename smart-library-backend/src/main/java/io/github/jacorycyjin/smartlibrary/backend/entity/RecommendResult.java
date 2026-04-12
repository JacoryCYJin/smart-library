package io.github.jacorycyjin.smartlibrary.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 推荐结果实体类
 * 
 * @author Jacory
 * @date 2025/04/11
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
     * 用户ID
     */
    private String userId;

    /**
     * 推荐资源ID
     */
    private String resourceId;

    /**
     * 推荐匹配度评分
     */
    private Double score;

    /**
     * 推荐理由
     */
    private String reason;

    /**
     * 创建时间
     */
    private LocalDateTime ctime;
}
