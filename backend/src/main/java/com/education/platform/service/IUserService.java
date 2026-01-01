package com.education.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.education.platform.entity.User;

import java.util.Map;

/**
 * 用户服务接口
 *
 * @author Education Platform Team
 */
public interface IUserService extends IService<User> {

    /**
     * 用户登录
     */
    Map<String, Object> login(String username, String password);

    /**
     * 用户注册（默认普通用户角色）
     */
    Boolean register(User user);

    /**
     * 用户注册并指定角色
     */
    Boolean registerWithRole(User user, String roleCode);

    /**
     * 教师注册（自动分配TEACHER角色）
     */
    Boolean registerTeacher(User user);

    /**
     * 为用户分配角色
     */
    Boolean assignRole(Long userId, String roleCode);

    /**
     * 根据用户名查询用户
     */
    User getByUsername(String username);

    /**
     * 修改密码
     */
    Boolean changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 获取用户信息（包含角色和权限）
     */
    Map<String, Object> getUserInfo(Long userId);
}
