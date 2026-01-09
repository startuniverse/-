package com.education.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.education.platform.entity.TeacherHonor;

import java.util.List;

/**
 * 教师荣誉称号服务接口
 *
 * @author Education Platform Team
 */
public interface ITeacherHonorService extends IService<TeacherHonor> {

    /**
     * 根据教师ID查询荣誉称号列表
     */
    List<TeacherHonor> listByTeacherId(Long teacherId);

    /**
     * 按称号类型查询
     */
    List<TeacherHonor> listByHonorType(Long teacherId, String honorType);

    /**
     * 添加荣誉称号
     */
    Boolean addHonor(TeacherHonor honor);
}
