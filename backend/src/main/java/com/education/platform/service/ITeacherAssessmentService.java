package com.education.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.education.platform.entity.TeacherAssessment;

import java.util.List;
import java.util.Map;

/**
 * 教师考核服务接口
 *
 * @author Education Platform Team
 */
public interface ITeacherAssessmentService extends IService<TeacherAssessment> {

    /**
     * 根据教师ID查询考核记录
     */
    List<TeacherAssessment> listByTeacherId(Long teacherId);

    /**
     * 按年度查询考核
     */
    TeacherAssessment getByYear(Long teacherId, String assessmentYear);

    /**
     * 添加考核记录
     */
    Boolean addAssessment(TeacherAssessment assessment);

    /**
     * 统计考核结果分布
     */
    Map<String, Long> countAssessmentResults(Long teacherId);
}
