package com.education.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.education.platform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 考试记录实体类
 *
 * @author Education Platform Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("training_exam_record")
public class TrainingExamRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 考试ID
     */
    private Long examId;

    /**
     * 学员ID
     */
    private Long studentId;

    /**
     * 学员姓名
     */
    private String studentName;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 得分
     */
    private BigDecimal score;

    /**
     * 是否通过
     */
    private Boolean isPassed;

    /**
     * 答案JSON
     */
    private String answers;

    /**
     * 第几次尝试
     */
    private Integer attemptNumber;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}
