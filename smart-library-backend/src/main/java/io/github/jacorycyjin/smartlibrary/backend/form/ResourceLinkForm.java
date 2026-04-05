package io.github.jacorycyjin.smartlibrary.backend.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 资源链接表单
 * 
 * @author Jacory
 * @date 2026/04/05
 */
@Data
public class ResourceLinkForm {

    /**
     * 链接ID（编辑时必填）
     */
    private String linkId;

    /**
     * 资源ID
     */
    @NotBlank(message = "资源ID不能为空")
    private String resourceId;

    /**
     * 链接类型：1=豆瓣主页，2=下载链接，3=视频解读
     */
    @NotNull(message = "链接类型不能为空")
    private Integer linkType;

    /**
     * 平台：1=豆瓣，2=Z-Library，3=B站，4=YouTube
     */
    @NotNull(message = "平台不能为空")
    private Integer platform;

    /**
     * 链接URL
     */
    @NotBlank(message = "链接URL不能为空")
    private String url;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 封面URL（视频封面）
     */
    private String coverUrl;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态：0=禁用，1=启用
     */
    private Integer status;
}
