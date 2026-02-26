package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import io.github.jacorycyjin.smartlibrary.backend.dto.TagDTO;
import io.github.jacorycyjin.smartlibrary.backend.mapper.TagMapper;
import io.github.jacorycyjin.smartlibrary.backend.service.TagService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 标签服务实现
 * 
 * @author Jacory
 * @date 2025/01/20
 */
@Service
public class TagServiceImpl implements TagService {

    @Resource
    private TagMapper tagMapper;

    /**
     * 获取所有标签列表
     * 
     * @return 标签DTO列表
     */
    @Override
    public List<TagDTO> getAllTags() {
        // 查询所有标签（返回 Map 格式）
        List<Map<String, Object>> tags = tagMapper.selectAll();
        
        // 使用 DTO 自带的 fromMap 方法转换
        return tags.stream()
                .map(TagDTO::fromMap)
                .collect(Collectors.toList());
    }
}
