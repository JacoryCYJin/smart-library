package io.github.jacorycyjin.smartlibrary.backend.service;

import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageDTO;
import io.github.jacorycyjin.smartlibrary.backend.form.BrowseHistoryQueryForm;
import io.github.jacorycyjin.smartlibrary.backend.vo.ResourcePublicVO;

/**
 * 浏览历史服务接口
 * 
 * @author Jacory
 * @date 2025/01/20
 */
public interface BrowseHistoryService {

    /**
     * 分页查询用户浏览历史
     * 
     * @param userId 用户ID
     * @param queryForm 分页查询表单
     * @return 分页浏览历史资源列表
     */
    PageDTO<ResourcePublicVO> getBrowseHistory(String userId, BrowseHistoryQueryForm queryForm);

    /**
     * 记录用户浏览历史
     * 如果已存在记录，则更新浏览次数和最后浏览时间
     * 
     * @param userId 用户ID
     * @param resourceId 资源ID
     */
    void recordBrowseHistory(String userId, String resourceId);
}
