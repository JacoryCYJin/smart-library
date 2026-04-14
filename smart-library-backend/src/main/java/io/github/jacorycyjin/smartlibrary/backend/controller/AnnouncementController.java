package io.github.jacorycyjin.smartlibrary.backend.controller;

import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageDTO;
import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;
import io.github.jacorycyjin.smartlibrary.backend.common.response.Result;
import io.github.jacorycyjin.smartlibrary.backend.common.util.UserContext;
import io.github.jacorycyjin.smartlibrary.backend.form.AnnouncementForm;
import io.github.jacorycyjin.smartlibrary.backend.form.AnnouncementSearchForm;
import io.github.jacorycyjin.smartlibrary.backend.service.AnnouncementService;
import io.github.jacorycyjin.smartlibrary.backend.vo.AnnouncementVO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公告控制器
 * 
 * @author jacorycyjin
 * @date 2025/01/15
 */
@RestController
@RequestMapping("/announcement")
public class AnnouncementController {
    
    @Resource
    private AnnouncementService announcementService;
    
    /**
     * 获取公告列表（公开接口）
     */
    @GetMapping("/list")
    public Result<List<AnnouncementVO>> getAnnouncementList(
            @RequestParam(required = false) Integer type) {
        List<AnnouncementVO> announcements = announcementService.getAnnouncementList(1, type);
        return Result.success(announcements);
    }
    
    /**
     * 搜索公告（管理员）
     */
    @PostMapping("/search")
    public Result<PageDTO<AnnouncementVO>> searchAnnouncements(@RequestBody AnnouncementSearchForm form) {
        String userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.fail(ApiCode.UNAUTHORIZED.getCode(), "未登录");
        }
        
        PageDTO<AnnouncementVO> result = announcementService.searchAnnouncements(form);
        return Result.success(result);
    }
    
    /**
     * 获取公告详情（公开接口）
     */
    @GetMapping("/{announcementId}")
    public Result<AnnouncementVO> getAnnouncementDetail(@PathVariable String announcementId) {
        AnnouncementVO announcement = announcementService.getAnnouncementDetail(announcementId);
        return Result.success(announcement);
    }
    
    /**
     * 创建公告（管理员）
     */
    @PostMapping("/create")
    public Result<Map<String, String>> createAnnouncement(@Valid @RequestBody AnnouncementForm form) {
        String userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.fail(ApiCode.UNAUTHORIZED.getCode(), "未登录");
        }
        
        String username = UserContext.getCurrentUser() != null ? UserContext.getCurrentUser().getUsername() : "管理员";
        
        String announcementId = announcementService.createAnnouncement(form, userId, username);
        
        Map<String, String> result = new HashMap<>();
        result.put("announcementId", announcementId);
        return Result.success(result);
    }
    
    /**
     * 更新公告（管理员）
     */
    @PutMapping("/{announcementId}")
    public Result<Void> updateAnnouncement(
            @PathVariable String announcementId,
            @Valid @RequestBody AnnouncementForm form) {
        String userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.fail(ApiCode.UNAUTHORIZED.getCode(), "未登录");
        }
        
        announcementService.updateAnnouncement(announcementId, form);
        return Result.success();
    }
    
    /**
     * 删除公告（管理员）
     */
    @DeleteMapping("/{announcementId}")
    public Result<Void> deleteAnnouncement(@PathVariable String announcementId) {
        String userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.fail(ApiCode.UNAUTHORIZED.getCode(), "未登录");
        }
        
        announcementService.deleteAnnouncement(announcementId);
        return Result.success();
    }
}
