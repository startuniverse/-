package com.education.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.platform.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 用户Mapper接口
 *
 * @author Education Platform Team
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     */
    User selectByUsername(@Param("username") String username);

    /**
     * 根据用户ID查询角色列表
     */
    List<String> selectRolesByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询权限列表
     */
    Set<String> selectPermissionsByUserId(@Param("userId") Long userId);

    /**
     * 根据学校ID查询用户列表
     */
    List<User> selectBySchoolId(@Param("schoolId") Long schoolId);
}
