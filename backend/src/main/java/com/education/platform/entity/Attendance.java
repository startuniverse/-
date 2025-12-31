package com.education.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.education.platform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 考勤实体类
 *
 * @author Education Platform Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("attendance")
public class Attendance extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 学校ID
     */
    private Long schoolId;

    /**
     * 记录日期
     */
    private java.time.LocalDate recordDate;

    /**
     * 签到时间
     */
    private LocalDateTime checkInTime;

    /**
     * 签退时间
     */
    private LocalDateTime checkOutTime;

    /**
     * 状态：normal/late/early/absent
     */
    private String status;

    /**
     * 签到位置
     */
    private String location;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}
