package io.github.jacorycyjin.smartlibrary.backend.mapper;

import io.github.jacorycyjin.smartlibrary.backend.entity.RecommendResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 推荐结果 Mapper
 * 
 * @author Jacory
 * @date 2025/04/11
 */
@Mapper
public interface RecommendResultMapper {

    /**
     * 根据用户ID查询推荐列表
     * 
     * @param userId 用户ID
     * @param limit 返回数量
     * @return 推荐结果列表
     */
    List<RecommendResult> selectByUserId(@Param("userId") String userId, @Param("limit") Integer limit);

    /**
     * 查询推荐覆盖的用户数
     * 
     * @return 用户数
     */
    Integer countUsersWithRecommendations();
}
