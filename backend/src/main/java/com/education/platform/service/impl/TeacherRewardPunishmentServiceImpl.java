package com.education.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.platform.entity.TeacherRewardPunishment;
import com.education.platform.mapper.TeacherRewardPunishmentMapper;
import com.education.platform.service.ITeacherRewardPunishmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 教师奖惩记录服务实现
 *
 * @author Education Platform Team
 */
@Slf4j
@Service
public class TeacherRewardPunishmentServiceImpl extends ServiceImpl<TeacherRewardPunishmentMapper, TeacherRewardPunishment> implements ITeacherRewardPunishmentService {

    @Override
    public List<TeacherRewardPunishment> listByTeacherId(Long teacherId) {
        LambdaQueryWrapper<TeacherRewardPunishment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherRewardPunishment::getTeacherId, teacherId)
              .eq(TeacherRewardPunishment::getDeleted, 0)
              .orderByDesc(TeacherRewardPunishment::getRecordDate);
        return this.list(wrapper);
    }

    @Override
    public List<TeacherRewardPunishment> listByType(Long teacherId, String type) {
        LambdaQueryWrapper<TeacherRewardPunishment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherRewardPunishment::getTeacherId, teacherId)
              .eq(TeacherRewardPunishment::getType, type)
              .eq(TeacherRewardPunishment::getDeleted, 0)
              .orderByDesc(TeacherRewardPunishment::getRecordDate);
        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addRewardPunishment(TeacherRewardPunishment record) {
        record.setDeleted(0);
        boolean result = this.save(record);
        if (result) {
            log.info("添加奖惩记录成功，教师ID：{}", record.getTeacherId());
        }
        return result;
    }
}
