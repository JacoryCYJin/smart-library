package io.github.jacorycyjin.smartlibrary.backend.controller;

import io.github.jacorycyjin.smartlibrary.backend.common.response.Result;
import io.github.jacorycyjin.smartlibrary.backend.service.CategoryService;
import io.github.jacorycyjin.smartlibrary.backend.vo.CategoryVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类控制器
 * 
 * @author Jacory
 * @date 2025/01/20
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    /**
     * 获取所有分类列表
     * 
     * @return 分类列表
     */
    @GetMapping("/list")
    public Result<List<CategoryVO>> listCategories() {
        List<CategoryVO> categories = categoryService.getAllCategories().stream()
                .map(CategoryVO::fromDTO)
                .collect(Collectors.toList());
        
        return Result.success(categories);
    }
}
