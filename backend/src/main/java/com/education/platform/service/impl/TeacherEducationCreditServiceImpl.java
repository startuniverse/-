package com.education.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.platform.entity.TeacherEducationCredit;
import com.education.platform.mapper.TeacherEducationCreditMapper;
import com.education.platform.service.ITeacherEducationCreditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教师继续教育学分服务实现
 *
 * @author Education Platform Team
 */
@Slf4j
@Service
public class TeacherEducationCreditServiceImpl extends ServiceImpl<TeacherEducationCreditMapper, TeacherEducationCredit> implements ITeacherEducationCreditService {

    @Override
    public List<TeacherEducationCredit> listByTeacherId(Long teacherId) {
        LambdaQueryWrapper<TeacherEducationCredit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherEducationCredit::getTeacherId, teacherId)
              .eq(TeacherEducationCredit::getDeleted, 0)
              .orderByDesc(TeacherEducationCredit::getCertificationDate);
        return this.list(wrapper);
    }

    @Override
    public List<TeacherEducationCredit> listByAcademicYear(Long teacherId, String academicYear) {
        LambdaQueryWrapper<TeacherEducationCredit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherEducationCredit::getTeacherId, teacherId)
              .eq(TeacherEducationCredit::getAcademicYear, academicYear)
              .eq(TeacherEducationCredit::getDeleted, 0)
              .orderByDesc(TeacherEducationCredit::getCertificationDate);
        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addEducationCredit(TeacherEducationCredit credit) {
        credit.setDeleted(0);
        boolean result = this.save(credit);
        if (result) {
            log.info("添加学分记录成功，教师ID：{}", credit.getTeacherId());
        }
        return result;
    }

    @Override
    public Map<String, Object> statisticsCredits(Long teacherId) {
        List<TeacherEducationCredit> list = listByTeacherId(teacherId);

        int totalCredits = 0;
        Map<String, Integer> creditsByType = new HashMap<>();

        for (TeacherEducationCredit credit : list) {
            if ("certified".equals(credit.getCertificationStatus())) {
                totalCredits += credit.getCreditHours();
                String type = credit.getCreditType();
                creditsByType.put(type, creditsByType.getOrDefault(type, 0) + credit.getCreditHours());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalCredits", totalCredits);
        result.put("creditsByType", creditsByType);
        result.put("certifiedCount", list.size());

        return result;
    }
}
