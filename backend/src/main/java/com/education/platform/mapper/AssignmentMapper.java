package com.education.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.platform.entity.Assignment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 作业Mapper接口
 *
 * @author Education Platform Team
 */
@Mapper
public interface AssignmentMapper extends BaseMapper<Assignment> {
}
