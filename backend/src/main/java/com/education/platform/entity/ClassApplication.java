package com.education.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 班级申请实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("class_application")
public class ClassApplication implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 申请ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 学生ID
     */
    @TableField("student_id")
    private Long studentId;

    /**
     * 学生姓名
     */
    @TableField("student_name")
    private String studentName;

    /**
     * 班级ID
     */
    @TableField("class_id")
    private Long classId;

    /**
     * 班级名称
     */
    @TableField("class_name")
    private String className;

    /**
     * 班主任ID
     */
    @TableField("teacher_id")
    private Long teacherId;

    /**
     * 班主任姓名
     */
    @TableField("teacher_name")
    private String teacherName;

    /**
     * 申请原因
     */
    @TableField("reason")
    private String reason;

    /**
     * 审核状态: 0-待审核, 1-已通过, 2-已驳回
     */
    @TableField("status")
    private Integer status;

    /**
     * 审核意见
     */
    @TableField("approval_comment")
    private String approvalComment;

    /**
     * 审核时间
     */
    @TableField("approval_time")
    private LocalDateTime approvalTime;

    /**
     * 逻辑删除: 0-未删除, 1-已删除
     */
    @TableField("deleted")
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
