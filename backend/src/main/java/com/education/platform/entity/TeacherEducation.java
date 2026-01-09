package com.education.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.education.platform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 教师学历学位信息实体类
 *
 * @author Education Platform Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("teacher_education")
public class TeacherEducation extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 学历层次: bachelor-本科, master-硕士, doctor-博士
     */
    private String degreeLevel;

    /**
     * 学位名称: bachelor-学士, master-硕士, doctor-博士
     */
    private String degreeName;

    /**
     * 毕业院校
     */
    private String schoolName;

    /**
     * 专业
     */
    private String major;

    /**
     * 毕业时间
     */
    private LocalDate graduationDate;

    /**
     * 学习形式: fulltime-全日制, parttime-在职
     */
    private String educationForm;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}
