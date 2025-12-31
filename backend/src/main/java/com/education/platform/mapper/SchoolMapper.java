package com.education.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.platform.entity.School;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学校Mapper接口
 *
 * @author Education Platform Team
 */
@Mapper
public interface SchoolMapper extends BaseMapper<School> {
}
