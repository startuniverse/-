package com.education.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.education.platform.entity.TeacherEthics;

import java.util.List;
import java.util.Map;

/**
 * 教师师德考核服务接口
 *
 * @author Education Platform Team
 */
public interface ITeacherEthicsService extends IService<TeacherEthics> {

    /**
     * 根据教师ID查询师德考核记录
     */
    List<TeacherEthics> listByTeacherId(Long teacherId);

    /**
     * 按年度查询师德考核
     */
    TeacherEthics getByYear(Long teacherId, String assessmentYear);

    /**
     * 添加师德考核记录
     */
    Boolean addEthics(TeacherEthics ethics);

    /**
     * 统计师德等级分布
     */
    Map<String, Long> countEthicsLevels(Long teacherId);
}
