package io.github.jacorycyjin.smartlibrary.backend.controller;

import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageDTO;
import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;
import io.github.jacorycyjin.smartlibrary.backend.common.exception.BusinessException;
import io.github.jacorycyjin.smartlibrary.backend.common.response.Result;
import io.github.jacorycyjin.smartlibrary.backend.common.util.UserContext;
import io.github.jacorycyjin.smartlibrary.backend.common.vo.PageVO;
import io.github.jacorycyjin.smartlibrary.backend.dto.CommentDTO;
import io.github.jacorycyjin.smartlibrary.backend.form.CommentCreateForm;
import io.github.jacorycyjin.smartlibrary.backend.service.CommentService;
import io.github.jacorycyjin.smartlibrary.backend.vo.CommentVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论控制器
 * 
 * @author Jacory
 * @date 2025/01/21
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @jakarta.annotation.Resource
    private CommentService commentService;

    /**
     * 获取资源的评论列表（分页）
     * 
     * @param resourceId 资源ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 评论列表
     */
    @GetMapping("/list")
    public Result<PageVO<CommentVO>> getComments(
            @RequestParam String resourceId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        PageDTO<CommentDTO> pageDTO = commentService.getCommentsByResourceId(resourceId, pageNum, pageSize);
        
        // 转换为 VO
        List<CommentVO> commentVOs = pageDTO.getList().stream()
                .map(CommentVO::fromDTO)
                .toList();
        
        PageVO<CommentVO> pageVO = new PageVO<>(pageDTO, commentVOs);
        
        return Result.success(pageVO);
    }

    /**
     * 创建评论（需要登录）
     * 
     * @param form 评论表单
     * @return 评论ID
     */
    @PostMapping("/create")
    public Result<String> createComment(@Valid @RequestBody CommentCreateForm form) {
        // 从 ThreadLocal 获取当前登录用户
        String userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ApiCode.UNAUTHORIZED.getCode(), "请先登录");
        }
        
        String commentId = commentService.createComment(userId, form);
        return Result.success(commentId);
    }

    /**
     * 删除评论（需要登录）
     * 
     * @param commentId 评论ID
     * @return 成功消息
     */
    @DeleteMapping("/{commentId}")
    public Result<Void> deleteComment(@PathVariable Long commentId) {
        // 从 ThreadLocal 获取当前登录用户
        String userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ApiCode.UNAUTHORIZED.getCode(), "请先登录");
        }
        
        commentService.deleteComment(userId, commentId);
        return Result.success(null);
    }
}
