package io.github.jacorycyjin.smartlibrary.backend.service;

import io.github.jacorycyjin.smartlibrary.backend.form.GlobalSearchForm;
import io.github.jacorycyjin.smartlibrary.backend.vo.GlobalSearchResultVO;

/**
 * 全局搜索服务接口
 * 
 * @author Jacory
 * @date 2025/01/28
 */
public interface SearchService {

    /**
     * 全局搜索（同时搜索图书和作者）
     * 
     * @param searchForm 搜索表单
     * @return 搜索结果
     */
    GlobalSearchResultVO globalSearch(GlobalSearchForm searchForm);
}
