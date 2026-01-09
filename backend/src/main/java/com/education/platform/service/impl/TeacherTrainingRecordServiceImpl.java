package com.education.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.platform.entity.TeacherTrainingRecord;
import com.education.platform.mapper.TeacherTrainingRecordMapper;
import com.education.platform.service.ITeacherTrainingRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教师培训记录服务实现
 *
 * @author Education Platform Team
 */
@Slf4j
@Service
public class TeacherTrainingRecordServiceImpl extends ServiceImpl<TeacherTrainingRecordMapper, TeacherTrainingRecord> implements ITeacherTrainingRecordService {

    @Override
    public List<TeacherTrainingRecord> listByTeacherId(Long teacherId) {
        LambdaQueryWrapper<TeacherTrainingRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherTrainingRecord::getTeacherId, teacherId)
              .eq(TeacherTrainingRecord::getDeleted, 0)
              .orderByDesc(TeacherTrainingRecord::getTrainingDate);
        return this.list(wrapper);
    }

    @Override
    public List<TeacherTrainingRecord> listByCategory(Long teacherId, String category) {
        LambdaQueryWrapper<TeacherTrainingRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherTrainingRecord::getTeacherId, teacherId)
              .eq(TeacherTrainingRecord::getCategory, category)
              .eq(TeacherTrainingRecord::getDeleted, 0)
              .orderByDesc(TeacherTrainingRecord::getTrainingDate);
        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addTrainingRecord(TeacherTrainingRecord record) {
        record.setDeleted(0);
        boolean result = this.save(record);
        if (result) {
            log.info("添加培训记录成功，教师ID：{}", record.getTeacherId());
        }
        return result;
    }

    @Override
    public Map<String, Object> statisticsTrainingHours(Long teacherId) {
        List<TeacherTrainingRecord> list = listByTeacherId(teacherId);

        int totalHours = 0;
        Map<String, Integer> hoursByCategory = new HashMap<>();

        for (TeacherTrainingRecord record : list) {
            totalHours += record.getDuration();
            String category = record.getCategory();
            hoursByCategory.put(category, hoursByCategory.getOrDefault(category, 0) + record.getDuration());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalHours", totalHours);
        result.put("hoursByCategory", hoursByCategory);
        result.put("recordCount", list.size());

        return result;
    }
}
