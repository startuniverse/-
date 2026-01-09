package com.education.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.education.platform.entity.TeacherEducationCredit;

import java.util.List;
import java.util.Map;

/**
 * 教师继续教育学分服务接口
 *
 * @author Education Platform Team
 */
public interface ITeacherEducationCreditService extends IService<TeacherEducationCredit> {

    /**
     * 根据教师ID查询学分记录
     */
    List<TeacherEducationCredit> listByTeacherId(Long teacherId);

    /**
     * 按学年度查询
     */
    List<TeacherEducationCredit> listByAcademicYear(Long teacherId, String academicYear);

    /**
     * 添加学分记录
     */
    Boolean addEducationCredit(TeacherEducationCredit credit);

    /**
     * 统计学分
     */
    Map<String, Object> statisticsCredits(Long teacherId);
}
