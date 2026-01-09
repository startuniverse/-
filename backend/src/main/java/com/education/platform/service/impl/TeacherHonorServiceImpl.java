package com.education.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.platform.entity.TeacherHonor;
import com.education.platform.mapper.TeacherHonorMapper;
import com.education.platform.service.ITeacherHonorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 教师荣誉称号服务实现
 *
 * @author Education Platform Team
 */
@Slf4j
@Service
public class TeacherHonorServiceImpl extends ServiceImpl<TeacherHonorMapper, TeacherHonor> implements ITeacherHonorService {

    @Override
    public List<TeacherHonor> listByTeacherId(Long teacherId) {
        LambdaQueryWrapper<TeacherHonor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherHonor::getTeacherId, teacherId)
              .eq(TeacherHonor::getDeleted, 0)
              .orderByDesc(TeacherHonor::getAwardDate);
        return this.list(wrapper);
    }

    @Override
    public List<TeacherHonor> listByHonorType(Long teacherId, String honorType) {
        LambdaQueryWrapper<TeacherHonor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherHonor::getTeacherId, teacherId)
              .eq(TeacherHonor::getHonorType, honorType)
              .eq(TeacherHonor::getDeleted, 0)
              .orderByDesc(TeacherHonor::getAwardDate);
        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addHonor(TeacherHonor honor) {
        honor.setDeleted(0);
        boolean result = this.save(honor);
        if (result) {
            log.info("添加荣誉称号成功，教师ID：{}", honor.getTeacherId());
        }
        return result;
    }
}
