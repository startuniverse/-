package com.education.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.education.platform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 文档实体类
 *
 * @author Education Platform Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("document")
public class Document extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 学校ID (null表示教育局)
     */
    private Long schoolId;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 文件URL
     */
    private String fileUrl;

    /**
     * 文件类型：pdf/doc/excel
     */
    private String fileType;

    /**
     * 分类：policy/notice/plan/report
     */
    private String category;

    /**
     * 作者ID
     */
    private Long authorId;

    /**
     * 状态：0-草稿，1-待审批，2-已发布，3-已驳回
     */
    private Integer status;

    /**
     * 审批人ID
     */
    private Long approvalId;

    /**
     * 审批时间
     */
    private LocalDateTime approvalTime;

    /**
     * 审批意见
     */
    private String approvalComment;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}
