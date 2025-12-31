package com.education.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.platform.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知公告Mapper接口
 *
 * @author Education Platform Team
 */
@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {
}
