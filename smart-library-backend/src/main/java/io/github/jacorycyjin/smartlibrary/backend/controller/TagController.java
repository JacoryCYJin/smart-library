package io.github.jacorycyjin.smartlibrary.backend.controller;

import io.github.jacorycyjin.smartlibrary.backend.common.response.Result;
import io.github.jacorycyjin.smartlibrary.backend.service.TagService;
import io.github.jacorycyjin.smartlibrary.backend.vo.TagVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签控制器
 * 
 * @author Jacory
 * @date 2025/01/20
 */
@RestController
@RequestMapping("/tag")
public class TagController {

    @Resource
    private TagService tagService;

    /**
     * 获取所有标签列表
     * 
     * @return 标签列表
     */
    @GetMapping("/list")
    public Result<List<TagVO>> listTags() {
        List<TagVO> tags = tagService.getAllTags().stream()
                .map(TagVO::fromDTO)
                .collect(Collectors.toList());
        
        return Result.success(tags);
    }
}
