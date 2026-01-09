package com.education.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.education.platform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 教师考核实体类
 *
 * @author Education Platform Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("teacher_assessment")
public class TeacherAssessment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 考核年度
     */
    private String assessmentYear;

    /**
     * 考核结果: excellent-优秀, good-良好, qualified-合格, unqualified-不合格
     */
    private String assessmentResult;

    /**
     * 考核意见
     */
    private String assessmentOpinion;

    /**
     * 考核人ID
     */
    private Long assessorId;

    /**
     * 考核人姓名
     */
    private String assessorName;

    /**
     * 考核日期
     */
    private LocalDate assessmentDate;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}
