package com.education.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.education.platform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 教师奖惩记录实体类
 *
 * @author Education Platform Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("teacher_reward_punishment")
public class TeacherRewardPunishment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 类型: reward-奖励, punishment-惩罚
     */
    private String type;

    /**
     * 分类: excellent_teacher-优秀教师, excellent_head-优秀班主任, violation-违规, warning-警告
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
     * 颁发/处理机构
     */
    private String awardAgency;

    /**
     * 记录日期
     */
    private LocalDate recordDate;

    /**
     * 原因
     */
    private String reason;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}
