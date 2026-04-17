package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import io.github.jacorycyjin.smartlibrary.backend.entity.Author;
import io.github.jacorycyjin.smartlibrary.backend.entity.Resource;
import io.github.jacorycyjin.smartlibrary.backend.form.GlobalSearchForm;
import io.github.jacorycyjin.smartlibrary.backend.mapper.AuthorMapper;
import io.github.jacorycyjin.smartlibrary.backend.mapper.ResourceMapper;
import io.github.jacorycyjin.smartlibrary.backend.service.SearchService;
import io.github.jacorycyjin.smartlibrary.backend.vo.AuthorPublicVO;
import io.github.jacorycyjin.smartlibrary.backend.vo.GlobalSearchResultVO;
import io.github.jacorycyjin.smartlibrary.backend.vo.ResourcePublicVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 全局搜索服务实现
 * 
 * @author Jacory
 * @date 2025/01/28
 */
@Service
public class SearchServiceImpl implements SearchService {

    @jakarta.annotation.Resource
    private ResourceMapper resourceMapper;

    @jakarta.annotation.Resource
    private AuthorMapper authorMapper;

    @Override
    public GlobalSearchResultVO globalSearch(GlobalSearchForm searchForm) {
        String keyword = searchForm.getKeyword();
        
        // 图书分页参数
        Integer bookPageNum = searchForm.getBookPageNum() != null ? searchForm.getBookPageNum() : 1;
        Integer bookPageSize = searchForm.getBookPageSize() != null ? searchForm.getBookPageSize() : 20;
        int bookOffset = (bookPageNum - 1) * bookPageSize;
        
        // 作者分页参数
        Integer authorPageNum = searchForm.getAuthorPageNum() != null ? searchForm.getAuthorPageNum() : 1;
        Integer authorPageSize = searchForm.getAuthorPageSize() != null ? searchForm.getAuthorPageSize() : 20;
        int authorOffset = (authorPageNum - 1) * authorPageSize;

        // 搜索图书（type=1）- 如果 pageSize <= 0 则跳过查询
        List<ResourcePublicVO> books = new ArrayList<>();
        if (bookPageSize > 0) {
            List<Resource> bookResources = resourceMapper.searchResourcesByKeyword(keyword, 1, bookOffset, bookPageSize);
            for (Resource resource : bookResources) {
                books.add(ResourcePublicVO.builder()
                        .resourceId(resource.getResourceId())
                        .type(resource.getType())
                        .title(resource.getTitle())
                        .authorName(resource.getAuthorName())
                        .coverUrl(resource.getCoverUrl())
                        .publisher(resource.getPublisher())
                        .sourceScore(resource.getSourceScore())
                        .userScore(resource.getUserScore())
                        .viewCount(resource.getViewCount())
                        .build());
            }
        }

        // 搜索作者 - 如果 pageSize <= 0 则跳过查询
        List<AuthorPublicVO> authorVOs = new ArrayList<>();
        if (authorPageSize > 0) {
            List<Author> authors = authorMapper.searchAuthorsByKeyword(keyword, authorOffset, authorPageSize);
            for (Author author : authors) {
                authorVOs.add(AuthorPublicVO.builder()
                        .authorId(author.getAuthorId())
                        .name(author.getName())
                        .originalName(author.getOriginalName())
                        .country(author.getCountry())
                        .photoUrl(author.getPhotoUrl())
                        .description(author.getDescription())
                        .build());
            }
        }

        // 统计总数（用于分页）
        Map<String, Object> bookParams = new HashMap<>();
        bookParams.put("keyword", keyword);
        bookParams.put("type", 1);
        Long bookTotal = resourceMapper.countByParams(bookParams);

        Map<String, Object> authorParams = new HashMap<>();
        authorParams.put("keyword", keyword);
        int authorTotal = authorMapper.countAuthors(authorParams);

        return new GlobalSearchResultVO(books, authorVOs, bookTotal, (long) authorTotal);
    }
}
