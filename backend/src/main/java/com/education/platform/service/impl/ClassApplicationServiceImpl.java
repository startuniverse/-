package com.education.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.platform.entity.ClassApplication;
import com.education.platform.entity.Student;
import com.education.platform.entity.User;
import com.education.platform.mapper.ClassApplicationMapper;
import com.education.platform.mapper.StudentMapper;
import com.education.platform.mapper.UserMapper;
import com.education.platform.service.IClassApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 班级申请服务实现类
 */
@Service
public class ClassApplicationServiceImpl extends ServiceImpl<ClassApplicationMapper, ClassApplication> implements IClassApplicationService {

    @Autowired
    private ClassApplicationMapper classApplicationMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createApplication(ClassApplication application) {
        // 检查是否已存在申请
        if (existsApplication(application.getStudentId(), application.getClassId())) {
            return false;
        }

        application.setStatus(0); // 待审核
        application.setDeleted(0);
        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());

        return classApplicationMapper.insert(application) > 0;
    }

    @Override
    public List<ClassApplication> getStudentApplications(Long studentId) {
        return classApplicationMapper.selectByStudentId(studentId);
    }

    @Override
    public List<ClassApplication> getPendingApplications(Long teacherId) {
        return classApplicationMapper.selectPendingByTeacherId(teacherId);
    }

    @Override
    public List<ClassApplication> getTeacherApplications(Long teacherId) {
        return classApplicationMapper.selectByTeacherId(teacherId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reviewApplication(Long applicationId, Integer status, String approvalComment, Long teacherId) {
        // 查询申请
        ClassApplication application = classApplicationMapper.selectById(applicationId);
        if (application == null) {
            return false;
        }

        // 只有班主任才能审核
        if (!application.getTeacherId().equals(teacherId)) {
            return false;
        }

        // 只有待审核的申请才能处理
        if (application.getStatus() != 0) {
            return false;
        }

        // 更新申请状态
        application.setStatus(status);
        application.setApprovalComment(approvalComment);
        application.setApprovalTime(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());

        boolean updated = classApplicationMapper.updateById(application) > 0;

        // 如果通过，更新学生的班级信息
        if (updated && status == 1) {
            // 更新Student表的classId
            Student student = studentMapper.selectById(application.getStudentId());
            if (student != null) {
                student.setClassId(application.getClassId());
                student.setUpdatedAt(LocalDateTime.now());
                studentMapper.updateById(student);
            }

            // 更新User表的classId（关键修复）
            if (student != null && student.getUserId() != null) {
                User user = userMapper.selectById(student.getUserId());
                if (user != null) {
                    user.setClassId(application.getClassId());
                    userMapper.updateById(user);
                }
            }
        }

        return updated;
    }

    @Override
    public boolean existsApplication(Long studentId, Long classId) {
        LambdaQueryWrapper<ClassApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassApplication::getStudentId, studentId)
               .eq(ClassApplication::getClassId, classId)
               .eq(ClassApplication::getDeleted, 0)
               .ne(ClassApplication::getStatus, 2); // 排除已驳回的
        return classApplicationMapper.selectCount(wrapper) > 0;
    }
}
