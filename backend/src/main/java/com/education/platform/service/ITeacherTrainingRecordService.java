package com.education.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.education.platform.entity.TeacherTrainingRecord;

import java.util.List;
import java.util.Map;

/**
 * 教师培训记录服务接口
 *
 * @author Education Platform Team
 */
public interface ITeacherTrainingRecordService extends IService<TeacherTrainingRecord> {

    /**
     * 根据教师ID查询培训记录
     */
    List<TeacherTrainingRecord> listByTeacherId(Long teacherId);

    /**
     * 按培训分类查询
     */
    List<TeacherTrainingRecord> listByCategory(Long teacherId, String category);

    /**
     * 添加培训记录
     */
    Boolean addTrainingRecord(TeacherTrainingRecord record);

    /**
     * 统计培训学时
     */
    Map<String, Object> statisticsTrainingHours(Long teacherId);
}
