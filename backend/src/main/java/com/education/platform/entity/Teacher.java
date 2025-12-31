package com.education.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.education.platform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 教师实体类
 *
 * @author Education Platform Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("teacher")
public class Teacher extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 教师编号
     */
    private String teacherNumber;

    /**
     * 职称
     */
    private String title;

    /**
     * 所教科目
     */
    private String subject;

    /**
     * 入职日期
     */
    private LocalDate hireDate;

    /**
     * 状态：1-在职，2-离职
     */
    private Integer status;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}
