package io.github.jacorycyjin.smartlibrary.backend.service;

import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageDTO;
import io.github.jacorycyjin.smartlibrary.backend.form.AuthorForm;

import java.util.Map;

/**
 * 管理员作者管理服务接口
 * 
 * @author Kiro
 * @date 2026/04/05
 */
public interface AdminAuthorService {
    
    /**
     * 获取作者列表
     * 
     * @param params 查询参数
     * @return 分页数据
     */
    PageDTO<Map<String, Object>> getAuthorList(Map<String, Object> params);
    
    /**
     * 创建作者
     * 
     * @param form 作者表单
     */
    void createAuthor(AuthorForm form);
    
    /**
     * 更新作者
     * 
     * @param form 作者表单
     */
    void updateAuthor(AuthorForm form);
    
    /**
     * 删除作者
     * 
     * @param authorId 作者ID
     */
    void deleteAuthor(String authorId);
    
    /**
     * 获取作者详情
     * 
     * @param authorId 作者ID
     * @return 作者详情
     */
    Map<String, Object> getAuthorDetail(String authorId);
}
