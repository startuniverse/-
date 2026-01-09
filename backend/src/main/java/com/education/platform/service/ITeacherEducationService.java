package com.education.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.education.platform.entity.TeacherEducation;

import java.util.List;

/**
 * 教师教育背景服务接口
 *
 * @author Education Platform Team
 */
public interface ITeacherEducationService extends IService<TeacherEducation> {

    /**
     * 根据教师ID查询教育背景列表
     */
    List<TeacherEducation> listByTeacherId(Long teacherId);

    /**
     * 获取最高学历信息
     */
    TeacherEducation getHighestEducation(Long teacherId);

    /**
     * 添加教育经历
     */
    Boolean addEducation(TeacherEducation education);
}
