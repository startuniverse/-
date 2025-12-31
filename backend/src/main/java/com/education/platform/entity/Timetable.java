package com.education.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.education.platform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;

/**
 * 课程表实体类
 *
 * @author Education Platform Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("timetable")
public class Timetable extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 学校ID
     */
    private Long schoolId;

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 科目
     */
    private String subject;

    /**
     * 星期几：1-7
     */
    private Integer weekDay;

    /**
     * 节次：1-8
     */
    private Integer period;

    /**
     * 开始时间
     */
    private LocalTime startTime;

    /**
     * 结束时间
     */
    private LocalTime endTime;

    /**
     * 教室
     */
    private String classroom;

    /**
     * 教学周：all/odd/even
     */
    private String academicWeek;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}
