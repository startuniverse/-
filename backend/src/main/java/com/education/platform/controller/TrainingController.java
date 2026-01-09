package com.education.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.platform.common.ApiResult;
import com.education.platform.common.PageResult;
import com.education.platform.entity.*;
import com.education.platform.mapper.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 在线学习培训控制器
 *
 * @author Education Platform Team
 */
@RestController
@RequestMapping("/training")
@Tag(name = "在线培训", description = "在线学习培训相关接口")
public class TrainingController {

    @Autowired
    private TrainingCourseMapper courseMapper;

    @Autowired
    private TrainingEnrollmentMapper enrollmentMapper;

    @Autowired
    private TrainingExamRecordMapper examRecordMapper;

    @Autowired
    private TrainingEvaluationMapper evaluationMapper;

    /**
     * 1. 获取培训课程列表
     */
    @GetMapping("/course/list")
    @Operation(summary = "课程列表", description = "获取培训课程列表")
    @PreAuthorize("isAuthenticated()")
    public ApiResult<PageResult<TrainingCourse>> listCourses(
            @Parameter(description = "分类") @RequestParam(required = false) String category,
            @Parameter(description = "科目") @RequestParam(required = false) String subject,
            @Parameter(description = "难度") @RequestParam(required = false) String difficultyLevel,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "12") Long size) {

        Page<TrainingCourse> page = new Page<>(current, size);
        LambdaQueryWrapper<TrainingCourse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TrainingCourse::getStatus, 1); // 只显示已发布的

        if (category != null && !category.isEmpty()) {
            wrapper.eq(TrainingCourse::getCategory, category);
        }
        if (subject != null && !subject.isEmpty()) {
            wrapper.eq(TrainingCourse::getSubject, subject);
        }
        if (difficultyLevel != null && !difficultyLevel.isEmpty()) {
            wrapper.eq(TrainingCourse::getDifficultyLevel, difficultyLevel);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(TrainingCourse::getTitle, keyword)
                   .or()
                   .like(TrainingCourse::getDescription, keyword);
        }

        wrapper.orderByDesc(TrainingCourse::getUploadTime);

        Page<TrainingCourse> result = courseMapper.selectPage(page, wrapper);
        return ApiResult.success(PageResult.of(result));
    }

    /**
     * 2. 获取课程详情
     */
    @GetMapping("/course/detail/{id}")
    @Operation(summary = "课程详情", description = "获取课程详情并增加浏览次数")
    @PreAuthorize("isAuthenticated()")
    public ApiResult<TrainingCourse> getCourseDetail(@PathVariable Long id) {
        TrainingCourse course = courseMapper.selectById(id);
        if (course == null) {
            return ApiResult.error("课程不存在");
        }

        if (course.getStatus() != 1) {
            return ApiResult.error("课程未发布");
        }

        // 增加浏览次数
        course.setViewCount(course.getViewCount() + 1);
        courseMapper.updateById(course);

        return ApiResult.success(course);
    }

    /**
     * 3. 报名课程
     */
    @PostMapping("/enroll")
    @Operation(summary = "报名课程", description = "学员报名培训课程")
    @PreAuthorize("isAuthenticated()")
    public ApiResult<Boolean> enrollCourse(@RequestBody TrainingEnrollment enrollment) {
        // 检查是否已报名
        LambdaQueryWrapper<TrainingEnrollment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TrainingEnrollment::getCourseId, enrollment.getCourseId())
               .eq(TrainingEnrollment::getStudentId, enrollment.getStudentId());

        if (enrollmentMapper.selectCount(wrapper) > 0) {
            return ApiResult.error("您已经报名该课程");
        }

        // 获取课程信息
        TrainingCourse course = courseMapper.selectById(enrollment.getCourseId());
        if (course == null || course.getStatus() != 1) {
            return ApiResult.error("课程不可用");
        }

        // 设置报名信息
        enrollment.setEnrollmentTime(LocalDateTime.now());
        enrollment.setStatus("enrolled");
        enrollment.setProgress(0);

        int result = enrollmentMapper.insert(enrollment);

        // 更新课程报名人数
        course.setEnrollCount(course.getEnrollCount() + 1);
        courseMapper.updateById(course);

        return ApiResult.success(result > 0);
    }

    /**
     * 4. 获取我的课程列表
     */
    @GetMapping("/my-courses")
    @Operation(summary = "我的课程", description = "获取我报名的课程")
    @PreAuthorize("isAuthenticated()")
    public ApiResult<List<Map<String, Object>>> getMyCourses(
            @Parameter(description = "学员ID") @RequestParam Long studentId) {

        LambdaQueryWrapper<TrainingEnrollment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TrainingEnrollment::getStudentId, studentId)
               .orderByDesc(TrainingEnrollment::getEnrollmentTime);

        List<TrainingEnrollment> enrollments = enrollmentMapper.selectList(wrapper);

        // 组装课程信息
        List<Map<String, Object>> result = enrollments.stream().map(enrollment -> {
            Map<String, Object> map = new HashMap<>();
            TrainingCourse course = courseMapper.selectById(enrollment.getCourseId());
            map.put("enrollment", enrollment);
            map.put("course", course);
            return map;
        }).toList();

        return ApiResult.success(result);
    }

    /**
     * 5. 提交考试记录
     */
    @PostMapping("/exam/submit")
    @Operation(summary = "提交考试", description = "提交考试记录")
    @PreAuthorize("isAuthenticated()")
    public ApiResult<Map<String, Object>> submitExam(@RequestBody TrainingExamRecord record) {
        // 设置开始时间（如果未设置）
        if (record.getStartTime() == null) {
            record.setStartTime(LocalDateTime.now());
        }

        // 计算分数（简化处理，实际应根据答案验证）
        BigDecimal score = record.getScore();

        // 检查是否通过（60分及格）
        boolean isPassed = score.compareTo(new BigDecimal(60)) >= 0;

        record.setEndTime(LocalDateTime.now());
        record.setScore(score);
        record.setIsPassed(isPassed);
        record.setAttemptNumber(1);

        int result = examRecordMapper.insert(record);

        // 如果通过，更新学员报名表的分数和证书
        if (isPassed) {
            // 查找对应的报名记录
            LambdaQueryWrapper<TrainingEnrollment> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TrainingEnrollment::getCourseId, record.getExamId())
                   .eq(TrainingEnrollment::getStudentId, record.getStudentId());

            TrainingEnrollment enrollment = enrollmentMapper.selectOne(wrapper);
            if (enrollment != null) {
                enrollment.setScore(score);
                enrollment.setStatus("completed");
                enrollment.setCompletionTime(LocalDateTime.now());
                // 生成证书URL
                enrollment.setCertificateUrl("/api/v1/training/certificate/" + record.getId());
                enrollmentMapper.updateById(enrollment);

                // 更新课程完成人数
                TrainingCourse course = courseMapper.selectById(record.getExamId());
                if (course != null) {
                    course.setCompletionCount(course.getCompletionCount() + 1);
                    courseMapper.updateById(course);
                }
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", result > 0);
        response.put("score", score);
        response.put("isPassed", isPassed);

        return ApiResult.success(response);
    }

    /**
     * 6. 课程评价
     */
    @PostMapping("/evaluate")
    @Operation(summary = "课程评价", description = "对课程进行评价")
    @PreAuthorize("isAuthenticated()")
    public ApiResult<Boolean> evaluateCourse(@RequestBody TrainingEvaluation evaluation) {
        // 检查是否已评价
        LambdaQueryWrapper<TrainingEvaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TrainingEvaluation::getCourseId, evaluation.getCourseId())
               .eq(TrainingEvaluation::getStudentId, evaluation.getStudentId());

        if (evaluationMapper.selectCount(wrapper) > 0) {
            return ApiResult.error("您已经评价过该课程");
        }

        evaluation.setCreatedAt(LocalDateTime.now());
        int result = evaluationMapper.insert(evaluation);

        return ApiResult.success(result > 0);
    }

    /**
     * 7. 管理员获取所有培训课程（包含所有状态）
     */
    @GetMapping("/admin/courses")
    @Operation(summary = "管理员课程列表", description = "管理员查看所有培训课程")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<PageResult<TrainingCourse>> adminListCourses(
            @Parameter(description = "分类") @RequestParam(required = false) String category,
            @Parameter(description = "科目") @RequestParam(required = false) String subject,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long size) {
        try {
            Page<TrainingCourse> page = new Page<>(current, size);
            LambdaQueryWrapper<TrainingCourse> wrapper = new LambdaQueryWrapper<>();

            if (category != null && !category.isEmpty()) {
                wrapper.eq(TrainingCourse::getCategory, category);
            }
            if (subject != null && !subject.isEmpty()) {
                wrapper.eq(TrainingCourse::getSubject, subject);
            }
            if (status != null) {
                wrapper.eq(TrainingCourse::getStatus, status);
            }
            if (keyword != null && !keyword.isEmpty()) {
                wrapper.like(TrainingCourse::getTitle, keyword)
                       .or()
                       .like(TrainingCourse::getDescription, keyword);
            }

            wrapper.orderByDesc(TrainingCourse::getUploadTime);

            Page<TrainingCourse> result = courseMapper.selectPage(page, wrapper);
            return ApiResult.success(PageResult.of(result));
        } catch (Exception e) {
            System.err.println("获取课程列表失败: " + e.getMessage());
            // 返回空结果
            Page<TrainingCourse> emptyPage = new Page<>(current, size);
            emptyPage.setRecords(new java.util.ArrayList<>());
            emptyPage.setTotal(0);
            return ApiResult.success(PageResult.of(emptyPage));
        }
    }

    /**
     * 8. 管理员添加/编辑课程
     */
    @PostMapping("/admin/course/save")
    @Operation(summary = "保存课程", description = "管理员添加或编辑课程")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> saveCourse(@RequestBody TrainingCourse course) {
        if (course.getId() == null) {
            // 新增
            course.setUploadTime(LocalDateTime.now());
            course.setViewCount(0);
            course.setEnrollCount(0);
            course.setCompletionCount(0);
            int result = courseMapper.insert(course);
            return ApiResult.success(result > 0);
        } else {
            // 编辑
            course.setUpdatedAt(LocalDateTime.now());
            int result = courseMapper.updateById(course);
            return ApiResult.success(result > 0);
        }
    }

    /**
     * 9. 获取培训统计
     */
    @GetMapping("/admin/statistics")
    @Operation(summary = "培训统计", description = "培训数据统计")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Map<String, Object>> getStatistics() {
        try {
            List<TrainingCourse> courses = courseMapper.selectList(null);
            List<TrainingEnrollment> enrollments = enrollmentMapper.selectList(null);
            List<TrainingExamRecord> exams = examRecordMapper.selectList(null);

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalCourses", courses != null ? courses.size() : 0);
            stats.put("totalEnrollments", enrollments != null ? enrollments.size() : 0);
            stats.put("totalExams", exams != null ? exams.size() : 0);
            stats.put("totalViews", courses != null ? courses.stream().mapToInt(TrainingCourse::getViewCount).sum() : 0);

            long completed = 0;
            if (enrollments != null) {
                completed = enrollments.stream()
                        .filter(e -> "completed".equals(e.getStatus()))
                        .count();
            }
            stats.put("completedCourses", completed);

            return ApiResult.success(stats);
        } catch (Exception e) {
            System.err.println("培训统计失败: " + e.getMessage());
            // 返回默认值，避免前端出错
            Map<String, Object> defaultStats = new HashMap<>();
            defaultStats.put("totalCourses", 0);
            defaultStats.put("totalEnrollments", 0);
            defaultStats.put("totalExams", 0);
            defaultStats.put("totalViews", 0);
            defaultStats.put("completedCourses", 0);
            return ApiResult.success(defaultStats);
        }
    }

    /**
     * 10. 获取考试记录
     */
    @GetMapping("/exam/records")
    @Operation(summary = "考试记录", description = "获取学员考试记录")
    @PreAuthorize("isAuthenticated()")
    public ApiResult<List<TrainingExamRecord>> getExamRecords(
            @Parameter(description = "学员ID") @RequestParam Long studentId) {

        LambdaQueryWrapper<TrainingExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TrainingExamRecord::getStudentId, studentId)
               .orderByDesc(TrainingExamRecord::getEndTime);

        List<TrainingExamRecord> records = examRecordMapper.selectList(wrapper);
        return ApiResult.success(records);
    }

    /**
     * 11. 获取课程评价列表
     */
    @GetMapping("/evaluation/list")
    @Operation(summary = "评价列表", description = "获取课程评价列表")
    @PreAuthorize("isAuthenticated()")
    public ApiResult<List<TrainingEvaluation>> listEvaluations(
            @Parameter(description = "课程ID") @RequestParam Long courseId) {

        LambdaQueryWrapper<TrainingEvaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TrainingEvaluation::getCourseId, courseId)
               .orderByDesc(TrainingEvaluation::getCreatedAt);

        List<TrainingEvaluation> evaluations = evaluationMapper.selectList(wrapper);
        return ApiResult.success(evaluations);
    }

    /**
     * 12. 获取课程平均评分
     */
    @GetMapping("/course/rating/{id}")
    @Operation(summary = "课程评分", description = "获取课程平均评分")
    @PreAuthorize("isAuthenticated()")
    public ApiResult<Map<String, Object>> getCourseRating(@PathVariable Long id) {
        LambdaQueryWrapper<TrainingEvaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TrainingEvaluation::getCourseId, id);

        List<TrainingEvaluation> evaluations = evaluationMapper.selectList(wrapper);

        Map<String, Object> result = new HashMap<>();
        if (evaluations.isEmpty()) {
            result.put("averageRating", 0);
            result.put("totalEvaluations", 0);
        } else {
            double avg = evaluations.stream()
                    .mapToInt(TrainingEvaluation::getRating)
                    .average()
                    .orElse(0);
            result.put("averageRating", Math.round(avg * 10) / 10.0);
            result.put("totalEvaluations", evaluations.size());
        }

        return ApiResult.success(result);
    }
}
