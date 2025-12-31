package com.education.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.education.platform.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 学生实体类
 *
 * @author Education Platform Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("student")
public class Student extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 学号
     */
    private String studentNumber;

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 入学日期
     */
    private LocalDate enrollmentDate;

    /**
     * 毕业日期
     */
    private LocalDate graduationDate;

    /**
     * 状态：1-在读，2-毕业，3-转学，4-休学
     */
    private Integer status;

    /**
     * 监护人姓名
     */
    private String guardianName;

    /**
     * 监护人电话
     */
    private String guardianPhone;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;
}
