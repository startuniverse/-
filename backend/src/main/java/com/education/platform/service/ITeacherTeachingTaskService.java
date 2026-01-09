package com.education.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.education.platform.entity.TeacherTeachingTask;

import java.util.List;
import java.util.Map;

/**
 * 教师教学任务服务接口
 *
 * @author Education Platform Team
 */
public interface ITeacherTeachingTaskService extends IService<TeacherTeachingTask> {

    /**
     * 根据教师ID查询教学任务
     */
    List<TeacherTeachingTask> listByTeacherId(Long teacherId);

    /**
     * 按学年学期查询
     */
    List<TeacherTeachingTask> listByPeriod(Long teacherId, String academicYear, Integer semester);

    /**
     * 添加教学任务
     */
    Boolean addTeachingTask(TeacherTeachingTask task);

    /**
     * 统计教学工作量
     */
    Map<String, Object> statisticsWorkload(Long teacherId);
}
