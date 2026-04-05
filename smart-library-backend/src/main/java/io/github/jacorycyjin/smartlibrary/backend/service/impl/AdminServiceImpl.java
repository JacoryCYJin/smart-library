package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageDTO;
import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;
import io.github.jacorycyjin.smartlibrary.backend.common.exception.BusinessException;
import io.github.jacorycyjin.smartlibrary.backend.common.util.UUIDUtil;
import io.github.jacorycyjin.smartlibrary.backend.entity.Comment;
import io.github.jacorycyjin.smartlibrary.backend.entity.Resource;
import io.github.jacorycyjin.smartlibrary.backend.entity.User;
import io.github.jacorycyjin.smartlibrary.backend.mapper.CommentMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.ResourceMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.UserFavoriteMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.UserMapper;
import io.github.jacorycyjin.smartlibrary.backend.service.AdminService;
import io.github.jacorycyjin.smartlibrary.backend.vo.AdminStatsVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员服务实现
 * 
 * @author Kiro
 * @date 2026/04/04
 */
@Service
public class AdminServiceImpl implements AdminService {

    @jakarta.annotation.Resource
    private UserMapper userMapper;

    @jakarta.annotation.Resource
    private ResourceMapper resourceMapper;

    @jakarta.annotation.Resource
    private CommentMapper commentMapper;

    @jakarta.annotation.Resource
    private UserFavoriteMapper userFavoriteMapper;

    @jakarta.annotation.Resource
    private io.github.jacorycyjin.smartlibrary.backend.mapper.CategoryMapper categoryMapper;

    @jakarta.annotation.Resource
    private io.github.jacorycyjin.smartlibrary.backend.mapper.TagMapper tagMapper;

    @jakarta.annotation.Resource
    private io.github.jacorycyjin.smartlibrary.backend.mapper.AuthorMapper authorMapper;

    @Override
    public AdminStatsVO getStats() {
        // 统计资源总数
        Map<String, Object> resourceParams = new HashMap<>();
        resourceParams.put("deleted", 0);
        Long resourceCount = resourceMapper.countByParams(resourceParams);

        // 统计用户总数
        Map<String, Object> userParams = new HashMap<>();
        userParams.put("deleted", 0);
        Long userCount = userMapper.countByParams(userParams);

        // 统计评论总数
        Map<String, Object> commentParams = new HashMap<>();
        commentParams.put("deleted", 0);
        Long commentCount = commentMapper.countByParams(commentParams);

        // 统计收藏总数
        Long favoriteCount = userFavoriteMapper.countAll();

        return AdminStatsVO.builder()
                .resourceCount(resourceCount)
                .userCount(userCount)
                .commentCount(commentCount)
                .favoriteCount(favoriteCount)
                .build();
    }

    @Override
    public PageDTO getUserList(Map<String, Object> params) {
        // 获取分页参数并转换类型
        Integer pageNum = 1;
        Integer pageSize = 10;
        
        if (params.get("page") != null) {
            Object pageObj = params.get("page");
            pageNum = pageObj instanceof Integer ? (Integer) pageObj : Integer.parseInt(pageObj.toString());
        }
        
        if (params.get("pageSize") != null) {
            Object pageSizeObj = params.get("pageSize");
            pageSize = pageSizeObj instanceof Integer ? (Integer) pageSizeObj : Integer.parseInt(pageSizeObj.toString());
        }
        
        // 设置分页参数
        PageHelper.startPage(pageNum, pageSize);

        // 查询用户列表
        params.put("deleted", 0);
        List<User> users = userMapper.searchUsers(params);
        PageInfo<User> pageInfo = new PageInfo<>(users);

        return new PageDTO<>(pageNum, (int) pageInfo.getTotal(), pageSize, pageInfo.getList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(String userId, Integer status) {
        User user = userMapper.findByUserId(userId);
        if (user == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "用户不存在");
        }

        user.setStatus(status);
        userMapper.updateByUserId(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserRole(String userId, Integer role) {
        User user = userMapper.findByUserId(userId);
        if (user == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "用户不存在");
        }

        user.setRole(role);
        userMapper.updateByUserId(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(String userId) {
        User user = userMapper.findByUserId(userId);
        if (user == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "用户不存在");
        }

        user.setDeleted(1);
        userMapper.updateByUserId(user);
    }

    @Override
    public PageDTO getResourceList(Map<String, Object> params) {
        // 获取分页参数并转换类型
        Integer pageNum = 1;
        Integer pageSize = 10;
        
        if (params.get("page") != null) {
            Object pageObj = params.get("page");
            pageNum = pageObj instanceof Integer ? (Integer) pageObj : Integer.parseInt(pageObj.toString());
        }
        
        if (params.get("pageSize") != null) {
            Object pageSizeObj = params.get("pageSize");
            pageSize = pageSizeObj instanceof Integer ? (Integer) pageSizeObj : Integer.parseInt(pageSizeObj.toString());
        }
        
        // 设置分页参数
        PageHelper.startPage(pageNum, pageSize);

        // 查询资源列表
        params.put("deleted", 0);
        List<Resource> resources = resourceMapper.searchResources(params);
        PageInfo<Resource> pageInfo = new PageInfo<>(resources);

        return new PageDTO<>(pageNum, (int) pageInfo.getTotal(), pageSize, pageInfo.getList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteResource(String resourceId) {
        Resource resource = resourceMapper.findByResourceId(resourceId);
        if (resource == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "资源不存在");
        }

        resource.setDeleted(1);
        resourceMapper.updateByResourceId(resource);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restoreResource(String resourceId) {
        Resource resource = resourceMapper.findByResourceId(resourceId);
        if (resource == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "资源不存在");
        }

        resource.setDeleted(0);
        resourceMapper.updateByResourceId(resource);
    }

    @Override
    public PageDTO getCommentList(Map<String, Object> params) {
        // 获取分页参数并转换类型
        Integer pageNum = 1;
        Integer pageSize = 10;
        
        if (params.get("page") != null) {
            Object pageObj = params.get("page");
            pageNum = pageObj instanceof Integer ? (Integer) pageObj : Integer.parseInt(pageObj.toString());
        }
        
        if (params.get("pageSize") != null) {
            Object pageSizeObj = params.get("pageSize");
            pageSize = pageSizeObj instanceof Integer ? (Integer) pageSizeObj : Integer.parseInt(pageSizeObj.toString());
        }
        
        // 设置分页参数
        PageHelper.startPage(pageNum, pageSize);

        // 查询评论列表
        params.put("deleted", 0);
        List<Comment> comments = commentMapper.searchComments(params);
        PageInfo<Comment> pageInfo = new PageInfo<>(comments);

        return new PageDTO<>(pageNum, (int) pageInfo.getTotal(), pageSize, pageInfo.getList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditComment(String commentId, Integer auditStatus, String rejectionReason) {
        Comment comment = commentMapper.findByCommentId(commentId);
        if (comment == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "评论不存在");
        }

        comment.setAuditStatus(auditStatus);
        if (auditStatus == 2 && rejectionReason != null) {
            comment.setRejectionReason(rejectionReason);
        }
        commentMapper.updateByCommentId(comment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(String commentId) {
        Comment comment = commentMapper.findByCommentId(commentId);
        if (comment == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "评论不存在");
        }

        comment.setDeleted(1);
        commentMapper.updateByCommentId(comment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restoreComment(String commentId) {
        Comment comment = commentMapper.findByCommentId(commentId);
        if (comment == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "评论不存在");
        }

        comment.setDeleted(0);
        commentMapper.updateByCommentId(comment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createResource(io.github.jacorycyjin.smartlibrary.backend.form.ResourceForm form) {
        // 生成资源ID
        String resourceId = UUIDUtil.generateUUID();

        // 构建资源实体
        Resource resource = Resource.builder()
                .resourceId(resourceId)
                .type(form.getType())
                .title(form.getTitle())
                .coverUrl(form.getCoverUrl())
                .summary(form.getSummary())
                .pubDate(form.getPubDate())
                .isbn(form.getIsbn())
                .publisher(form.getPublisher())
                .price(form.getPrice())
                .pageCount(form.getPageCount())
                .doi(form.getDoi())
                .journal(form.getJournal())
                .sourceOrigin(form.getSourceOrigin() != null ? form.getSourceOrigin() : 99) // 默认手动录入
                .sourceUrl(form.getSourceUrl())
                .viewCount(0)
                .commentCount(0)
                .starCount(0)
                .hasGraph(0)
                .deleted(0)
                .build();

        // 插入资源
        resourceMapper.insert(resource);

        // 关联分类
        if (form.getCategoryIds() != null && !form.getCategoryIds().isEmpty()) {
            for (String categoryId : form.getCategoryIds()) {
                Map<String, Object> relParams = new HashMap<>();
                relParams.put("resourceId", resourceId);
                relParams.put("categoryId", categoryId);
                // 这里需要调用关联表的插入方法，暂时省略
            }
        }

        // 关联标签
        if (form.getTagIds() != null && !form.getTagIds().isEmpty()) {
            for (String tagId : form.getTagIds()) {
                Map<String, Object> relParams = new HashMap<>();
                relParams.put("resourceId", resourceId);
                relParams.put("tagId", tagId);
                // 这里需要调用关联表的插入方法，暂时省略
            }
        }

        // 关联作者
        if (form.getAuthorIds() != null && !form.getAuthorIds().isEmpty()) {
            int sort = 1;
            for (String authorId : form.getAuthorIds()) {
                Map<String, Object> relParams = new HashMap<>();
                relParams.put("resourceId", resourceId);
                relParams.put("authorId", authorId);
                relParams.put("sort", sort++);
                // 这里需要调用关联表的插入方法，暂时省略
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateResource(io.github.jacorycyjin.smartlibrary.backend.form.ResourceForm form) {
        Resource resource = resourceMapper.findByResourceId(form.getResourceId());
        if (resource == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "资源不存在");
        }

        // 更新基本信息
        resource.setType(form.getType());
        resource.setTitle(form.getTitle());
        resource.setCoverUrl(form.getCoverUrl());
        resource.setSummary(form.getSummary());
        resource.setPubDate(form.getPubDate());
        resource.setIsbn(form.getIsbn());
        resource.setPublisher(form.getPublisher());
        resource.setPrice(form.getPrice());
        resource.setPageCount(form.getPageCount());
        resource.setDoi(form.getDoi());
        resource.setJournal(form.getJournal());
        resource.setSourceUrl(form.getSourceUrl());

        resourceMapper.updateByResourceId(resource);

        // 更新关联关系（需要先删除旧关联，再插入新关联）
        // 这里暂时省略关联表的更新逻辑
    }

    @Override
    public Map<String, Object> getResourceDetail(String resourceId) {
        Resource resource = resourceMapper.findByResourceId(resourceId);
        if (resource == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "资源不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("resource", resource);

        // 查询关联的分类
        List<io.github.jacorycyjin.smartlibrary.backend.entity.Category> categories = 
            categoryMapper.selectCategoriesByResourceId(resourceId);
        result.put("categories", categories);

        // 查询关联的标签
        List<Map<String, Object>> tags = tagMapper.selectTagsByResourceIds(List.of(resourceId));
        result.put("tags", tags);

        // 查询关联的作者
        List<Map<String, Object>> authors = authorMapper.selectAuthorsByResourceId(resourceId);
        result.put("authors", authors);

        return result;
    }

    @Override
    public List<Map<String, Object>> getAllCategories() {
        Map<String, Object> params = new HashMap<>();
        params.put("deleted", 0);
        List<io.github.jacorycyjin.smartlibrary.backend.entity.Category> categories = 
            categoryMapper.searchCategories(params);
        
        // 转换为 Map 列表
        return categories.stream()
                .map(category -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("categoryId", category.getCategoryId());
                    map.put("name", category.getName());
                    map.put("parentId", category.getParentId());
                    map.put("level", category.getLevel());
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getAllTags() {
        return tagMapper.selectAll();
    }

    @Override
    public List<Map<String, Object>> searchAuthors(String keyword) {
        // 这里需要实现作者搜索逻辑
        // 暂时返回空列表
        return new java.util.ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> getCategoryTree() {
        // 查询所有分类
        Map<String, Object> params = new HashMap<>();
        params.put("deleted", 0);
        List<io.github.jacorycyjin.smartlibrary.backend.entity.Category> categories = 
            categoryMapper.searchCategories(params);
        
        // 构建分类 Map（用于快速查找）
        Map<String, io.github.jacorycyjin.smartlibrary.backend.entity.Category> categoryMap = 
            categories.stream()
                .collect(java.util.stream.Collectors.toMap(
                    io.github.jacorycyjin.smartlibrary.backend.entity.Category::getCategoryId,
                    c -> c
                ));
        
        // 统计每个分类的资源数量
        Map<String, Long> resourceCountMap = new HashMap<>();
        for (io.github.jacorycyjin.smartlibrary.backend.entity.Category category : categories) {
            Long count = categoryMapper.countResourcesByCategoryId(category.getCategoryId());
            resourceCountMap.put(category.getCategoryId(), count);
        }
        
        // 构建树形结构
        List<Map<String, Object>> tree = new java.util.ArrayList<>();
        for (io.github.jacorycyjin.smartlibrary.backend.entity.Category category : categories) {
            if (category.getParentId() == null || category.getParentId().isEmpty()) {
                // 一级分类
                Map<String, Object> node = buildCategoryNode(category, categoryMap, resourceCountMap, categories);
                tree.add(node);
            }
        }
        
        return tree;
    }
    
    /**
     * 构建分类节点（递归）
     */
    private Map<String, Object> buildCategoryNode(
            io.github.jacorycyjin.smartlibrary.backend.entity.Category category,
            Map<String, io.github.jacorycyjin.smartlibrary.backend.entity.Category> categoryMap,
            Map<String, Long> resourceCountMap,
            List<io.github.jacorycyjin.smartlibrary.backend.entity.Category> allCategories) {
        
        Map<String, Object> node = new HashMap<>();
        node.put("categoryId", category.getCategoryId());
        node.put("categoryName", category.getName());
        node.put("parentId", category.getParentId());
        node.put("level", category.getLevel());
        node.put("sortOrder", category.getSortOrder());
        
        // 当前分类的资源数量
        Long resourceCount = resourceCountMap.getOrDefault(category.getCategoryId(), 0L);
        node.put("resourceCount", resourceCount);
        
        // 查找子分类
        List<Map<String, Object>> children = new java.util.ArrayList<>();
        for (io.github.jacorycyjin.smartlibrary.backend.entity.Category child : allCategories) {
            if (category.getCategoryId().equals(child.getParentId())) {
                Map<String, Object> childNode = buildCategoryNode(child, categoryMap, resourceCountMap, allCategories);
                children.add(childNode);
                // 累加子分类的资源数量
                resourceCount += (Long) childNode.get("resourceCount");
            }
        }
        
        if (!children.isEmpty()) {
            node.put("children", children);
        }
        
        // 更新总资源数量（包含子分类）
        node.put("resourceCount", resourceCount);
        
        return node;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createCategory(io.github.jacorycyjin.smartlibrary.backend.form.CategoryForm form) {
        // 生成分类ID
        String categoryId = UUIDUtil.generateUUID();

        // 构建分类实体
        io.github.jacorycyjin.smartlibrary.backend.entity.Category category = 
            io.github.jacorycyjin.smartlibrary.backend.entity.Category.builder()
                .categoryId(categoryId)
                .parentId(form.getParentId())
                .name(form.getName())
                .level(form.getLevel())
                .sortOrder(form.getSortOrder() != null ? form.getSortOrder() : 0)
                .deleted(0)
                .build();

        // 插入分类
        categoryMapper.insert(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(io.github.jacorycyjin.smartlibrary.backend.form.CategoryForm form) {
        io.github.jacorycyjin.smartlibrary.backend.entity.Category category = 
            categoryMapper.findByCategoryId(form.getCategoryId());
        if (category == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "分类不存在");
        }

        // 更新基本信息
        category.setName(form.getName());
        category.setParentId(form.getParentId());
        category.setLevel(form.getLevel());
        category.setSortOrder(form.getSortOrder());

        categoryMapper.update(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(String categoryId) {
        io.github.jacorycyjin.smartlibrary.backend.entity.Category category = 
            categoryMapper.findByCategoryId(categoryId);
        if (category == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "分类不存在");
        }

        // 检查是否有子分类
        Map<String, Object> params = new HashMap<>();
        params.put("parentId", categoryId);
        params.put("deleted", 0);
        List<io.github.jacorycyjin.smartlibrary.backend.entity.Category> children = 
            categoryMapper.searchCategories(params);
        if (!children.isEmpty()) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "该分类下有子分类，无法删除");
        }

        // 检查是否有关联资源
        Long resourceCount = categoryMapper.countResourcesByCategoryId(categoryId);
        if (resourceCount > 0) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), 
                "该分类下有 " + resourceCount + " 个资源，无法删除");
        }

        // 软删除
        category.setDeleted(1);
        categoryMapper.update(category);
    }
}
