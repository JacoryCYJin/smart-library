package io.github.jacorycyjin.smartlibrary.backend.service;

import io.github.jacorycyjin.smartlibrary.backend.dto.TagDTO;

import java.util.List;

/**
 * 标签服务接口
 * 
 * @author Jacory
 * @date 2025/01/20
 */
public interface TagService {

    /**
     * 获取所有标签列表
     * 
     * @return 标签DTO列表
     */
    List<TagDTO> getAllTags();
}
