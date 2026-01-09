package com.education.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.education.platform.entity.TeacherResearchActivity;

import java.util.List;
import java.util.Map;

/**
 * 教师教研活动服务接口
 *
 * @author Education Platform Team
 */
public interface ITeacherResearchActivityService extends IService<TeacherResearchActivity> {

    /**
     * 根据教师ID查询教研活动
     */
    List<TeacherResearchActivity> listByTeacherId(Long teacherId);

    /**
     * 按活动类型查询
     */
    List<TeacherResearchActivity> listByActivityType(Long teacherId, String activityType);

    /**
     * 添加教研活动
     */
    Boolean addResearchActivity(TeacherResearchActivity activity);

    /**
     * 统计教研活动
     */
    Map<String, Long> statisticsActivities(Long teacherId);
}
