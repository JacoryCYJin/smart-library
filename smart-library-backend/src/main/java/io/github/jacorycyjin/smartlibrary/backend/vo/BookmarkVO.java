package io.github.jacorycyjin.smartlibrary.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 书签视图对象（前端展示）
 * 
 * @author Jacory
 * @date 2025/03/14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkVO {

    /**
     * 书签业务ID
     */
    private String bookmarkId;

    /**
     * 关联资源ID（用于跳转）
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
     * 资源封面URL（用于书签背景）
     */
    private String resourceCoverUrl;
}
