package com.education.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.education.platform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学员报名实体类
 *
 * @author Education Platform Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("training_enrollment")
public class TrainingEnrollment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 学员ID
     */
    private Long studentId;

    /**
     * 学员姓名
     */
    private String studentName;

    /**
     * 报名时间
     */
    private LocalDateTime enrollmentTime;

    /**
     * 学习进度(百分比)
     */
    private Integer progress;

    /**
     * 状态：enrolled-已报名, learning-学习中, completed-已完成, dropped-已退出
     */
    private String status;

    /**
     * 完成时间
     */
    private LocalDateTime completionTime;

    /**
     * 最终得分
     */
    private BigDecimal score;

    /**
     * 证书URL
     */
    private String certificateUrl;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}
