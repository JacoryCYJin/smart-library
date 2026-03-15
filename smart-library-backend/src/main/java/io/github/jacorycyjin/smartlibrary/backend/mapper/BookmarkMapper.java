package io.github.jacorycyjin.smartlibrary.backend.mapper;

import io.github.jacorycyjin.smartlibrary.backend.entity.Bookmark;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 书签 Mapper 接口
 * 
 * @author Jacory
 * @date 2025/03/14
 */
@Mapper
public interface BookmarkMapper {

    /**
     * 随机获取一个上架状态的书签（排除用户已深度交互的资源）
     * 
     * 过滤规则：
     * - 排除已收藏的资源（用户明确喜欢）
     * - 排除已评论的资源（用户已深度交互）
     * - 不排除已浏览的资源（允许重新发现）
     * 
     * @param userId 用户ID（可为空，用于排除已交互资源）
     * @return 随机书签
     */
    Bookmark selectRandomBookmark(@Param("userId") String userId);

    /**
     * 根据书签ID查询书签详情
     * 
     * @param bookmarkId 书签ID
     * @return 书签实体
     */
    Bookmark selectByBookmarkId(@Param("bookmarkId") String bookmarkId);

    /**
     * 增加书签点击次数
     * 
     * @param bookmarkId 书签ID
     * @return 影响行数
     */
    int incrementClickCount(@Param("bookmarkId") String bookmarkId);

    /**
     * 查询所有上架书签列表（管理后台用）
     * 
     * @return 书签列表
     */
    List<Bookmark> selectAllActive();
}
