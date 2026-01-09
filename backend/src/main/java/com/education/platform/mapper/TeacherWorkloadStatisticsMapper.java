package com.education.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.platform.entity.TeacherWorkloadStatistics;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教师工作量统计Mapper接口
 *
 * @author Education Platform Team
 */
@Mapper
public interface TeacherWorkloadStatisticsMapper extends BaseMapper<TeacherWorkloadStatistics> {
}
