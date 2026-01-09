package com.education.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.platform.entity.TeacherWorkloadStatistics;
import com.education.platform.mapper.TeacherWorkloadStatisticsMapper;
import com.education.platform.service.ITeacherWorkloadStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 教师工作量统计服务实现
 *
 * @author Education Platform Team
 */
@Slf4j
@Service
public class TeacherWorkloadStatisticsServiceImpl extends ServiceImpl<TeacherWorkloadStatisticsMapper, TeacherWorkloadStatistics> implements ITeacherWorkloadStatisticsService {

    @Override
    public List<TeacherWorkloadStatistics> listByTeacherId(Long teacherId) {
        LambdaQueryWrapper<TeacherWorkloadStatistics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherWorkloadStatistics::getTeacherId, teacherId)
              .eq(TeacherWorkloadStatistics::getDeleted, 0)
              .orderByDesc(TeacherWorkloadStatistics::getStatisticalYear);
        return this.list(wrapper);
    }

    @Override
    public TeacherWorkloadStatistics getByPeriod(Long teacherId, String statisticalYear, String statisticalPeriod) {
        LambdaQueryWrapper<TeacherWorkloadStatistics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherWorkloadStatistics::getTeacherId, teacherId)
              .eq(TeacherWorkloadStatistics::getStatisticalYear, statisticalYear)
              .eq(TeacherWorkloadStatistics::getStatisticalPeriod, statisticalPeriod)
              .eq(TeacherWorkloadStatistics::getDeleted, 0);
        return this.getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addWorkloadStatistics(TeacherWorkloadStatistics statistics) {
        statistics.setDeleted(0);
        boolean result = this.save(statistics);
        if (result) {
            log.info("添加工作量统计成功，教师ID：{}", statistics.getTeacherId());
        }
        return result;
    }

    @Override
    public List<TeacherWorkloadStatistics> getWorkloadTrend(Long teacherId, int years) {
        LambdaQueryWrapper<TeacherWorkloadStatistics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherWorkloadStatistics::getTeacherId, teacherId)
              .eq(TeacherWorkloadStatistics::getDeleted, 0)
              .orderByDesc(TeacherWorkloadStatistics::getStatisticalYear)
              .last("LIMIT " + years);
        return this.list(wrapper);
    }
}
