package com.education.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.education.platform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 通知公告实体类
 *
 * @author Education Platform Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("announcement")
public class Announcement extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 学校ID (null表示教育局级别)
     */
    private Long schoolId;

    /**
     * 班级ID (null表示全校)
     */
    private Long classId;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 类型：notice/announcement/emergency
     */
    private String type;

    /**
     * 发布者ID
     */
    private Long publisherId;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 过期日期
     */
    private java.time.LocalDate expiryDate;

    /**
     * 优先级：0-普通，1-紧急
     */
    private Integer priority;

    /**
     * 状态：1-已发布，0-草稿
     */
    private Integer status;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}
