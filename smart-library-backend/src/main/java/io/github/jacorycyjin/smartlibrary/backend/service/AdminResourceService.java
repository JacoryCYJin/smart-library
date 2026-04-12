package io.github.jacorycyjin.smartlibrary.backend.service;

import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageDTO;
import io.github.jacorycyjin.smartlibrary.backend.entity.Resource;

import java.util.List;
import java.util.Map;

/**
 * 管理员资源管理服务接口
 * 
 * @author Kiro
 * @date 2026/04/05
 */
public interface AdminResourceService {

    /**
     * 获取资源列表
     */
    PageDTO<Resource> getResourceList(Map<String, Object> params);

    /**
     * 删除资源
     */
    void deleteResource(String resourceId);

    /**
     * 恢复资源
     */
    void restoreResource(String resourceId);

    /**
     * 添加资源
     */
    void createResource(io.github.jacorycyjin.smartlibrary.backend.form.ResourceForm form);

    /**
     * 更新资源
     */
    void updateResource(io.github.jacorycyjin.smartlibrary.backend.form.ResourceForm form);

    /**
     * 获取资源详情（用于编辑）
     */
    Map<String, Object> getResourceDetail(String resourceId);

    /**
     * 获取所有分类列表
     */
    List<Map<String, Object>> getAllCategories();

    /**
     * 获取所有标签列表
     */
    List<Map<String, Object>> getAllTags();

    /**
     * 搜索作者
     */
    List<Map<String, Object>> searchAuthors(String keyword);
}
