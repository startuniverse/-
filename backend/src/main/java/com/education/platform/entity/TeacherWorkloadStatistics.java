package com.education.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.education.platform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 教师工作量统计实体类
 *
 * @author Education Platform Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("teacher_workload_statistics")
public class TeacherWorkloadStatistics extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 统计年度
     */
    private String statisticalYear;

    /**
     * 统计周期: semester-学期, year-学年
     */
    private String statisticalPeriod;

    /**
     * 教学工作量(课时)
     */
    private BigDecimal teachingWorkload;

    /**
     * 教研工作量(小时)
     */
    private BigDecimal researchWorkload;

    /**
     * 管理工作量(小时)
     */
    private BigDecimal managementWorkload;

    /**
     * 指导工作量(小时)
     */
    private BigDecimal guidanceWorkload;

    /**
     * 其他工作量(小时)
     */
    private BigDecimal otherWorkload;

    /**
     * 总工作量
     */
    private BigDecimal totalWorkload;

    /**
     * 标准工作量
     */
    private BigDecimal standardWorkload;

    /**
     * 工作量完成率(%)
     */
    private BigDecimal completionRate;

    /**
     * 超课时费
     */
    private BigDecimal overtimePay;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}
