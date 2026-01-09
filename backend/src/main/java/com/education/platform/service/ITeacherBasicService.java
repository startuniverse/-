package com.education.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.education.platform.entity.TeacherBasic;

import java.util.List;
import java.util.Map;

/**
 * 教师基础信息服务接口
 *
 * @author Education Platform Team
 */
public interface ITeacherBasicService extends IService<TeacherBasic> {

    /**
     * 根据教师ID查询教师基础信息
     */
    TeacherBasic getByTeacherId(Long teacherId);

    /**
     * 根据学校ID查询教师列表
     */
    List<TeacherBasic> listBySchoolId(Long schoolId);

    /**
     * 条件查询教师列表
     */
    List<TeacherBasic> searchTeachers(Map<String, Object> conditions);

    /**
     * 批量导入教师信息
     */
    Boolean batchImport(List<TeacherBasic> teacherList);

    /**
     * 更新教师状态
     */
    Boolean updateTeacherStatus(Long teacherId, Integer status);
}
