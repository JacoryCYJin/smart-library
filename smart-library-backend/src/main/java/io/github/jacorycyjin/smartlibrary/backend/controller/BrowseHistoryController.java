package io.github.jacorycyjin.smartlibrary.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.github.jacorycyjin.smartlibrary.backend.service.BrowseHistoryService;
import io.github.jacorycyjin.smartlibrary.backend.common.response.Result;
import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;
import io.github.jacorycyjin.smartlibrary.backend.common.util.UserContext;
import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageDTO;
import io.github.jacorycyjin.smartlibrary.backend.common.vo.PageVO;
import io.github.jacorycyjin.smartlibrary.backend.form.BrowseHistoryQueryForm;
import io.github.jacorycyjin.smartlibrary.backend.vo.ResourcePublicVO;

/**
 * 浏览历史控制器
 * 
 * @author Jacory
 * @date 2025/01/20
 */
@RestController
@RequestMapping("/history")
public class BrowseHistoryController {

    @jakarta.annotation.Resource
    private BrowseHistoryService browseHistoryService;

    /**
     * 分页查询用户浏览历史
     * 
     * @param queryForm 分页查询表单（pageNum, pageSize）
     * @return 分页浏览历史资源列表
     */
    @PostMapping("/list")
    public Result<PageVO<ResourcePublicVO>> getBrowseHistory(
            @RequestBody(required = false) BrowseHistoryQueryForm queryForm) {
        
        String userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.fail(ApiCode.UNAUTHORIZED.getCode(), "未登录");
        }
        
        // 如果没有传查询表单，使用默认值
        if (queryForm == null) {
            queryForm = new BrowseHistoryQueryForm();
        }
        
        // 调用 Service 层获取分页数据
        PageDTO<ResourcePublicVO> pageDTO = browseHistoryService.getBrowseHistory(userId, queryForm);
        
        // 构建分页 VO
        PageVO<ResourcePublicVO> pageVO = new PageVO<>(pageDTO, pageDTO.getList());
        
        return Result.success(pageVO);
    }
}
