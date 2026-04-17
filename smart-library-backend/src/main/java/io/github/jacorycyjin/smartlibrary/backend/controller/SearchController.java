package io.github.jacorycyjin.smartlibrary.backend.controller;

import io.github.jacorycyjin.smartlibrary.backend.common.response.Result;
import io.github.jacorycyjin.smartlibrary.backend.form.GlobalSearchForm;
import io.github.jacorycyjin.smartlibrary.backend.service.SearchService;
import io.github.jacorycyjin.smartlibrary.backend.vo.GlobalSearchResultVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 全局搜索控制器
 * 
 * @author Jacory
 * @date 2025/01/28
 */
@RestController
@RequestMapping("/search")
public class SearchController {

    @Resource
    private SearchService searchService;

    /**
     * 全局搜索（同时搜索图书和作者）
     * 
     * @param searchForm 搜索表单
     * @return 搜索结果
     */
    @PostMapping("/global")
    public Result<GlobalSearchResultVO> globalSearch(@RequestBody GlobalSearchForm searchForm) {
        GlobalSearchResultVO result = searchService.globalSearch(searchForm);
        return Result.success(result);
    }
}
