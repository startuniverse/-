package com.education.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.platform.entity.TeacherAssessment;
import com.education.platform.mapper.TeacherAssessmentMapper;
import com.education.platform.service.ITeacherAssessmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教师考核服务实现
 *
 * @author Education Platform Team
 */
@Slf4j
@Service
public class TeacherAssessmentServiceImpl extends ServiceImpl<TeacherAssessmentMapper, TeacherAssessment> implements ITeacherAssessmentService {

    @Override
    public List<TeacherAssessment> listByTeacherId(Long teacherId) {
        LambdaQueryWrapper<TeacherAssessment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherAssessment::getTeacherId, teacherId)
              .eq(TeacherAssessment::getDeleted, 0)
              .orderByDesc(TeacherAssessment::getAssessmentDate);
        return this.list(wrapper);
    }

    @Override
    public TeacherAssessment getByYear(Long teacherId, String assessmentYear) {
        LambdaQueryWrapper<TeacherAssessment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherAssessment::getTeacherId, teacherId)
              .eq(TeacherAssessment::getAssessmentYear, assessmentYear)
              .eq(TeacherAssessment::getDeleted, 0);
        return this.getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addAssessment(TeacherAssessment assessment) {
        assessment.setDeleted(0);
        boolean result = this.save(assessment);
        if (result) {
            log.info("添加考核记录成功，教师ID：{}", assessment.getTeacherId());
        }
        return result;
    }

    @Override
    public Map<String, Long> countAssessmentResults(Long teacherId) {
        List<TeacherAssessment> list = listByTeacherId(teacherId);
        Map<String, Long> result = new HashMap<>();

        for (TeacherAssessment assessment : list) {
            String key = assessment.getAssessmentResult();
            result.put(key, result.getOrDefault(key, 0L) + 1);
        }

        return result;
    }
}
