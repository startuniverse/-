package com.education.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.platform.entity.ClassApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 班级申请Mapper接口
 */
@Mapper
public interface ClassApplicationMapper extends BaseMapper<ClassApplication> {

    /**
     * 获取学生的申请列表
     */
    List<ClassApplication> selectByStudentId(@Param("studentId") Long studentId);

    /**
     * 获取老师的待审核申请列表
     */
    List<ClassApplication> selectPendingByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * 获取老师的所有申请列表
     */
    List<ClassApplication> selectByTeacherId(@Param("teacherId") Long teacherId);
}
