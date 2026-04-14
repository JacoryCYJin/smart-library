package io.github.jacorycyjin.smartlibrary.backend.service;

import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageDTO;
import io.github.jacorycyjin.smartlibrary.backend.form.AnnouncementForm;
import io.github.jacorycyjin.smartlibrary.backend.form.AnnouncementSearchForm;
import io.github.jacorycyjin.smartlibrary.backend.vo.AnnouncementVO;

import java.util.List;

/**
 * 公告服务接口
 * 
 * @author jacorycyjin
 * @date 2025/01/15
 */
public interface AnnouncementService {
    
    /**
     * 创建公告（管理员）
     */
    String createAnnouncement(AnnouncementForm form, String publisherId, String publisherName);
    
    /**
     * 获取公告列表
     */
    List<AnnouncementVO> getAnnouncementList(Integer status, Integer type);
    
    /**
     * 搜索公告（管理员）
     */
    PageDTO<AnnouncementVO> searchAnnouncements(AnnouncementSearchForm form);
    
    /**
     * 获取公告详情
     */
    AnnouncementVO getAnnouncementDetail(String announcementId);
    
    /**
     * 更新公告（管理员）
     */
    void updateAnnouncement(String announcementId, AnnouncementForm form);
    
    /**
     * 删除公告（管理员）
     */
    void deleteAnnouncement(String announcementId);
}
