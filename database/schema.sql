-- =============================================
-- 城市教育局综合信息服务平台 - 数据库架构
-- 数据库: MySQL 8.x
-- 字符集: utf8mb4 (支持中文和emoji)
-- =============================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- 1. 基础信息表
-- =============================================

-- 1.1 学校表
DROP TABLE IF EXISTS `school`;
CREATE TABLE `school` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '学校ID',
  `school_code` VARCHAR(50) NOT NULL UNIQUE COMMENT '学校编码',
  `school_name` VARCHAR(100) NOT NULL COMMENT '学校名称',
  `school_type` VARCHAR(20) NOT NULL COMMENT '学校类型: primary/secondary/vocational',
  `address` VARCHAR(255) COMMENT '地址',
  `contact_person` VARCHAR(50) COMMENT '联系人',
  `contact_phone` VARCHAR(20) COMMENT '联系电话',
  `principal_id` BIGINT COMMENT '校长ID(关联user表)',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1-正常, 0-停用',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学校信息表';

-- 1.2 用户表 (统一用户中心)
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码(加密存储)',
  `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
  `id_card` VARCHAR(18) COMMENT '身份证号',
  `phone` VARCHAR(20) COMMENT '手机号',
  `email` VARCHAR(100) COMMENT '邮箱',
  `avatar` VARCHAR(255) COMMENT '头像URL',
  `gender` TINYINT COMMENT '性别: 0-未知, 1-男, 2-女',
  `birth_date` DATE COMMENT '出生日期',
  `school_id` BIGINT COMMENT '所属学校ID',
  `class_id` BIGINT COMMENT '所属班级ID',
  `department` VARCHAR(100) COMMENT '部门/院系',
  `title` VARCHAR(50) COMMENT '职称/职务',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1-正常, 0-禁用',
  `last_login_time` DATETIME COMMENT '最后登录时间',
  `last_login_ip` VARCHAR(50) COMMENT '最后登录IP',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_school_id` (`school_id`),
  INDEX `idx_class_id` (`class_id`),
  INDEX `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- 1.3 角色表
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_code` VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
  `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `description` VARCHAR(200) COMMENT '角色描述',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 1.4 用户角色关联表
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 1.5 权限表
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `parent_id` BIGINT COMMENT '父权限ID',
  `permission_code` VARCHAR(100) NOT NULL UNIQUE COMMENT '权限编码',
  `permission_name` VARCHAR(100) NOT NULL COMMENT '权限名称',
  `type` TINYINT NOT NULL COMMENT '类型: 1-菜单, 2-按钮, 3-接口',
  `path` VARCHAR(200) COMMENT '路由路径',
  `component` VARCHAR(200) COMMENT '组件路径',
  `icon` VARCHAR(50) COMMENT '图标',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 1.6 角色权限关联表
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `permission_id` BIGINT NOT NULL COMMENT '权限ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
  INDEX `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 1.7 学生信息表
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '学生ID',
  `user_id` BIGINT NOT NULL UNIQUE COMMENT '用户ID',
  `student_number` VARCHAR(50) NOT NULL UNIQUE COMMENT '学号',
  `class_id` BIGINT NOT NULL COMMENT '班级ID',
  `enrollment_date` DATE COMMENT '入学日期',
  `graduation_date` DATE COMMENT '毕业日期',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1-在读, 2-毕业, 3-转学, 4-休学',
  `guardian_name` VARCHAR(50) COMMENT '监护人姓名',
  `guardian_phone` VARCHAR(20) COMMENT '监护人电话',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_class_id` (`class_id`),
  INDEX `idx_student_number` (`student_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生信息表';

-- 1.8 教师信息表
DROP TABLE IF EXISTS `teacher`;
CREATE TABLE `teacher` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '教师ID',
  `user_id` BIGINT NOT NULL UNIQUE COMMENT '用户ID',
  `teacher_number` VARCHAR(50) NOT NULL UNIQUE COMMENT '教师编号',
  `title` VARCHAR(50) COMMENT '职称',
  `subject` VARCHAR(50) COMMENT '所教科目',
  `hire_date` DATE COMMENT '入职日期',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1-在职, 2-离职',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_teacher_number` (`teacher_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师信息表';

-- 1.8.1 教师基本信息扩展表
DROP TABLE IF EXISTS `teacher_basic`;
CREATE TABLE `teacher_basic` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
  `teacher_name` VARCHAR(50) COMMENT '教师姓名',
  `teacher_number` VARCHAR(50) COMMENT '教师工号',
  `gender` VARCHAR(10) COMMENT '性别: male-男, female-女',
  `birth_date` DATE COMMENT '出生日期',
  `ethnicity` VARCHAR(20) COMMENT '民族',
  `native_place` VARCHAR(100) COMMENT '籍贯',
  `id_card` VARCHAR(18) COMMENT '身份证号',
  `phone` VARCHAR(20) COMMENT '手机号',
  `email` VARCHAR(100) COMMENT '邮箱',
  `address` VARCHAR(255) COMMENT '家庭住址',
  `school_id` BIGINT COMMENT '所属学校ID',
  `department` VARCHAR(100) COMMENT '部门/院系',
  `position` VARCHAR(50) COMMENT '职务',
  `title` VARCHAR(50) COMMENT '职称',
  `hire_date` DATE COMMENT '入职日期',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1-在职, 0-离职',
  `photo_url` VARCHAR(255) COMMENT '证件照URL',
  `work_photo_url` VARCHAR(255) COMMENT '工作照URL',
  `signature_url` VARCHAR(255) COMMENT '电子签名URL',
  `role_type` VARCHAR(50) COMMENT '角色类型: teacher/teacher_head/department_head/admin',
  `remarks` VARCHAR(500) COMMENT '备注',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_teacher` (`teacher_id`),
  INDEX `idx_teacher_id` (`teacher_id`),
  INDEX `idx_school_id` (`school_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师基本信息扩展表';

-- 1.8.2 教师任职岗位信息表
DROP TABLE IF EXISTS `teacher_position`;
CREATE TABLE `teacher_position` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
  `school_id` BIGINT NOT NULL COMMENT '学校ID',
  `department` VARCHAR(100) COMMENT '部门/院系',
  `position_type` VARCHAR(50) COMMENT '岗位类别: fulltime-专任教师, admin-行政人员, support-教辅人员',
  `position_title` VARCHAR(50) COMMENT '职务: principal-校长, director-主任, head-组长, teacher-教师',
  `is_head_teacher` TINYINT DEFAULT 0 COMMENT '是否班主任: 0-否, 1-是',
  `class_id` BIGINT COMMENT '班级ID(班主任)',
  `appointment_date` DATE COMMENT '任命时间',
  `conversion_date` DATE COMMENT '转正时间',
  `contract_expiry` DATE COMMENT '合同到期时间',
  `teaching_subject` VARCHAR(50) COMMENT '任教科目',
  `teaching_grade` VARCHAR(50) COMMENT '任教年级',
  `teaching_class` VARCHAR(50) COMMENT '任教班级',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_teacher_id` (`teacher_id`),
  INDEX `idx_school_id` (`school_id`),
  INDEX `idx_class_id` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师任职岗位信息表';

-- 1.8.3 教师学历学位信息表
DROP TABLE IF EXISTS `teacher_education`;
CREATE TABLE `teacher_education` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
  `degree_level` VARCHAR(50) COMMENT '学历层次: bachelor-本科, master-硕士, doctor-博士',
  `degree_name` VARCHAR(50) COMMENT '学位名称: bachelor-学士, master-硕士, doctor-博士',
  `school_name` VARCHAR(100) COMMENT '毕业院校',
  `major` VARCHAR(100) COMMENT '专业',
  `graduation_date` DATE COMMENT '毕业时间',
  `education_form` VARCHAR(50) COMMENT '学习形式: fulltime-全日制, parttime-在职',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_teacher_id` (`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师学历学位信息表';

-- 1.8.4 教师专业技术资格表
DROP TABLE IF EXISTS `teacher_qualification`;
CREATE TABLE `teacher_qualification` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
  `title_level` VARCHAR(50) COMMENT '职称级别: junior-初级, intermediate-中级, senior-高级, professor-正高级',
  `title_name` VARCHAR(50) COMMENT '职称名称',
  `obtain_date` DATE COMMENT '取得时间',
  `approval_agency` VARCHAR(100) COMMENT '评审机构',
  `certificate_number` VARCHAR(50) COMMENT '证书编号',
  `qualification_type` VARCHAR(50) COMMENT '资格类型: teacher-教师资格证, professional-专业技术资格',
  `teaching_stage` VARCHAR(50) COMMENT '任教学段: primary-小学, junior-初中, senior-高中',
  `teaching_subject` VARCHAR(50) COMMENT '任教学科',
  `expiry_date` DATE COMMENT '有效期',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_teacher_id` (`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师专业技术资格表';

-- 1.8.5 教师专业发展称号表
DROP TABLE IF EXISTS `teacher_honor`;
CREATE TABLE `teacher_honor` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
  `honor_type` VARCHAR(50) NOT NULL COMMENT '称号类型: backbone-骨干教师, leader-学科带头人, master-名师, expert-专家',
  `honor_name` VARCHAR(100) NOT NULL COMMENT '称号名称',
  `award_date` DATE COMMENT '获得日期',
  `award_agency` VARCHAR(100) COMMENT '颁发机构',
  `expiry_date` DATE COMMENT '有效期',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_teacher_id` (`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师专业发展称号表';

-- 1.8.6 教师考核表
DROP TABLE IF EXISTS `teacher_assessment`;
CREATE TABLE `teacher_assessment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
  `assessment_year` VARCHAR(20) NOT NULL COMMENT '考核年度',
  `assessment_result` VARCHAR(20) NOT NULL COMMENT '考核结果: excellent-优秀, good-良好, qualified-合格, unqualified-不合格',
  `assessment_opinion` TEXT COMMENT '考核意见',
  `assessor_id` BIGINT COMMENT '考核人ID',
  `assessor_name` VARCHAR(50) COMMENT '考核人姓名',
  `assessment_date` DATE COMMENT '考核日期',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_teacher_id` (`teacher_id`),
  INDEX `idx_year` (`assessment_year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师考核表';

-- 1.8.7 教师奖惩记录表
DROP TABLE IF EXISTS `teacher_reward_punishment`;
CREATE TABLE `teacher_reward_punishment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
  `type` VARCHAR(20) NOT NULL COMMENT '类型: reward-奖励, punishment-惩罚',
  `category` VARCHAR(50) NOT NULL COMMENT '分类: excellent_teacher-优秀教师, excellent_head-优秀班主任, violation-违规, warning-警告',
  `title` VARCHAR(100) NOT NULL COMMENT '标题',
  `description` TEXT COMMENT '详细描述',
  `award_agency` VARCHAR(100) COMMENT '颁发/处理机构',
  `record_date` DATE NOT NULL COMMENT '记录日期',
  `reason` VARCHAR(500) COMMENT '原因',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_teacher_id` (`teacher_id`),
  INDEX `idx_record_date` (`record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师奖惩记录表';

-- 1.8.8 教师师德考核表
DROP TABLE IF EXISTS `teacher_ethics`;
CREATE TABLE `teacher_ethics` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
  `assessment_year` VARCHAR(20) NOT NULL COMMENT '考核年度',
  `ethics_level` VARCHAR(20) NOT NULL COMMENT '师德等级: excellent-优秀, good-良好, qualified-合格, unqualified-不合格',
  `evaluation_record` TEXT COMMENT '评价记录',
  `evaluator_id` BIGINT COMMENT '评价人ID',
  `evaluator_name` VARCHAR(50) COMMENT '评价人姓名',
  `evaluation_date` DATE COMMENT '评价日期',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_teacher_id` (`teacher_id`),
  INDEX `idx_year` (`assessment_year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师师德考核表';

-- 1.8.9 教师培训记录表
DROP TABLE IF EXISTS `teacher_training_record`;
CREATE TABLE `teacher_training_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
  `training_name` VARCHAR(200) NOT NULL COMMENT '培训项目名称',
  `training_date` DATE NOT NULL COMMENT '培训日期',
  `duration` INT COMMENT '学时',
  `training_agency` VARCHAR(100) COMMENT '培训机构',
  `result` VARCHAR(50) COMMENT '成绩/结业情况',
  `certificate_url` VARCHAR(255) COMMENT '证书URL',
  `category` VARCHAR(50) COMMENT '培训分类: skill-教学技能, info-信息化, ethics-师德',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_teacher_id` (`teacher_id`),
  INDEX `idx_training_date` (`training_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师培训记录表';

-- 1.8.10 教师继续教育学分表
DROP TABLE IF EXISTS `teacher_education_credit`;
CREATE TABLE `teacher_education_credit` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
  `academic_year` VARCHAR(20) COMMENT '学年度',
  `credit_type` VARCHAR(50) COMMENT '学分类型: training-培训学习, research-教研活动, academic-学术研修, online-在线学习',
  `project_name` VARCHAR(100) COMMENT '项目名称',
  `credit_hours` INT DEFAULT 0 COMMENT '学分数',
  `certification_status` VARCHAR(20) COMMENT '认证状态: pending-待认证, certified-已认证, rejected-已驳回',
  `certification_date` DATE COMMENT '认证日期',
  `certifier_id` BIGINT COMMENT '认证人ID',
  `certifier_name` VARCHAR(50) COMMENT '认证人姓名',
  `remarks` VARCHAR(500) COMMENT '备注',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_teacher_id` (`teacher_id`),
  INDEX `idx_academic_year` (`academic_year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师继续教育学分表';

-- 1.8.11 教师教学任务表
DROP TABLE IF EXISTS `teacher_teaching_task`;
CREATE TABLE `teacher_teaching_task` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
  `academic_year` VARCHAR(20) COMMENT '学年度',
  `semester` INT COMMENT '学期: 1-第一学期, 2-第二学期',
  `course_name` VARCHAR(100) COMMENT '课程名称',
  `course_code` VARCHAR(50) COMMENT '课程代码',
  `class_number` VARCHAR(50) COMMENT '教学班号',
  `target_grade` VARCHAR(50) COMMENT '授课对象年级',
  `student_count` INT DEFAULT 0 COMMENT '学生人数',
  `weekly_hours` INT DEFAULT 0 COMMENT '周学时',
  `total_hours` INT DEFAULT 0 COMMENT '总学时',
  `teaching_location` VARCHAR(100) COMMENT '教学场所',
  `teaching_mode` VARCHAR(20) COMMENT '教学模式: online-线上, offline-线下, hybrid-混合',
  `course_nature` VARCHAR(20) COMMENT '课程性质: required-必修, elective-选修, public-公选',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_teacher_id` (`teacher_id`),
  INDEX `idx_academic_year` (`academic_year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师教学任务表';

-- 1.8.12 教师教研活动表
DROP TABLE IF EXISTS `teacher_research_activity`;
CREATE TABLE `teacher_research_activity` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
  `activity_type` VARCHAR(50) COMMENT '活动类型: lecture-讲座, seminar-研讨会, training-培训, competition-竞赛, project-课题研究',
  `activity_name` VARCHAR(200) COMMENT '活动名称',
  `activity_date` DATE COMMENT '活动日期',
  `activity_level` VARCHAR(50) COMMENT '活动级别: school-校级, district-区级, city-市级, provincial-省级, national-国家级',
  `role` VARCHAR(50) COMMENT '担任角色: host-主持人, speaker-主讲人, participant-参与者, organizer-组织者',
  `credit_hours` INT DEFAULT 0 COMMENT '学时/学分',
  `achievement` VARCHAR(500) COMMENT '成果描述',
  `proof_url` VARCHAR(255) COMMENT '证明文件URL',
  `review_status` VARCHAR(20) COMMENT '审核状态: pending-待审核, approved-已通过, rejected-已驳回',
  `reviewer_id` BIGINT COMMENT '审核人ID',
  `reviewer_name` VARCHAR(50) COMMENT '审核人姓名',
  `review_date` DATE COMMENT '审核日期',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_teacher_id` (`teacher_id`),
  INDEX `idx_activity_date` (`activity_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师教研活动表';

-- 1.8.13 教师工作量统计表
DROP TABLE IF EXISTS `teacher_workload_statistics`;
CREATE TABLE `teacher_workload_statistics` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
  `statistical_year` VARCHAR(20) COMMENT '统计年度',
  `statistical_period` VARCHAR(20) COMMENT '统计周期: semester-学期, year-学年',
  `teaching_workload` DECIMAL(8,2) DEFAULT 0 COMMENT '教学工作量(课时)',
  `research_workload` DECIMAL(8,2) DEFAULT 0 COMMENT '教研工作量(小时)',
  `management_workload` DECIMAL(8,2) DEFAULT 0 COMMENT '管理工作量(小时)',
  `guidance_workload` DECIMAL(8,2) DEFAULT 0 COMMENT '指导工作量(小时)',
  `other_workload` DECIMAL(8,2) DEFAULT 0 COMMENT '其他工作量(小时)',
  `total_workload` DECIMAL(8,2) DEFAULT 0 COMMENT '总工作量',
  `standard_workload` DECIMAL(8,2) DEFAULT 0 COMMENT '标准工作量',
  `completion_rate` DECIMAL(5,2) COMMENT '完成率(%)',
  `overtime_pay` DECIMAL(8,2) DEFAULT 0 COMMENT '超课时费',
  `remarks` VARCHAR(500) COMMENT '备注',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_teacher_id` (`teacher_id`),
  INDEX `idx_statistical_year` (`statistical_year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师工作量统计表';

-- 1.8.14 教师作业表
DROP TABLE IF EXISTS `assignment`;
CREATE TABLE `assignment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '作业ID',
  `title` VARCHAR(200) NOT NULL COMMENT '作业标题',
  `subject` VARCHAR(50) NOT NULL COMMENT '科目',
  `content` TEXT COMMENT '作业内容',
  `target_classes` VARCHAR(500) COMMENT '发布班级(JSON格式)',
  `deadline` DATETIME COMMENT '截止时间',
  `difficulty` VARCHAR(20) COMMENT '难度等级: easy/medium/hard',
  `is_graded` TINYINT DEFAULT 1 COMMENT '是否计分: 0-否, 1-是',
  `notes` VARCHAR(500) COMMENT '补充说明',
  `attachments` VARCHAR(500) COMMENT '附件路径(JSON格式)',
  `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
  `school_id` BIGINT COMMENT '学校ID',
  `status` VARCHAR(20) COMMENT '状态: 进行中/已完成/已截止',
  `publish_time` DATETIME COMMENT '发布时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_teacher_id` (`teacher_id`),
  INDEX `idx_school_id` (`school_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业表';

-- 1.9 班级表
DROP TABLE IF EXISTS `class`;
CREATE TABLE `class` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '班级ID',
  `school_id` BIGINT NOT NULL COMMENT '学校ID',
  `class_name` VARCHAR(50) NOT NULL COMMENT '班级名称',
  `class_code` VARCHAR(50) NOT NULL COMMENT '班级编码',
  `grade` VARCHAR(20) COMMENT '年级',
  `academic_year` VARCHAR(20) COMMENT '学年',
  `head_teacher_id` BIGINT COMMENT '班主任ID(关联teacher表)',
  `student_count` INT DEFAULT 0 COMMENT '学生人数',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1-正常, 0-解散',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_school_id` (`school_id`),
  INDEX `idx_head_teacher_id` (`head_teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

-- 1.10 家长表
DROP TABLE IF EXISTS `parent`;
CREATE TABLE `parent` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '家长ID',
  `user_id` BIGINT NOT NULL UNIQUE COMMENT '用户ID',
  `relationship` VARCHAR(20) COMMENT '关系: father/mother/grandfather/grandmother/other',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家长信息表';

-- 1.11 学籍异动表
DROP TABLE IF EXISTS `student_status_change`;
CREATE TABLE `student_status_change` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '异动ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID(申请人)',
  `change_type` TINYINT NOT NULL COMMENT '异动类型: 1-休学, 2-转学, 3-复学, 4-退学, 5-其他',
  `reason` TEXT COMMENT '异动原因',
  `start_date` DATE COMMENT '开始日期',
  `end_date` DATE COMMENT '结束日期',
  `target_school` VARCHAR(100) COMMENT '目标学校(转学)',
  `status` TINYINT DEFAULT 0 COMMENT '审核状态: 0-待审核, 1-已通过, 2-已驳回',
  `approval_comment` VARCHAR(500) COMMENT '审核意见',
  `approver_id` BIGINT COMMENT '审核人ID',
  `approval_time` DATETIME COMMENT '审核时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_student_id` (`student_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学籍异动表';

-- =============================================
-- 2. 校园门户模块
-- =============================================

-- 2.1 课程表
DROP TABLE IF EXISTS `timetable`;
CREATE TABLE `timetable` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '课程表ID',
  `school_id` BIGINT NOT NULL COMMENT '学校ID',
  `class_id` BIGINT NOT NULL COMMENT '班级ID',
  `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
  `subject` VARCHAR(50) NOT NULL COMMENT '科目',
  `week_day` TINYINT NOT NULL COMMENT '星期几: 1-7',
  `period` TINYINT NOT NULL COMMENT '节次: 1-8',
  `start_time` TIME NOT NULL COMMENT '开始时间',
  `end_time` TIME NOT NULL COMMENT '结束时间',
  `classroom` VARCHAR(50) COMMENT '教室',
  `academic_week` VARCHAR(20) COMMENT '教学周: all/odd/even',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_class_id` (`class_id`),
  INDEX `idx_teacher_id` (`teacher_id`),
  INDEX `idx_week_day` (`week_day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 2.2 成绩表
DROP TABLE IF EXISTS `grade`;
CREATE TABLE `grade` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '成绩ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `subject` VARCHAR(50) NOT NULL COMMENT '科目',
  `exam_type` VARCHAR(20) NOT NULL COMMENT '考试类型: midterm/final/daily',
  `score` DECIMAL(5,2) COMMENT '分数',
  `exam_date` DATE NOT NULL COMMENT '考试日期',
  `academic_term` VARCHAR(20) NOT NULL COMMENT '学期: 2024-2025-1',
  `teacher_id` BIGINT COMMENT '录入教师ID',
  `remark` VARCHAR(200) COMMENT '备注',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_student_id` (`student_id`),
  INDEX `idx_subject` (`subject`),
  INDEX `idx_exam_date` (`exam_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩表';

-- 2.3 通知公告表
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `school_id` BIGINT COMMENT '学校ID (null表示教育局级别)',
  `class_id` BIGINT COMMENT '班级ID (null表示全校)',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `content` TEXT NOT NULL COMMENT '内容',
  `type` VARCHAR(20) NOT NULL COMMENT '类型: notice/announcement/emergency',
  `publisher_id` BIGINT NOT NULL COMMENT '发布者ID',
  `publish_time` DATETIME NOT NULL COMMENT '发布时间',
  `expiry_date` DATE COMMENT '过期日期',
  `priority` TINYINT DEFAULT 0 COMMENT '优先级: 0-普通, 1-紧急',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1-已发布, 0-草稿',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_school_class` (`school_id`, `class_id`),
  INDEX `idx_publisher_id` (`publisher_id`),
  INDEX `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知公告表';

-- 2.4 公告阅读记录表
DROP TABLE IF EXISTS `announcement_read`;
CREATE TABLE `announcement_read` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `announcement_id` BIGINT NOT NULL COMMENT '公告ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `read_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_announcement_user` (`announcement_id`, `user_id`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告阅读记录表';

-- 2.5 学生表现记录表
DROP TABLE IF EXISTS `student_behavior`;
CREATE TABLE `student_behavior` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `record_date` DATE NOT NULL COMMENT '记录日期',
  `type` VARCHAR(20) NOT NULL COMMENT '类型: merit/demerit/remark',
  `content` VARCHAR(500) NOT NULL COMMENT '内容',
  `teacher_id` BIGINT NOT NULL COMMENT '记录教师ID',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1-正常',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_student_id` (`student_id`),
  INDEX `idx_record_date` (`record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表现记录表';

-- =============================================
-- 3. 后台管理模块
-- =============================================

-- 3.1 文档表
DROP TABLE IF EXISTS `document`;
CREATE TABLE `document` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '文档ID',
  `school_id` BIGINT COMMENT '学校ID (null表示教育局)',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `content` TEXT COMMENT '内容',
  `file_url` VARCHAR(255) COMMENT '文件URL',
  `file_type` VARCHAR(20) COMMENT '文件类型: pdf/doc/excel',
  `category` VARCHAR(50) NOT NULL COMMENT '分类: policy/notice/plan/report',
  `author_id` BIGINT NOT NULL COMMENT '作者ID',
  `status` TINYINT DEFAULT 0 COMMENT '状态: 0-草稿, 1-待审批, 2-已发布, 3-已驳回',
  `approval_id` BIGINT COMMENT '审批人ID',
  `approval_time` DATETIME COMMENT '审批时间',
  `approval_comment` VARCHAR(500) COMMENT '审批意见',
  `publish_time` DATETIME COMMENT '发布时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_school_id` (`school_id`),
  INDEX `idx_category` (`category`),
  INDEX `idx_status` (`status`),
  INDEX `idx_author_id` (`author_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文档表';

-- 3.2 文档分发记录表
DROP TABLE IF EXISTS `document_distribution`;
CREATE TABLE `document_distribution` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `document_id` BIGINT NOT NULL COMMENT '文档ID',
  `target_type` VARCHAR(20) NOT NULL COMMENT '目标类型: school/user/all',
  `target_id` BIGINT COMMENT '目标ID (学校ID或用户ID)',
  `distribution_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '分发时间',
  `read_status` TINYINT DEFAULT 0 COMMENT '阅读状态: 0-未读, 1-已读',
  `read_time` DATETIME COMMENT '阅读时间',
  PRIMARY KEY (`id`),
  INDEX `idx_document_id` (`document_id`),
  INDEX `idx_target` (`target_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文档分发记录表';

-- 3.3 学生成长记录表
DROP TABLE IF EXISTS `growth_record`;
CREATE TABLE `growth_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `record_date` DATE NOT NULL COMMENT '记录日期',
  `category` VARCHAR(50) NOT NULL COMMENT '分类: achievement/behavior/special',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `description` TEXT COMMENT '详细描述',
  `evidence_url` VARCHAR(255) COMMENT '证据文件URL',
  `teacher_id` BIGINT NOT NULL COMMENT '记录教师ID',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1-正常',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_student_id` (`student_id`),
  INDEX `idx_record_date` (`record_date`),
  INDEX `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生成长记录表';

-- 3.4 办公资产表
DROP TABLE IF EXISTS `office_asset`;
CREATE TABLE `office_asset` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '资产ID',
  `school_id` BIGINT NOT NULL COMMENT '学校ID',
  `asset_code` VARCHAR(50) NOT NULL UNIQUE COMMENT '资产编码',
  `asset_name` VARCHAR(100) NOT NULL COMMENT '资产名称',
  `category` VARCHAR(50) NOT NULL COMMENT '分类: equipment/furniture/electronic',
  `specification` VARCHAR(200) COMMENT '规格型号',
  `quantity` INT NOT NULL COMMENT '数量',
  `unit_price` DECIMAL(10,2) COMMENT '单价',
  `total_value` DECIMAL(12,2) COMMENT '总价值',
  `purchase_date` DATE COMMENT '采购日期',
  `location` VARCHAR(100) COMMENT '存放位置',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1-正常, 2-维修中, 3-报废',
  `responsible_person` VARCHAR(50) COMMENT '责任人',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_school_id` (`school_id`),
  INDEX `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='办公资产表';

-- 3.5 会议表
DROP TABLE IF EXISTS `meeting`;
CREATE TABLE `meeting` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '会议ID',
  `school_id` BIGINT NOT NULL COMMENT '学校ID',
  `title` VARCHAR(200) NOT NULL COMMENT '会议标题',
  `description` TEXT COMMENT '会议描述',
  `room` VARCHAR(100) NOT NULL COMMENT '会议室',
  `start_time` DATETIME NOT NULL COMMENT '开始时间',
  `end_time` DATETIME NOT NULL COMMENT '结束时间',
  `organizer_id` BIGINT NOT NULL COMMENT '组织者ID',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1-已安排, 2-进行中, 3-已完成, 4-已取消',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_school_id` (`school_id`),
  INDEX `idx_start_time` (`start_time`),
  INDEX `idx_organizer_id` (`organizer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会议表';

-- 3.6 会议参与者表
DROP TABLE IF EXISTS `meeting_participant`;
CREATE TABLE `meeting_participant` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `meeting_id` BIGINT NOT NULL COMMENT '会议ID',
  `user_id` BIGINT NOT NULL COMMENT '参与者ID',
  `attendance_status` TINYINT DEFAULT 0 COMMENT '出席状态: 0-待定, 1-出席, 2-缺席',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_meeting_user` (`meeting_id`, `user_id`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会议参与者表';

-- 3.7 考勤表
DROP TABLE IF EXISTS `attendance`;
CREATE TABLE `attendance` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '考勤ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `school_id` BIGINT NOT NULL COMMENT '学校ID',
  `record_date` DATE NOT NULL COMMENT '记录日期',
  `check_in_time` DATETIME COMMENT '签到时间',
  `check_out_time` DATETIME COMMENT '签退时间',
  `status` VARCHAR(20) COMMENT '状态: normal/late/early/absent',
  `location` VARCHAR(100) COMMENT '签到位置',
  `device_id` VARCHAR(50) COMMENT '设备ID',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_user_date` (`user_id`, `record_date`),
  INDEX `idx_school_date` (`school_id`, `record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤表';

-- 3.8 财务表
DROP TABLE IF EXISTS `finance`;
CREATE TABLE `finance` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '财务ID',
  `school_id` BIGINT NOT NULL COMMENT '学校ID',
  `type` VARCHAR(20) NOT NULL COMMENT '类型: income/expense',
  `category` VARCHAR(50) NOT NULL COMMENT '分类: budget/salary/equipment/other',
  `amount` DECIMAL(12,2) NOT NULL COMMENT '金额',
  `description` VARCHAR(500) NOT NULL COMMENT '描述',
  `transaction_date` DATE NOT NULL COMMENT '交易日期',
  `applicant_id` BIGINT COMMENT '申请人ID',
  `approver_id` BIGINT COMMENT '审批人ID',
  `status` TINYINT DEFAULT 0 COMMENT '状态: 0-待审批, 1-已通过, 2-已拒绝',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_school_date` (`school_id`, `transaction_date`),
  INDEX `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='财务表';

-- 3.9 工作流表
DROP TABLE IF EXISTS `workflow`;
CREATE TABLE `workflow` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '工作流ID',
  `name` VARCHAR(100) NOT NULL COMMENT '工作流名称',
  `type` VARCHAR(50) NOT NULL COMMENT '类型: document/expense/asset',
  `config` JSON NOT NULL COMMENT '配置信息 (JSON)',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作流表';

-- 3.10 工作流实例表
DROP TABLE IF EXISTS `workflow_instance`;
CREATE TABLE `workflow_instance` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '实例ID',
  `workflow_id` BIGINT NOT NULL COMMENT '工作流ID',
  `business_id` BIGINT NOT NULL COMMENT '业务ID',
  `current_step` INT NOT NULL COMMENT '当前步骤',
  `status` TINYINT DEFAULT 0 COMMENT '状态: 0-处理中, 1-通过, 2-拒绝',
  `applicant_id` BIGINT NOT NULL COMMENT '申请人ID',
  `result` VARCHAR(500) COMMENT '审批结果',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_workflow_business` (`workflow_id`, `business_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作流实例表';

-- 3.11 系统设置表
DROP TABLE IF EXISTS `system_setting`;
CREATE TABLE `system_setting` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '设置ID',
  `setting_key` VARCHAR(100) NOT NULL UNIQUE COMMENT '设置键',
  `setting_value` TEXT COMMENT '设置值',
  `description` VARCHAR(200) COMMENT '描述',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统设置表';

-- =============================================
-- 4. 大数据分析模块
-- =============================================

-- 4.1 数据采集表
DROP TABLE IF EXISTS `data_collection`;
CREATE TABLE `data_collection` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '采集ID',
  `school_id` BIGINT NOT NULL COMMENT '学校ID',
  `data_type` VARCHAR(50) NOT NULL COMMENT '数据类型: layout/teacher/activity/student',
  `data_source` VARCHAR(100) NOT NULL COMMENT '数据来源',
  `data_content` JSON NOT NULL COMMENT '数据内容',
  `collection_date` DATE NOT NULL COMMENT '采集日期',
  `status` TINYINT DEFAULT 0 COMMENT '状态: 0-待清洗, 1-已清洗, 2-已入库',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_school_type_date` (`school_id`, `data_type`, `collection_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据采集表';

-- 4.2 数据清洗记录表
DROP TABLE IF EXISTS `data_cleaning`;
CREATE TABLE `data_cleaning` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '清洗ID',
  `collection_id` BIGINT NOT NULL COMMENT '采集ID',
  `clean_rules` JSON COMMENT '清洗规则',
  `clean_result` TEXT COMMENT '清洗结果',
  `status` TINYINT DEFAULT 0 COMMENT '状态: 0-失败, 1-成功',
  `operator_id` BIGINT COMMENT '操作人ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_collection_id` (`collection_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据清洗记录表';

-- 4.3 数据分析结果表
DROP TABLE IF EXISTS `data_analysis`;
CREATE TABLE `data_analysis` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分析ID',
  `analysis_type` VARCHAR(50) NOT NULL COMMENT '分析类型: distribution/ratio/trend',
  `school_id` BIGINT COMMENT '学校ID',
  `result_data` JSON NOT NULL COMMENT '结果数据',
  `chart_type` VARCHAR(20) COMMENT '图表类型: bar/line/pie/map',
  `analysis_date` DATE NOT NULL COMMENT '分析日期',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_analysis_type` (`analysis_type`),
  INDEX `idx_analysis_date` (`analysis_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据分析结果表';

-- =============================================
-- 5. 一卡通系统模块
-- =============================================

-- 5.1 一卡通表
DROP TABLE IF EXISTS `smart_card`;
CREATE TABLE `smart_card` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '卡片ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `card_number` VARCHAR(50) NOT NULL UNIQUE COMMENT '卡号',
  `card_type` VARCHAR(20) NOT NULL COMMENT '卡片类型: student/teacher/staff',
  `balance` DECIMAL(10,2) DEFAULT 0.00 COMMENT '余额',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1-正常, 2-挂失, 3-注销',
  `issue_date` DATE NOT NULL COMMENT '发卡日期',
  `expiry_date` DATE COMMENT '过期日期',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_card` (`user_id`, `card_number`),
  INDEX `idx_card_number` (`card_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='一卡通表';

-- 5.2 门禁记录表
DROP TABLE IF EXISTS `access_control`;
CREATE TABLE `access_control` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `card_id` BIGINT NOT NULL COMMENT '卡片ID',
  `door_id` VARCHAR(50) NOT NULL COMMENT '门禁ID',
  `access_time` DATETIME NOT NULL COMMENT '通行时间',
  `access_type` VARCHAR(20) NOT NULL COMMENT '类型: entry/exit',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1-成功, 0-失败',
  `remark` VARCHAR(200) COMMENT '备注',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_card_time` (`card_id`, `access_time`),
  INDEX `idx_door_time` (`door_id`, `access_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门禁记录表';

-- 5.3 支付记录表
DROP TABLE IF EXISTS `payment_record`;
CREATE TABLE `payment_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '支付ID',
  `card_id` BIGINT NOT NULL COMMENT '卡片ID',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
  `category` VARCHAR(50) NOT NULL COMMENT '分类: meal/books/other',
  `merchant` VARCHAR(100) NOT NULL COMMENT '商户',
  `payment_time` DATETIME NOT NULL COMMENT '支付时间',
  `balance_before` DECIMAL(10,2) NOT NULL COMMENT '支付前余额',
  `balance_after` DECIMAL(10,2) NOT NULL COMMENT '支付后余额',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1-成功, 0-失败',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_card_time` (`card_id`, `payment_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

-- =============================================
-- 6. 资源管理模块
-- =============================================

-- 6.1 教育资源表
DROP TABLE IF EXISTS `educational_resource`;
CREATE TABLE `educational_resource` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '资源ID',
  `school_id` BIGINT COMMENT '学校ID (null表示跨校资源)',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `description` TEXT COMMENT '描述',
  `category` VARCHAR(50) NOT NULL COMMENT '分类: lesson/video/exam/other',
  `subject` VARCHAR(50) COMMENT '科目',
  `file_url` VARCHAR(255) NOT NULL COMMENT '文件URL',
  `file_size` BIGINT COMMENT '文件大小',
  `file_type` VARCHAR(20) COMMENT '文件类型',
  `uploader_id` BIGINT NOT NULL COMMENT '上传者ID',
  `upload_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `status` TINYINT DEFAULT 0 COMMENT '状态: 0-待审核, 1-已发布, 2-已拒绝',
  `approver_id` BIGINT COMMENT '审批人ID',
  `approval_time` DATETIME COMMENT '审批时间',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `download_count` INT DEFAULT 0 COMMENT '下载次数',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_school_category` (`school_id`, `category`),
  INDEX `idx_uploader_id` (`uploader_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教育资源表';

-- 6.2 资源分享表
DROP TABLE IF EXISTS `resource_share`;
CREATE TABLE `resource_share` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分享ID',
  `resource_id` BIGINT NOT NULL COMMENT '资源ID',
  `share_type` VARCHAR(20) NOT NULL COMMENT '分享类型: user/school/link',
  `target_id` BIGINT COMMENT '目标ID (用户ID或学校ID)',
  `share_code` VARCHAR(50) UNIQUE COMMENT '分享码',
  `expiry_date` DATE COMMENT '过期日期',
  `created_by` BIGINT NOT NULL COMMENT '创建者ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_resource_id` (`resource_id`),
  INDEX `idx_share_code` (`share_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源分享表';

-- 6.3 资源评价表
DROP TABLE IF EXISTS `resource_evaluation`;
CREATE TABLE `resource_evaluation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `resource_id` BIGINT NOT NULL COMMENT '资源ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `rating` TINYINT NOT NULL COMMENT '评分: 1-5',
  `comment` VARCHAR(500) COMMENT '评论',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_resource_user` (`resource_id`, `user_id`),
  INDEX `idx_resource_id` (`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源评价表';

-- 6.4 资源统计表
DROP TABLE IF EXISTS `resource_statistics`;
CREATE TABLE `resource_statistics` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '统计ID',
  `school_id` BIGINT COMMENT '学校ID',
  `resource_id` BIGINT COMMENT '资源ID',
  `stat_date` DATE NOT NULL COMMENT '统计日期',
  `view_count` INT DEFAULT 0 COMMENT '浏览数',
  `download_count` INT DEFAULT 0 COMMENT '下载数',
  `share_count` INT DEFAULT 0 COMMENT '分享数',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_school_date` (`school_id`, `stat_date`),
  INDEX `idx_resource_id` (`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源统计表';

-- =============================================
-- 7. 电子班牌模块
-- =============================================

-- 7.1 班牌信息表
DROP TABLE IF EXISTS `class_sign`;
CREATE TABLE `class_sign` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '班牌ID',
  `class_id` BIGINT NOT NULL COMMENT '班级ID',
  `device_id` VARCHAR(50) NOT NULL UNIQUE COMMENT '设备ID',
  `device_name` VARCHAR(100) COMMENT '设备名称',
  `location` VARCHAR(100) COMMENT '位置',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1-在线, 0-离线',
  `last_active` DATETIME COMMENT '最后活跃时间',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_class_id` (`class_id`),
  INDEX `idx_device_id` (`device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班牌信息表';

-- 7.2 班牌内容表
DROP TABLE IF EXISTS `class_sign_content`;
CREATE TABLE `class_sign_content` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '内容ID',
  `sign_id` BIGINT NOT NULL COMMENT '班牌ID',
  `content_type` VARCHAR(20) NOT NULL COMMENT '类型: schedule/announcement/photo',
  `content` TEXT COMMENT '内容',
  `file_url` VARCHAR(255) COMMENT '文件URL',
  `start_time` DATETIME NOT NULL COMMENT '开始显示时间',
  `end_time` DATETIME NOT NULL COMMENT '结束显示时间',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
  `created_by` BIGINT NOT NULL COMMENT '创建者ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_sign_time` (`sign_id`, `start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班牌内容表';

-- =============================================
-- 8. 通讯模块
-- =============================================

-- 8.1 消息表
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` BIGINT NOT NULL COMMENT '发送者ID',
  `receiver_id` BIGINT NOT NULL COMMENT '接收者ID',
  `content` TEXT NOT NULL COMMENT '内容',
  `type` VARCHAR(20) NOT NULL COMMENT '类型: text/image/file',
  `file_url` VARCHAR(255) COMMENT '文件URL',
  `status` TINYINT DEFAULT 0 COMMENT '状态: 0-未读, 1-已读',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`id`),
  INDEX `idx_sender_receiver` (`sender_id`, `receiver_id`),
  INDEX `idx_receiver_status` (`receiver_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';

-- 8.2 通知推送表
DROP TABLE IF EXISTS `notification_push`;
CREATE TABLE `notification_push` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '推送ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `content` TEXT NOT NULL COMMENT '内容',
  `type` VARCHAR(20) NOT NULL COMMENT '类型: system/message/announcement',
  `device_token` VARCHAR(255) COMMENT '设备令牌',
  `status` TINYINT DEFAULT 0 COMMENT '状态: 0-待推送, 1-已推送, 2-失败',
  `push_time` DATETIME COMMENT '推送时间',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_user_status` (`user_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知推送表';

-- =============================================
-- 9. 日志表
-- =============================================

-- 9.1 操作日志表
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` BIGINT COMMENT '用户ID',
  `username` VARCHAR(50) COMMENT '用户名',
  `operation` VARCHAR(100) NOT NULL COMMENT '操作',
  `module` VARCHAR(50) NOT NULL COMMENT '模块',
  `ip_address` VARCHAR(50) COMMENT 'IP地址',
  `user_agent` VARCHAR(500) COMMENT '用户代理',
  `request_params` TEXT COMMENT '请求参数',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1-成功, 0-失败',
  `error_msg` TEXT COMMENT '错误信息',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_user_time` (`user_id`, `created_at`),
  INDEX `idx_module_time` (`module`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 9.2 登录日志表
DROP TABLE IF EXISTS `login_log`;
CREATE TABLE `login_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` BIGINT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `login_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  `ip_address` VARCHAR(50) COMMENT 'IP地址',
  `user_agent` VARCHAR(500) COMMENT '用户代理',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1-成功, 0-失败',
  `fail_reason` VARCHAR(200) COMMENT '失败原因',
  PRIMARY KEY (`id`),
  INDEX `idx_username_time` (`username`, `login_time`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- =============================================
-- 10. 在线学习培训模块
-- =============================================

-- 10.1 培训课程表
DROP TABLE IF EXISTS `training_course`;
CREATE TABLE `training_course` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `title` VARCHAR(255) NOT NULL COMMENT '课程标题',
  `description` TEXT COMMENT '课程描述',
  `category` VARCHAR(50) COMMENT '课程分类',
  `subject` VARCHAR(50) COMMENT '所属科目',
  `cover_image` VARCHAR(500) COMMENT '封面图片URL',
  `video_url` VARCHAR(500) COMMENT '视频URL',
  `duration` INT COMMENT '课程时长(分钟)',
  `difficulty_level` VARCHAR(20) COMMENT '难度等级: beginner, intermediate, advanced',
  `target_audience` VARCHAR(100) COMMENT '目标受众',
  `instructor_id` BIGINT COMMENT '讲师ID',
  `instructor_name` VARCHAR(50) COMMENT '讲师姓名',
  `status` TINYINT DEFAULT 0 COMMENT '状态: 0-草稿, 1-已发布, 2-已下架',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `enroll_count` INT DEFAULT 0 COMMENT '报名人数',
  `completion_count` INT DEFAULT 0 COMMENT '完成人数',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `start_date` DATETIME COMMENT '开始日期',
  `end_date` DATETIME COMMENT '结束日期',
  `upload_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_category` (`category`),
  INDEX `idx_subject` (`subject`),
  INDEX `idx_status` (`status`),
  INDEX `idx_instructor` (`instructor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训课程表';

-- 10.2 学员报名表
DROP TABLE IF EXISTS `training_enrollment`;
CREATE TABLE `training_enrollment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '报名ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `student_id` BIGINT NOT NULL COMMENT '学员ID',
  `student_name` VARCHAR(50) COMMENT '学员姓名',
  `enrollment_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
  `progress` INT DEFAULT 0 COMMENT '学习进度(百分比)',
  `status` VARCHAR(20) DEFAULT 'enrolled' COMMENT '状态: enrolled-已报名, learning-学习中, completed-已完成, dropped-已退出',
  `completion_time` DATETIME COMMENT '完成时间',
  `score` DECIMAL(5,2) COMMENT '最终得分',
  `certificate_url` VARCHAR(500) COMMENT '证书URL',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_course_student` (`course_id`, `student_id`),
  INDEX `idx_student` (`student_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学员报名表';

-- 10.3 培训章节表
DROP TABLE IF EXISTS `training_chapter`;
CREATE TABLE `training_chapter` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '章节ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `title` VARCHAR(255) NOT NULL COMMENT '章节标题',
  `content` TEXT COMMENT '章节内容',
  `video_url` VARCHAR(500) COMMENT '视频URL',
  `duration` INT COMMENT '章节时长',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `is_required` BOOLEAN DEFAULT TRUE COMMENT '是否必修',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_course` (`course_id`),
  INDEX `idx_sort` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训章节表';

-- 10.4 学习记录表
DROP TABLE IF EXISTS `training_learning_record`;
CREATE TABLE `training_learning_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `chapter_id` BIGINT NOT NULL COMMENT '章节ID',
  `student_id` BIGINT NOT NULL COMMENT '学员ID',
  `watch_time` INT DEFAULT 0 COMMENT '观看时长(秒)',
  `is_completed` BOOLEAN DEFAULT FALSE COMMENT '是否完成',
  `completed_at` DATETIME COMMENT '完成时间',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_course_student` (`course_id`, `student_id`),
  INDEX `idx_chapter` (`chapter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习记录表';

-- 10.5 培训考试表
DROP TABLE IF EXISTS `training_exam`;
CREATE TABLE `training_exam` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '考试ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `title` VARCHAR(255) NOT NULL COMMENT '考试标题',
  `description` TEXT COMMENT '考试说明',
  `total_score` INT DEFAULT 100 COMMENT '总分',
  `passing_score` INT DEFAULT 60 COMMENT '及格分数',
  `duration` INT COMMENT '考试时长(分钟)',
  `attempt_limit` INT DEFAULT 1 COMMENT '尝试次数限制',
  `random_order` BOOLEAN DEFAULT FALSE COMMENT '题目是否随机',
  `status` TINYINT DEFAULT 0 COMMENT '状态: 0-未发布, 1-已发布',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_course` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训考试表';

-- 10.6 考试题库表
DROP TABLE IF EXISTS `training_question`;
CREATE TABLE `training_question` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '题目ID',
  `exam_id` BIGINT NOT NULL COMMENT '考试ID',
  `question_type` VARCHAR(20) NOT NULL COMMENT '题型: single-单选, multiple-多选, judge-判断, subjective-主观',
  `question_text` TEXT NOT NULL COMMENT '题目内容',
  `options` JSON COMMENT '选项: [{"id":1,"text":"A","is_correct":true}]',
  `correct_answer` VARCHAR(255) COMMENT '正确答案',
  `score` INT DEFAULT 1 COMMENT '分值',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_exam` (`exam_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训题库表';

-- 10.7 考试记录表
DROP TABLE IF EXISTS `training_exam_record`;
CREATE TABLE `training_exam_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `exam_id` BIGINT NOT NULL COMMENT '考试ID',
  `student_id` BIGINT NOT NULL COMMENT '学员ID',
  `student_name` VARCHAR(50) COMMENT '学员姓名',
  `start_time` DATETIME COMMENT '开始时间',
  `end_time` DATETIME COMMENT '结束时间',
  `score` DECIMAL(5,2) COMMENT '得分',
  `is_passed` BOOLEAN DEFAULT FALSE COMMENT '是否通过',
  `answers` JSON COMMENT '答案: {"question_id": {"answer": "A", "is_correct": true}}',
  `attempt_number` INT DEFAULT 1 COMMENT '第几次尝试',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_exam_student` (`exam_id`, `student_id`),
  INDEX `idx_student` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试记录表';

-- 10.8 培训评价表
DROP TABLE IF EXISTS `training_evaluation`;
CREATE TABLE `training_evaluation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `student_id` BIGINT NOT NULL COMMENT '学员ID',
  `student_name` VARCHAR(50) COMMENT '学员姓名',
  `rating` INT COMMENT '评分(1-5)',
  `comment` TEXT COMMENT '评价内容',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_course_student` (`course_id`, `student_id`),
  INDEX `idx_course` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训评价表';

-- 10.9 班级申请表
DROP TABLE IF EXISTS `class_application`;
CREATE TABLE `class_application` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `student_name` VARCHAR(50) COMMENT '学生姓名',
  `class_id` BIGINT NOT NULL COMMENT '班级ID',
  `class_name` VARCHAR(50) COMMENT '班级名称',
  `teacher_id` BIGINT NOT NULL COMMENT '班主任ID',
  `teacher_name` VARCHAR(50) COMMENT '班主任姓名',
  `reason` TEXT COMMENT '申请原因',
  `status` TINYINT DEFAULT 0 COMMENT '审核状态: 0-待审核, 1-已通过, 2-已驳回',
  `approval_comment` VARCHAR(500) COMMENT '审核意见',
  `approval_time` DATETIME COMMENT '审核时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_student_class` (`student_id`, `class_id`),
  INDEX `idx_teacher_status` (`teacher_id`, `status`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级申请表';

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================
-- 初始化数据
-- =============================================

-- 初始化用户数据（密码: admin）
INSERT INTO user (username, password, real_name, phone, email, status, deleted) VALUES
('admin', '$2a$10$0FbH16apzWwgCl5/S6.FBezzcCMOkAuOkkTQk1JKo4vs02jztBsIq', '系统管理员', '13800138000', 'admin@education.com', 1, 0);

-- 初始化角色
INSERT INTO role (role_code, role_name, description) VALUES
('ADMIN', '系统管理员', '系统最高权限管理员'),
('TEACHER', '教师', '教师用户'),
('USER', '普通用户', '普通用户');

-- 为 admin 分配 ADMIN 角色
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id FROM user u, role r WHERE u.username = 'admin' AND r.role_code = 'ADMIN';

-- 初始化学校数据
INSERT INTO school (school_code, school_name, school_type, address, contact_person, contact_phone, status) VALUES
('S001', '第一中学', 'secondary', '北京市朝阳区XX路1号', '张校长', '13900000001', 1),
('S002', '第二小学', 'primary', '北京市朝阳区XX路2号', '李校长', '13900000002', 1);
