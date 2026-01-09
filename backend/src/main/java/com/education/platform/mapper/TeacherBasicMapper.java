package com.education.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.platform.entity.TeacherBasic;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教师基础信息Mapper接口
 *
 * @author Education Platform Team
 */
@Mapper
public interface TeacherBasicMapper extends BaseMapper<TeacherBasic> {
}
