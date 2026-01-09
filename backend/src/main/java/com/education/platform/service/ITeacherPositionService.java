package com.education.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.education.platform.entity.TeacherPosition;

import java.util.List;

/**
 * 教师岗位信息服务接口
 *
 * @author Education Platform Team
 */
public interface ITeacherPositionService extends IService<TeacherPosition> {

    /**
     * 根据教师ID查询岗位信息
     */
    List<TeacherPosition> listByTeacherId(Long teacherId);

    /**
     * 获取教师当前岗位信息
     */
    TeacherPosition getCurrentPosition(Long teacherId);

    /**
     * 添加岗位变动记录
     */
    Boolean addPositionChange(TeacherPosition position);
}
