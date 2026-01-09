package com.education.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.education.platform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 培训课程实体类
 *
 * @author Education Platform Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("training_course")
public class TrainingCourse extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 课程标题
     */
    private String title;

    /**
     * 课程描述
     */
    private String description;

    /**
     * 课程分类：职业技能/安全教育/师德师风/专业技能
     */
    private String category;

    /**
     * 所属科目：语文/数学/英语/综合
     */
    private String subject;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 视频URL
     */
    private String videoUrl;

    /**
     * 课程时长(分钟)
     */
    private Integer duration;

    /**
     * 难度等级：beginner/intermediate/advanced
     */
    private String difficultyLevel;

    /**
     * 目标受众
     */
    private String targetAudience;

    /**
     * 讲师ID
     */
    private Long instructorId;

    /**
     * 讲师姓名
     */
    private String instructorName;

    /**
     * 状态：0-草稿，1-已发布，2-已下架
     */
    private Integer status;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 报名人数
     */
    private Integer enrollCount;

    /**
     * 完成人数
     */
    private Integer completionCount;

    /**
     * 开始日期
     */
    private LocalDateTime startDate;

    /**
     * 结束日期
     */
    private LocalDateTime endDate;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}
