# 数据库ER图说明

## 实体关系概述

本系统采用关系型数据库设计，主要实体及其关系如下：

### 核心实体

1. **用户中心**
   - `user` (用户表) - 核心表，所有用户统一存储
   - `role` (角色表) - 系统角色
   - `permission` (权限表) - 系统权限
   - `user_role` (用户-角色关联)
   - `role_permission` (角色-权限关联)

2. **学校组织**
   - `school` (学校表)
   - `class` (班级表)
   - `student` (学生信息表)
   - `teacher` (教师信息表)
   - `parent` (家长信息表)

3. **教学管理**
   - `timetable` (课程表)
   - `grade` (成绩表)
   - `announcement` (通知公告)
   - `student_behavior` (学生表现)

4. **后台管理**
   - `document` (文档管理)
   - `growth_record` (成长记录)
   - `office_asset` (办公资产)
   - `meeting` (会议管理)
   - `attendance` (考勤管理)
   - `finance` (财务管理)
   - `workflow` (工作流)

5. **大数据分析**
   - `data_collection` (数据采集)
   - `data_cleaning` (数据清洗)
   - `data_analysis` (数据分析)

6. **一卡通系统**
   - `smart_card` (智能卡)
   - `access_control` (门禁记录)
   - `payment_record` (支付记录)

7. **资源管理**
   - `educational_resource` (教育资源)
   - `resource_share` (资源分享)
   - `resource_evaluation` (资源评价)
   - `resource_statistics` (资源统计)

8. **电子班牌**
   - `class_sign` (班牌设备)
   - `class_sign_content` (班牌内容)

9. **通讯模块**
   - `message` (消息)
   - `notification_push` (通知推送)

10. **日志系统**
    - `operation_log` (操作日志)
    - `login_log` (登录日志)

## 主要关系说明

### 1. 用户相关关系
```
user (1) ── (n) user_role (n) ── (1) role
role (1) ── (n) role_permission (n) ── (1) permission

user (1) ── (1) student (1) ── (n) class
user (1) ── (1) teacher
user (1) ── (1) parent (1) ── (1) student
```

### 2. 学校组织关系
```
school (1) ── (n) class (1) ── (n) student
school (1) ── (n) teacher
school (1) ── (n) class (1) ── (1) teacher (head_teacher)
```

### 3. 教学关系
```
class (1) ── (n) timetable (n) ── (1) teacher
student (1) ── (n) grade
student (1) ── (n) student_behavior (n) ── (1) teacher
```

### 4. 文档流程关系
```
user (author) (1) ── (n) document
user (approver) (1) ── (n) document
document (1) ── (n) document_distribution
```

### 5. 资源关系
```
user (1) ── (n) educational_resource
educational_resource (1) ── (n) resource_share
educational_resource (1) ── (n) resource_evaluation (n) ── (1) user
educational_resource (1) ── (n) resource_statistics
```

### 6. 一卡通关系
```
user (1) ── (1) smart_card
smart_card (1) ── (n) access_control
smart_card (1) ── (n) payment_record
```

### 7. 会议关系
```
user (1) ── (n) meeting (organizer)
meeting (1) ── (n) meeting_participant (n) ── (1) user
```

### 8. 班牌关系
```
class (1) ── (1) class_sign
class_sign (1) ── (n) class_sign_content
```

### 9. 消息关系
```
user (sender) (1) ── (n) message
user (receiver) (1) ── (n) message
```

### 10. 数据分析关系
```
school (1) ── (n) data_collection
data_collection (1) ── (1) data_cleaning
data_collection (1) ── (n) data_analysis
```

## 索引策略

### 主要索引
- 所有主键使用自增BIGINT
- 外键字段建立索引
- 常用查询字段建立复合索引

### 示例索引
- `idx_user_id` - 用户ID查询
- `idx_school_id` - 学校维度查询
- `idx_date_range` - 时间范围查询
- `idx_status` - 状态查询

## 数据类型说明

### 数值类型
- `BIGINT` - 主键、ID
- `INT` - 数量、计数
- `DECIMAL(10,2)` - 金额、分数
- `TINYINT` - 状态、类型标志

### 字符类型
- `VARCHAR(50)` - 编码、简短名称
- `VARCHAR(100)` - 名称、标题
- `VARCHAR(200)` - 描述、地址
- `VARCHAR(255)` - URL、路径
- `TEXT` - 长文本内容

### 时间类型
- `DATE` - 日期
- `DATETIME` - 时间戳
- `TIME` - 时间点

### 特殊类型
- `JSON` - 配置、复杂数据结构

## 安全设计

1. **密码加密** - 使用BCrypt加密存储
2. **权限控制** - RBAC模型
3. **数据隔离** - 多租户设计（学校级别）
4. **审计日志** - 完整操作记录
5. **敏感信息** - 身份证、手机号脱敏

## 扩展性考虑

1. **JSON字段** - 用于存储可变结构数据
2. **状态字段** - 支持软删除和状态流转
3. **时间戳** - 所有表包含created_at/updated_at
4. **索引优化** - 覆盖常见查询场景
5. **分表策略** - 日志类表可按时间分表

## 性能优化

1. **分页查询** - 使用MyBatis-Plus分页插件
2. **批量操作** - 支持批量插入/更新
3. **缓存策略** - 热点数据Redis缓存
4. **读写分离** - 主从数据库配置
5. **连接池** - HikariCP优化连接管理
