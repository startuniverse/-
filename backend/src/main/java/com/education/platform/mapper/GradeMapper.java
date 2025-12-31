package com.education.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.platform.entity.Grade;
import org.apache.ibatis.annotations.Mapper;

/**
 * 成绩Mapper接口
 *
 * @author Education Platform Team
 */
@Mapper
public interface GradeMapper extends BaseMapper<Grade> {
}
