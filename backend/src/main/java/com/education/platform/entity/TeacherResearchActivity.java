package com.education.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.education.platform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 教师教研活动实体类
 *
 * @author Education Platform Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("teacher_research_activity")
public class TeacherResearchActivity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 活动类型: lecture-讲座, seminar-研讨会, training-培训, competition-竞赛, project-课题研究
     */
    private String activityType;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 活动日期
     */
    private LocalDate activityDate;

    /**
     * 活动级别: school-校级, district-区级, city-市级, provincial-省级, national-国家级
     */
    private String activityLevel;

    /**
     * 担任角色: host-主持人, speaker-主讲人, participant-参与者, organizer-组织者
     */
    private String role;

    /**
     * 学时/学分
     */
    private Integer creditHours;

    /**
     * 成果描述
     */
    private String achievement;

    /**
     * 证明文件URL
     */
    private String proofUrl;

    /**
     * 审核状态: pending-待审核, approved-已通过, rejected-已驳回
     */
    private String reviewStatus;

    /**
     * 审核人ID
     */
    private Long reviewerId;

    /**
     * 审核人姓名
     */
    private String reviewerName;

    /**
     * 审核日期
     */
    private LocalDate reviewDate;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}
