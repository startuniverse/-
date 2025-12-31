# 项目结构详细说明

## 目录结构

```
shixun/
│
├── backend/                          # Spring Boot 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/education/platform/
│   │   │   │   ├── common/          # 公共类
│   │   │   │   │   ├── ApiResult.java          # 统一响应格式
│   │   │   │   │   ├── BaseEntity.java         # 基础实体
│   │   │   │   │   └── PageResult.java         # 分页结果
│   │   │   │   │
│   │   │   │   ├── config/          # 配置类
│   │   │   │   │   ├── MybatisPlusConfig.java  # MyBatis-Plus配置
│   │   │   │   │   ├── MybatisMetaObjectHandler.java  # 自动填充
│   │   │   │   │   └── SecurityConfig.java     # Spring Security配置
│   │   │   │   │
│   │   │   │   ├── controller/      # 控制器
│   │   │   │   │   ├── AuthController.java     # 认证控制器
│   │   │   │   │   ├── CampusPortalController.java  # 校园门户
│   │   │   │   │   ├── BackendController.java  # 后台管理
│   │   │   │   │   ├── ResourceController.java # 资源管理
│   │   │   │   │   └── BigDataController.java  # 大数据
│   │   │   │   │
│   │   │   │   ├── entity/          # 实体类 (30+个)
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── Role.java
│   │   │   │   │   ├── Permission.java
│   │   │   │   │   ├── School.java
│   │   │   │   │   ├── Student.java
│   │   │   │   │   ├── Teacher.java
│   │   │   │   │   ├── Class.java
│   │   │   │   │   ├── Timetable.java
│   │   │   │   │   ├── Grade.java
│   │   │   │   │   ├── Announcement.java
│   │   │   │   │   ├── Document.java
│   │   │   │   │   ├── GrowthRecord.java
│   │   │   │   │   ├── OfficeAsset.java
│   │   │   │   │   ├── Meeting.java
│   │   │   │   │   ├── Attendance.java
│   │   │   │   │   ├── EducationalResource.java
│   │   │   │   │   ├── DataAnalysis.java
│   │   │   │   │   └── ... (更多实体)
│   │   │   │   │
│   │   │   │   ├── mapper/          # Mapper接口
│   │   │   │   │   ├── UserMapper.java
│   │   │   │   │   ├── RoleMapper.java
│   │   │   │   │   ├── PermissionMapper.java
│   │   │   │   │   ├── StudentMapper.java
│   │   │   │   │   ├── TeacherMapper.java
│   │   │   │   │   ├── ClassMapper.java
│   │   │   │   │   ├── TimetableMapper.java
│   │   │   │   │   ├── GradeMapper.java
│   │   │   │   │   ├── AnnouncementMapper.java
│   │   │   │   │   ├── DocumentMapper.java
│   │   │   │   │   ├── EducationalResourceMapper.java
│   │   │   │   │   ├── DataAnalysisMapper.java
│   │   │   │   │   └── ... (更多Mapper)
│   │   │   │   │
│   │   │   │   ├── security/        # 安全相关
│   │   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   │   ├── JwtAuthenticationEntryPoint.java
│   │   │   │   │   └── UserDetailsServiceImpl.java
│   │   │   │   │
│   │   │   │   ├── service/         # 服务接口
│   │   │   │   │   ├── IUserService.java
│   │   │   │   │   └── ... (其他服务接口)
│   │   │   │   │
│   │   │   │   ├── service/impl/    # 服务实现
│   │   │   │   │   ├── UserServiceImpl.java
│   │   │   │   │   └── ... (其他实现)
│   │   │   │   │
│   │   │   │   └── util/            # 工具类
│   │   │   │       └── JwtUtils.java
│   │   │   │
│   │   │   └── resources/
│   │   │       ├── mapper/          # MyBatis XML
│   │   │       │   ├── UserMapper.xml
│   │   │       │   └── ... (其他XML)
│   │   │       ├── application.yml          # 主配置
│   │   │       ├── application-dev.yml      # 开发配置
│   │   │       └── application-prod.yml     # 生产配置
│   │   │
│   │   └── test/                    # 测试代码
│   │       └── java/
│   │
│   ├── pom.xml                      # Maven依赖
│   └── README.md                    # 后端文档
│
├── frontend/                        # Vue 3 前端项目
│   ├── public/                      # 静态资源
│   │   └── favicon.ico
│   │
│   ├── src/
│   │   ├── api/                     # API接口
│   │   │   ├── auth.js              # 认证接口
│   │   │   ├── campus.js            # 校园门户接口
│   │   │   └── admin.js             # 后台管理接口
│   │   │
│   │   ├── store/                   # Pinia状态管理
│   │   │   ├── index.js
│   │   │   ├── modules/
│   │   │   │   ├── user.js          # 用户状态
│   │   │   │   └── app.js           # 应用状态
│   │   │
│   │   ├── router/                  # 路由配置
│   │   │   └── index.js
│   │   │
│   │   ├── utils/                   # 工具函数
│   │   │   └── request.js           # Axios封装
│   │   │
│   │   ├── views/                   # 页面组件
│   │   │   ├── Login.vue            # 登录页
│   │   │   ├── Register.vue         # 注册页
│   │   │   ├── Layout.vue           # 校园门户布局
│   │   │   ├── NotFound.vue         # 404页面
│   │   │   │
│   │   │   ├── campus/              # 校园门户页面
│   │   │   │   ├── Dashboard.vue    # 仪表盘
│   │   │   │   ├── Profile.vue      # 个人信息
│   │   │   │   ├── ClassInfo.vue    # 班级信息
│   │   │   │   ├── Timetable.vue    # 课程表
│   │   │   │   ├── Grades.vue       # 成绩查询
│   │   │   │   └── Announcements.vue  # 通知公告
│   │   │   │
│   │   │   └── admin/               # 后台管理页面
│   │   │       ├── Layout.vue       # 管理后台布局
│   │   │       ├── Dashboard.vue    # 管理仪表盘
│   │   │       ├── StudentManagement.vue  # 学生管理
│   │   │       ├── DocumentManagement.vue  # 文档管理
│   │   │       ├── ResourceManagement.vue  # 资源管理
│   │   │       ├── Analytics.vue    # 数据分析（含图表）
│   │   │       └── AssetManagement.vue  # 资产管理
│   │   │
│   │   ├── App.vue                  # 根组件
│   │   ├── main.js                  # 入口文件
│   │   └── style.css                # 全局样式
│   │
│   ├── .env                         # 环境变量
│   ├── .env.production              # 生产环境变量
│   ├── .gitignore                   # Git忽略
│   ├── package.json                 # 依赖配置
│   ├── vite.config.js               # Vite配置
│   └── README.md                    # 前端文档
│
├── database/                        # 数据库相关
│   ├── schema.sql                  # 完整建表脚本
│   └── ER_DIAGRAM.md               # ER图说明
│
├── .gitignore                       # 项目Git忽略
├── README.md                        # 项目总览
├── DEPLOYMENT.md                   # 部署说明
└── PROJECT_STRUCTURE.md            # 本文件
```

## 核心功能模块

### 1. 用户认证系统
- **JWT Token认证**
- **RBAC权限控制**
- **多角色支持** (5种角色)

### 2. 校园门户 (3.1)
- 仪表盘
- 个人信息管理
- 班级信息查看
- 课程表查询
- 成绩查询
- 通知公告

### 3. 后台管理 (4.x)
- 文档管理 (发布/审批/分发/归档/查询)
- 学生管理 (CRUD)
- 成长记录
- 办公资产管理
- 会议管理
- 考勤管理
- 财务管理
- 工作流管理
- 系统设置

### 4. 大数据分析 (4.4)
- 数据采集
- 数据清洗
- 数据存储
- 数据分析
- 数据可视化 (ECharts)
- 政策参考报告

### 5. 一卡通系统 (4.5)
- 卡片发行
- 门禁控制
- 支付/考勤记录

### 6. 电子班牌 (4.6)
- 班牌设备管理
- 内容更新

### 7. 资源管理 (5.x)
- 教育资源 (上传/审核/下载/分享/统计)
- 教学资源
- 云平台 (数据聚合/负载均衡/安全管理)

### 8. 系统集成 (6.x)
- 第三方集成
- 数据同步
- API管理

### 9. 权限管理 (7.x)
- 角色分配
- 权限控制

### 10. 系统维护 (8.x)
- 日志管理
- 备份恢复
- 系统更新

## 数据库表结构 (30+张表)

### 用户中心 (5张)
- user (用户表)
- role (角色表)
- permission (权限表)
- user_role (用户角色关联)
- role_permission (角色权限关联)

### 学校组织 (5张)
- school (学校表)
- class (班级表)
- student (学生表)
- teacher (教师表)
- parent (家长表)

### 教学管理 (4张)
- timetable (课程表)
- grade (成绩表)
- announcement (通知公告)
- student_behavior (学生表现)

### 后台管理 (8张)
- document (文档表)
- growth_record (成长记录)
- office_asset (办公资产)
- meeting (会议表)
- meeting_participant (会议参与者)
- attendance (考勤表)
- finance (财务表)
- workflow (工作流)
- workflow_instance (工作流实例)
- system_setting (系统设置)

### 大数据 (3张)
- data_collection (数据采集)
- data_cleaning (数据清洗)
- data_analysis (数据分析)

### 一卡通 (3张)
- smart_card (智能卡)
- access_control (门禁记录)
- payment_record (支付记录)

### 资源管理 (4张)
- educational_resource (教育资源)
- resource_share (资源分享)
- resource_evaluation (资源评价)
- resource_statistics (资源统计)

### 电子班牌 (2张)
- class_sign (班牌设备)
- class_sign_content (班牌内容)

### 通讯 (2张)
- message (消息)
- notification_push (通知推送)

### 日志 (2张)
- operation_log (操作日志)
- login_log (登录日志)

## API接口数量

- **认证模块**: 3个接口
- **校园门户**: 6个接口
- **后台管理**: 10+个接口
- **资源管理**: 6个接口
- **大数据分析**: 5个接口
- **总计**: 30+个RESTful API

## 前端页面数量

- **公共页面**: 3个 (登录、注册、404)
- **校园门户**: 6个 (仪表盘、个人信息、班级、课程表、成绩、公告)
- **后台管理**: 7个 (布局、仪表盘、学生管理、文档管理、资源管理、数据分析、资产管理)
- **总计**: 16个页面组件

## 技术特点

### 后端
- ✅ Spring Boot 3.x
- ✅ Spring Security + JWT
- ✅ MyBatis-Plus (简化CRUD)
- ✅ 统一异常处理
- ✅ 统一响应格式
- ✅ 自动填充 (创建/更新时间)
- ✅ 乐观锁
- ✅ 分页查询
- ✅ Swagger API文档
- ✅ 全部接口都有注释

### 前端
- ✅ Vue 3 Composition API
- ✅ Element Plus UI组件
- ✅ Pinia状态管理
- ✅ Vue Router 4
- ✅ Axios封装
- ✅ ECharts图表
- ✅ 响应式设计
- ✅ 权限控制
- ✅ 环境变量配置

## 开发规范

### 代码规范
- Java: 阿里巴巴Java开发手册
- Vue: ESLint + Prettier
- SQL: 标准SQL规范

### 提交规范
- feat: 新功能
- fix: 修复bug
- docs: 文档更新
- style: 代码格式
- refactor: 重构
- test: 测试
- chore: 构建/工具

### 命名规范
- Java类: PascalCase
- Java方法: camelCase
- 数据库表: snake_case
- Vue组件: PascalCase
- API路径: kebab-case

## 扩展性

### 易于扩展的模块
1. **新增角色**: 只需在role表添加记录，配置权限
2. **新增功能**: 遵循MVC模式，快速开发
3. **新增API**: 统一格式，自动文档
4. **新增页面**: Vue组件化，复用性强

### 性能优化点
1. **数据库**: 索引优化、查询优化
2. **缓存**: Redis缓存热点数据
3. **前端**: 路由懒加载、组件按需加载
4. **网络**: Nginx压缩、CDN加速

## 安全特性

- ✅ JWT Token加密
- ✅ 密码BCrypt加密
- ✅ RBAC权限控制
- ✅ SQL注入防护
- ✅ XSS防护
- ✅ CSRF防护
- ✅ 敏感数据脱敏
- ✅ 访问日志审计

## 项目优势

1. **完整性**: 覆盖教育管理全场景
2. **规范性**: 遵循最佳实践
3. **可维护性**: 清晰的代码结构
4. **可扩展性**: 模块化设计
5. **安全性**: 多层安全防护
6. **用户体验**: 美观的UI和交互
7. **文档完善**: 详细的开发文档
8. **易于部署**: 提供完整部署方案

---

**版本**: v1.0.0
**更新日期**: 2025-01-01
