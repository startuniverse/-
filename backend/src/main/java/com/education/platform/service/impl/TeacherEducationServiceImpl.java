package com.education.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.platform.entity.TeacherEducation;
import com.education.platform.mapper.TeacherEducationMapper;
import com.education.platform.service.ITeacherEducationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 教师教育背景服务实现
 *
 * @author Education Platform Team
 */
@Slf4j
@Service
public class TeacherEducationServiceImpl extends ServiceImpl<TeacherEducationMapper, TeacherEducation> implements ITeacherEducationService {

    @Override
    public List<TeacherEducation> listByTeacherId(Long teacherId) {
        LambdaQueryWrapper<TeacherEducation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherEducation::getTeacherId, teacherId)
              .eq(TeacherEducation::getDeleted, 0)
              .orderByDesc(TeacherEducation::getGraduationDate);
        return this.list(wrapper);
    }

    @Override
    public TeacherEducation getHighestEducation(Long teacherId) {
        List<TeacherEducation> list = listByTeacherId(teacherId);
        if (list.isEmpty()) {
            return null;
        }

        // 按学历等级排序，获取最高学历
        // 简化处理：返回第一条记录（按毕业日期倒序）
        return list.get(0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addEducation(TeacherEducation education) {
        education.setDeleted(0);
        boolean result = this.save(education);
        if (result) {
            log.info("添加教育经历成功，教师ID：{}", education.getTeacherId());
        }
        return result;
    }
}
