package com.education.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.platform.entity.TeacherEducation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教师教育背景Mapper接口
 *
 * @author Education Platform Team
 */
@Mapper
public interface TeacherEducationMapper extends BaseMapper<TeacherEducation> {
}
