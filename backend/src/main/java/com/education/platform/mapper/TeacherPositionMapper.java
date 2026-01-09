package com.education.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.platform.entity.TeacherPosition;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教师岗位信息Mapper接口
 *
 * @author Education Platform Team
 */
@Mapper
public interface TeacherPositionMapper extends BaseMapper<TeacherPosition> {
}
