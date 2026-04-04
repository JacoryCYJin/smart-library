package io.github.jacorycyjin.smartlibrary.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员统计数据 VO
 * 
 * @author Kiro
 * @date 2026/04/04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsVO {
    
    /**
     * 资源总数
     */
    private Long resourceCount;
    
    /**
     * 用户总数
     */
    private Long userCount;
    
    /**
     * 评论总数
     */
    private Long commentCount;
    
    /**
     * 收藏总数
     */
    private Long favoriteCount;
}
