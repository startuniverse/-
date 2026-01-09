package com.education.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.platform.entity.TeacherBasic;
import com.education.platform.mapper.TeacherBasicMapper;
import com.education.platform.service.ITeacherBasicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 教师基础信息服务实现
 *
 * @author Education Platform Team
 */
@Slf4j
@Service
public class TeacherBasicServiceImpl extends ServiceImpl<TeacherBasicMapper, TeacherBasic> implements ITeacherBasicService {

    @Override
    public TeacherBasic getByTeacherId(Long teacherId) {
        LambdaQueryWrapper<TeacherBasic> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherBasic::getTeacherId, teacherId)
              .eq(TeacherBasic::getDeleted, 0);
        return this.getOne(wrapper);
    }

    @Override
    public List<TeacherBasic> listBySchoolId(Long schoolId) {
        LambdaQueryWrapper<TeacherBasic> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherBasic::getSchoolId, schoolId)
              .eq(TeacherBasic::getDeleted, 0)
              .orderByDesc(TeacherBasic::getCreatedAt);
        return this.list(wrapper);
    }

    @Override
    public List<TeacherBasic> searchTeachers(Map<String, Object> conditions) {
        LambdaQueryWrapper<TeacherBasic> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherBasic::getDeleted, 0);

        if (conditions.containsKey("schoolId")) {
            wrapper.eq(TeacherBasic::getSchoolId, conditions.get("schoolId"));
        }
        if (conditions.containsKey("department")) {
            wrapper.eq(TeacherBasic::getDepartment, conditions.get("department"));
        }
        if (conditions.containsKey("status")) {
            wrapper.eq(TeacherBasic::getStatus, conditions.get("status"));
        }
        if (conditions.containsKey("name")) {
            wrapper.like(TeacherBasic::getTeacherName, conditions.get("name"));
        }

        wrapper.orderByDesc(TeacherBasic::getCreatedAt);
        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchImport(List<TeacherBasic> teacherList) {
        if (teacherList == null || teacherList.isEmpty()) {
            return false;
        }

        for (TeacherBasic teacher : teacherList) {
            teacher.setDeleted(0);
            this.save(teacher);
        }

        log.info("批量导入教师信息成功，共{}条", teacherList.size());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateTeacherStatus(Long teacherId, Integer status) {
        TeacherBasic teacher = this.getByTeacherId(teacherId);
        if (teacher == null) {
            throw new RuntimeException("教师信息不存在");
        }

        teacher.setStatus(status);
        return this.updateById(teacher);
    }
}

