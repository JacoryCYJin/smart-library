package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageDTO;
import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageQueryDTO;
import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;
import io.github.jacorycyjin.smartlibrary.backend.common.exception.BusinessException;
import io.github.jacorycyjin.smartlibrary.backend.converter.ResourceConverter;
import io.github.jacorycyjin.smartlibrary.backend.dto.ResourceDTO;
import io.github.jacorycyjin.smartlibrary.backend.form.ResourceSearchForm;
import io.github.jacorycyjin.smartlibrary.backend.mapper.CategoryMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.ResourceMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.TagMapper;
import io.github.jacorycyjin.smartlibrary.backend.service.CategoryService;
import io.github.jacorycyjin.smartlibrary.backend.service.ResourceService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资源服务实现
 * 
 * @author Jacory
 * @date 2025/01/19
 */
@Service
public class ResourceServiceImpl implements ResourceService {

    @jakarta.annotation.Resource
    private ResourceMapper resourceMapper;

    @jakarta.annotation.Resource
    private CategoryMapper categoryMapper;

    @jakarta.annotation.Resource
    private TagMapper tagMapper;

    @jakarta.annotation.Resource
    private CategoryService categoryService;

    /**
     * 搜索资源（支持多条件查询和分页）
     * 
     * @param searchForm 搜索表单
     * @return 分页结果
     */
    @Override
    public PageDTO<ResourceDTO> searchResources(ResourceSearchForm searchForm) {
        // 校验并设置分页参数默认值（必须使用返回的 DTO）
        PageQueryDTO pageQuery = searchForm.toPageQueryDTO();
        
        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        params.put("type", searchForm.getType());
        params.put("keyword", searchForm.getKeyword());
        
        // 展开分类ID：如果传入的是父分类，需要查询所有子分类（调用 CategoryService）
        List<String> expandedCategoryIds = categoryService.expandCategoryIds(searchForm.getCategoryIds());
        params.put("categoryIds", expandedCategoryIds);
        
        params.put("tagIds", searchForm.getTagIds());
        params.put("authorName", searchForm.getAuthorName());
        params.put("publisher", searchForm.getPublisher());
        params.put("journal", searchForm.getJournal());
        params.put("sortBy", searchForm.getSortBy());

        // 分页查询（使用校验后的分页参数）
        PageHelper.startPage(pageQuery.getPageNum(), pageQuery.getPageSize());
        List<io.github.jacorycyjin.smartlibrary.backend.entity.Resource> resources = resourceMapper.searchResources(params);
        PageInfo<io.github.jacorycyjin.smartlibrary.backend.entity.Resource> pageInfo = new PageInfo<>(resources);

        // 使用 Converter 转换为 DTO
        List<ResourceDTO> resourceDTOs = resources.stream()
                .map(resource -> ResourceConverter.toDTO(resource, categoryMapper, tagMapper))
                .toList();

        return new PageDTO<>(
                pageQuery.getPageNum(),
                (int) pageInfo.getTotal(),
                pageQuery.getPageSize(),
                resourceDTOs
        );
    }

    /**
     * 获取资源详情
     * 
     * @param resourceId 资源业务ID
     * @return 资源DTO
     */
    @Override
    public ResourceDTO getResourceDetail(String resourceId) {
        // 使用通用查询方法查询单个资源
        Map<String, Object> params = new HashMap<>();
        params.put("resourceId", resourceId);
        params.put("limit", 1);
        
        List<io.github.jacorycyjin.smartlibrary.backend.entity.Resource> resources = resourceMapper.searchResources(params);
        if (resources == null || resources.isEmpty()) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), ApiCode.RESOURCE_NOT_FOUND.getMessage());
        }

        // 使用 Converter 转换为 DTO（包含完整分类层级和标签）
        return ResourceConverter.toDetailDTO(resources.get(0), categoryMapper, tagMapper);
    }

    /**
     * 增加资源浏览量
     * 
     * @param resourceId 资源业务ID
     */
    @Override
    public void incrementViewCount(String resourceId) {
        resourceMapper.incrementViewCount(resourceId);
    }
}
