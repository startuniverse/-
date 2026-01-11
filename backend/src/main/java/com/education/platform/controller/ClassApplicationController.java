package com.education.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.platform.common.ApiResult;
import com.education.platform.common.PageResult;
import com.education.platform.entity.ClassApplication;
import com.education.platform.entity.Student;
import com.education.platform.entity.Teacher;
import com.education.platform.entity.User;
import com.education.platform.mapper.ClassApplicationMapper;
import com.education.platform.mapper.ClassMapper;
import com.education.platform.mapper.StudentMapper;
import com.education.platform.mapper.TeacherMapper;
import com.education.platform.mapper.UserMapper;
import com.education.platform.service.IClassApplicationService;
import com.education.platform.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 班级申请控制器
 */
@Slf4j
@RestController
@RequestMapping("/class-application")
@Tag(name = "班级申请", description = "班级申请相关接口")
public class ClassApplicationController {

    @Autowired
    private IClassApplicationService classApplicationService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ClassApplicationMapper classApplicationMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ClassMapper classMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    /**
     * 学生创建班级申请
     */
    @PostMapping("/apply")
    @Operation(summary = "创建班级申请", description = "学生创建加入班级的申请")
    public ApiResult<?> createApplication(@RequestBody ClassApplication application) {
        // 获取班主任信息
        Teacher teacher = teacherMapper.selectById(application.getTeacherId());
        if (teacher == null) {
            return ApiResult.error("班主任不存在");
        }

        // 获取班主任姓名（从用户表中）
        User teacherUser = userMapper.selectById(teacher.getUserId());
        if (teacherUser != null) {
            application.setTeacherName(teacherUser.getRealName());
        }

        // 如果传入的是userId，需要转换为studentId
        if (application.getStudentId() != null) {
            // 先尝试作为studentId
            Student student = studentMapper.selectById(application.getStudentId());
            if (student == null) {
                // 尝试作为userId
                LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Student::getUserId, application.getStudentId());
                student = studentMapper.selectOne(wrapper);

                if (student != null) {
                    application.setStudentId(student.getId());
                }
            }

            if (student != null) {
                // 获取学生姓名
                User user = userMapper.selectById(student.getUserId());
                if (user != null) {
                    application.setStudentName(user.getRealName());
                }
            }
        }

        boolean success = classApplicationService.createApplication(application);
        if (success) {
            return ApiResult.success("申请提交成功");
        }
        return ApiResult.error("申请提交失败，可能已存在相同申请");
    }

    /**
     * 获取学生的申请列表
     */
    @GetMapping("/student/{studentId}")
    @Operation(summary = "获取学生申请列表", description = "获取指定学生的班级申请列表")
    public ApiResult<List<ClassApplication>> getStudentApplications(
            @Parameter(description = "学生ID或用户ID") @PathVariable Long studentId) {

        // 先尝试作为studentId查询
        List<ClassApplication> applications = classApplicationService.getStudentApplications(studentId);

        // 如果没有结果，尝试作为userId查询对应的student
        if (applications.isEmpty()) {
            LambdaQueryWrapper<Student> studentWrapper = new LambdaQueryWrapper<>();
            studentWrapper.eq(Student::getUserId, studentId);
            Student student = studentMapper.selectOne(studentWrapper);

            if (student != null) {
                applications = classApplicationService.getStudentApplications(student.getId());
            }
        }

        return ApiResult.success(applications);
    }

    /**
     * 获取老师的待审核申请列表
     */
    @GetMapping("/teacher/pending/{teacherId}")
    public ApiResult<List<ClassApplication>> getPendingApplications(@PathVariable Long teacherId) {
        // 尝试作为teacherId查询，如果不存在则尝试作为userId查询对应的teacher
        Teacher teacher = teacherMapper.selectById(teacherId);
        if (teacher == null) {
            LambdaQueryWrapper<Teacher> teacherWrapper = new LambdaQueryWrapper<>();
            teacherWrapper.eq(Teacher::getUserId, teacherId);
            teacher = teacherMapper.selectOne(teacherWrapper);

            if (teacher != null) {
                teacherId = teacher.getId();
            }
        }

        List<ClassApplication> applications = classApplicationService.getPendingApplications(teacherId);
        return ApiResult.success(applications);
    }

    /**
     * 获取老师的所有申请列表
     */
    @GetMapping("/teacher/{teacherId}")
    public ApiResult<List<ClassApplication>> getTeacherApplications(@PathVariable Long teacherId) {
        // 尝试作为teacherId查询，如果不存在则尝试作为userId查询对应的teacher
        Teacher teacher = teacherMapper.selectById(teacherId);
        if (teacher == null) {
            LambdaQueryWrapper<Teacher> teacherWrapper = new LambdaQueryWrapper<>();
            teacherWrapper.eq(Teacher::getUserId, teacherId);
            teacher = teacherMapper.selectOne(teacherWrapper);

            if (teacher != null) {
                teacherId = teacher.getId();
            }
        }

        List<ClassApplication> applications = classApplicationService.getTeacherApplications(teacherId);
        return ApiResult.success(applications);
    }

    /**
     * 老师审核申请
     */
    @PostMapping("/review/{applicationId}")
    public ApiResult<?> reviewApplication(
            @PathVariable Long applicationId,
            @RequestBody Map<String, Object> params) {

        Integer status = (Integer) params.get("status");
        String approvalComment = (String) params.get("approvalComment");
        Long teacherIdOrUserId = ((Number) params.get("teacherId")).longValue();

        // 尝试作为teacherId，如果不存在则尝试作为userId
        Teacher teacher = teacherMapper.selectById(teacherIdOrUserId);
        if (teacher == null) {
            LambdaQueryWrapper<Teacher> teacherWrapper = new LambdaQueryWrapper<>();
            teacherWrapper.eq(Teacher::getUserId, teacherIdOrUserId);
            teacher = teacherMapper.selectOne(teacherWrapper);

            if (teacher != null) {
                teacherIdOrUserId = teacher.getId();
            }
        }

        boolean success = classApplicationService.reviewApplication(applicationId, status, approvalComment, teacherIdOrUserId);
        if (success) {
            return ApiResult.success("审核成功");
        }
        return ApiResult.error("审核失败");
    }

    /**
     * 搜索老师（用于学生选择班主任）
     */
    @GetMapping("/search-teachers")
    @Operation(summary = "搜索老师", description = "学生搜索老师用于申请加入班级")
    public ApiResult<List<Map<String, Object>>> searchTeachers(
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {

        // 如果有关键词，需要先查询匹配的用户，再找到对应的老师
        List<Teacher> teachers;
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 先通过关键词查询匹配的用户
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.like(User::getRealName, keyword);
            List<User> users = userMapper.selectList(userWrapper);
            List<Long> userIds = users.stream().map(User::getId).toList();

            // 构建查询条件
            LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();

            // 如果找到匹配的用户，查询这些用户对应的老师
            if (!userIds.isEmpty()) {
                wrapper.in(Teacher::getUserId, userIds);
            }

            // 同时也匹配工号
            if (!userIds.isEmpty()) {
                wrapper.or().like(Teacher::getTeacherNumber, keyword);
            } else {
                wrapper.like(Teacher::getTeacherNumber, keyword);
            }

            teachers = teacherMapper.selectList(wrapper);
        } else {
            // 没有关键词，返回所有老师
            LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
            teachers = teacherMapper.selectList(wrapper);
        }

        // 补充用户信息
        List<Map<String, Object>> result = teachers.stream().map(teacher -> {
            Map<String, Object> map = new HashMap<>();
            map.put("teacherId", teacher.getId());
            map.put("teacherNumber", teacher.getTeacherNumber());
            map.put("title", teacher.getTitle());
            map.put("subject", teacher.getSubject());

            // 获取用户信息
            User user = userMapper.selectById(teacher.getUserId());
            if (user != null) {
                map.put("teacherName", user.getRealName());
                map.put("phone", user.getPhone());
                map.put("email", user.getEmail());
            } else {
                map.put("teacherName", "");
            }

            return map;
        }).toList();

        return ApiResult.success(result);
    }

    /**
     * 管理员获取所有班级申请列表
     */
    @GetMapping("/admin/all")
    @Operation(summary = "管理员获取所有申请", description = "管理员查看所有班级申请")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<PageResult<Map<String, Object>>> getAllApplications(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "状态：0-待审核，1-已通过，2-已驳回，null-全部") @RequestParam(required = false) Integer status) {

        Page<ClassApplication> page = new Page<>(current, size);
        LambdaQueryWrapper<ClassApplication> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(ClassApplication::getStatus, status);
        }

        wrapper.orderByDesc(ClassApplication::getCreatedAt);

        Page<ClassApplication> result = classApplicationMapper.selectPage(page, wrapper);

        // 构建返回数据
        List<Map<String, Object>> records = new java.util.ArrayList<>();
        for (ClassApplication app : result.getRecords()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", app.getId());
            map.put("studentId", app.getStudentId());
            map.put("studentName", app.getStudentName());
            map.put("classId", app.getClassId());
            map.put("className", app.getClassName());
            map.put("teacherId", app.getTeacherId());
            map.put("teacherName", app.getTeacherName());
            map.put("reason", app.getReason());
            map.put("status", app.getStatus());
            map.put("approvalComment", app.getApprovalComment());
            map.put("approvalTime", app.getApprovalTime());
            map.put("createdAt", app.getCreatedAt());
            records.add(map);
        }

        Page<Map<String, Object>> finalPage = new Page<>(current, size);
        finalPage.setRecords(records);
        finalPage.setTotal(result.getTotal());
        finalPage.setSize(result.getSize());
        finalPage.setCurrent(result.getCurrent());

        return ApiResult.success(PageResult.of(finalPage));
    }

    /**
     * 老师获取自己的学生列表
     */
    @GetMapping("/teacher/students")
    @Operation(summary = "老师获取学生列表", description = "老师查看自己班级的学生")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResult<PageResult<Map<String, Object>>> getTeacherStudents(
            @Parameter(description = "老师ID") @RequestParam Long teacherId,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "学生姓名搜索") @RequestParam(required = false) String keyword) {

        // 先通过老师ID获取老师信息，如果不存在则尝试通过user_id查询
        Teacher teacher = teacherMapper.selectById(teacherId);
        Long actualTeacherId = teacherId;
        if (teacher == null) {
            // 尝试通过user_id查询teacher记录
            LambdaQueryWrapper<Teacher> teacherWrapper = new LambdaQueryWrapper<>();
            teacherWrapper.eq(Teacher::getUserId, teacherId);
            teacher = teacherMapper.selectOne(teacherWrapper);

            if (teacher != null) {
                actualTeacherId = teacher.getId();
            }
        }

        if (teacher == null) {
            return ApiResult.error("教师不存在");
        }

        // 获取教师负责的班级
        LambdaQueryWrapper<com.education.platform.entity.Class> classWrapper = new LambdaQueryWrapper<>();
        classWrapper.eq(com.education.platform.entity.Class::getHeadTeacherId, actualTeacherId);
        List<com.education.platform.entity.Class> classes = classMapper.selectList(classWrapper);

        if (classes.isEmpty()) {
            Page<Map<String, Object>> emptyPage = new Page<>(current, size);
            emptyPage.setRecords(new java.util.ArrayList<>());
            return ApiResult.success(PageResult.of(emptyPage));
        }

        // 获取班级ID列表
        List<Long> classIds = classes.stream()
                .map(com.education.platform.entity.Class::getId)
                .toList();

        // 构建学生查询条件 - 优先从学生表直接查询
        Page<Student> page = new Page<>(current, size);
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Student::getClassId, classIds);
        wrapper.eq(Student::getDeleted, 0);

        // 关键词搜索
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 先通过用户表查找匹配的学生
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.like(User::getRealName, keyword);
            List<User> users = userMapper.selectList(userWrapper);
            List<Long> userIds = users.stream().map(User::getId).toList();

            if (!userIds.isEmpty()) {
                wrapper.in(Student::getUserId, userIds);
            } else {
                // 如果没有匹配的用户，返回空结果
                wrapper.eq(Student::getId, -1L); // 永远不会匹配
            }
        }

        Page<Student> result = studentMapper.selectPage(page, wrapper);

        // 构建返回数据
        List<Map<String, Object>> records = new java.util.ArrayList<>();
        for (Student student : result.getRecords()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", student.getId());
            map.put("studentNumber", student.getStudentNumber());
            map.put("classId", student.getClassId());
            map.put("guardianName", student.getGuardianName());
            map.put("guardianPhone", student.getGuardianPhone());
            map.put("status", student.getStatus());

            // 关联查询用户信息
            User user = userMapper.selectById(student.getUserId());
            if (user != null) {
                map.put("realName", user.getRealName());
                map.put("gender", user.getGender());
                map.put("phone", user.getPhone());
            }

            // 查询班级名称
            if (student.getClassId() != null) {
                com.education.platform.entity.Class classInfo = classMapper.selectById(student.getClassId());
                if (classInfo != null) {
                    map.put("className", classInfo.getClassName());
                }
            }

            records.add(map);
        }

        Page<Map<String, Object>> finalPage = new Page<>(current, size);
        finalPage.setRecords(records);
        finalPage.setTotal(result.getTotal());
        finalPage.setSize(result.getSize());
        finalPage.setCurrent(result.getCurrent());

        return ApiResult.success(PageResult.of(finalPage));
    }

    /**
     * 获取老师管理的班级列表
     */
    @GetMapping("/teacher/classes")
    @Operation(summary = "获取老师管理的班级列表", description = "获取指定老师作为班主任的班级列表")
    public ApiResult<List<Map<String, Object>>> getTeacherClasses(
            @Parameter(description = "老师ID") @RequestParam Long teacherId) {

        // 先通过老师ID查询Teacher记录
        Teacher teacher = teacherMapper.selectById(teacherId);
        if (teacher == null) {
            // 尝试通过user_id查询
            LambdaQueryWrapper<Teacher> teacherWrapper = new LambdaQueryWrapper<>();
            teacherWrapper.eq(Teacher::getUserId, teacherId);
            teacher = teacherMapper.selectOne(teacherWrapper);

            if (teacher == null) {
                return ApiResult.error("老师不存在");
            }
        }

        Long actualTeacherId = teacher.getId();

        // 查询该老师作为班主任的所有班级
        LambdaQueryWrapper<com.education.platform.entity.Class> classWrapper = new LambdaQueryWrapper<>();
        classWrapper.eq(com.education.platform.entity.Class::getHeadTeacherId, actualTeacherId);
        classWrapper.eq(com.education.platform.entity.Class::getStatus, 1); // 正常状态的班级

        List<com.education.platform.entity.Class> classes = classMapper.selectList(classWrapper);

        // 构建返回数据
        List<Map<String, Object>> result = new ArrayList<>();
        for (com.education.platform.entity.Class cls : classes) {
            Map<String, Object> classInfo = new HashMap<>();
            classInfo.put("id", cls.getId());
            classInfo.put("className", cls.getClassName());
            classInfo.put("grade", cls.getGrade());
            classInfo.put("academicYear", cls.getAcademicYear());
            classInfo.put("studentCount", cls.getStudentCount());
            result.add(classInfo);
        }

        return ApiResult.success(result);
    }
}
