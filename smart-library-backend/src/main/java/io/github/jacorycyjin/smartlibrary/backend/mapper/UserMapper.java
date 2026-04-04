package io.github.jacorycyjin.smartlibrary.backend.mapper;

import io.github.jacorycyjin.smartlibrary.backend.entity.User;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;
/**
 * @author Jacory
 * @date 2025/12/11
 */
@Mapper
public interface UserMapper {

    /**
     * 查询用户
     * 
     * @param params
     * @return 用户列表
     */
    List<User> searchUser(Map<String, Object> params);

    /**
     * 插入用户
     * 
     * @param user
     * @return 是否插入成功
     */
    int insertUser(User user);

    /**
     * 根据用户ID列表批量查询用户
     * 
     * @param userIds 用户业务ID列表
     * @return 用户列表
     */
    List<User> selectByUserIds(@Param("userIds") List<String> userIds);

    /**
     * 更新用户信息
     * 
     * @param params 更新参数
     * @return 影响行数
     */
    int updateUser(Map<String, Object> params);

    /**
     * 根据用户ID查询用户
     * 
     * @param userId 用户业务ID
     * @return 用户信息
     */
    User findByUserId(@Param("userId") String userId);

    /**
     * 根据用户ID更新用户
     * 
     * @param user 用户信息
     * @return 影响行数
     */
    int updateByUserId(User user);

    /**
     * 统计用户数量
     * 
     * @param params 查询参数
     * @return 用户数量
     */
    Long countByParams(Map<String, Object> params);

    /**
     * 管理员查询用户列表
     * 
     * @param params 查询参数
     * @return 用户列表
     */
    List<User> searchUsers(Map<String, Object> params);
}
