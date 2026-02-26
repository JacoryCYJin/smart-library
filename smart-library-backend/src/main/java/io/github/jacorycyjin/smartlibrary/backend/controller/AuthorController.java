package io.github.jacorycyjin.smartlibrary.backend.controller;

import io.github.jacorycyjin.smartlibrary.backend.common.response.Result;
import io.github.jacorycyjin.smartlibrary.backend.service.AuthorService;
import io.github.jacorycyjin.smartlibrary.backend.vo.AuthorDetailVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 作者控制器
 * 
 * @author Jacory
 * @date 2025/01/27
 */
@RestController
@RequestMapping("/author")
public class AuthorController {

    @Resource
    private AuthorService authorService;

    /**
     * 获取作者详情（包含作品列表）
     * 
     * @param authorId 作者业务ID
     * @return 作者详情
     */
    @GetMapping("/{authorId}")
    public Result<AuthorDetailVO> getAuthorDetail(@PathVariable String authorId) {
        AuthorDetailVO detailVO = authorService.getAuthorDetail(authorId);
        return Result.success(detailVO);
    }

    /**
     * 根据资源ID和排序查询作者ID
     * 
     * @param resourceId 资源ID
     * @param sort 排序（1=第一作者，2=第二作者...）
     * @return 作者ID
     */
    @GetMapping("/id")
    public Result<String> getAuthorId(@RequestParam String resourceId, @RequestParam Integer sort) {
        String authorId = authorService.getAuthorIdByResourceAndSort(resourceId, sort);
        return Result.success(authorId);
    }
}
