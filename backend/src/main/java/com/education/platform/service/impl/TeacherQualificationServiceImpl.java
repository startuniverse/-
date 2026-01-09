package com.education.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.platform.entity.TeacherQualification;
import com.education.platform.mapper.TeacherQualificationMapper;
import com.education.platform.service.ITeacherQualificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 教师资格职称服务实现
 *
 * @author Education Platform Team
 */
@Slf4j
@Service
public class TeacherQualificationServiceImpl extends ServiceImpl<TeacherQualificationMapper, TeacherQualification> implements ITeacherQualificationService {

    @Override
    public List<TeacherQualification> listByTeacherId(Long teacherId) {
        LambdaQueryWrapper<TeacherQualification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherQualification::getTeacherId, teacherId)
              .eq(TeacherQualification::getDeleted, 0)
              .orderByDesc(TeacherQualification::getObtainDate);
        return this.list(wrapper);
    }

    @Override
    public TeacherQualification getCurrentQualification(Long teacherId) {
        LambdaQueryWrapper<TeacherQualification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherQualification::getTeacherId, teacherId)
              .eq(TeacherQualification::getDeleted, 0)
              .orderByDesc(TeacherQualification::getObtainDate)
              .last("LIMIT 1");
        return this.getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addQualification(TeacherQualification qualification) {
        qualification.setDeleted(0);
        boolean result = this.save(qualification);
        if (result) {
            log.info("添加职称记录成功，教师ID：{}", qualification.getTeacherId());
        }
        return result;
    }
}
