package io.github.jacorycyjin.smartlibrary.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 每日流量统计实体类
 * 
 * @author Jacory
 * @date 2025/01/19
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class DailyStat {

    /**
     * 主键ID，自增
     */
    private Long id;

    /**
     * 资源ID
     */
    private String resourceId;

    /**
     * 统计日期
     */
    private LocalDate date;

    /**
     * 当日增量
     */
    private Integer viewCount;

    /**
     * 创建时间
     */
    private LocalDateTime ctime;
}
