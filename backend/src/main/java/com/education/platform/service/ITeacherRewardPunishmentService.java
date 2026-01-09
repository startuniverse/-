package com.education.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.education.platform.entity.TeacherRewardPunishment;

import java.util.List;

/**
 * 教师奖惩记录服务接口
 *
 * @author Education Platform Team
 */
public interface ITeacherRewardPunishmentService extends IService<TeacherRewardPunishment> {

    /**
     * 根据教师ID查询奖惩记录
     */
    List<TeacherRewardPunishment> listByTeacherId(Long teacherId);

    /**
     * 按类型查询 (reward/punishment)
     */
    List<TeacherRewardPunishment> listByType(Long teacherId, String type);

    /**
     * 添加奖惩记录
     */
    Boolean addRewardPunishment(TeacherRewardPunishment record);
}
