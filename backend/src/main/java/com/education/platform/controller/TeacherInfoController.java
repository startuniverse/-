package com.education.platform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.platform.common.ApiResult;
import com.education.platform.common.PageResult;
import com.education.platform.entity.*;
import com.education.platform.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教师信息管理控制器
 *
 * @author Education Platform Team
 */
@RestController
@RequestMapping("/teacher-info")
@Tag(name = "教师信息管理", description = "教师信息管理相关接口")
public class TeacherInfoController {

    @Autowired
    private ITeacherBasicService teacherBasicService;

    @Autowired
    private ITeacherPositionService teacherPositionService;

    @Autowired
    private ITeacherEducationService teacherEducationService;

    @Autowired
    private ITeacherQualificationService teacherQualificationService;

    @Autowired
    private ITeacherHonorService teacherHonorService;

    @Autowired
    private ITeacherAssessmentService teacherAssessmentService;

    @Autowired
    private ITeacherRewardPunishmentService teacherRewardPunishmentService;

    @Autowired
    private ITeacherEthicsService teacherEthicsService;

    @Autowired
    private ITeacherTrainingRecordService teacherTrainingRecordService;

    @Autowired
    private ITeacherEducationCreditService teacherEducationCreditService;

    @Autowired
    private ITeacherTeachingTaskService teacherTeachingTaskService;

    @Autowired
    private ITeacherResearchActivityService teacherResearchActivityService;

    @Autowired
    private ITeacherWorkloadStatisticsService teacherWorkloadStatisticsService;

    // ==================== 教师基础信息 ====================

    @GetMapping("/basic/list")
    @Operation(summary = "教师基础信息列表", description = "分页查询教师基础信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<PageResult<TeacherBasic>> listBasicInfo(
            @Parameter(description = "学校ID") @RequestParam(required = false) Long schoolId,
            @Parameter(description = "姓名模糊查询") @RequestParam(required = false) String keyword,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long size) {

        Page<TeacherBasic> page = new Page<>(current, size);
        LambdaQueryWrapper<TeacherBasic> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherBasic::getDeleted, 0);

        if (schoolId != null) {
            wrapper.eq(TeacherBasic::getSchoolId, schoolId);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(TeacherBasic::getTeacherName, keyword);
        }

        wrapper.orderByDesc(TeacherBasic::getCreatedAt);
        Page<TeacherBasic> result = teacherBasicService.page(page, wrapper);
        return ApiResult.success(PageResult.of(result));
    }

    @PostMapping("/basic/add")
    @Operation(summary = "新增教师基础信息", description = "新增教师基础信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> addBasicInfo(@RequestBody TeacherBasic teacherBasic) {
        try {
            teacherBasic.setDeleted(0);
            boolean result = teacherBasicService.save(teacherBasic);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("新增失败: " + e.getMessage());
        }
    }

    @PostMapping("/basic/update")
    @Operation(summary = "更新教师基础信息", description = "更新教师基础信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> updateBasicInfo(@RequestBody TeacherBasic teacherBasic) {
        try {
            boolean result = teacherBasicService.updateById(teacherBasic);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("更新失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/basic/delete")
    @Operation(summary = "删除教师基础信息", description = "删除教师基础信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> deleteBasicInfo(@Parameter(description = "ID") @RequestParam Long id) {
        try {
            boolean result = teacherBasicService.removeById(id);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("删除失败: " + e.getMessage());
        }
    }

    @GetMapping("/basic/{teacherId}")
    @Operation(summary = "根据教师ID查询基础信息", description = "查询单个教师的完整基础信息")
    @PreAuthorize("isAuthenticated()")
    public ApiResult<Map<String, Object>> getTeacherInfo(@PathVariable Long teacherId) {
        Map<String, Object> result = new HashMap<>();

        // 基础信息
        TeacherBasic basic = teacherBasicService.getByTeacherId(teacherId);
        result.put("basic", basic);

        if (basic != null) {
            // 岗位信息
            TeacherPosition position = teacherPositionService.getCurrentPosition(teacherId);
            result.put("position", position);

            // 教育背景
            List<TeacherEducation> educations = teacherEducationService.listByTeacherId(teacherId);
            result.put("educations", educations);

            // 资格职称
            List<TeacherQualification> qualifications = teacherQualificationService.listByTeacherId(teacherId);
            result.put("qualifications", qualifications);

            // 荣誉称号
            List<TeacherHonor> honors = teacherHonorService.listByTeacherId(teacherId);
            result.put("honors", honors);
        }

        return ApiResult.success(result);
    }

    // ==================== 岗位信息 ====================

    @GetMapping("/position/list")
    @Operation(summary = "岗位信息列表", description = "查询教师岗位信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<List<TeacherPosition>> listPositions(@Parameter(description = "教师ID") @RequestParam Long teacherId) {
        List<TeacherPosition> list = teacherPositionService.listByTeacherId(teacherId);
        return ApiResult.success(list);
    }

    @PostMapping("/position/add")
    @Operation(summary = "新增岗位信息", description = "新增教师岗位信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> addPosition(@RequestBody TeacherPosition position) {
        try {
            boolean result = teacherPositionService.addPositionChange(position);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("新增失败: " + e.getMessage());
        }
    }

    // ==================== 教育背景 ====================

    @GetMapping("/education/list")
    @Operation(summary = "教育背景列表", description = "查询教师教育背景")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<List<TeacherEducation>> listEducations(@Parameter(description = "教师ID") @RequestParam Long teacherId) {
        List<TeacherEducation> list = teacherEducationService.listByTeacherId(teacherId);
        return ApiResult.success(list);
    }

    @PostMapping("/education/add")
    @Operation(summary = "新增教育背景", description = "新增教师教育背景")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> addEducation(@RequestBody TeacherEducation education) {
        try {
            boolean result = teacherEducationService.addEducation(education);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("新增失败: " + e.getMessage());
        }
    }

    // ==================== 资格职称 ====================

    @GetMapping("/qualification/list")
    @Operation(summary = "资格职称列表", description = "查询教师资格职称")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<List<TeacherQualification>> listQualifications(@Parameter(description = "教师ID") @RequestParam Long teacherId) {
        List<TeacherQualification> list = teacherQualificationService.listByTeacherId(teacherId);
        return ApiResult.success(list);
    }

    @PostMapping("/qualification/add")
    @Operation(summary = "新增资格职称", description = "新增教师资格职称")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> addQualification(@RequestBody TeacherQualification qualification) {
        try {
            boolean result = teacherQualificationService.addQualification(qualification);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("新增失败: " + e.getMessage());
        }
    }

    // ==================== 荣誉称号 ====================

    @GetMapping("/honor/list")
    @Operation(summary = "荣誉称号列表", description = "查询教师荣誉称号")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<List<TeacherHonor>> listHonors(@Parameter(description = "教师ID") @RequestParam Long teacherId) {
        List<TeacherHonor> list = teacherHonorService.listByTeacherId(teacherId);
        return ApiResult.success(list);
    }

    @PostMapping("/honor/add")
    @Operation(summary = "新增荣誉称号", description = "新增教师荣誉称号")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> addHonor(@RequestBody TeacherHonor honor) {
        try {
            boolean result = teacherHonorService.addHonor(honor);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("新增失败: " + e.getMessage());
        }
    }

    // ==================== 考核记录 ====================

    @GetMapping("/assessment/list")
    @Operation(summary = "考核记录列表", description = "查询教师考核记录")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<List<TeacherAssessment>> listAssessments(@Parameter(description = "教师ID") @RequestParam Long teacherId) {
        List<TeacherAssessment> list = teacherAssessmentService.listByTeacherId(teacherId);
        return ApiResult.success(list);
    }

    @PostMapping("/assessment/add")
    @Operation(summary = "新增考核记录", description = "新增教师考核记录")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> addAssessment(@RequestBody TeacherAssessment assessment) {
        try {
            boolean result = teacherAssessmentService.addAssessment(assessment);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("新增失败: " + e.getMessage());
        }
    }

    @GetMapping("/assessment/statistics")
    @Operation(summary = "考核统计", description = "统计教师考核结果分布")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Map<String, Long>> statisticsAssessments(@Parameter(description = "教师ID") @RequestParam Long teacherId) {
        Map<String, Long> result = teacherAssessmentService.countAssessmentResults(teacherId);
        return ApiResult.success(result);
    }

    // ==================== 奖惩记录 ====================

    @GetMapping("/reward-punishment/list")
    @Operation(summary = "奖惩记录列表", description = "查询教师奖惩记录")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<List<TeacherRewardPunishment>> listRewardPunishments(
            @Parameter(description = "教师ID") @RequestParam Long teacherId,
            @Parameter(description = "类型：reward-奖励，punishment-惩罚") @RequestParam(required = false) String type) {

        List<TeacherRewardPunishment> list;
        if (type != null) {
            list = teacherRewardPunishmentService.listByType(teacherId, type);
        } else {
            list = teacherRewardPunishmentService.listByTeacherId(teacherId);
        }
        return ApiResult.success(list);
    }

    @PostMapping("/reward-punishment/add")
    @Operation(summary = "新增奖惩记录", description = "新增教师奖惩记录")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> addRewardPunishment(@RequestBody TeacherRewardPunishment record) {
        try {
            boolean result = teacherRewardPunishmentService.addRewardPunishment(record);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("新增失败: " + e.getMessage());
        }
    }

    // ==================== 师德考核 ====================

    @GetMapping("/ethics/list")
    @Operation(summary = "师德考核列表", description = "查询教师师德考核记录")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<List<TeacherEthics>> listEthics(@Parameter(description = "教师ID") @RequestParam Long teacherId) {
        List<TeacherEthics> list = teacherEthicsService.listByTeacherId(teacherId);
        return ApiResult.success(list);
    }

    @PostMapping("/ethics/add")
    @Operation(summary = "新增师德考核", description = "新增教师师德考核记录")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> addEthics(@RequestBody TeacherEthics ethics) {
        try {
            boolean result = teacherEthicsService.addEthics(ethics);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("新增失败: " + e.getMessage());
        }
    }

    @GetMapping("/ethics/statistics")
    @Operation(summary = "师德考核统计", description = "统计师德考核等级分布")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Map<String, Long>> statisticsEthics(@Parameter(description = "教师ID") @RequestParam Long teacherId) {
        Map<String, Long> result = teacherEthicsService.countEthicsLevels(teacherId);
        return ApiResult.success(result);
    }

    // ==================== 培训记录 ====================

    @GetMapping("/training/list")
    @Operation(summary = "培训记录列表", description = "查询教师培训记录")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<List<TeacherTrainingRecord>> listTrainings(
            @Parameter(description = "教师ID") @RequestParam Long teacherId,
            @Parameter(description = "分类：skill-教学技能，info-信息化，ethics-师德") @RequestParam(required = false) String category) {

        List<TeacherTrainingRecord> list;
        if (category != null) {
            list = teacherTrainingRecordService.listByCategory(teacherId, category);
        } else {
            list = teacherTrainingRecordService.listByTeacherId(teacherId);
        }
        return ApiResult.success(list);
    }

    @PostMapping("/training/add")
    @Operation(summary = "新增培训记录", description = "新增教师培训记录")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> addTraining(@RequestBody TeacherTrainingRecord record) {
        try {
            boolean result = teacherTrainingRecordService.addTrainingRecord(record);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("新增失败: " + e.getMessage());
        }
    }

    @GetMapping("/training/statistics")
    @Operation(summary = "培训统计", description = "统计培训学时")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Map<String, Object>> statisticsTrainings(@Parameter(description = "教师ID") @RequestParam Long teacherId) {
        Map<String, Object> result = teacherTrainingRecordService.statisticsTrainingHours(teacherId);
        return ApiResult.success(result);
    }

    // ==================== 继续教育学分 ====================

    @GetMapping("/credit/list")
    @Operation(summary = "学分记录列表", description = "查询继续教育学分记录")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<List<TeacherEducationCredit>> listCredits(
            @Parameter(description = "教师ID") @RequestParam Long teacherId,
            @Parameter(description = "学年度") @RequestParam(required = false) String academicYear) {

        List<TeacherEducationCredit> list;
        if (academicYear != null) {
            list = teacherEducationCreditService.listByAcademicYear(teacherId, academicYear);
        } else {
            list = teacherEducationCreditService.listByTeacherId(teacherId);
        }
        return ApiResult.success(list);
    }

    @PostMapping("/credit/add")
    @Operation(summary = "新增学分记录", description = "新增继续教育学分记录")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> addCredit(@RequestBody TeacherEducationCredit credit) {
        try {
            boolean result = teacherEducationCreditService.addEducationCredit(credit);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("新增失败: " + e.getMessage());
        }
    }

    @GetMapping("/credit/statistics")
    @Operation(summary = "学分统计", description = "统计继续教育学分")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Map<String, Object>> statisticsCredits(@Parameter(description = "教师ID") @RequestParam Long teacherId) {
        Map<String, Object> result = teacherEducationCreditService.statisticsCredits(teacherId);
        return ApiResult.success(result);
    }

    // ==================== 教学任务 ====================

    @GetMapping("/teaching-task/list")
    @Operation(summary = "教学任务列表", description = "查询教师教学任务")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<List<TeacherTeachingTask>> listTeachingTasks(
            @Parameter(description = "教师ID") @RequestParam Long teacherId,
            @Parameter(description = "学年度") @RequestParam(required = false) String academicYear,
            @Parameter(description = "学期") @RequestParam(required = false) Integer semester) {

        List<TeacherTeachingTask> list;
        if (academicYear != null && semester != null) {
            list = teacherTeachingTaskService.listByPeriod(teacherId, academicYear, semester);
        } else {
            list = teacherTeachingTaskService.listByTeacherId(teacherId);
        }
        return ApiResult.success(list);
    }

    @PostMapping("/teaching-task/add")
    @Operation(summary = "新增教学任务", description = "新增教师教学任务")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> addTeachingTask(@RequestBody TeacherTeachingTask task) {
        try {
            boolean result = teacherTeachingTaskService.addTeachingTask(task);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("新增失败: " + e.getMessage());
        }
    }

    @GetMapping("/teaching-task/statistics")
    @Operation(summary = "教学工作量统计", description = "统计教学工作量")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Map<String, Object>> statisticsTeachingTasks(@Parameter(description = "教师ID") @RequestParam Long teacherId) {
        Map<String, Object> result = teacherTeachingTaskService.statisticsWorkload(teacherId);
        return ApiResult.success(result);
    }

    // ==================== 教研活动 ====================

    @GetMapping("/research/list")
    @Operation(summary = "教研活动列表", description = "查询教师教研活动")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<List<TeacherResearchActivity>> listResearchActivities(
            @Parameter(description = "教师ID") @RequestParam Long teacherId,
            @Parameter(description = "活动类型") @RequestParam(required = false) String activityType) {

        List<TeacherResearchActivity> list;
        if (activityType != null) {
            list = teacherResearchActivityService.listByActivityType(teacherId, activityType);
        } else {
            list = teacherResearchActivityService.listByTeacherId(teacherId);
        }
        return ApiResult.success(list);
    }

    @PostMapping("/research/add")
    @Operation(summary = "新增教研活动", description = "新增教师教研活动")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> addResearchActivity(@RequestBody TeacherResearchActivity activity) {
        try {
            boolean result = teacherResearchActivityService.addResearchActivity(activity);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("新增失败: " + e.getMessage());
        }
    }

    @GetMapping("/research/statistics")
    @Operation(summary = "教研活动统计", description = "统计教研活动")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Map<String, Long>> statisticsResearch(@Parameter(description = "教师ID") @RequestParam Long teacherId) {
        Map<String, Long> result = teacherResearchActivityService.statisticsActivities(teacherId);
        return ApiResult.success(result);
    }

    // ==================== 工作量统计 ====================

    @GetMapping("/workload/list")
    @Operation(summary = "工作量统计列表", description = "查询教师工作量统计")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<List<TeacherWorkloadStatistics>> listWorkloadStatistics(
            @Parameter(description = "教师ID") @RequestParam Long teacherId,
            @Parameter(description = "统计年度") @RequestParam(required = false) String statisticalYear,
            @Parameter(description = "统计周期：semester-学期，year-学年") @RequestParam(required = false) String statisticalPeriod) {

        List<TeacherWorkloadStatistics> list;
        if (statisticalYear != null && statisticalPeriod != null) {
            TeacherWorkloadStatistics workload = teacherWorkloadStatisticsService.getByPeriod(teacherId, statisticalYear, statisticalPeriod);
            list = workload != null ? List.of(workload) : List.of();
        } else {
            list = teacherWorkloadStatisticsService.listByTeacherId(teacherId);
        }
        return ApiResult.success(list);
    }

    @PostMapping("/workload/add")
    @Operation(summary = "新增工作量统计", description = "新增教师工作量统计")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Boolean> addWorkloadStatistics(@RequestBody TeacherWorkloadStatistics statistics) {
        try {
            boolean result = teacherWorkloadStatisticsService.addWorkloadStatistics(statistics);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error("新增失败: " + e.getMessage());
        }
    }

    @GetMapping("/workload/trend")
    @Operation(summary = "工作量趋势", description = "查询工作量趋势")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<List<TeacherWorkloadStatistics>> getWorkloadTrend(
            @Parameter(description = "教师ID") @RequestParam Long teacherId,
            @Parameter(description = "年数") @RequestParam(defaultValue = "5") int years) {

        List<TeacherWorkloadStatistics> result = teacherWorkloadStatisticsService.getWorkloadTrend(teacherId, years);
        return ApiResult.success(result);
    }

    // ==================== 综合查询 ====================

    @GetMapping("/comprehensive/{teacherId}")
    @Operation(summary = "教师综合信息", description = "获取教师完整信息（所有模块）")
    @PreAuthorize("isAuthenticated()")
    public ApiResult<Map<String, Object>> getComprehensiveInfo(@PathVariable Long teacherId) {
        Map<String, Object> result = new HashMap<>();

        // 基础信息
        TeacherBasic basic = teacherBasicService.getByTeacherId(teacherId);
        result.put("basic", basic);

        if (basic != null) {
            // 岗位信息
            TeacherPosition position = teacherPositionService.getCurrentPosition(teacherId);
            result.put("position", position);

            // 教育背景
            result.put("educations", teacherEducationService.listByTeacherId(teacherId));

            // 资格职称
            result.put("qualifications", teacherQualificationService.listByTeacherId(teacherId));

            // 荣誉称号
            result.put("honors", teacherHonorService.listByTeacherId(teacherId));

            // 考核记录
            result.put("assessments", teacherAssessmentService.listByTeacherId(teacherId));

            // 奖惩记录
            result.put("rewardPunishments", teacherRewardPunishmentService.listByTeacherId(teacherId));

            // 师德考核
            result.put("ethics", teacherEthicsService.listByTeacherId(teacherId));

            // 培训记录
            result.put("trainings", teacherTrainingRecordService.listByTeacherId(teacherId));

            // 学分记录
            result.put("credits", teacherEducationCreditService.listByTeacherId(teacherId));

            // 教学任务
            result.put("teachingTasks", teacherTeachingTaskService.listByTeacherId(teacherId));

            // 教研活动
            result.put("researchActivities", teacherResearchActivityService.listByTeacherId(teacherId));

            // 工作量统计
            result.put("workloadStatistics", teacherWorkloadStatisticsService.listByTeacherId(teacherId));
        }

        return ApiResult.success(result);
    }
}
