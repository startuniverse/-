package com.education.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.platform.entity.StudentStatusChange;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学籍异动Mapper接口
 *
 * @author Education Platform Team
 */
@Mapper
public interface StudentStatusChangeMapper extends BaseMapper<StudentStatusChange> {
}
