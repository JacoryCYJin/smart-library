package io.github.jacorycyjin.smartlibrary.backend.service;

import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageDTO;
import io.github.jacorycyjin.smartlibrary.backend.form.ResourceLinkForm;

import java.util.Map;

/**
 * 管理员资源链接管理服务接口
 * 
 * @author Kiro
 * @date 2026/04/05
 */
public interface AdminLinkService {
    
    /**
     * 获取资源链接列表
     * 
     * @param params 查询参数
     * @return 分页数据
     */
    PageDTO getLinkList(Map<String, Object> params);
    
    /**
     * 创建资源链接
     * 
     * @param form 链接表单
     */
    void createLink(ResourceLinkForm form);
    
    /**
     * 更新资源链接
     * 
     * @param form 链接表单
     */
    void updateLink(ResourceLinkForm form);
    
    /**
     * 删除资源链接
     * 
     * @param linkId 链接ID
     */
    void deleteLink(String linkId);
    
    /**
     * 获取资源链接详情
     * 
     * @param linkId 链接ID
     * @return 链接详情
     */
    Map<String, Object> getLinkDetail(String linkId);
}
