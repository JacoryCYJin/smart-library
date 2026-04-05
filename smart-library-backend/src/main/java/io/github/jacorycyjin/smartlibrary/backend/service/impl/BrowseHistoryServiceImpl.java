package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import io.github.jacorycyjin.smartlibrary.backend.service.BrowseHistoryService;
import io.github.jacorycyjin.smartlibrary.backend.mapper.UserBrowseHistoryMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.ResourceMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.CategoryMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.TagMapper;
import io.github.jacorycyjin.smartlibrary.backend.entity.UserBrowseHistory;
import io.github.jacorycyjin.smartlibrary.backend.entity.Resource;
import io.github.jacorycyjin.smartlibrary.backend.vo.ResourcePublicVO;
import io.github.jacorycyjin.smartlibrary.backend.dto.ResourceDTO;
import io.github.jacorycyjin.smartlibrary.backend.converter.ResourceConverter;
import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageDTO;
import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageQueryDTO;
import io.github.jacorycyjin.smartlibrary.backend.form.BrowseHistoryQueryForm;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * 浏览历史服务实现
 * 
 * @author Jacory
 * @date 2025/01/20
 */
@Service
public class BrowseHistoryServiceImpl implements BrowseHistoryService {

    @jakarta.annotation.Resource
    private UserBrowseHistoryMapper browseHistoryMapper;

    @jakarta.annotation.Resource
    private ResourceMapper resourceMapper;

    @jakarta.annotation.Resource
    private CategoryMapper categoryMapper;

    @jakarta.annotation.Resource
    private TagMapper tagMapper;

    /**
     * 分页查询用户浏览历史
     * 
     * @param userId 用户ID
     * @param queryForm 分页查询表单
     * @return 分页浏览历史资源列表
     */
    @Override
    public PageDTO<ResourcePublicVO> getBrowseHistory(String userId, BrowseHistoryQueryForm queryForm) {
        // 转换为 PageQueryDTO 并校验参数
        PageQueryDTO pageQuery = queryForm.toPageQueryDTO();
        
        // 统计总数
        Integer totalCount = browseHistoryMapper.countByUserId(userId);
        
        // 计算 offset
        Integer offset = (pageQuery.getPageNum() - 1) * pageQuery.getPageSize();
        
        // 查询浏览历史记录
        List<UserBrowseHistory> historyList = browseHistoryMapper.selectByUserId(
            userId, 
            pageQuery.getPageSize(), 
            offset
        );
        
        List<ResourcePublicVO> resourceVOs;
        
        if (historyList == null || historyList.isEmpty()) {
            resourceVOs = List.of();
        } else {
            // 提取资源ID列表
            List<String> resourceIds = historyList.stream()
                    .map(UserBrowseHistory::getResourceId)
                    .collect(Collectors.toList());
            
            // 批量查询资源信息
            Map<String, Object> params = new HashMap<>();
            params.put("resourceIds", resourceIds);
            List<Resource> resources = resourceMapper.searchResources(params);
            
            // 创建资源ID到浏览历史的映射（用于获取 mtime）
            Map<String, UserBrowseHistory> historyMap = historyList.stream()
                    .collect(Collectors.toMap(
                        UserBrowseHistory::getResourceId,
                        h -> h
                    ));
            
            // 转换为 VO，并设置 mtime
            resourceVOs = resources.stream()
                    .map(resource -> {
                        ResourceDTO dto = ResourceConverter.toDTO(resource, categoryMapper, tagMapper);
                        ResourcePublicVO vo = ResourcePublicVO.fromDTO(dto);
                        
                        // 设置浏览历史的 mtime
                        UserBrowseHistory history = historyMap.get(resource.getResourceId());
                        if (history != null) {
                            vo.setMtime(history.getMtime());
                        }
                        
                        return vo;
                    })
                    .collect(Collectors.toList());
        }
        
        // 构建分页结果
        return new PageDTO<>(pageQuery.getPageNum(), totalCount, pageQuery.getPageSize(), resourceVOs);
    }

    /**
     * 记录用户浏览历史
     * 
     * @param userId 用户ID
     * @param resourceId 资源ID
     */
    @Override
    public void recordBrowseHistory(String userId, String resourceId) {
        browseHistoryMapper.insertOrUpdate(userId, resourceId);
    }

    /**
     * 统计用户浏览历史数量
     * 
     * @param userId 用户ID
     * @return 浏览历史数量
     */
    @Override
    public Integer countBrowseHistory(String userId) {
        return browseHistoryMapper.countByUserId(userId);
    }
}
