package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageDTO;
import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;
import io.github.jacorycyjin.smartlibrary.backend.common.exception.BusinessException;
import io.github.jacorycyjin.smartlibrary.backend.common.util.UUIDUtil;
import io.github.jacorycyjin.smartlibrary.backend.entity.Resource;
import io.github.jacorycyjin.smartlibrary.backend.entity.ResourceLink;
import io.github.jacorycyjin.smartlibrary.backend.form.ResourceLinkForm;
import io.github.jacorycyjin.smartlibrary.backend.mapper.ResourceLinkMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.ResourceMapper;
import io.github.jacorycyjin.smartlibrary.backend.service.AdminLinkService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员资源链接管理服务实现
 * 
 * @author Kiro
 * @date 2026/04/05
 */
@Service
public class AdminLinkServiceImpl implements AdminLinkService {

    @jakarta.annotation.Resource
    private ResourceLinkMapper resourceLinkMapper;

    @jakarta.annotation.Resource
    private ResourceMapper resourceMapper;

    @Override
    public PageDTO<Map<String, Object>> getLinkList(Map<String, Object> params) {
        int total = resourceLinkMapper.countLinks(params);
        List<ResourceLink> links = resourceLinkMapper.searchLinks(params);

        // 转换为 Map 列表
        List<Map<String, Object>> linkList = new ArrayList<>();
        for (ResourceLink link : links) {
            Map<String, Object> linkMap = new HashMap<>();
            linkMap.put("linkId", link.getLinkId());
            linkMap.put("resourceId", link.getResourceId());
            linkMap.put("linkType", link.getLinkType());
            linkMap.put("platform", link.getPlatform());
            linkMap.put("url", link.getUrl());
            linkMap.put("title", link.getTitle());
            linkMap.put("description", link.getDescription());
            linkMap.put("coverUrl", link.getCoverUrl());
            linkMap.put("sortOrder", link.getSortOrder());
            linkMap.put("clickCount", link.getClickCount());
            linkMap.put("status", link.getStatus());
            linkMap.put("ctime", link.getCtime());
            
            // 查询资源标题
            Map<String, Object> resourceParams = new HashMap<>();
            resourceParams.put("resourceId", link.getResourceId());
            List<Resource> resources = resourceMapper.searchResources(resourceParams);
            if (!resources.isEmpty()) {
                linkMap.put("resourceTitle", resources.get(0).getTitle());
            }
            
            linkList.add(linkMap);
        }

        PageDTO<Map<String, Object>> result = new PageDTO<>();
        result.setTotalCount(total);
        result.setList(linkList);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createLink(ResourceLinkForm form) {
        ResourceLink link = ResourceLink.builder()
                .linkId(UUIDUtil.generateUUID())
                .resourceId(form.getResourceId())
                .linkType(form.getLinkType())
                .platform(form.getPlatform())
                .url(form.getUrl())
                .title(form.getTitle())
                .description(form.getDescription())
                .coverUrl(form.getCoverUrl())
                .sortOrder(form.getSortOrder() != null ? form.getSortOrder() : 0)
                .status(form.getStatus() != null ? form.getStatus() : 1)
                .build();

        resourceLinkMapper.insert(link);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLink(ResourceLinkForm form) {
        ResourceLink link = resourceLinkMapper.selectLinkById(form.getLinkId());
        if (link == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "链接不存在");
        }

        link.setLinkType(form.getLinkType());
        link.setPlatform(form.getPlatform());
        link.setUrl(form.getUrl());
        link.setTitle(form.getTitle());
        link.setDescription(form.getDescription());
        link.setCoverUrl(form.getCoverUrl());
        link.setSortOrder(form.getSortOrder());
        link.setStatus(form.getStatus());

        resourceLinkMapper.update(link);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteLink(String linkId) {
        ResourceLink link = resourceLinkMapper.selectLinkById(linkId);
        if (link == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "链接不存在");
        }

        resourceLinkMapper.deleteById(linkId);
    }

    @Override
    public Map<String, Object> getLinkDetail(String linkId) {
        ResourceLink link = resourceLinkMapper.selectLinkById(linkId);
        if (link == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "链接不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("linkId", link.getLinkId());
        result.put("resourceId", link.getResourceId());
        result.put("linkType", link.getLinkType());
        result.put("platform", link.getPlatform());
        result.put("url", link.getUrl());
        result.put("title", link.getTitle());
        result.put("description", link.getDescription());
        result.put("coverUrl", link.getCoverUrl());
        result.put("sortOrder", link.getSortOrder());
        result.put("status", link.getStatus());

        return result;
    }
}
