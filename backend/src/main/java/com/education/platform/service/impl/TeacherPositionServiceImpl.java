package com.education.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.platform.entity.TeacherPosition;
import com.education.platform.mapper.TeacherPositionMapper;
import com.education.platform.service.ITeacherPositionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 教师岗位信息服务实现
 *
 * @author Education Platform Team
 */
@Slf4j
@Service
public class TeacherPositionServiceImpl extends ServiceImpl<TeacherPositionMapper, TeacherPosition> implements ITeacherPositionService {

    @Override
    public List<TeacherPosition> listByTeacherId(Long teacherId) {
        LambdaQueryWrapper<TeacherPosition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherPosition::getTeacherId, teacherId)
              .eq(TeacherPosition::getDeleted, 0)
              .orderByDesc(TeacherPosition::getAppointmentDate);
        return this.list(wrapper);
    }

    @Override
    public TeacherPosition getCurrentPosition(Long teacherId) {
        LambdaQueryWrapper<TeacherPosition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherPosition::getTeacherId, teacherId)
              .eq(TeacherPosition::getDeleted, 0)
              .orderByDesc(TeacherPosition::getAppointmentDate)
              .last("LIMIT 1");
        return this.getOne(wrapper);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addPositionChange(TeacherPosition position) {
        position.setDeleted(0);
        boolean result = this.save(position);
        if (result) {
            log.info("添加岗位变动记录成功，教师ID：{}", position.getTeacherId());
        }
        return result;
    }
}
