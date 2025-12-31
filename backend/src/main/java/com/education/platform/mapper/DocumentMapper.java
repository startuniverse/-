package com.education.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.platform.entity.Document;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文档Mapper接口
 *
 * @author Education Platform Team
 */
@Mapper
public interface DocumentMapper extends BaseMapper<Document> {
}
