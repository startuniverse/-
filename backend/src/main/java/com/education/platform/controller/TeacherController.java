package com.education.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.platform.common.ApiResult;
import com.education.platform.common.PageResult;
import com.education.platform.entity.Assignment;
import com.education.platform.entity.User;
import com.education.platform.mapper.AssignmentMapper;
import com.education.platform.mapper.UserMapper;
import com.education.platform.util.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教师控制器
 *
 * @author Education Platform Team
 */
@RestController
@RequestMapping("/teacher")
@Tag(name = "教师管理", description = "教师相关接口")
public class TeacherController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AssignmentMapper assignmentMapper;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 从token中获取用户ID的辅助方法
     */
    private Long getUserIdFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        try {
            String jwt = token.substring(7);
            String username = jwtUtils.getUsernameFromToken(jwt);
            User user = userMapper.selectByUsername(username);
            if (user != null) {
                return user.getId();
            }
        } catch (Exception e) {
            System.out.println("=== 从token获取用户ID失败: " + e.getMessage());
        }
        return null;
    }

    /**
     * 发布作业
     */
    @PostMapping("/assignments")
    @Operation(summary = "发布作业", description = "教师发布作业")
    public ApiResult<Map<String, Object>> publishAssignment(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody Map<String, Object> assignmentData) {
        try {
            // 从token中获取当前用户
            Long teacherId = getUserIdFromToken(token);
            if (teacherId == null) {
                return ApiResult.error("未登录或token无效");
            }

            // 获取用户信息（用于获取学校ID）
            User teacher = userMapper.selectById(teacherId);
            if (teacher == null) {
                return ApiResult.error("教师用户不存在");
            }

            // 创建作业对象
            Assignment assignment = new Assignment();

            // 设置基本字段
            Object titleObj = assignmentData.get("title");
            if (titleObj == null || titleObj.toString().trim().isEmpty()) {
                return ApiResult.error("作业标题不能为空");
            }
            assignment.setTitle(titleObj.toString());

            Object subjectObj = assignmentData.get("subject");
            if (subjectObj == null || subjectObj.toString().trim().isEmpty()) {
                return ApiResult.error("科目不能为空");
            }
            assignment.setSubject(subjectObj.toString());

            Object contentObj = assignmentData.get("content");
            if (contentObj == null || contentObj.toString().trim().isEmpty()) {
                return ApiResult.error("作业内容不能为空");
            }
            assignment.setContent(contentObj.toString());

            // 处理班级列表（转换为JSON字符串）
            Object targetClassesObj = assignmentData.get("targetClasses");
            if (targetClassesObj != null) {
                // 如果是List，转换为JSON字符串
                if (targetClassesObj instanceof List) {
                    List<?> classes = (List<?>) targetClassesObj;
                    assignment.setTargetClasses(classes.toString());
                } else {
                    assignment.setTargetClasses(targetClassesObj.toString());
                }
            }

            // 截止时间
            Object deadlineObj = assignmentData.get("deadline");
            if (deadlineObj != null) {
                try {
                    LocalDateTime deadline = LocalDateTime.parse(deadlineObj.toString().replace(" ", "T"));
                    assignment.setDeadline(deadline);
                } catch (Exception e) {
                    // 尝试其他格式
                    assignment.setDeadline(LocalDateTime.now().plusDays(7));
                }
            }

            // 难度等级
            Object difficultyObj = assignmentData.get("difficulty");
            assignment.setDifficulty(difficultyObj != null ? difficultyObj.toString() : "medium");

            // 是否计分
            Object isGradedObj = assignmentData.get("isGraded");
            assignment.setIsGraded(isGradedObj != null ? Boolean.parseBoolean(isGradedObj.toString()) : true);

            // 补充说明
            Object notesObj = assignmentData.get("notes");
            if (notesObj != null) {
                assignment.setNotes(notesObj.toString());
            }

            // 设置教师ID和学校ID
            assignment.setTeacherId(teacherId);
            assignment.setSchoolId(teacher.getSchoolId());

            // 设置状态和发布时间
            assignment.setStatus("进行中");
            assignment.setPublishTime(LocalDateTime.now());

            // 保存到数据库
            int result = assignmentMapper.insert(assignment);

            if (result > 0) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("id", assignment.getId());
                responseData.put("title", assignment.getTitle());
                responseData.put("publishTime", assignment.getPublishTime());

                return ApiResult.success("作业发布成功", responseData);
            } else {
                return ApiResult.error("作业发布失败");
            }
        } catch (Exception e) {
            System.err.println("作业发布失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResult.error("作业发布失败: " + e.getMessage());
        }
    }

    /**
     * 获取作业列表
     */
    @GetMapping("/assignments")
    @Operation(summary = "获取作业列表", description = "获取教师发布的作业列表")
    public ApiResult<PageResult<Assignment>> getAssignments(
            @RequestHeader(value = "Authorization", required = false) String token,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long size) {
        try {
            // 从token中获取当前用户
            Long teacherId = getUserIdFromToken(token);
            if (teacherId == null) {
                return ApiResult.error("未登录或token无效");
            }

            Page<Assignment> page = new Page<>(current, size);
            LambdaQueryWrapper<Assignment> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Assignment::getTeacherId, teacherId);
            wrapper.orderByDesc(Assignment::getPublishTime);

            Page<Assignment> result = assignmentMapper.selectPage(page, wrapper);
            return ApiResult.success(PageResult.of(result));
        } catch (Exception e) {
            System.err.println("获取作业列表失败: " + e.getMessage());
            return ApiResult.error("获取作业列表失败: " + e.getMessage());
        }
    }

    /**
     * 删除作业
     */
    @DeleteMapping("/assignments/{id}")
    @Operation(summary = "删除作业", description = "删除指定作业")
    public ApiResult<Boolean> deleteAssignment(
            @RequestHeader(value = "Authorization", required = false) String token,
            @Parameter(description = "作业ID") @PathVariable Long id) {
        try {
            // 从token中获取当前用户
            Long teacherId = getUserIdFromToken(token);
            if (teacherId == null) {
                return ApiResult.error("未登录或token无效");
            }

            // 验证作业是否存在且属于当前教师
            Assignment assignment = assignmentMapper.selectById(id);
            if (assignment == null) {
                return ApiResult.error("作业不存在");
            }

            if (!assignment.getTeacherId().equals(teacherId)) {
                return ApiResult.error("无权删除该作业");
            }

            int result = assignmentMapper.deleteById(id);
            return ApiResult.success(result > 0);
        } catch (Exception e) {
            System.err.println("删除作业失败: " + e.getMessage());
            return ApiResult.error("删除作业失败: " + e.getMessage());
        }
    }

    /**
     * 教师仪表盘（简化版）
     */
    @GetMapping("/dashboard")
    @Operation(summary = "教师仪表盘", description = "获取教师仪表盘数据")
    public ApiResult<Map<String, Object>> getDashboard(
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long teacherId = getUserIdFromToken(token);
            if (teacherId == null) {
                return ApiResult.error("未登录或token无效");
            }

            // 统计作业数量
            LambdaQueryWrapper<Assignment> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Assignment::getTeacherId, teacherId);
            long totalAssignments = assignmentMapper.selectCount(wrapper);

            // 统计进行中的作业
            LambdaQueryWrapper<Assignment> activeWrapper = new LambdaQueryWrapper<>();
            activeWrapper.eq(Assignment::getTeacherId, teacherId);
            activeWrapper.eq(Assignment::getStatus, "进行中");
            long activeAssignments = assignmentMapper.selectCount(activeWrapper);

            Map<String, Object> result = new HashMap<>();
            result.put("totalAssignments", totalAssignments);
            result.put("activeAssignments", activeAssignments);
            result.put("studentCount", 45); // 模拟数据
            result.put("classCount", 3); // 模拟数据

            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("获取仪表盘数据失败: " + e.getMessage());
        }
    }
}
