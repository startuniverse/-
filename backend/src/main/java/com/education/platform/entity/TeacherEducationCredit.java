package com.education.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.education.platform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 教师继续教育学分实体类
 *
 * @author Education Platform Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("teacher_education_credit")
public class TeacherEducationCredit extends BaseEntity {

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
     * 学分类型: training-培训学习, research-教研活动, academic-学术研修, online-在线学习
     */
    private String creditType;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 学分数
     */
    private Integer creditHours;

    /**
     * 认证状态: pending-待认证, certified-已认证, rejected-已驳回
     */
    private String certificationStatus;

    /**
     * 认证日期
     */
    private LocalDate certificationDate;

    /**
     * 认证人ID
     */
    private Long certifierId;

    /**
     * 认证人姓名
     */
    private String certifierName;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}
