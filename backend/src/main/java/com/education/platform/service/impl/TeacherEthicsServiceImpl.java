package com.education.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.platform.entity.TeacherEthics;
import com.education.platform.mapper.TeacherEthicsMapper;
import com.education.platform.service.ITeacherEthicsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教师师德考核服务实现
 *
 * @author Education Platform Team
 */
@Slf4j
@Service
public class TeacherEthicsServiceImpl extends ServiceImpl<TeacherEthicsMapper, TeacherEthics> implements ITeacherEthicsService {

    @Override
    public List<TeacherEthics> listByTeacherId(Long teacherId) {
        LambdaQueryWrapper<TeacherEthics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherEthics::getTeacherId, teacherId)
              .eq(TeacherEthics::getDeleted, 0)
              .orderByDesc(TeacherEthics::getEvaluationDate);
        return this.list(wrapper);
    }

    @Override
    public TeacherEthics getByYear(Long teacherId, String assessmentYear) {
        LambdaQueryWrapper<TeacherEthics> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherEthics::getTeacherId, teacherId)
              .eq(TeacherEthics::getAssessmentYear, assessmentYear)
              .eq(TeacherEthics::getDeleted, 0);
        return this.getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addEthics(TeacherEthics ethics) {
        ethics.setDeleted(0);
        boolean result = this.save(ethics);
        if (result) {
            log.info("添加师德考核记录成功，教师ID：{}", ethics.getTeacherId());
        }
        return result;
    }

    @Override
    public Map<String, Long> countEthicsLevels(Long teacherId) {
        List<TeacherEthics> list = listByTeacherId(teacherId);
        Map<String, Long> result = new HashMap<>();

        for (TeacherEthics ethics : list) {
            String key = ethics.getEthicsLevel();
            result.put(key, result.getOrDefault(key, 0L) + 1);
        }

        return result;
    }
}
