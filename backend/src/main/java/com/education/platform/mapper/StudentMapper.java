package com.education.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.platform.entity.Student;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生Mapper接口
 *
 * @author Education Platform Team
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {
}
