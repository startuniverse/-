package com.education.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.education.platform.entity.TeacherQualification;

import java.util.List;

/**
 * 教师资格职称服务接口
 *
 * @author Education Platform Team
 */
public interface ITeacherQualificationService extends IService<TeacherQualification> {

    /**
     * 根据教师ID查询资格职称列表
     */
    List<TeacherQualification> listByTeacherId(Long teacherId);

    /**
     * 获取当前有效职称
     */
    TeacherQualification getCurrentQualification(Long teacherId);

    /**
     * 添加职称记录
     */
    Boolean addQualification(TeacherQualification qualification);
}
