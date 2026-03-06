package io.github.jacorycyjin.smartlibrary.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 资源链接分组 VO
 * 按照 linkType（书籍页、下载页、解读页）组织链接列表
 *
 * @author jcy
 * @date 2026/03/06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceLinksGroupVO {
    
    /**
     * I. 书籍页链接（linkType=1）
     * 包含：豆瓣主页、ZLibrary书籍页等
     */
    private List<ResourceLinkVO> infoLinks;
    
    /**
     * II. 下载页链接（linkType=2）
     * 包含：ZLibrary下载链接等
     */
    private List<ResourceLinkVO> downloadLinks;
    
    /**
     * III. 解读页链接（linkType=3）
     * 包含：B站解读、YouTube解读等
     */
    private List<ResourceLinkVO> reviewLinks;
    
    /**
     * 其他链接
     */
    private List<ResourceLinkVO> otherLinks;
}
