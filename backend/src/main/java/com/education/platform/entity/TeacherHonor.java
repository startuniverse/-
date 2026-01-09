package com.education.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.education.platform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 教师专业发展称号实体类
 *
 * @author Education Platform Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("teacher_honor")
public class TeacherHonor extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 称号类型: backbone-骨干教师, leader-学科带头人, master-名师, expert-专家
     */
    private String honorType;

    /**
     * 称号名称
     */
    private String honorName;

    /**
     * 获得日期
     */
    private LocalDate awardDate;

    /**
     * 颁发机构
     */
    private String awardAgency;

    /**
     * 有效期
     */
    private LocalDate expiryDate;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}
