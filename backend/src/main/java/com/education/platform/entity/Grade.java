package com.education.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.education.platform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 成绩实体类
 *
 * @author Education Platform Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("grade")
public class Grade extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 科目
     */
    private String subject;

    /**
     * 考试类型：midterm/final/daily
     */
    private String examType;

    /**
     * 分数
     */
    private BigDecimal score;

    /**
     * 考试日期
     */
    private LocalDate examDate;

    /**
     * 学期
     */
    private String academicTerm;

    /**
     * 录入教师ID
     */
    private Long teacherId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}
