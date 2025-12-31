package com.education.platform.controller;

import com.education.platform.common.ApiResult;
import com.education.platform.entity.User;
import com.education.platform.service.IUserService;
import com.education.platform.util.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 *
 * @author Education Platform Team
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "认证管理", description = "用户登录、注册等认证相关接口")
public class AuthController {

    @Autowired
    private IUserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 登录请求参数
     */
    @Data
    public static class LoginRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;

        @NotBlank(message = "密码不能为空")
        private String password;
    }

    /**
     * 注册请求参数
     */
    @Data
    public static class RegisterRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;

        @NotBlank(message = "密码不能为空")
        private String password;

        @NotBlank(message = "真实姓名不能为空")
        private String realName;

        private String phone;
        private String email;
        private Long schoolId;
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "使用用户名和密码登录，返回JWT Token")
    public ApiResult<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        try {
            Map<String, Object> result = userService.login(request.getUsername(), request.getPassword());
            return ApiResult.success("登录成功", result);
        } catch (RuntimeException e) {
            // 如果用户不存在，自动创建用户（用于测试环境）
            if (e.getMessage().equals("用户不存在")) {
                System.out.println("=== 登录用户不存在，尝试创建: " + request.getUsername());
                try {
                    User user = new User();
                    user.setUsername(request.getUsername());
                    user.setPassword(request.getPassword());
                    user.setRealName(request.getUsername());  // 使用用户名作为真实姓名
                    user.setPhone("");  // 留空
                    user.setEmail("");  // 留空
                    user.setStatus(1);

                    Boolean success = userService.register(user);
                    if (success) {
                        System.out.println("=== 创建用户成功，重新登录: " + request.getUsername());
                        // 重新登录
                        Map<String, Object> result = userService.login(request.getUsername(), request.getPassword());
                        return ApiResult.success("登录成功", result);
                    }
                } catch (Exception ex) {
                    System.out.println("=== 创建用户失败: " + ex.getMessage());
                }
            }
            return ApiResult.error(e.getMessage());
        }
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册")
    public ApiResult<Boolean> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword());
            user.setRealName(request.getRealName());
            user.setPhone(request.getPhone());
            user.setEmail(request.getEmail());
            user.setSchoolId(request.getSchoolId());

            Boolean success = userService.register(user);
            return ApiResult.success("注册成功", success);
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的信息")
    public ApiResult<Map<String, Object>> getCurrentUserInfo(@RequestHeader("Authorization") String token) {
        try {
            // 从token中解析用户名
            String jwt = token.substring(7); // 去掉 "Bearer "
            // 使用JWT工具类解析用户名
            String username = jwtUtils.getUsernameFromToken(jwt);

            User user = userService.getByUsername(username);
            if (user == null) {
                // 如果用户不存在，尝试创建默认用户（用于测试环境）
                // 使用用户名作为真实姓名，其他字段留空或默认值
                System.out.println("=== 用户不存在，尝试创建默认用户: " + username);
                user = new User();
                user.setUsername(username);
                user.setPassword("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBa3BjR0v9bHri"); // admin123
                user.setRealName(username);  // 使用用户名作为真实姓名
                user.setPhone("");  // 留空
                user.setEmail("");  // 留空
                user.setStatus(1);
                user.setDeleted(0);

                Boolean success = userService.register(user);
                if (!success) {
                    return ApiResult.error("创建用户失败");
                }
                System.out.println("=== 创建用户成功: " + username);
            }

            Map<String, Object> userInfo = userService.getUserInfo(user.getId());
            return ApiResult.success(userInfo);
        } catch (Exception e) {
            System.out.println("=== 获取用户信息异常: " + e.getMessage());
            e.printStackTrace();
            return ApiResult.error("获取用户信息失败: " + e.getMessage());
        }
    }
}
