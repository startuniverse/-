package com.education.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.education.platform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 作业实体类
 *
 * @author Education Platform Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("assignment")
public class Assignment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 作业标题
     */
    private String title;

    /**
     * 科目：chinese/math/english/physics/chemistry/biology/history/geography
     */
    private String subject;

    /**
     * 作业内容
     */
    private String content;

    /**
     * 发布班级（JSON格式存储）
     */
    private String targetClasses;

    /**
     * 截止时间
     */
    private LocalDateTime deadline;

    /**
     * 难度等级：easy/medium/hard
     */
    private String difficulty;

    /**
     * 是否计分：true/false
     */
    private Boolean isGraded;

    /**
     * 补充说明
     */
    private String notes;

    /**
     * 附件路径（JSON格式存储）
     */
    private String attachments;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 学校ID
     */
    private Long schoolId;

    /**
     * 状态：进行中/已完成/已截止
     */
    private String status;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}
