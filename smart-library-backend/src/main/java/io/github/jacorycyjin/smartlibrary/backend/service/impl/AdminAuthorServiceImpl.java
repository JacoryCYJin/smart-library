package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageDTO;
import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;
import io.github.jacorycyjin.smartlibrary.backend.common.exception.BusinessException;
import io.github.jacorycyjin.smartlibrary.backend.common.util.UUIDUtil;
import io.github.jacorycyjin.smartlibrary.backend.entity.Author;
import io.github.jacorycyjin.smartlibrary.backend.form.AuthorForm;
import io.github.jacorycyjin.smartlibrary.backend.mapper.AuthorMapper;
import io.github.jacorycyjin.smartlibrary.backend.service.AdminAuthorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员作者管理服务实现
 * 
 * @author Kiro
 * @date 2026/04/05
 */
@Service
public class AdminAuthorServiceImpl implements AdminAuthorService {

    @jakarta.annotation.Resource
    private AuthorMapper authorMapper;

    @Override
    public PageDTO<Map<String, Object>> getAuthorList(Map<String, Object> params) {
        int total = authorMapper.countAuthors(params);
        List<Author> authors = authorMapper.searchAuthors(params);

        // 查询每个作者的作品数量
        List<Map<String, Object>> authorList = new ArrayList<>();
        for (Author author : authors) {
            Map<String, Object> authorMap = new HashMap<>();
            authorMap.put("authorId", author.getAuthorId());
            authorMap.put("name", author.getName());
            authorMap.put("originalName", author.getOriginalName());
            authorMap.put("country", author.getCountry());
            authorMap.put("photoUrl", author.getPhotoUrl());
            authorMap.put("description", author.getDescription());
            authorMap.put("sourceOrigin", author.getSourceOrigin());
            authorMap.put("ctime", author.getCtime());
            
            // 查询作品数量
            int resourceCount = authorMapper.countResourcesByAuthorId(author.getAuthorId());
            authorMap.put("resourceCount", resourceCount);
            
            authorList.add(authorMap);
        }

        PageDTO<Map<String, Object>> result = new PageDTO<>();
        result.setTotalCount(total);
        result.setList(authorList);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createAuthor(AuthorForm form) {
        Author author = Author.builder()
                .authorId(UUIDUtil.generateUUID())
                .name(form.getName())
                .originalName(form.getOriginalName())
                .country(form.getCountry())
                .photoUrl(form.getPhotoUrl())
                .description(form.getDescription())
                .sourceOrigin(form.getSourceOrigin() != null ? form.getSourceOrigin() : 99)
                .sourceUrl(form.getSourceUrl())
                .build();

        authorMapper.insert(author);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAuthor(AuthorForm form) {
        Author author = authorMapper.selectAuthorById(form.getAuthorId());
        if (author == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "作者不存在");
        }

        author.setName(form.getName());
        author.setOriginalName(form.getOriginalName());
        author.setCountry(form.getCountry());
        author.setPhotoUrl(form.getPhotoUrl());
        author.setDescription(form.getDescription());
        author.setSourceOrigin(form.getSourceOrigin());
        author.setSourceUrl(form.getSourceUrl());

        authorMapper.update(author);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAuthor(String authorId) {
        Author author = authorMapper.selectAuthorById(authorId);
        if (author == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "作者不存在");
        }

        // 检查是否有关联资源
        int resourceCount = authorMapper.countResourcesByAuthorId(authorId);
        if (resourceCount > 0) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), 
                "该作者有 " + resourceCount + " 个关联资源，无法删除");
        }

        // 软删除
        author.setDeleted(1);
        authorMapper.update(author);
    }

    @Override
    public Map<String, Object> getAuthorDetail(String authorId) {
        Author author = authorMapper.selectAuthorById(authorId);
        if (author == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "作者不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("authorId", author.getAuthorId());
        result.put("name", author.getName());
        result.put("originalName", author.getOriginalName());
        result.put("country", author.getCountry());
        result.put("photoUrl", author.getPhotoUrl());
        result.put("description", author.getDescription());
        result.put("sourceOrigin", author.getSourceOrigin());
        result.put("sourceUrl", author.getSourceUrl());

        return result;
    }
}
