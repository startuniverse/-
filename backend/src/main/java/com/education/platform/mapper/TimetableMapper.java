package com.education.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.platform.entity.Timetable;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程表Mapper接口
 *
 * @author Education Platform Team
 */
@Mapper
public interface TimetableMapper extends BaseMapper<Timetable> {
}
