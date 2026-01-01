package com.education.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.platform.common.ApiResult;
import com.education.platform.common.PageResult;
import com.education.platform.entity.Announcement;
import com.education.platform.entity.Grade;
import com.education.platform.entity.School;
import com.education.platform.entity.Student;
import com.education.platform.entity.Timetable;
import com.education.platform.entity.User;
import com.education.platform.mapper.AnnouncementMapper;
import com.education.platform.mapper.ClassMapper;
import com.education.platform.mapper.GradeMapper;
import com.education.platform.mapper.SchoolMapper;
import com.education.platform.mapper.StudentMapper;
import com.education.platform.mapper.TeacherMapper;
import com.education.platform.mapper.TimetableMapper;
import com.education.platform.mapper.UserMapper;
import com.education.platform.util.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.access.prepost.PreAuthorize; // 已禁用权限控制
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 校园门户控制器
 *
 * @author Education Platform Team
 */
@RestController
@RequestMapping("/campus")
@Tag(name = "校园门户", description = "校园门户相关接口")
public class CampusPortalController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private ClassMapper classMapper;

    @Autowired
    private TimetableMapper timetableMapper;

    @Autowired
    private GradeMapper gradeMapper;

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Autowired
    private SchoolMapper schoolMapper;

    /**
     * 从token中获取用户ID的辅助方法
     */
    private Long getUserIdFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        try {
            String jwt = token.substring(7); // 去掉 "Bearer "
            String username = jwtUtils.getUsernameFromToken(jwt);

            // 通过用户名查询用户
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
     * 3.1.1 获取仪表盘信息
     */
    @GetMapping("/dashboard")
    @Operation(summary = "获取仪表盘信息", description = "获取用户的仪表盘信息，包括通知、快捷链接等")
    public ApiResult<Map<String, Object>> getDashboard(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // 从token中获取当前用户
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return ApiResult.error("未登录或token无效");
            }

            // 获取用户信息
            User user = userMapper.selectById(userId);
            if (user == null) {
                return ApiResult.error("用户不存在");
            }

            Map<String, Object> dashboard = new HashMap<>();

            // 获取用户角色
            List<String> roles = userMapper.selectRolesByUserId(userId);
            dashboard.put("roles", roles);
            dashboard.put("username", user.getUsername());
            dashboard.put("realName", user.getRealName());

            // 获取未读通知数量 - 根据用户学校和班级过滤
            LambdaQueryWrapper<Announcement> announcementWrapper = new LambdaQueryWrapper<>();
            announcementWrapper.eq(Announcement::getStatus, 1);

            // 如果用户有学校ID和班级ID，只显示相关的通知
            if (user.getSchoolId() != null) {
                announcementWrapper.and(q -> q
                    .eq(Announcement::getSchoolId, user.getSchoolId())
                    .or()
                    .eq(Announcement::getClassId, user.getClassId())
                    .or()
                    .isNull(Announcement::getClassId)
                );
            }

            long unreadCount = announcementMapper.selectCount(announcementWrapper);
            dashboard.put("unreadCount", unreadCount);

            // 根据用户角色生成个性化统计数据
            // 模拟基于用户ID生成不同的数据
            int homeworkCompleted = 70 + (int)(userId % 20); // 70-89
            int rewardsEarned = 8 + (int)(userId % 8); // 8-15
            int pendingTodos = 2 + (int)(userId % 5); // 2-6

            dashboard.put("homeworkCompleted", homeworkCompleted);
            dashboard.put("rewardsEarned", rewardsEarned);
            dashboard.put("pendingTodos", pendingTodos);

            // 获取快捷链接
            List<Map<String, String>> quickLinks = List.of(
                Map.of("name", "个人信息", "path", "/profile"),
                Map.of("name", "课程表", "path", "/timetable"),
                Map.of("name", "成绩查询", "path", "/grades"),
                Map.of("name", "通知公告", "path", "/announcements")
            );
            dashboard.put("quickLinks", quickLinks);

            // 生成个性化通知列表
            List<Map<String, String>> recentNotifications = new ArrayList<>();
            String[] notificationTemplates = {
                "关于近期教学安排的通知",
                "本周作业提醒",
                "考试时间安排",
                "班级活动通知",
                "家长会安排"
            };

            for (int i = 0; i < 3; i++) {
                Map<String, String> notification = new HashMap<>();
                int index = (int)((userId + i) % notificationTemplates.length);
                notification.put("type", "通知");
                notification.put("title", notificationTemplates[index] + " " + (i + 1));
                notification.put("time", (2 + i) + "小时前");
                recentNotifications.add(notification);
            }
            dashboard.put("recentNotifications", recentNotifications);

            return ApiResult.success(dashboard);
        } catch (Exception e) {
            return ApiResult.error("获取仪表盘信息失败: " + e.getMessage());
        }
    }

    /**
     * 3.1.2 获取个人信息
     */
    @GetMapping("/profile")
    @Operation(summary = "获取个人信息", description = "获取当前用户的基本信息")
    public ApiResult<Map<String, Object>> getProfile(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // 从token中获取当前用户
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return ApiResult.error("未登录或token无效");
            }

            User user = userMapper.selectById(userId);
            if (user == null) {
                // 如果用户不存在，返回空数据，让前端处理
                Map<String, Object> emptyProfile = new HashMap<>();
                emptyProfile.put("id", userId);
                emptyProfile.put("username", "");
                emptyProfile.put("realName", "");
                emptyProfile.put("phone", "");
                emptyProfile.put("email", "");
                emptyProfile.put("avatar", "");
                emptyProfile.put("schoolId", null);
                emptyProfile.put("department", "");
                emptyProfile.put("title", "");
                return ApiResult.success(emptyProfile);
            }

            Map<String, Object> profile = new HashMap<>();
            profile.put("id", user.getId());
            profile.put("username", user.getUsername());
            profile.put("realName", user.getRealName());
            profile.put("phone", user.getPhone());
            profile.put("email", user.getEmail());
            profile.put("avatar", user.getAvatar());
            profile.put("schoolId", user.getSchoolId());
            profile.put("department", user.getDepartment());
            profile.put("title", user.getTitle());

            return ApiResult.success(profile);
        } catch (Exception e) {
            return ApiResult.error("获取个人信息失败: " + e.getMessage());
        }
    }

    /**
     * 3.1.2.1 更新个人信息
     */
    @PostMapping("/profile/update")
    @Operation(summary = "更新个人信息", description = "更新当前用户的基本信息")
    public ApiResult<String> updateProfile(@RequestHeader(value = "Authorization", required = false) String token, @RequestBody Map<String, Object> profileData) {
        // 使用日志框架而不是System.out
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CampusPortalController.class);

        try {
            logger.info("=== 收到更新请求数据: {}", profileData);
            System.out.println("=== 收到更新请求数据: " + profileData);

            // 从token中获取当前用户
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return ApiResult.error("未登录或token无效");
            }
            System.out.println("=== 当前用户ID: " + userId);

            // 先查询用户是否存在
            User existingUser = userMapper.selectById(userId);
            logger.info("=== 数据库查询结果: {}", existingUser);
            System.out.println("=== 数据库查询结果: " + existingUser);

            User user;
            if (existingUser == null) {
                logger.warn("=== 用户ID={}不存在，无法更新", userId);
                System.out.println("=== 用户不存在，无法更新");
                return ApiResult.error("用户不存在，请先登录注册");
            } else {
                // 使用现有用户，只更新ID用于后续操作
                user = new User();
                user.setId(userId);
            }

            // 更新用户信息 - 只设置需要更新的字段
            Object realNameObj = profileData.get("realName");
            if (realNameObj != null) {
                String realName = String.valueOf(realNameObj);
                user.setRealName(realName);
                logger.info("=== 设置 realName: {}", realName);
                System.out.println("=== 设置 realName: " + realName);
            }

            Object phoneObj = profileData.get("phone");
            if (phoneObj != null) {
                String phone = String.valueOf(phoneObj);
                user.setPhone(phone);
                logger.info("=== 设置 phone: {}", phone);
                System.out.println("=== 设置 phone: " + phone);
            }

            Object emailObj = profileData.get("email");
            if (emailObj != null) {
                String email = String.valueOf(emailObj);
                user.setEmail(email);
                logger.info("=== 设置 email: {}", email);
                System.out.println("=== 设置 email: " + email);
            }

            Object departmentObj = profileData.get("department");
            if (departmentObj != null) {
                String department = String.valueOf(departmentObj);
                user.setDepartment(department);
                logger.info("=== 设置 department: {}", department);
                System.out.println("=== 设置 department: " + department);
            }

            Object titleObj = profileData.get("title");
            if (titleObj != null) {
                String title = String.valueOf(titleObj);
                user.setTitle(title);
                logger.info("=== 设置 title: {}", title);
                System.out.println("=== 设置 title: " + title);
            }

            // 处理学校名称（前端传递的是schoolName，需要转换为schoolId）
            Object schoolNameObj = profileData.get("schoolName");
            if (schoolNameObj != null) {
                String schoolName = String.valueOf(schoolNameObj);
                // 查询学校是否存在，如果不存在则创建
                LambdaQueryWrapper<School> schoolWrapper = new LambdaQueryWrapper<>();
                schoolWrapper.eq(School::getSchoolName, schoolName);
                // 使用selectList而不是selectOne，避免多条记录报错
                List<School> schools = schoolMapper.selectList(schoolWrapper);
                School school = null;

                if (schools != null && !schools.isEmpty()) {
                    school = schools.get(0);  // 取第一条记录
                    logger.info("=== 找到学校: {} (ID: {})", schoolName, school.getId());
                    System.out.println("=== 找到学校: " + schoolName + " (ID: " + school.getId() + ")");
                }

                if (school == null) {
                    // 创建新学校
                    school = new School();
                    school.setSchoolName(schoolName);
                    school.setSchoolCode("S" + System.currentTimeMillis());
                    school.setSchoolType("secondary");
                    school.setAddress("待补充");
                    school.setContactPerson("待补充");
                    school.setContactPhone("待补充");
                    school.setStatus(1);
                    schoolMapper.insert(school);
                    logger.info("=== 创建新学校: {} (ID: {})", schoolName, school.getId());
                    System.out.println("=== 创建新学校: " + schoolName + " (ID: " + school.getId() + ")");
                }
                user.setSchoolId(school.getId());
            }

            logger.info("=== 准备更新的用户对象: {}", user);
            System.out.println("=== 准备更新的用户对象: " + user);

            // 使用MyBatis-Plus的updateById方法
            int result = userMapper.updateById(user);

            logger.info("=== updateById 返回值: {}", result);
            System.out.println("=== updateById 返回值: " + result);

            // 验证更新是否成功
            User updatedUser = userMapper.selectById(userId);
            logger.info("=== 更新后重新查询结果: {}", updatedUser);
            System.out.println("=== 更新后重新查询结果: " + updatedUser);

            if (result > 0) {
                logger.info("=== 更新成功");
                System.out.println("=== 更新成功");
                return ApiResult.success("个人信息更新成功");
            } else {
                logger.warn("=== 更新失败，影响行数为0");
                System.out.println("=== 更新失败，影响行数为0");
                return ApiResult.error("更新失败，影响行数为0");
            }
        } catch (Exception e) {
            logger.error("=== 更新异常: {}", e.getMessage(), e);
            System.out.println("=== 更新异常: " + e.getMessage());
            e.printStackTrace();
            return ApiResult.error("更新个人信息失败: " + e.getMessage());
        }
    }

    /**
     * 3.1.2.2 修改密码
     */
    @PostMapping("/profile/change-password")
    @Operation(summary = "修改密码", description = "修改当前用户的密码")
    public ApiResult<String> changePassword(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody Map<String, Object> passwordData) {
        try {
            // 从token中获取当前用户
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return ApiResult.error("未登录或token无效");
            }

            // 获取用户信息
            User user = userMapper.selectById(userId);
            if (user == null) {
                return ApiResult.error("用户不存在");
            }

            // 获取原密码和新密码
            String oldPassword = (String) passwordData.get("oldPassword");
            String newPassword = (String) passwordData.get("newPassword");

            if (oldPassword == null || oldPassword.trim().isEmpty()) {
                return ApiResult.error("原密码不能为空");
            }
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return ApiResult.error("新密码不能为空");
            }
            if (newPassword.length() < 6) {
                return ApiResult.error("新密码长度至少6位");
            }

            // 验证原密码是否正确
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                return ApiResult.error("原密码不正确");
            }

            // 如果新密码与原密码相同，提示错误
            if (passwordEncoder.matches(newPassword, user.getPassword())) {
                return ApiResult.error("新密码不能与原密码相同");
            }

            // 更新密码（加密存储）
            user.setPassword(passwordEncoder.encode(newPassword));
            int result = userMapper.updateById(user);

            if (result > 0) {
                return ApiResult.success("密码修改成功");
            } else {
                return ApiResult.error("密码修改失败");
            }
        } catch (Exception e) {
            System.out.println("=== 修改密码异常: " + e.getMessage());
            e.printStackTrace();
            return ApiResult.error("修改密码失败: " + e.getMessage());
        }
    }

    /**
     * 3.1.3 获取班级信息
     */
    @GetMapping("/class/info")
    @Operation(summary = "获取班级信息", description = "获取班级花名册和基本信息")
    public ApiResult<Map<String, Object>> getClassInfo(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // 从token中获取当前用户
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return ApiResult.error("未登录或token无效");
            }

            // 获取用户信息
            User user = userMapper.selectById(userId);
            if (user == null) {
                return ApiResult.error("用户不存在");
            }

            // 使用用户的班级ID，如果没有则使用默认班级ID
            Long classId = user.getClassId();
            if (classId == null) {
                classId = 1L; // 默认班级
            }

            com.education.platform.entity.Class classInfo = classMapper.selectById(classId);
            if (classInfo == null) {
                // 如果班级不存在，返回空数据而不是错误
                Map<String, Object> result = new HashMap<>();
                result.put("classInfo", null);
                result.put("studentCount", 0);
                result.put("students", List.of());
                return ApiResult.success(result);
            }

            // 获取班级学生列表
            LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Student::getClassId, classId);
            List<Student> students = studentMapper.selectList(wrapper);

            // 为每个学生关联用户信息，获取真实姓名
            List<Map<String, Object>> studentList = new ArrayList<>();
            for (Student student : students) {
                Map<String, Object> studentInfo = new HashMap<>();
                studentInfo.put("id", student.getId());
                studentInfo.put("studentNumber", student.getStudentNumber());
                studentInfo.put("guardianName", student.getGuardianName());
                studentInfo.put("guardianPhone", student.getGuardianPhone());
                studentInfo.put("status", student.getStatus());

                // 通过userId查询用户信息，获取真实姓名
                if (student.getUserId() != null) {
                    User studentUser = userMapper.selectById(student.getUserId());
                    if (studentUser != null) {
                        studentInfo.put("realName", studentUser.getRealName());
                    } else {
                        studentInfo.put("realName", "");
                    }
                } else {
                    studentInfo.put("realName", "");
                }

                studentList.add(studentInfo);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("classInfo", classInfo);
            result.put("studentCount", students.size());
            result.put("students", studentList);

            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("加载班级信息失败: " + e.getMessage());
        }
    }

    /**
     * 3.1.4 获取课程表
     */
    @GetMapping("/timetable")
    @Operation(summary = "获取课程表", description = "获取用户的课程表")
    public ApiResult<List<Timetable>> getTimetable(
            @RequestHeader(value = "Authorization", required = false) String token,
            @Parameter(description = "星期几 (1-7)")
            @RequestParam(required = false) Integer weekDay) {
        try {
            // 从token中获取当前用户
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return ApiResult.error("未登录或token无效");
            }

            // 获取用户信息
            User user = userMapper.selectById(userId);
            if (user == null) {
                return ApiResult.error("用户不存在");
            }

            // 使用用户的班级ID，如果没有则使用默认班级ID
            Long classId = user.getClassId();
            if (classId == null) {
                classId = 1L; // 默认班级
            }

            LambdaQueryWrapper<Timetable> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Timetable::getClassId, classId);
            if (weekDay != null) {
                wrapper.eq(Timetable::getWeekDay, weekDay);
            }
            wrapper.orderByAsc(Timetable::getWeekDay, Timetable::getPeriod);

            List<Timetable> timetables = timetableMapper.selectList(wrapper);
            return ApiResult.success(timetables);
        } catch (Exception e) {
            return ApiResult.error("加载课程表失败: " + e.getMessage());
        }
    }

    /**
     * 3.1.5 成绩查询
     */
    @GetMapping("/grades")
    @Operation(summary = "成绩查询", description = "查询学生成绩")
    public ApiResult<PageResult<Grade>> getGrades(
            @RequestHeader(value = "Authorization", required = false) String token,
            @Parameter(description = "科目")
            @RequestParam(required = false) String subject,
            @Parameter(description = "学期")
            @RequestParam(required = false) String academicTerm,
            @Parameter(description = "当前页")
            @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页条数")
            @RequestParam(defaultValue = "10") Long size) {
        try {
            // 从token中获取当前用户
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return ApiResult.error("未登录或token无效");
            }

            // 获取用户信息
            User user = userMapper.selectById(userId);
            if (user == null) {
                return ApiResult.error("用户不存在");
            }

            // 通过用户ID查找对应的学生ID
            LambdaQueryWrapper<Student> studentWrapper = new LambdaQueryWrapper<>();
            studentWrapper.eq(Student::getUserId, userId);
            Student student = studentMapper.selectOne(studentWrapper);

            if (student == null) {
                // 如果没有对应的学生记录，返回空结果
                Page<Grade> emptyPage = new Page<>(current, size);
                return ApiResult.success(PageResult.of(emptyPage));
            }

            Long studentId = student.getId();

            Page<Grade> page = new Page<>(current, size);
            LambdaQueryWrapper<Grade> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Grade::getStudentId, studentId);
            if (subject != null) {
                wrapper.eq(Grade::getSubject, subject);
            }
            if (academicTerm != null) {
                wrapper.eq(Grade::getAcademicTerm, academicTerm);
            }
            wrapper.orderByDesc(Grade::getExamDate);

            Page<Grade> result = gradeMapper.selectPage(page, wrapper);
            return ApiResult.success(PageResult.of(result));
        } catch (Exception e) {
            return ApiResult.error("加载成绩失败: " + e.getMessage());
        }
    }

    /**
     * 3.1.6 获取通知公告列表
     */
    @GetMapping("/announcements")
    @Operation(summary = "获取通知公告", description = "获取通知公告列表")
    public ApiResult<PageResult<Announcement>> getAnnouncements(
            @RequestHeader(value = "Authorization", required = false) String token,
            @Parameter(description = "类型")
            @RequestParam(required = false) String type,
            @Parameter(description = "当前页")
            @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页条数")
            @RequestParam(defaultValue = "10") Long size) {
        try {
            // 从token中获取当前用户
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return ApiResult.error("未登录或token无效");
            }

            // 获取用户信息
            User user = userMapper.selectById(userId);
            if (user == null) {
                return ApiResult.error("用户不存在");
            }

            // 使用用户的学校ID和班级ID
            Long schoolId = user.getSchoolId();
            Long classId = user.getClassId();

            Page<Announcement> page = new Page<>(current, size);
            LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Announcement::getStatus, 1);

            // 查询学校级别和班级级别的公告
            if (schoolId != null) {
                wrapper.and(q -> q
                    .eq(Announcement::getSchoolId, schoolId)
                    .or()
                    .eq(Announcement::getClassId, classId)
                    .or()
                    .isNull(Announcement::getClassId)
                );
            }

            if (type != null) {
                wrapper.eq(Announcement::getType, type);
            }

            wrapper.orderByDesc(Announcement::getPublishTime);

            Page<Announcement> result = announcementMapper.selectPage(page, wrapper);
            return ApiResult.success(PageResult.of(result));
        } catch (Exception e) {
            return ApiResult.error("加载公告失败: " + e.getMessage());
        }
    }

    /**
     * 3.2.2 模拟推送通知
     */
    @PostMapping("/notifications/push")
    @Operation(summary = "推送通知", description = "模拟推送通知到移动端")
    public ApiResult<String> pushNotification(
            @Parameter(description = "用户ID")
            @RequestParam Long userId,
            @Parameter(description = "标题")
            @RequestParam String title,
            @Parameter(description = "内容")
            @RequestParam String content) {
        try {
            // 这里可以集成WebSocket或第三方推送服务
            // 模拟推送成功
            return ApiResult.success("推送成功");
        } catch (Exception e) {
            return ApiResult.error("推送失败: " + e.getMessage());
        }
    }

    /**
     * 3.2.3 模拟发送消息
     */
    @PostMapping("/message/send")
    @Operation(summary = "发送消息", description = "发送消息给其他用户")
    public ApiResult<String> sendMessage(
            @Parameter(description = "接收者ID")
            @RequestParam Long receiverId,
            @Parameter(description = "内容")
            @RequestParam String content) {
        try {
            // 这里可以实现消息发送逻辑
            // 模拟发送成功
            return ApiResult.success("消息发送成功");
        } catch (Exception e) {
            return ApiResult.error("发送消息失败: " + e.getMessage());
        }
    }
}
