package com.education.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.platform.common.ApiResult;
import com.education.platform.common.PageResult;
import com.education.platform.entity.Announcement;
import com.education.platform.entity.Assignment;
import com.education.platform.entity.Class;
import com.education.platform.entity.Grade;
import com.education.platform.entity.Student;
import com.education.platform.entity.Teacher;
import com.education.platform.entity.Timetable;
import com.education.platform.entity.User;
import com.education.platform.mapper.AnnouncementMapper;
import com.education.platform.mapper.AssignmentMapper;
import com.education.platform.mapper.ClassMapper;
import com.education.platform.mapper.GradeMapper;
import com.education.platform.mapper.StudentMapper;
import com.education.platform.mapper.TeacherMapper;
import com.education.platform.mapper.TimetableMapper;
import com.education.platform.mapper.UserMapper;
import com.education.platform.util.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private TimetableMapper timetableMapper;

    @Autowired
    private ClassMapper classMapper;

    @Autowired
    private GradeMapper gradeMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private AnnouncementMapper announcementMapper;

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
            Long teacherUserId = getUserIdFromToken(token);
            if (teacherUserId == null) {
                return ApiResult.error("未登录或token无效");
            }

            // 获取教师记录（用于获取实际教师ID）
            LambdaQueryWrapper<Teacher> teacherWrapper = new LambdaQueryWrapper<>();
            teacherWrapper.eq(Teacher::getUserId, teacherUserId);
            Teacher teacher = teacherMapper.selectOne(teacherWrapper);
            if (teacher == null) {
                return ApiResult.error("教师记录不存在，请联系管理员");
            }

            // 获取用户信息（用于获取学校ID）
            User user = userMapper.selectById(teacherUserId);
            if (user == null) {
                return ApiResult.error("用户信息不存在");
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
            assignment.setTeacherId(teacher.getId());
            assignment.setSchoolId(user.getSchoolId());

            // 设置状态和发布时间
            assignment.setStatus("进行中");
            assignment.setPublishTime(LocalDateTime.now());

            // 保存到数据库
            int result = assignmentMapper.insert(assignment);

            if (result > 0) {
                // 同时创建通知公告，让学生能在通知中心看到
                try {
                    Announcement announcement = new Announcement();
                    announcement.setTitle("新作业: " + assignment.getTitle());
                    announcement.setContent(assignment.getContent());
                    announcement.setType("notice");
                    announcement.setPublisherId(teacherUserId);
                    announcement.setPublishTime(LocalDateTime.now());
                    announcement.setStatus(1);
                    announcement.setSchoolId(user.getSchoolId());

                    // 作业通知的班级ID处理：优先使用targetClasses中的第一个班级
                    if (targetClassesObj != null && targetClassesObj instanceof List) {
                        List<?> classes = (List<?>) targetClassesObj;
                        if (!classes.isEmpty()) {
                            try {
                                Long classId = Long.valueOf(classes.get(0).toString());
                                announcement.setClassId(classId);
                            } catch (Exception e) {
                                // 如果转换失败，尝试获取教师的第一个班级
                                LambdaQueryWrapper<Class> classWrapper = new LambdaQueryWrapper<>();
                                classWrapper.eq(Class::getHeadTeacherId, teacher.getId());
                                List<Class> teacherClasses = classMapper.selectList(classWrapper);
                                if (!teacherClasses.isEmpty()) {
                                    announcement.setClassId(teacherClasses.get(0).getId());
                                }
                            }
                        }
                    } else {
                        // 没有指定目标班级，使用教师的第一个班级
                        LambdaQueryWrapper<Class> classWrapper = new LambdaQueryWrapper<>();
                        classWrapper.eq(Class::getHeadTeacherId, teacher.getId());
                        List<Class> teacherClasses = classMapper.selectList(classWrapper);
                        if (!teacherClasses.isEmpty()) {
                            announcement.setClassId(teacherClasses.get(0).getId());
                        }
                    }

                    announcement.setPriority(0);
                    announcementMapper.insert(announcement);
                } catch (Exception e) {
                    // 创建通知失败不影响作业发布
                    System.err.println("创建作业通知失败: " + e.getMessage());
                }

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

    /**
     * 获取教师的课程表
     */
    @GetMapping("/timetable")
    @Operation(summary = "获取教师课程表", description = "获取当前教师的课程表")
    public ApiResult<List<Timetable>> getTeacherTimetable(
            @RequestHeader(value = "Authorization", required = false) String token,
            @Parameter(description = "星期几 (1-7)") @RequestParam(required = false) Integer weekDay) {
        try {
            // 从token中获取当前教师用户
            Long teacherUserId = getUserIdFromToken(token);
            if (teacherUserId == null) {
                return ApiResult.error("未登录或token无效");
            }

            // 获取教师信息
            User teacherUser = userMapper.selectById(teacherUserId);
            if (teacherUser == null) {
                return ApiResult.error("教师用户不存在");
            }

            // 获取教师ID（通过teacher表）
            // 这里需要通过teacher表查询，因为timetable表中的teacher_id是teacher表的ID
            LambdaQueryWrapper<Teacher> teacherWrapper = new LambdaQueryWrapper<>();
            teacherWrapper.eq(Teacher::getUserId, teacherUserId);
            Teacher teacher = teacherMapper.selectOne(teacherWrapper);
            if (teacher == null) {
                return ApiResult.error("教师记录不存在，请联系管理员");
            }
            Long teacherId = teacher.getId();

            LambdaQueryWrapper<Timetable> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Timetable::getTeacherId, teacherId);
            if (weekDay != null) {
                wrapper.eq(Timetable::getWeekDay, weekDay);
            }
            wrapper.orderByAsc(Timetable::getWeekDay, Timetable::getPeriod);

            List<Timetable> timetables = timetableMapper.selectList(wrapper);
            return ApiResult.success(timetables);
        } catch (Exception e) {
            System.err.println("获取教师课程表失败: " + e.getMessage());
            return ApiResult.error("获取课程表失败: " + e.getMessage());
        }
    }

    /**
     * 添加课程表条目
     */
    @PostMapping("/timetable")
    @Operation(summary = "添加课程表", description = "为教师添加课程表条目")
    public ApiResult<Boolean> addTimetable(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody Timetable timetable) {
        try {
            // 从token中获取当前教师用户
            Long teacherUserId = getUserIdFromToken(token);
            if (teacherUserId == null) {
                return ApiResult.error("未登录或token无效");
            }

            // 获取教师信息
            User teacherUser = userMapper.selectById(teacherUserId);
            if (teacherUser == null) {
                return ApiResult.error("教师用户不存在");
            }

            // 获取教师记录
            LambdaQueryWrapper<Teacher> teacherWrapper = new LambdaQueryWrapper<>();
            teacherWrapper.eq(Teacher::getUserId, teacherUserId);
            Teacher teacher = teacherMapper.selectOne(teacherWrapper);
            if (teacher == null) {
                return ApiResult.error("教师记录不存在，请联系管理员");
            }

            // 如果没有设置班级ID，自动获取教师负责的第一个班级
            if (timetable.getClassId() == null) {
                LambdaQueryWrapper<Class> classWrapper = new LambdaQueryWrapper<>();
                classWrapper.eq(Class::getHeadTeacherId, teacher.getId());
                List<Class> teacherClasses = classMapper.selectList(classWrapper);
                if (teacherClasses == null || teacherClasses.isEmpty()) {
                    return ApiResult.error("您还没有负责的班级，请联系管理员");
                }
                timetable.setClassId(teacherClasses.get(0).getId());
            }

            // 设置教师ID和学校ID
            timetable.setTeacherId(teacher.getId());
            timetable.setSchoolId(teacherUser.getSchoolId());
            timetable.setDeleted(0);

            // 保存到数据库
            int result = timetableMapper.insert(timetable);

            return ApiResult.success(result > 0);
        } catch (Exception e) {
            System.err.println("添加课程表失败: " + e.getMessage());
            return ApiResult.error("添加课程表失败: " + e.getMessage());
        }
    }

    /**
     * 删除课程表条目
     */
    @DeleteMapping("/timetable/{id}")
    @Operation(summary = "删除课程表", description = "删除指定的课程表条目")
    public ApiResult<Boolean> deleteTimetable(
            @RequestHeader(value = "Authorization", required = false) String token,
            @Parameter(description = "课程表ID") @PathVariable Long id) {
        try {
            // 从token中获取当前教师用户
            Long teacherUserId = getUserIdFromToken(token);
            if (teacherUserId == null) {
                return ApiResult.error("未登录或token无效");
            }

            // 获取教师记录
            LambdaQueryWrapper<Teacher> teacherWrapper = new LambdaQueryWrapper<>();
            teacherWrapper.eq(Teacher::getUserId, teacherUserId);
            Teacher teacher = teacherMapper.selectOne(teacherWrapper);
            if (teacher == null) {
                return ApiResult.error("教师记录不存在，请联系管理员");
            }

            // 验证该课程表条目是否属于当前教师
            Timetable timetable = timetableMapper.selectById(id);
            if (timetable == null) {
                return ApiResult.error("课程表条目不存在");
            }

            if (!timetable.getTeacherId().equals(teacher.getId())) {
                return ApiResult.error("无权删除该课程表条目");
            }

            int result = timetableMapper.deleteById(id);
            return ApiResult.success(result > 0);
        } catch (Exception e) {
            System.err.println("删除课程表失败: " + e.getMessage());
            return ApiResult.error("删除课程表失败: " + e.getMessage());
        }
    }

    /**
     * 批量添加课程表
     */
    @PostMapping("/timetable/batch")
    @Operation(summary = "批量添加课程表", description = "批量添加多个课程表条目")
    public ApiResult<Map<String, Object>> batchAddTimetable(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody List<Timetable> timetables) {
        try {
            // 从token中获取当前教师用户
            Long teacherUserId = getUserIdFromToken(token);
            if (teacherUserId == null) {
                return ApiResult.error("未登录或token无效");
            }

            // 获取教师信息
            User teacherUser = userMapper.selectById(teacherUserId);
            if (teacherUser == null) {
                return ApiResult.error("教师用户不存在");
            }

            // 获取教师记录
            LambdaQueryWrapper<Teacher> teacherWrapper = new LambdaQueryWrapper<>();
            teacherWrapper.eq(Teacher::getUserId, teacherUserId);
            Teacher teacher = teacherMapper.selectOne(teacherWrapper);
            if (teacher == null) {
                return ApiResult.error("教师记录不存在，请联系管理员");
            }

            // 获取教师负责的班级ID（第一个班级）
            LambdaQueryWrapper<Class> classWrapper = new LambdaQueryWrapper<>();
            classWrapper.eq(Class::getHeadTeacherId, teacher.getId());
            List<Class> teacherClasses = classMapper.selectList(classWrapper);
            if (teacherClasses == null || teacherClasses.isEmpty()) {
                return ApiResult.error("您还没有负责的班级，请联系管理员");
            }
            Long defaultClassId = teacherClasses.get(0).getId();

            // 批量插入
            int count = 0;
            for (Timetable timetable : timetables) {
                // 如果没有设置班级ID，使用教师的第一个班级
                if (timetable.getClassId() == null) {
                    timetable.setClassId(defaultClassId);
                }
                timetable.setTeacherId(teacher.getId());
                timetable.setSchoolId(teacherUser.getSchoolId());
                timetable.setDeleted(0);
                timetableMapper.insert(timetable);
                count++;
            }

            if (count > 0) {
                Map<String, Object> result = new HashMap<>();
                result.put("count", count);
                return ApiResult.success("成功添加 " + count + " 条课程表记录", result);
            } else {
                return ApiResult.error("添加失败");
            }
        } catch (Exception e) {
            System.err.println("批量添加课程表失败: " + e.getMessage());
            return ApiResult.error("批量添加课程表失败: " + e.getMessage());
        }
    }

    /**
     * 获取教师负责的班级列表
     */
    @GetMapping("/classes")
    @Operation(summary = "获取教师负责的班级", description = "获取当前教师负责的班级列表")
    public ApiResult<List<Map<String, Object>>> getTeacherClasses(
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // 从token中获取当前教师用户
            Long teacherUserId = getUserIdFromToken(token);
            if (teacherUserId == null) {
                return ApiResult.error("未登录或token无效");
            }

            // 获取教师记录
            LambdaQueryWrapper<Teacher> teacherWrapper = new LambdaQueryWrapper<>();
            teacherWrapper.eq(Teacher::getUserId, teacherUserId);
            Teacher teacher = teacherMapper.selectOne(teacherWrapper);
            if (teacher == null) {
                return ApiResult.error("教师记录不存在，请联系管理员");
            }

            // 获取教师负责的班级
            LambdaQueryWrapper<Class> classWrapper = new LambdaQueryWrapper<>();
            classWrapper.eq(Class::getHeadTeacherId, teacher.getId());
            List<Class> classes = classMapper.selectList(classWrapper);

            // 构建返回数据
            List<Map<String, Object>> result = new ArrayList<>();
            for (Class cls : classes) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", cls.getId());
                item.put("className", cls.getClassName());
                result.add(item);
            }

            return ApiResult.success(result);
        } catch (Exception e) {
            System.err.println("获取教师班级失败: " + e.getMessage());
            return ApiResult.error("获取教师班级失败: " + e.getMessage());
        }
    }

    /**
     * 添加成绩
     */
    @PostMapping("/grades")
    @Operation(summary = "添加成绩", description = "教师为学生添加成绩")
    public ApiResult<Map<String, Object>> addGrade(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody Map<String, Object> gradeData) {
        try {
            // 从token中获取当前教师用户
            Long teacherUserId = getUserIdFromToken(token);
            if (teacherUserId == null) {
                return ApiResult.error("未登录或token无效");
            }

            // 获取教师记录
            LambdaQueryWrapper<Teacher> teacherWrapper = new LambdaQueryWrapper<>();
            teacherWrapper.eq(Teacher::getUserId, teacherUserId);
            Teacher teacher = teacherMapper.selectOne(teacherWrapper);
            if (teacher == null) {
                return ApiResult.error("教师记录不存在，请联系管理员");
            }

            // 获取学生ID
            Object studentIdObj = gradeData.get("studentId");
            if (studentIdObj == null) {
                return ApiResult.error("学生ID不能为空");
            }
            Long studentId = Long.valueOf(studentIdObj.toString());

            // 验证学生是否存在
            Student student = studentMapper.selectById(studentId);
            if (student == null) {
                return ApiResult.error("学生不存在");
            }

            // 创建成绩对象
            Grade grade = new Grade();

            // 设置学生ID
            grade.setStudentId(studentId);

            // 设置科目
            Object subjectObj = gradeData.get("subject");
            if (subjectObj == null || subjectObj.toString().trim().isEmpty()) {
                return ApiResult.error("科目不能为空");
            }
            grade.setSubject(subjectObj.toString());

            // 设置成绩
            Object scoreObj = gradeData.get("score");
            if (scoreObj == null) {
                return ApiResult.error("成绩不能为空");
            }
            try {
                grade.setScore(new BigDecimal(scoreObj.toString()));
            } catch (Exception e) {
                return ApiResult.error("成绩格式错误，请输入数字");
            }

            // 设置考试类型（期中/期末/日常）
            Object examTypeObj = gradeData.get("examType");
            grade.setExamType(examTypeObj != null ? examTypeObj.toString() : "日常");

            // 设置考试日期
            Object examDateObj = gradeData.get("examDate");
            if (examDateObj != null) {
                try {
                    grade.setExamDate(LocalDate.parse(examDateObj.toString()));
                } catch (Exception e) {
                    grade.setExamDate(LocalDate.now());
                }
            } else {
                grade.setExamDate(LocalDate.now());
            }

            // 设置学年
            Object academicTermObj = gradeData.get("academicTerm");
            grade.setAcademicTerm(academicTermObj != null ? academicTermObj.toString() : "2024-2025");

            // 设置教师ID
            grade.setTeacherId(teacher.getId());

            // 设置备注
            Object remarkObj = gradeData.get("remark");
            if (remarkObj != null) {
                grade.setRemark(remarkObj.toString());
            }

            // 保存到数据库
            int result = gradeMapper.insert(grade);

            if (result > 0) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("id", grade.getId());
                responseData.put("studentId", grade.getStudentId());
                responseData.put("subject", grade.getSubject());
                responseData.put("score", grade.getScore());
                responseData.put("examType", grade.getExamType());

                return ApiResult.success("成绩添加成功", responseData);
            } else {
                return ApiResult.error("成绩添加失败");
            }
        } catch (Exception e) {
            System.err.println("添加成绩失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResult.error("添加成绩失败: " + e.getMessage());
        }
    }

    /**
     * 获取成绩管理列表（带筛选和分页）
     */
    @GetMapping("/grades")
    @Operation(summary = "获取成绩管理列表", description = "获取教师管理的成绩列表，支持筛选")
    public ApiResult<PageResult<Map<String, Object>>> getGradeManagement(
            @RequestHeader(value = "Authorization", required = false) String token,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "学生ID") @RequestParam(required = false) Long studentId,
            @Parameter(description = "科目") @RequestParam(required = false) String subject,
            @Parameter(description = "考试类型") @RequestParam(required = false) String examType) {
        try {
            // 从token中获取当前教师用户
            Long teacherUserId = getUserIdFromToken(token);
            if (teacherUserId == null) {
                return ApiResult.error("未登录或token无效");
            }

            // 获取教师记录
            LambdaQueryWrapper<Teacher> teacherWrapper = new LambdaQueryWrapper<>();
            teacherWrapper.eq(Teacher::getUserId, teacherUserId);
            Teacher teacher = teacherMapper.selectOne(teacherWrapper);
            if (teacher == null) {
                return ApiResult.error("教师记录不存在，请联系管理员");
            }

            // 构建查询条件
            Page<Grade> page = new Page<>(current, size);
            LambdaQueryWrapper<Grade> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Grade::getTeacherId, teacher.getId());
            wrapper.eq(Grade::getDeleted, 0);

            if (studentId != null) {
                wrapper.eq(Grade::getStudentId, studentId);
            }
            if (subject != null && !subject.trim().isEmpty()) {
                wrapper.eq(Grade::getSubject, subject);
            }
            if (examType != null && !examType.trim().isEmpty()) {
                wrapper.eq(Grade::getExamType, examType);
            }

            wrapper.orderByDesc(Grade::getExamDate);

            // 执行查询
            Page<Grade> result = gradeMapper.selectPage(page, wrapper);

            // 构建返回数据（关联学生信息）
            List<Map<String, Object>> records = new ArrayList<>();
            for (Grade grade : result.getRecords()) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", grade.getId());
                item.put("studentId", grade.getStudentId());
                item.put("subject", grade.getSubject());
                item.put("score", grade.getScore());
                item.put("examType", grade.getExamType());
                item.put("examDate", grade.getExamDate());
                item.put("academicTerm", grade.getAcademicTerm());
                item.put("remark", grade.getRemark());

                // 获取学生信息
                Student student = studentMapper.selectById(grade.getStudentId());
                if (student != null) {
                    User user = userMapper.selectById(student.getUserId());
                    if (user != null) {
                        item.put("studentName", user.getRealName());
                        item.put("studentNumber", student.getStudentNumber());
                    }
                }

                records.add(item);
            }

            PageResult<Map<String, Object>> pageResult = new PageResult<>();
            pageResult.setRecords(records);
            pageResult.setTotal(result.getTotal());
            pageResult.setSize(result.getSize());
            pageResult.setCurrent(result.getCurrent());
            pageResult.setPages(result.getPages());

            return ApiResult.success(pageResult);
        } catch (Exception e) {
            System.err.println("获取成绩列表失败: " + e.getMessage());
            return ApiResult.error("获取成绩列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取学生列表（用于成绩管理中的学生选择）
     */
    @GetMapping("/students")
    @Operation(summary = "获取教师班级的学生列表", description = "获取教师负责班级的所有学生")
    public ApiResult<List<Map<String, Object>>> getTeacherStudents(
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // 从token中获取当前教师用户
            Long teacherUserId = getUserIdFromToken(token);
            if (teacherUserId == null) {
                return ApiResult.error("未登录或token无效");
            }

            // 获取教师记录
            LambdaQueryWrapper<Teacher> teacherWrapper = new LambdaQueryWrapper<>();
            teacherWrapper.eq(Teacher::getUserId, teacherUserId);
            Teacher teacher = teacherMapper.selectOne(teacherWrapper);
            if (teacher == null) {
                return ApiResult.error("教师记录不存在，请联系管理员");
            }

            // 获取教师负责的班级
            LambdaQueryWrapper<Class> classWrapper = new LambdaQueryWrapper<>();
            classWrapper.eq(Class::getHeadTeacherId, teacher.getId());
            List<Class> classes = classMapper.selectList(classWrapper);

            if (classes == null || classes.isEmpty()) {
                return ApiResult.error("您还没有负责的班级，请联系管理员");
            }

            // 获取这些班级的所有学生
            List<Long> classIds = new ArrayList<>();
            for (Class cls : classes) {
                classIds.add(cls.getId());
            }

            LambdaQueryWrapper<Student> studentWrapper = new LambdaQueryWrapper<>();
            studentWrapper.in(Student::getClassId, classIds);
            studentWrapper.eq(Student::getDeleted, 0);
            List<Student> students = studentMapper.selectList(studentWrapper);

            // 构建返回数据
            List<Map<String, Object>> result = new ArrayList<>();
            for (Student student : students) {
                User user = userMapper.selectById(student.getUserId());
                if (user != null) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", student.getId());
                    item.put("username", user.getUsername());
                    item.put("realName", user.getRealName());
                    item.put("studentNumber", student.getStudentNumber());
                    item.put("className", classes.stream()
                            .filter(c -> c.getId().equals(student.getClassId()))
                            .findFirst()
                            .map(Class::getClassName)
                            .orElse(""));
                    result.add(item);
                }
            }

            return ApiResult.success(result);
        } catch (Exception e) {
            System.err.println("获取学生列表失败: " + e.getMessage());
            return ApiResult.error("获取学生列表失败: " + e.getMessage());
        }
    }

    /**
     * 删除成绩
     */
    @DeleteMapping("/grades/{id}")
    @Operation(summary = "删除成绩", description = "删除指定的成绩记录")
    public ApiResult<Boolean> deleteGrade(
            @RequestHeader(value = "Authorization", required = false) String token,
            @Parameter(description = "成绩ID") @PathVariable Long id) {
        try {
            // 从token中获取当前教师用户
            Long teacherUserId = getUserIdFromToken(token);
            if (teacherUserId == null) {
                return ApiResult.error("未登录或token无效");
            }

            // 获取教师记录
            LambdaQueryWrapper<Teacher> teacherWrapper = new LambdaQueryWrapper<>();
            teacherWrapper.eq(Teacher::getUserId, teacherUserId);
            Teacher teacher = teacherMapper.selectOne(teacherWrapper);
            if (teacher == null) {
                return ApiResult.error("教师记录不存在，请联系管理员");
            }

            // 验证成绩是否存在且属于当前教师
            Grade grade = gradeMapper.selectById(id);
            if (grade == null) {
                return ApiResult.error("成绩不存在");
            }

            if (!grade.getTeacherId().equals(teacher.getId())) {
                return ApiResult.error("无权删除该成绩");
            }

            // 逻辑删除
            grade.setDeleted(1);
            int result = gradeMapper.updateById(grade);

            return ApiResult.success(result > 0);
        } catch (Exception e) {
            System.err.println("删除成绩失败: " + e.getMessage());
            return ApiResult.error("删除成绩失败: " + e.getMessage());
        }
    }

    /**
     * 更新成绩
     */
    @PutMapping("/grades/{id}")
    @Operation(summary = "更新成绩", description = "更新指定的成绩记录")
    public ApiResult<Boolean> updateGrade(
            @RequestHeader(value = "Authorization", required = false) String token,
            @Parameter(description = "成绩ID") @PathVariable Long id,
            @RequestBody Map<String, Object> gradeData) {
        try {
            // 从token中获取当前教师用户
            Long teacherUserId = getUserIdFromToken(token);
            if (teacherUserId == null) {
                return ApiResult.error("未登录或token无效");
            }

            // 获取教师记录
            LambdaQueryWrapper<Teacher> teacherWrapper = new LambdaQueryWrapper<>();
            teacherWrapper.eq(Teacher::getUserId, teacherUserId);
            Teacher teacher = teacherMapper.selectOne(teacherWrapper);
            if (teacher == null) {
                return ApiResult.error("教师记录不存在，请联系管理员");
            }

            // 验证成绩是否存在且属于当前教师
            Grade grade = gradeMapper.selectById(id);
            if (grade == null) {
                return ApiResult.error("成绩不存在");
            }

            if (!grade.getTeacherId().equals(teacher.getId())) {
                return ApiResult.error("无权修改该成绩");
            }

            // 更新字段
            Object scoreObj = gradeData.get("score");
            if (scoreObj != null) {
                try {
                    grade.setScore(new BigDecimal(scoreObj.toString()));
                } catch (Exception e) {
                    return ApiResult.error("成绩格式错误");
                }
            }

            Object remarkObj = gradeData.get("remark");
            if (remarkObj != null) {
                grade.setRemark(remarkObj.toString());
            }

            Object examDateObj = gradeData.get("examDate");
            if (examDateObj != null) {
                try {
                    grade.setExamDate(LocalDate.parse(examDateObj.toString()));
                } catch (Exception e) {
                    // 忽略格式错误
                }
            }

            int result = gradeMapper.updateById(grade);

            return ApiResult.success(result > 0);
        } catch (Exception e) {
            System.err.println("更新成绩失败: " + e.getMessage());
            return ApiResult.error("更新成绩失败: " + e.getMessage());
        }
    }

    /**
     * 发布通知公告
     */
    @PostMapping("/announcements")
    @Operation(summary = "发布通知", description = "教师发布通知公告")
    public ApiResult<Map<String, Object>> publishAnnouncement(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody Map<String, Object> announcementData) {
        try {
            // 从token中获取当前教师用户
            Long teacherUserId = getUserIdFromToken(token);
            if (teacherUserId == null) {
                return ApiResult.error("未登录或token无效");
            }

            // 获取教师记录
            LambdaQueryWrapper<Teacher> teacherWrapper = new LambdaQueryWrapper<>();
            teacherWrapper.eq(Teacher::getUserId, teacherUserId);
            Teacher teacher = teacherMapper.selectOne(teacherWrapper);
            if (teacher == null) {
                return ApiResult.error("教师记录不存在，请联系管理员");
            }

            // 获取当前用户信息（用于学校ID）
            User currentUser = userMapper.selectById(teacherUserId);
            if (currentUser == null) {
                return ApiResult.error("用户信息不存在");
            }

            // 创建公告对象
            Announcement announcement = new Announcement();

            // 设置标题
            Object titleObj = announcementData.get("title");
            if (titleObj == null || titleObj.toString().trim().isEmpty()) {
                return ApiResult.error("通知标题不能为空");
            }
            announcement.setTitle(titleObj.toString());

            // 设置内容
            Object contentObj = announcementData.get("content");
            if (contentObj == null || contentObj.toString().trim().isEmpty()) {
                return ApiResult.error("通知内容不能为空");
            }
            announcement.setContent(contentObj.toString());

            // 设置类型（转换前端类型到后端类型）
            Object typeObj = announcementData.get("type");
            String type = "notice"; // 默认为通知
            if (typeObj != null) {
                String frontendType = typeObj.toString();
                // 映射前端类型到后端类型
                if ("important".equals(frontendType)) {
                    type = "emergency"; // 重要通知映射为紧急
                } else if ("daily".equals(frontendType) || "activity".equals(frontendType) ||
                           "exam".equals(frontendType) || "holiday".equals(frontendType)) {
                    type = "notice"; // 其他类型映射为普通通知
                } else {
                    type = "notice";
                }
            }
            announcement.setType(type);

            // 设置发布者
            announcement.setPublisherId(teacherUserId);

            // 设置发布时间
            Object publishTimeObj = announcementData.get("publishTime");
            if ("schedule".equals(publishTimeObj)) {
                // 定时发布
                Object scheduleTimeObj = announcementData.get("scheduleTime");
                if (scheduleTimeObj != null) {
                    try {
                        announcement.setPublishTime(LocalDateTime.parse(scheduleTimeObj.toString().replace(" ", "T")));
                    } catch (Exception e) {
                        announcement.setPublishTime(LocalDateTime.now());
                    }
                } else {
                    announcement.setPublishTime(LocalDateTime.now());
                }
            } else {
                // 立即发布
                announcement.setPublishTime(LocalDateTime.now());
            }

            // 设置学校ID
            announcement.setSchoolId(currentUser.getSchoolId());

            // 设置班级ID（教师通知只对本班级可见）
            // 优先使用前端指定的classId，如果没有则使用教师负责的第一个班级
            Object targetClassIdObj = announcementData.get("classId");
            if (targetClassIdObj != null) {
                try {
                    Long targetClassId = Long.valueOf(targetClassIdObj.toString());
                    announcement.setClassId(targetClassId);
                } catch (Exception e) {
                    // 如果转换失败，尝试获取教师的第一个班级
                    LambdaQueryWrapper<Class> classWrapper = new LambdaQueryWrapper<>();
                    classWrapper.eq(Class::getHeadTeacherId, teacher.getId());
                    List<Class> teacherClasses = classMapper.selectList(classWrapper);
                    if (!teacherClasses.isEmpty()) {
                        announcement.setClassId(teacherClasses.get(0).getId());
                    }
                }
            } else {
                // 没有指定班级，使用教师的第一个班级
                LambdaQueryWrapper<Class> classWrapper = new LambdaQueryWrapper<>();
                classWrapper.eq(Class::getHeadTeacherId, teacher.getId());
                List<Class> teacherClasses = classMapper.selectList(classWrapper);
                if (!teacherClasses.isEmpty()) {
                    announcement.setClassId(teacherClasses.get(0).getId());
                } else {
                    // 如果没有班级，设置为null（学校级别通知）
                    announcement.setClassId(null);
                }
            }

            // 设置优先级（置顶设置）
            Object isTopObj = announcementData.get("isTop");
            announcement.setPriority(isTopObj != null && Boolean.parseBoolean(isTopObj.toString()) ? 1 : 0);

            // 设置状态为已发布
            announcement.setStatus(1);

            // 保存到数据库
            int result = announcementMapper.insert(announcement);

            if (result > 0) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("id", announcement.getId());
                responseData.put("title", announcement.getTitle());
                responseData.put("publishTime", announcement.getPublishTime());
                responseData.put("type", announcement.getType());

                return ApiResult.success("通知发布成功", responseData);
            } else {
                return ApiResult.error("通知发布失败");
            }
        } catch (Exception e) {
            System.err.println("发布通知失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResult.error("发布通知失败: " + e.getMessage());
        }
    }

    /**
     * 获取教师发布的通知列表
     */
    @GetMapping("/announcements")
    @Operation(summary = "获取教师通知列表", description = "获取教师发布的通知公告列表")
    public ApiResult<PageResult<Announcement>> getTeacherAnnouncements(
            @RequestHeader(value = "Authorization", required = false) String token,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long size) {
        try {
            // 从token中获取当前教师用户
            Long teacherUserId = getUserIdFromToken(token);
            if (teacherUserId == null) {
                return ApiResult.error("未登录或token无效");
            }

            Page<Announcement> page = new Page<>(current, size);
            LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Announcement::getPublisherId, teacherUserId);
            wrapper.eq(Announcement::getDeleted, 0);
            wrapper.orderByDesc(Announcement::getPublishTime);

            Page<Announcement> result = announcementMapper.selectPage(page, wrapper);
            return ApiResult.success(PageResult.of(result));
        } catch (Exception e) {
            System.err.println("获取通知列表失败: " + e.getMessage());
            return ApiResult.error("获取通知列表失败: " + e.getMessage());
        }
    }

    /**
     * 删除通知
     */
    @DeleteMapping("/announcements/{id}")
    @Operation(summary = "删除通知", description = "删除指定的通知")
    public ApiResult<Boolean> deleteAnnouncement(
            @RequestHeader(value = "Authorization", required = false) String token,
            @Parameter(description = "通知ID") @PathVariable Long id) {
        try {
            // 从token中获取当前教师用户
            Long teacherUserId = getUserIdFromToken(token);
            if (teacherUserId == null) {
                return ApiResult.error("未登录或token无效");
            }

            // 验证通知是否存在且属于当前教师
            Announcement announcement = announcementMapper.selectById(id);
            if (announcement == null) {
                return ApiResult.error("通知不存在");
            }

            if (!announcement.getPublisherId().equals(teacherUserId)) {
                return ApiResult.error("无权删除该通知");
            }

            // 逻辑删除
            announcement.setDeleted(1);
            int result = announcementMapper.updateById(announcement);

            return ApiResult.success(result > 0);
        } catch (Exception e) {
            System.err.println("删除通知失败: " + e.getMessage());
            return ApiResult.error("删除通知失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有可选班级列表（供教师选择）
     */
    @GetMapping("/available-classes")
    @Operation(summary = "获取可选班级列表", description = "获取所有未分配班主任的班级列表")
    public ApiResult<List<Map<String, Object>>> getAvailableClasses(
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // 从token中获取当前教师用户
            Long teacherUserId = getUserIdFromToken(token);
            if (teacherUserId == null) {
                return ApiResult.error("未登录或token无效");
            }

            // 获取教师记录
            LambdaQueryWrapper<Teacher> teacherWrapper = new LambdaQueryWrapper<>();
            teacherWrapper.eq(Teacher::getUserId, teacherUserId);
            Teacher teacher = teacherMapper.selectOne(teacherWrapper);
            if (teacher == null) {
                return ApiResult.error("教师记录不存在，请联系管理员");
            }

            // 查询所有未分配班主任或已分配给当前教师的班级
            LambdaQueryWrapper<Class> classWrapper = new LambdaQueryWrapper<>();
            classWrapper.eq(Class::getDeleted, 0);
            classWrapper.eq(Class::getStatus, 1); // 正常状态
            classWrapper.and(wrapper ->
                wrapper.isNull(Class::getHeadTeacherId)
                    .or().eq(Class::getHeadTeacherId, teacher.getId())
            );

            List<Class> classes = classMapper.selectList(classWrapper);

            // 构建返回数据
            List<Map<String, Object>> result = new ArrayList<>();
            for (Class cls : classes) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", cls.getId());
                item.put("className", cls.getClassName());
                item.put("grade", cls.getGrade());
                item.put("academicYear", cls.getAcademicYear());
                item.put("studentCount", cls.getStudentCount());

                // 检查是否已分配给当前教师
                boolean isSelected = cls.getHeadTeacherId() != null && cls.getHeadTeacherId().equals(teacher.getId());
                item.put("isSelected", isSelected);

                // 获取班主任姓名（如果有）
                if (cls.getHeadTeacherId() != null) {
                    Teacher headTeacher = teacherMapper.selectById(cls.getHeadTeacherId());
                    if (headTeacher != null) {
                        User headTeacherUser = userMapper.selectById(headTeacher.getUserId());
                        if (headTeacherUser != null) {
                            item.put("headTeacherName", headTeacherUser.getRealName());
                        }
                    }
                }

                result.add(item);
            }

            return ApiResult.success(result);
        } catch (Exception e) {
            System.err.println("获取可选班级列表失败: " + e.getMessage());
            return ApiResult.error("获取可选班级列表失败: " + e.getMessage());
        }
    }

    /**
     * 教师选择/分配班级
     */
    @PostMapping("/select-class")
    @Operation(summary = "教师选择班级", description = "教师选择负责的班级")
    public ApiResult<String> selectClass(
            @RequestHeader(value = "Authorization", required = false) String token,
            @Parameter(description = "班级ID") @RequestParam Long classId) {
        try {
            // 从token中获取当前教师用户
            Long teacherUserId = getUserIdFromToken(token);
            if (teacherUserId == null) {
                return ApiResult.error("未登录或token无效");
            }

            // 获取教师记录
            LambdaQueryWrapper<Teacher> teacherWrapper = new LambdaQueryWrapper<>();
            teacherWrapper.eq(Teacher::getUserId, teacherUserId);
            Teacher teacher = teacherMapper.selectOne(teacherWrapper);
            if (teacher == null) {
                return ApiResult.error("教师记录不存在，请联系管理员");
            }

            // 验证班级是否存在
            Class classInfo = classMapper.selectById(classId);
            if (classInfo == null) {
                return ApiResult.error("班级不存在");
            }

            if (classInfo.getDeleted() != null && classInfo.getDeleted() == 1) {
                return ApiResult.error("班级已删除");
            }

            if (classInfo.getStatus() != null && classInfo.getStatus() != 1) {
                return ApiResult.error("班级状态异常");
            }

            // 检查班级是否已被其他教师占用
            if (classInfo.getHeadTeacherId() != null && !classInfo.getHeadTeacherId().equals(teacher.getId())) {
                Teacher existingTeacher = teacherMapper.selectById(classInfo.getHeadTeacherId());
                if (existingTeacher != null) {
                    User existingUser = userMapper.selectById(existingTeacher.getUserId());
                    if (existingUser != null) {
                        return ApiResult.error("该班级已被其他教师(" + existingUser.getRealName() + ")负责");
                    }
                }
            }

            // 更新班级的班主任
            classInfo.setHeadTeacherId(teacher.getId());
            int result = classMapper.updateById(classInfo);

            if (result > 0) {
                return ApiResult.success("成功选择班级", "成功选择班级");
            } else {
                return ApiResult.error("选择班级失败");
            }
        } catch (Exception e) {
            System.err.println("选择班级失败: " + e.getMessage());
            return ApiResult.error("选择班级失败: " + e.getMessage());
        }
    }

    /**
     * 教师取消选择班级
     */
    @PostMapping("/unselect-class")
    @Operation(summary = "教师取消选择班级", description = "教师取消负责的班级")
    public ApiResult<String> unselectClass(
            @RequestHeader(value = "Authorization", required = false) String token,
            @Parameter(description = "班级ID") @RequestParam Long classId) {
        try {
            // 从token中获取当前教师用户
            Long teacherUserId = getUserIdFromToken(token);
            if (teacherUserId == null) {
                return ApiResult.error("未登录或token无效");
            }

            // 获取教师记录
            LambdaQueryWrapper<Teacher> teacherWrapper = new LambdaQueryWrapper<>();
            teacherWrapper.eq(Teacher::getUserId, teacherUserId);
            Teacher teacher = teacherMapper.selectOne(teacherWrapper);
            if (teacher == null) {
                return ApiResult.error("教师记录不存在，请联系管理员");
            }

            // 验证班级是否存在
            Class classInfo = classMapper.selectById(classId);
            if (classInfo == null) {
                return ApiResult.error("班级不存在");
            }

            // 验证该班级是否属于当前教师
            if (!teacher.getId().equals(classInfo.getHeadTeacherId())) {
                return ApiResult.error("您不是该班级的班主任，无法取消");
            }

            // 取消班主任 - 允许取消，即使班级有学生
            // 学生关系可以通过其他方式处理，这里只解除教师与班级的关联
            classInfo.setHeadTeacherId(null);
            int result = classMapper.updateById(classInfo);

            if (result > 0) {
                return ApiResult.success("成功取消班级", "成功取消班级");
            } else {
                return ApiResult.error("取消班级失败");
            }
        } catch (Exception e) {
            System.err.println("取消班级失败: " + e.getMessage());
            return ApiResult.error("取消班级失败: " + e.getMessage());
        }
    }

    /**
     * 获取教师已选择的班级列表
     */
    @GetMapping("/my-classes")
    @Operation(summary = "获取我的班级", description = "获取教师已选择的班级列表")
    public ApiResult<List<Map<String, Object>>> getMyClasses(
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // 从token中获取当前教师用户
            Long teacherUserId = getUserIdFromToken(token);
            if (teacherUserId == null) {
                return ApiResult.error("未登录或token无效");
            }

            // 获取教师记录
            LambdaQueryWrapper<Teacher> teacherWrapper = new LambdaQueryWrapper<>();
            teacherWrapper.eq(Teacher::getUserId, teacherUserId);
            Teacher teacher = teacherMapper.selectOne(teacherWrapper);
            if (teacher == null) {
                return ApiResult.error("教师记录不存在，请联系管理员");
            }

            // 查询该教师负责的所有班级
            LambdaQueryWrapper<Class> classWrapper = new LambdaQueryWrapper<>();
            classWrapper.eq(Class::getHeadTeacherId, teacher.getId());
            classWrapper.eq(Class::getDeleted, 0);
            classWrapper.eq(Class::getStatus, 1);

            List<Class> classes = classMapper.selectList(classWrapper);

            // 构建返回数据
            List<Map<String, Object>> result = new ArrayList<>();
            for (Class cls : classes) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", cls.getId());
                item.put("className", cls.getClassName());
                item.put("grade", cls.getGrade());
                item.put("academicYear", cls.getAcademicYear());
                item.put("studentCount", cls.getStudentCount());

                // 获取该班级的学生数量
                LambdaQueryWrapper<Student> studentWrapper = new LambdaQueryWrapper<>();
                studentWrapper.eq(Student::getClassId, cls.getId());
                studentWrapper.eq(Student::getDeleted, 0);
                long actualStudentCount = studentMapper.selectCount(studentWrapper);
                item.put("actualStudentCount", actualStudentCount);

                result.add(item);
            }

            return ApiResult.success(result);
        } catch (Exception e) {
            System.err.println("获取我的班级失败: " + e.getMessage());
            return ApiResult.error("获取我的班级失败: " + e.getMessage());
        }
    }
}
