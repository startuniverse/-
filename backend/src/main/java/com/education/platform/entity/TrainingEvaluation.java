package com.education.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.education.platform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 培训评价实体类
 *
 * @author Education Platform Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("training_evaluation")
public class TrainingEvaluation extends BaseEntity {

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
     * 评分(1-5)
     */
    private Integer rating;

    /**
     * 评价内容
     */
    private String comment;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}
