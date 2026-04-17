package io.github.jacorycyjin.smartlibrary.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 全局搜索结果 VO
 * 
 * @author Jacory
 * @date 2025/01/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalSearchResultVO {

    /**
     * 图书结果列表
     */
    private List<ResourcePublicVO> books;

    /**
     * 作者结果列表
     */
    private List<AuthorPublicVO> authors;

    /**
     * 图书总数
     */
    private Long bookTotal;

    /**
     * 作者总数
     */
    private Long authorTotal;
}
