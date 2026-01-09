package com.education.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.education.platform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 教师教学任务实体类
 *
 * @author Education Platform Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("teacher_teaching_task")
public class TeacherTeachingTask extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 学年度
     */
    private String academicYear;

    /**
     * 学期: 1-第一学期, 2-第二学期
     */
    private Integer semester;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程代码
     */
    private String courseCode;

    /**
     * 教学班号
     */
    private String classNumber;

    /**
     * 授课对象年级
     */
    private String targetGrade;

    /**
     * 学生人数
     */
    private Integer studentCount;

    /**
     * 周学时
     */
    private Integer weeklyHours;

    /**
     * 总学时
     */
    private Integer totalHours;

    /**
     * 教学场所
     */
    private String teachingLocation;

    /**
     * 教学模式: online-线上, offline-线下, hybrid-混合
     */
    private String teachingMode;

    /**
     * 课程性质: required-必修, elective-选修, public-公选
     */
    private String courseNature;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}
