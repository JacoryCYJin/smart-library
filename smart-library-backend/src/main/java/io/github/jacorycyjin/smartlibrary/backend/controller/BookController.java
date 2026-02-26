package io.github.jacorycyjin.smartlibrary.backend.controller;

import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageDTO;
import io.github.jacorycyjin.smartlibrary.backend.common.response.Result;
import io.github.jacorycyjin.smartlibrary.backend.common.vo.PageVO;
import io.github.jacorycyjin.smartlibrary.backend.converter.ResourceConverter;
import io.github.jacorycyjin.smartlibrary.backend.dto.ResourceDTO;
import io.github.jacorycyjin.smartlibrary.backend.form.ResourceSearchForm;
import io.github.jacorycyjin.smartlibrary.backend.service.ResourceService;
import io.github.jacorycyjin.smartlibrary.backend.vo.ResourceDetailVO;
import io.github.jacorycyjin.smartlibrary.backend.vo.ResourcePublicVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 图书控制器
 * 
 * @author Jacory
 * @date 2025/01/19
 */
@RestController
@RequestMapping("/book")
public class BookController {

    @Resource
    private ResourceService resourceService;

    /**
     * 搜索图书（支持多条件查询和分页）
     * 
     * @param searchForm 查询条件（包含分页参数：pageNum, pageSize）
     * @return 分页图书列表
     */
    @PostMapping("/search")
    public Result<PageVO<ResourcePublicVO>> searchBooks(@RequestBody(required = false) ResourceSearchForm searchForm) {
        if (searchForm == null) {
            searchForm = new ResourceSearchForm();
        }
        
        // 强制设置为图书类型
        searchForm.setType(1);
        
        // 调用 Service 层获取分页数据
        PageDTO<ResourceDTO> pageDTO = resourceService.searchResources(searchForm);
        
        // 转换 DTO -> VO
        List<ResourcePublicVO> resourceVOs = pageDTO.getList().stream()
                .map(ResourcePublicVO::fromDTO)
                .toList();
        
        // 构建分页 VO
        PageVO<ResourcePublicVO> pageVO = new PageVO<>(pageDTO, resourceVOs);
        
        return Result.success(pageVO);
    }

    /**
     * 获取图书详情（包含完整分类层级、所有标签）
     * 
     * @param bookId 图书业务ID
     * @return 图书详情
     */
    @GetMapping("/{bookId}")
    public Result<ResourceDetailVO> getBookDetail(@PathVariable String bookId) {
        // 查询资源详情
        ResourceDTO resourceDTO = resourceService.getResourceDetail(bookId);
        
        // 增加浏览量
        resourceService.incrementViewCount(bookId);
        
        // 转换为 DetailVO（包含完整分类层级）
        ResourceDetailVO detailVO = ResourceConverter.toDetailVO(resourceDTO);
        
        return Result.success(detailVO);
    }
}
