package com.education.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.education.platform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 办公资产实体类
 *
 * @author Education Platform Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("office_asset")
public class OfficeAsset extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 学校ID
     */
    private Long schoolId;

    /**
     * 资产名称
     */
    private String assetName;

    /**
     * 分类：equipment/furniture/electronic
     */
    private String category;

    /**
     * 规格型号
     */
    private String specification;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 单价
     */
    private BigDecimal unitPrice;

    /**
     * 总价值
     */
    private BigDecimal totalValue;

    /**
     * 采购日期
     */
    private LocalDate purchaseDate;

    /**
     * 存放位置
     */
    private String location;

    /**
     * 状态：1-正常，2-维修中，3-报废
     */
    private Integer status;

    /**
     * 责任人
     */
    private String responsiblePerson;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}
