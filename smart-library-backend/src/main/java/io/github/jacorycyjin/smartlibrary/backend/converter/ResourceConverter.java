package io.github.jacorycyjin.smartlibrary.backend.converter;

import io.github.jacorycyjin.smartlibrary.backend.dto.CategoryDTO;
import io.github.jacorycyjin.smartlibrary.backend.dto.ResourceDTO;
import io.github.jacorycyjin.smartlibrary.backend.dto.TagDTO;
import io.github.jacorycyjin.smartlibrary.backend.entity.Category;
import io.github.jacorycyjin.smartlibrary.backend.entity.Resource;
import io.github.jacorycyjin.smartlibrary.backend.mapper.CategoryMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.TagMapper;
import io.github.jacorycyjin.smartlibrary.backend.vo.CategoryVO;
import io.github.jacorycyjin.smartlibrary.backend.vo.ResourceDetailVO;
import io.github.jacorycyjin.smartlibrary.backend.vo.ResourceFileVO;
import io.github.jacorycyjin.smartlibrary.backend.vo.ResourcePublicVO;
import io.github.jacorycyjin.smartlibrary.backend.vo.TagVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资源转换器（处理复杂的 Entity -> DTO 和 DTO -> VO 转换）
 * 
 * @author Jacory
 * @date 2025/01/19
 */
public class ResourceConverter {

    /**
     * 转换为基础 DTO（用于列表页，不查询作者关联）
     * 
     * @param resource 资源实体
     * @param categoryMapper 分类 Mapper
     * @param tagMapper 标签 Mapper
     * @return 资源 DTO
     */
    public static ResourceDTO toDTO(Resource resource, CategoryMapper categoryMapper, TagMapper tagMapper) {
        if (resource == null) {
            return null;
        }

        // 查询分类（直接关联的分类，通常是最子分类）
        List<Category> categories = categoryMapper.selectCategoriesByResourceId(resource.getResourceId());
        
        // 只保留 level 最大的分类（最子分类）
        List<CategoryDTO> categoryDTOs = categories.stream()
                .filter(c -> c.getLevel() != null)
                .sorted((c1, c2) -> Integer.compare(c2.getLevel(), c1.getLevel())) // 降序
                .limit(1) // 只取最子分类
                .map(CategoryDTO::fromEntity)
                .toList();

        // 查询标签
        List<Map<String, Object>> tagMaps = tagMapper.selectTagsByResourceIds(List.of(resource.getResourceId()));
        List<TagDTO> tags = tagMaps.stream()
                .map(TagDTO::fromMap)
                .toList();

        // 转换为 DTO（列表页不查询 authors，使用 authorName 字段即可）
        ResourceDTO dto = ResourceDTO.fromEntity(resource);
        dto.setCategories(categoryDTOs);
        dto.setTags(tags);
        
        return dto;
    }

    /**
     * 转换为详情 DTO（用于详情页，包含完整分类层级，不查询作者关联）
     * 
     * @param resource 资源实体
     * @param categoryMapper 分类 Mapper
     * @param tagMapper 标签 Mapper
     * @return 资源 DTO
     */
    public static ResourceDTO toDetailDTO(Resource resource, CategoryMapper categoryMapper, TagMapper tagMapper) {
        if (resource == null) {
            return null;
        }

        // 查询完整分类层级
        List<Category> categoryPaths = categoryMapper.selectCategoryPathsByResourceId(resource.getResourceId());
        List<CategoryDTO> categoryDTOs = categoryPaths.stream()
                .map(CategoryDTO::fromEntity)
                .toList();

        // 查询所有标签
        List<Map<String, Object>> tagMaps = tagMapper.selectTagsByResourceIds(List.of(resource.getResourceId()));
        List<TagDTO> tags = tagMaps.stream()
                .map(TagDTO::fromMap)
                .toList();

        // 转换为 DTO（详情页也不查询 authors，使用 authorName 字段）
        ResourceDTO dto = ResourceDTO.fromEntity(resource);
        dto.setCategories(categoryDTOs);
        dto.setTags(tags);
        
        return dto;
    }

    /**
     * 将 ResourceDTO 转换为 ResourceDetailVO（包含完整分类层级）
     * 
     * @param dto 资源 DTO
     * @return 资源详情 VO
     */
    public static ResourceDetailVO toDetailVO(ResourceDTO dto) {
        if (dto == null) {
            return null;
        }

        // 先转换为 PublicVO（复用基础字段转换逻辑）
        ResourcePublicVO publicVO = ResourcePublicVO.fromDTO(dto);

        // 构建完整分类层级路径
        List<List<CategoryVO>> categoryPaths = buildCategoryPaths(dto.getCategories());

        // 转换标签
        List<TagVO> tags = dto.getTags() != null
                ? dto.getTags().stream()
                .map(TagVO::fromDTO)
                .toList()
                : null;

        // 构建 DetailVO
        return ResourceDetailVO.builder()
                // 继承 PublicVO 的字段
                .resourceId(publicVO.getResourceId())
                .type(publicVO.getType())
                .title(publicVO.getTitle())
                .authorName(publicVO.getAuthorName())
                .translatorName(publicVO.getTranslatorName())
                .coverUrl(publicVO.getCoverUrl())
                .pubDate(publicVO.getPubDate())
                .publisher(publicVO.getPublisher())
                .journal(publicVO.getJournal())
                .sourceScore(publicVO.getSourceScore())
                .viewCount(publicVO.getViewCount())
                .commentCount(publicVO.getCommentCount())
                .starCount(publicVO.getStarCount())
                .categoryNames(publicVO.getCategoryNames())
                .tags(tags)
                // DetailVO 特有字段
                .isbn(dto.getIsbn())
                .price(dto.getPrice())
                .pageCount(dto.getPageCount())
                .doi(dto.getDoi())
                .files(dto.getFiles() != null ? dto.getFiles().stream().map(f -> ResourceFileVO.builder()
                        .resourceId(f.getResourceId())
                        .fileType(f.getFileType())
                        .fileTypeDesc(f.getFileTypeDesc())
                        .fileUrl(f.getFileUrl())
                        .fileSize(f.getFileSize())
                        .ctime(f.getCtime())
                        .build()).toList() : null)
                .summary(dto.getSummary())
                .sourceOrigin(dto.getSourceOrigin())
                .sourceUrl(dto.getSourceUrl())
                .sentimentScore(dto.getSentimentScore())
                .categoryPaths(categoryPaths)
                .ctime(dto.getCtime())
                .mtime(dto.getMtime())
                .build();
    }

    /**
     * 构建分类层级路径
     * 
     * 输入: [Java(id=3, pid=2), 编程语言(id=2, pid=1), 计算机(id=1, pid=null)]
     * 输出: [[计算机, 编程语言, Java]]
     * 
     * @param categories 扁平化的分类列表
     * @return 分类层级路径列表
     */
    private static List<List<CategoryVO>> buildCategoryPaths(List<CategoryDTO> categories) {
        if (categories == null || categories.isEmpty()) {
            return new ArrayList<>();
        }

        // 按 categoryId 分组（同一个 categoryId 的所有祖先节点）
        Map<String, List<CategoryDTO>> groupedByLeaf = new HashMap<>();
        
        for (CategoryDTO category : categories) {
            // 找到叶子节点（最深层的分类）
            String leafId = findLeafCategoryId(category, categories);
            groupedByLeaf.computeIfAbsent(leafId, k -> new ArrayList<>()).add(category);
        }

        // 为每个叶子节点构建完整路径
        List<List<CategoryVO>> paths = new ArrayList<>();
        for (Map.Entry<String, List<CategoryDTO>> entry : groupedByLeaf.entrySet()) {
            List<CategoryDTO> pathCategories = entry.getValue();
            
            // 按层级排序（从根到叶子）
            pathCategories.sort((a, b) -> {
                int levelA = calculateLevel(a, pathCategories);
                int levelB = calculateLevel(b, pathCategories);
                return Integer.compare(levelA, levelB);
            });

            // 转换为 VO
            List<CategoryVO> path = pathCategories.stream()
                    .map(CategoryVO::fromDTO)
                    .toList();
            
            paths.add(path);
        }

        return paths;
    }

    /**
     * 找到叶子节点ID（没有子节点的分类）
     */
    private static String findLeafCategoryId(CategoryDTO category, List<CategoryDTO> allCategories) {
        // 检查是否有其他分类的 parentId 指向当前分类
        boolean hasChildren = allCategories.stream()
                .anyMatch(c -> category.getCategoryId().equals(c.getParentId()));
        
        if (hasChildren) {
            // 如果有子节点，继续查找
            CategoryDTO child = allCategories.stream()
                    .filter(c -> category.getCategoryId().equals(c.getParentId()))
                    .findFirst()
                    .orElse(null);
            return child != null ? findLeafCategoryId(child, allCategories) : category.getCategoryId();
        } else {
            // 没有子节点，就是叶子节点
            return category.getCategoryId();
        }
    }

    /**
     * 计算分类的层级（根节点为0）
     */
    private static int calculateLevel(CategoryDTO category, List<CategoryDTO> allCategories) {
        if (category.getParentId() == null) {
            return 0;
        }
        
        CategoryDTO parent = allCategories.stream()
                .filter(c -> c.getCategoryId().equals(category.getParentId()))
                .findFirst()
                .orElse(null);
        
        return parent != null ? calculateLevel(parent, allCategories) + 1 : 0;
    }
}
