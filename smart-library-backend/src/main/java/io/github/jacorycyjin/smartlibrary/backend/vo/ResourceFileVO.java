package io.github.jacorycyjin.smartlibrary.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 资源文件 VO
 * 
 * @author Jacory
 * @date 2026/02/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceFileVO {

    /**
     * 关联核心资源标识
     */
    private String resourceId;

    /**
     * 文件格式: 1-PDF / 2-EPUB / 3-MOBI
     */
    private Integer fileType;

    /**
     * 文件格式描述（如：PDF）
     */
    private String fileTypeDesc;

    /**
     * 对象存储绝对路径
     */
    private String fileUrl;

    /**
     * 文件体积字节数
     */
    private Long fileSize;

    /**
     * 上传入库时间
     */
    private LocalDateTime ctime;
}
