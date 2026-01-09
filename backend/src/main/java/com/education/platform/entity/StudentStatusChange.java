package com.education.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.education.platform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 学籍异动实体类
 * 用于记录学生的休学、转学等学籍变动申请
 *
 * @author Education Platform Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("student_status_change")
public class StudentStatusChange extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 用户ID（申请人）
     */
    private Long userId;

    /**
     * 异动类型：1-休学，2-转学，3-复学，4-退学，5-其他
     */
    private Integer changeType;

    /**
     * 异动原因
     */
    private String reason;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期（休学等需要）
     */
    private LocalDate endDate;

    /**
     * 目标学校（转学需要）
     */
    private String targetSchool;

    /**
     * 审核状态：0-待审核，1-已通过，2-已驳回
     */
    private Integer status;

    /**
     * 审核意见
     */
    private String approvalComment;

    /**
     * 审核人ID
     */
    private Long approverId;

    /**
     * 审核时间
     */
    private java.time.LocalDateTime approvalTime;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}
