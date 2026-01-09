package com.education.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.education.platform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 教师培训记录实体类
 *
 * @author Education Platform Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("teacher_training_record")
public class TeacherTrainingRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 培训项目名称
     */
    private String trainingName;

    /**
     * 培训日期
     */
    private LocalDate trainingDate;

    /**
     * 学时
     */
    private Integer duration;

    /**
     * 培训机构
     */
    private String trainingAgency;

    /**
     * 成绩/结业情况
     */
    private String result;

    /**
     * 证书URL
     */
    private String certificateUrl;

    /**
     * 培训分类: skill-教学技能, info-信息化, ethics-师德
     */
    private String category;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}
