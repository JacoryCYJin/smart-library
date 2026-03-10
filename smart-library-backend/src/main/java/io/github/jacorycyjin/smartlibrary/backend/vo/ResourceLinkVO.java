package io.github.jacorycyjin.smartlibrary.backend.vo;

import io.github.jacorycyjin.smartlibrary.backend.common.enums.LinkPlatform;
import io.github.jacorycyjin.smartlibrary.backend.common.enums.LinkType;
import io.github.jacorycyjin.smartlibrary.backend.entity.ResourceLink;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 资源链接 VO
 *
 * @author jcy
 * @date 2026/03/06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceLinkVO {
    
    /**
     * 链接ID
     */
    private String linkId;
    
    /**
     * 链接类型代码（1-书籍页 / 2-下载页 / 3-解读页）
     */
    private Integer linkType;
    
    /**
     * 链接类型描述
     */
    private String linkTypeDesc;
    
    /**
     * 平台代码（1-豆瓣 / 2-ZLibrary / 3-B站 / 4-YouTube / 99-其他）
     */
    private Integer platform;
    
    /**
     * 平台名称
     */
    private String platformName;
    
    /**
     * 链接URL
     */
    private String url;
    
    /**
     * 链接标题
     */
    private String title;
    
    /**
     * 链接描述
     */
    private String description;
    
    /**
     * 视频封面URL（B站/YouTube）
     */
    private String coverUrl;
    
    /**
     * 排序权重
     */
    private Integer sortOrder;
    
    /**
     * 点击次数
     */
    private Integer clickCount;
    
    /**
     * 状态：1-有效，0-失效
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime ctime;

    /**
     * 从 Entity 转换为 VO
     */
    public static ResourceLinkVO fromEntity(ResourceLink entity) {
        if (entity == null) {
            return null;
        }
        
        // 获取链接类型枚举
        LinkType linkType = LinkType.fromCode(entity.getLinkType());
        String linkTypeDesc = linkType != null ? linkType.getDescription() : "未知类型";
        
        // 获取平台枚举
        LinkPlatform linkPlatform = LinkPlatform.fromCode(entity.getPlatform());
        String platformName = linkPlatform != null ? linkPlatform.getName() : "其他";
        
        return ResourceLinkVO.builder()
                .linkId(entity.getLinkId())
                .linkType(entity.getLinkType())
                .linkTypeDesc(linkTypeDesc)
                .platform(entity.getPlatform())
                .platformName(platformName)
                .url(entity.getUrl())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .coverUrl(entity.getCoverUrl())
                .sortOrder(entity.getSortOrder())
                .clickCount(entity.getClickCount())
                .status(entity.getStatus())
                .ctime(entity.getCtime())
                .build();
    }
}
