package com.education.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.platform.entity.Meeting;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会议Mapper接口
 *
 * @author Education Platform Team
 */
@Mapper
public interface MeetingMapper extends BaseMapper<Meeting> {
}
