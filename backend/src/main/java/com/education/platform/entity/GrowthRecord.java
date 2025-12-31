package com.education.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.education.platform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 学生成长记录实体类
 *
 * @author Education Platform Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("growth_record")
public class GrowthRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 记录日期
     */
    private LocalDate recordDate;

    /**
     * 分类：achievement/behavior/special
     */
    private String category;

    /**
     * 标题
     */
    private String title;

    /**
     * 详细描述
     */
    private String description;

    /**
     * 证据文件URL
     */
    private String evidenceUrl;

    /**
     * 记录教师ID
     */
    private Long teacherId;

    /**
     * 状态：1-正常
     */
    private Integer status;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}
