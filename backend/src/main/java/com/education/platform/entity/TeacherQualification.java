package com.education.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.education.platform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 教师专业技术资格实体类
 *
 * @author Education Platform Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("teacher_qualification")
public class TeacherQualification extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 职称级别: junior-初级, intermediate-中级, senior-高级, professor-正高级
     */
    private String titleLevel;

    /**
     * 职称名称
     */
    private String titleName;

    /**
     * 取得时间
     */
    private LocalDate obtainDate;

    /**
     * 评审机构
     */
    private String approvalAgency;

    /**
     * 证书编号
     */
    private String certificateNumber;

    /**
     * 资格类型: teacher-教师资格证, professional-专业技术资格
     */
    private String qualificationType;

    /**
     * 任教学段: primary-小学, junior-初中, senior-高中
     */
    private String teachingStage;

    /**
     * 任教学科
     */
    private String teachingSubject;

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
