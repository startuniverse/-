package com.education.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.platform.entity.TrainingEnrollment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学员报名 Mapper
 *
 * @author Education Platform Team
 */
@Mapper
public interface TrainingEnrollmentMapper extends BaseMapper<TrainingEnrollment> {
}
