package com.education.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.platform.entity.Attendance;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考勤Mapper接口
 *
 * @author Education Platform Team
 */
@Mapper
public interface AttendanceMapper extends BaseMapper<Attendance> {
}
