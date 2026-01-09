package com.education.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.education.platform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 教师师德考核实体类
 *
 * @author Education Platform Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("teacher_ethics")
public class TeacherEthics extends BaseEntity {

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
     * 师德等级: excellent-优秀, good-良好, qualified-合格, unqualified-不合格
     */
    private String ethicsLevel;

    /**
     * 评价记录
     */
    private String evaluationRecord;

    /**
     * 评价人ID
     */
    private Long evaluatorId;

    /**
     * 评价人姓名
     */
    private String evaluatorName;

    /**
     * 评价日期
     */
    private LocalDate evaluationDate;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}
