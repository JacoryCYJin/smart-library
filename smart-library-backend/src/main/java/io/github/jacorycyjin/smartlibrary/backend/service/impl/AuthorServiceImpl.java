package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;
import io.github.jacorycyjin.smartlibrary.backend.common.exception.BusinessException;
import io.github.jacorycyjin.smartlibrary.backend.dto.AuthorDTO;
import io.github.jacorycyjin.smartlibrary.backend.entity.Author;
import io.github.jacorycyjin.smartlibrary.backend.mapper.AuthorMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.CategoryMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.ResourceMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.TagMapper;
import io.github.jacorycyjin.smartlibrary.backend.service.AuthorService;
import io.github.jacorycyjin.smartlibrary.backend.vo.AuthorDetailVO;
import io.github.jacorycyjin.smartlibrary.backend.vo.ResourcePublicVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 作者服务实现
 * 
 * @author Jacory
 * @date 2025/01/27
 */
@Service
public class AuthorServiceImpl implements AuthorService {

    @jakarta.annotation.Resource
    private AuthorMapper authorMapper;

    @jakarta.annotation.Resource
    private ResourceMapper resourceMapper;

    @jakarta.annotation.Resource
    private CategoryMapper categoryMapper;

    @jakarta.annotation.Resource
    private TagMapper tagMapper;

    /**
     * 获取作者详情
     * 
     * @param authorId 作者业务ID
     * @return 作者详情 VO
     */
    @Override
    public AuthorDetailVO getAuthorDetail(String authorId) {
        // 查询作者基本信息
        Author author = authorMapper.selectAuthorById(authorId);
        if (author == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "作者不存在");
        }

        AuthorDTO authorDTO = AuthorDTO.fromEntity(author);
        
        // 查询该作者的所有作品
        List<io.github.jacorycyjin.smartlibrary.backend.entity.Resource> resources = 
            resourceMapper.selectResourcesByAuthorId(authorId);
        
        // 转换为 VO（简化版本，不查询作者信息，避免循环）
        List<ResourcePublicVO> works = resources.stream()
            .map(resource -> {
                // 使用简化的转换方法
                io.github.jacorycyjin.smartlibrary.backend.dto.ResourceDTO dto = 
                    io.github.jacorycyjin.smartlibrary.backend.dto.ResourceDTO.fromEntity(resource);
                
                // 只查询分类和标签，不查询作者（避免循环）
                List<io.github.jacorycyjin.smartlibrary.backend.entity.Category> categories = 
                    categoryMapper.selectCategoriesByResourceId(resource.getResourceId());
                dto.setCategories(categories.stream()
                    .map(io.github.jacorycyjin.smartlibrary.backend.dto.CategoryDTO::fromEntity)
                    .toList());
                
                List<java.util.Map<String, Object>> tagMaps = 
                    tagMapper.selectTagsByResourceIds(List.of(resource.getResourceId()));
                dto.setTags(tagMaps.stream()
                    .map(io.github.jacorycyjin.smartlibrary.backend.dto.TagDTO::fromMap)
                    .toList());
                
                return ResourcePublicVO.fromDTO(dto);
            })
            .toList();
        
        // 构建作者详情 VO
        AuthorDetailVO detailVO = AuthorDetailVO.fromDTO(authorDTO);
        detailVO.setWorks(works);
        detailVO.setWorksCount(works.size());
        
        return detailVO;
    }

    /**
     * 根据资源ID和排序查询作者ID
     * 
     * @param resourceId 资源ID
     * @param sort 排序（1=第一作者，2=第二作者...）
     * @return 作者ID
     */
    @Override
    public String getAuthorIdByResourceAndSort(String resourceId, Integer sort) {
        // 查询该资源的所有作者
        List<Map<String, Object>> authorMaps = authorMapper.selectAuthorsByResourceId(resourceId);
        
        // 根据 sort 匹配
        for (Map<String, Object> map : authorMaps) {
            Integer authorSort = (Integer) map.get("sort");
            if (sort.equals(authorSort)) {
                return (String) map.get("authorId");
            }
        }
        
        throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "未找到该作者");
    }
}
