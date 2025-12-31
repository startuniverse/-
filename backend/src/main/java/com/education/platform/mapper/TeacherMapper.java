package com.education.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.platform.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教师Mapper接口
 *
 * @author Education Platform Team
 */
@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {
}
