package com.education.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.education.platform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 会议实体类
 *
 * @author Education Platform Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("meeting")
public class Meeting extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 学校ID
     */
    private Long schoolId;

    /**
     * 会议标题
     */
    private String title;

    /**
     * 会议描述
     */
    private String description;

    /**
     * 会议室
     */
    private String room;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 组织者ID
     */
    private Long organizerId;

    /**
     * 状态：1-已安排，2-进行中，3-已完成，4-已取消
     */
    private Integer status;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}
