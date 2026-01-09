package com.education.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.education.platform.entity.TeacherWorkloadStatistics;

import java.util.List;
import java.util.Map;

/**
 * 教师工作量统计服务接口
 *
 * @author Education Platform Team
 */
public interface ITeacherWorkloadStatisticsService extends IService<TeacherWorkloadStatistics> {

    /**
     * 根据教师ID查询工作量统计
     */
    List<TeacherWorkloadStatistics> listByTeacherId(Long teacherId);

    /**
     * 按统计周期查询
     */
    TeacherWorkloadStatistics getByPeriod(Long teacherId, String statisticalYear, String statisticalPeriod);

    /**
     * 添加工作量统计
     */
    Boolean addWorkloadStatistics(TeacherWorkloadStatistics statistics);

    /**
     * 获取工作量趋势
     */
    List<TeacherWorkloadStatistics> getWorkloadTrend(Long teacherId, int years);
}
