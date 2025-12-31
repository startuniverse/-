package com.education.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.education.platform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 班级实体类
 *
 * @author Education Platform Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("class")
public class Class extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 学校ID
     */
    private Long schoolId;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 班级编码
     */
    private String classCode;

    /**
     * 年级
     */
    private String grade;

    /**
     * 学年
     */
    private String academicYear;

    /**
     * 班主任ID
     */
    private Long headTeacherId;

    /**
     * 学生人数
     */
    private Integer studentCount;

    /**
     * 状态：1-正常，0-解散
     */
    private Integer status;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}
