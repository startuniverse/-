package com.education.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.education.platform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 数据分析结果实体类
 *
 * @author Education Platform Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("data_analysis")
public class DataAnalysis extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 分析类型：distribution/ratio/trend
     */
    private String analysisType;

    /**
     * 学校ID
     */
    private Long schoolId;

    /**
     * 结果数据 (JSON)
     */
    private String resultData;

    /**
     * 图表类型：bar/line/pie/map
     */
    private String chartType;

    /**
     * 分析日期
     */
    private LocalDate analysisDate;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}
