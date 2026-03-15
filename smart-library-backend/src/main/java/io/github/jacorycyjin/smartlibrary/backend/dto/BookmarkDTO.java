package io.github.jacorycyjin.smartlibrary.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 书签数据传输对象
 * 
 * @author Jacory
 * @date 2025/03/14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkDTO {

    /**
     * 书签业务ID
     */
    private String bookmarkId;

    /**
     * 关联资源ID
     */
    private String resourceId;

    /**
     * 金句内容
     */
    private String content;

    /**
     * 作者姓名
     */
    private String authorName;

    /**
     * 书籍标题
     */
    private String bookTitle;

    /**
     * 引流次数
     */
    private Integer clickCount;

    /**
     * 状态：1=上架，0=下架
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime ctime;
}
