package io.github.jacorycyjin.smartlibrary.backend.mapper;

import io.github.jacorycyjin.smartlibrary.backend.entity.UserBrowseHistory;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 用户浏览历史 Mapper
 * 
 * @author Jacory
 * @date 2025/01/20
 */
@Mapper
public interface UserBrowseHistoryMapper {

    /**
     * 根据用户ID查询浏览历史
     * 
     * @param userId 用户ID
     * @param limit 查询数量
     * @param offset 偏移量
     * @return 浏览历史列表
     */
    List<UserBrowseHistory> selectByUserId(
            @Param("userId") String userId,
            @Param("limit") Integer limit,
            @Param("offset") Integer offset);

    /**
     * 统计用户浏览历史数量
     * 
     * @param userId 用户ID
     * @return 浏览历史数量
     */
    Integer countByUserId(@Param("userId") String userId);

    /**
     * 记录或更新浏览历史
     * 如果记录已存在，则更新 view_count 和 mtime
     * 如果记录不存在，则插入新记录
     * 
     * @param userId 用户ID
     * @param resourceId 资源ID
     * @return 影响行数
     */
    int insertOrUpdate(@Param("userId") String userId, @Param("resourceId") String resourceId);

    /**
     * 获取用户浏览过的资源的分类ID列表（去重）
     * 
     * @param userId 用户ID
     * @param limit 最多返回多少个分类
     * @return 分类ID列表
     */
    List<String> selectCategoryIdsByUserId(@Param("userId") String userId, @Param("limit") Integer limit);
}
