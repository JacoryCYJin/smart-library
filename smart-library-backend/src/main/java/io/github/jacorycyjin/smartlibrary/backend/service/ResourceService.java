package io.github.jacorycyjin.smartlibrary.backend.service;

import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageDTO;
import io.github.jacorycyjin.smartlibrary.backend.dto.ResourceDTO;
import io.github.jacorycyjin.smartlibrary.backend.form.ResourceSearchForm;

/**
 * 资源服务接口
 * 
 * @author Jacory
 * @date 2025/01/19
 */
public interface ResourceService {

    /**
     * 搜索资源（支持多条件查询和分页）
     * 
     * @param searchForm 搜索表单
     * @return 分页资源列表
     */
    PageDTO<ResourceDTO> searchResources(ResourceSearchForm searchForm);

    /**
     * 获取资源详情（包含完整分类层级、所有标签）
     * 
     * @param resourceId 资源业务ID
     * @return 资源详情
     */
    ResourceDTO getResourceDetail(String resourceId);

    /**
     * 增加浏览量
     * 
     * @param resourceId 资源ID
     */
    void incrementViewCount(String resourceId);
}
