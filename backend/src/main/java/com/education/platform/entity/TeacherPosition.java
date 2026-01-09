package com.education.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.education.platform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 教师任职岗位信息实体类
 *
 * @author Education Platform Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("teacher_position")
public class TeacherPosition extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 学校ID
     */
    private Long schoolId;

    /**
     * 部门/院系
     */
    private String department;

    /**
     * 岗位类别: fulltime-专任教师, admin-行政人员, support-教辅人员
     */
    private String positionType;

    /**
     * 职务: principal-校长, director-主任, head-组长, teacher-教师
     */
    private String positionTitle;

    /**
     * 是否班主任: 0-否, 1-是
     */
    private Integer isHeadTeacher;

    /**
     * 班级ID(班主任)
     */
    private Long classId;

    /**
     * 任命时间
     */
    private LocalDate appointmentDate;

    /**
     * 转正时间
     */
    private LocalDate conversionDate;

    /**
     * 合同到期时间
     */
    private LocalDate contractExpiry;

    /**
     * 任教科目
     */
    private String teachingSubject;

    /**
     * 任教年级
     */
    private String teachingGrade;

    /**
     * 任教班级
     */
    private String teachingClass;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}
