package io.github.jacorycyjin.smartlibrary.backend.common.util;

import io.github.jacorycyjin.smartlibrary.backend.common.enums.LinkType;
import io.github.jacorycyjin.smartlibrary.backend.entity.ResourceLink;
import io.github.jacorycyjin.smartlibrary.backend.vo.ResourceLinkVO;
import io.github.jacorycyjin.smartlibrary.backend.vo.ResourceLinksGroupVO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 资源链接分组工具类
 * 将链接列表按照 linkType（书籍页、下载页、解读页）进行分组
 *
 * @author jcy
 * @date 2026/03/06
 */
public class ResourceLinkGroupUtil {

    /**
     * 将链接列表按 linkType 分组
     *
     * @param links 链接实体列表
     * @return 分组后的链接 VO
     */
    public static ResourceLinksGroupVO groupLinks(List<ResourceLink> links) {
        if (links == null || links.isEmpty()) {
            return ResourceLinksGroupVO.builder()
                    .infoLinks(new ArrayList<>())
                    .downloadLinks(new ArrayList<>())
                    .reviewLinks(new ArrayList<>())
                    .otherLinks(new ArrayList<>())
                    .build();
        }

        // 转换为 VO 并按 sortOrder 排序
        List<ResourceLinkVO> linkVOs = links.stream()
                .filter(link -> link.getDeleted() == 0 && link.getStatus() == 1)
                .map(ResourceLinkVO::fromEntity)
                .sorted((a, b) -> {
                    // 先按 sortOrder 排序，再按创建时间排序
                    int sortCompare = Integer.compare(
                            a.getSortOrder() != null ? a.getSortOrder() : 0,
                            b.getSortOrder() != null ? b.getSortOrder() : 0
                    );
                    if (sortCompare != 0) {
                        return sortCompare;
                    }
                    return b.getCtime().compareTo(a.getCtime());
                })
                .collect(Collectors.toList());

        // 按 linkType 分组
        List<ResourceLinkVO> infoLinks = new ArrayList<>();
        List<ResourceLinkVO> downloadLinks = new ArrayList<>();
        List<ResourceLinkVO> reviewLinks = new ArrayList<>();
        List<ResourceLinkVO> otherLinks = new ArrayList<>();

        for (ResourceLinkVO linkVO : linkVOs) {
            LinkType linkType = LinkType.fromCode(linkVO.getLinkType());
            
            if (linkType == null) {
                otherLinks.add(linkVO);
                continue;
            }

            switch (linkType) {
                case INFO_PAGE:
                    infoLinks.add(linkVO);
                    break;
                case DOWNLOAD_PAGE:
                    downloadLinks.add(linkVO);
                    break;
                case REVIEW_PAGE:
                    reviewLinks.add(linkVO);
                    break;
                default:
                    otherLinks.add(linkVO);
                    break;
            }
        }

        return ResourceLinksGroupVO.builder()
                .infoLinks(infoLinks)
                .downloadLinks(downloadLinks)
                .reviewLinks(reviewLinks)
                .otherLinks(otherLinks)
                .build();
    }
}
