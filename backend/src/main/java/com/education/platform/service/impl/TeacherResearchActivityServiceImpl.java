package com.education.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.platform.entity.TeacherResearchActivity;
import com.education.platform.mapper.TeacherResearchActivityMapper;
import com.education.platform.service.ITeacherResearchActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教师教研活动服务实现
 *
 * @author Education Platform Team
 */
@Slf4j
@Service
public class TeacherResearchActivityServiceImpl extends ServiceImpl<TeacherResearchActivityMapper, TeacherResearchActivity> implements ITeacherResearchActivityService {

    @Override
    public List<TeacherResearchActivity> listByTeacherId(Long teacherId) {
        LambdaQueryWrapper<TeacherResearchActivity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherResearchActivity::getTeacherId, teacherId)
              .eq(TeacherResearchActivity::getDeleted, 0)
              .orderByDesc(TeacherResearchActivity::getActivityDate);
        return this.list(wrapper);
    }

    @Override
    public List<TeacherResearchActivity> listByActivityType(Long teacherId, String activityType) {
        LambdaQueryWrapper<TeacherResearchActivity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherResearchActivity::getTeacherId, teacherId)
              .eq(TeacherResearchActivity::getActivityType, activityType)
              .eq(TeacherResearchActivity::getDeleted, 0)
              .orderByDesc(TeacherResearchActivity::getActivityDate);
        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addResearchActivity(TeacherResearchActivity activity) {
        activity.setDeleted(0);
        boolean result = this.save(activity);
        if (result) {
            log.info("添加教研活动成功，教师ID：{}", activity.getTeacherId());
        }
        return result;
    }

    @Override
    public Map<String, Long> statisticsActivities(Long teacherId) {
        List<TeacherResearchActivity> list = listByTeacherId(teacherId);
        Map<String, Long> result = new HashMap<>();

        for (TeacherResearchActivity activity : list) {
            String type = activity.getActivityType();
            result.put(type, result.getOrDefault(type, 0L) + 1);
        }

        return result;
    }
}
