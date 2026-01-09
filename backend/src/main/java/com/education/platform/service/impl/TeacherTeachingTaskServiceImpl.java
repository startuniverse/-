package com.education.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.platform.entity.TeacherTeachingTask;
import com.education.platform.mapper.TeacherTeachingTaskMapper;
import com.education.platform.service.ITeacherTeachingTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教师教学任务服务实现
 *
 * @author Education Platform Team
 */
@Slf4j
@Service
public class TeacherTeachingTaskServiceImpl extends ServiceImpl<TeacherTeachingTaskMapper, TeacherTeachingTask> implements ITeacherTeachingTaskService {

    @Override
    public List<TeacherTeachingTask> listByTeacherId(Long teacherId) {
        LambdaQueryWrapper<TeacherTeachingTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherTeachingTask::getTeacherId, teacherId)
              .eq(TeacherTeachingTask::getDeleted, 0)
              .orderByDesc(TeacherTeachingTask::getCreatedAt);
        return this.list(wrapper);
    }

    @Override
    public List<TeacherTeachingTask> listByPeriod(Long teacherId, String academicYear, Integer semester) {
        LambdaQueryWrapper<TeacherTeachingTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherTeachingTask::getTeacherId, teacherId)
              .eq(TeacherTeachingTask::getAcademicYear, academicYear)
              .eq(TeacherTeachingTask::getSemester, semester)
              .eq(TeacherTeachingTask::getDeleted, 0);
        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addTeachingTask(TeacherTeachingTask task) {
        task.setDeleted(0);
        boolean result = this.save(task);
        if (result) {
            log.info("添加教学任务成功，教师ID：{}", task.getTeacherId());
        }
        return result;
    }

    @Override
    public Map<String, Object> statisticsWorkload(Long teacherId) {
        List<TeacherTeachingTask> list = listByTeacherId(teacherId);

        BigDecimal totalHours = BigDecimal.ZERO;
        int studentCount = 0;
        int courseCount = list.size();

        for (TeacherTeachingTask task : list) {
            totalHours = totalHours.add(BigDecimal.valueOf(task.getTotalHours()));
            studentCount += task.getStudentCount();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalHours", totalHours);
        result.put("studentCount", studentCount);
        result.put("courseCount", courseCount);
        result.put("averageHoursPerCourse", courseCount > 0 ? totalHours.divide(BigDecimal.valueOf(courseCount), 2) : 0);

        return result;
    }
}
