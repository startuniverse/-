package com.education.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.education.platform.entity.ClassApplication;

import java.util.List;

/**
 * 班级申请服务接口
 */
public interface IClassApplicationService extends IService<ClassApplication> {

    /**
     * 学生创建班级申请
     */
    boolean createApplication(ClassApplication application);

    /**
     * 获取学生的申请列表
     */
    List<ClassApplication> getStudentApplications(Long studentId);

    /**
     * 获取老师的待审核申请列表
     */
    List<ClassApplication> getPendingApplications(Long teacherId);

    /**
     * 获取老师的所有申请列表
     */
    List<ClassApplication> getTeacherApplications(Long teacherId);

    /**
     * 老师审核申请
     */
    boolean reviewApplication(Long applicationId, Integer status, String approvalComment, Long teacherId);

    /**
     * 检查是否已存在申请
     */
    boolean existsApplication(Long studentId, Long classId);
}
