# 城市教育局综合信息服务平台

## 项目概述

本项目是一个综合性的教育管理平台，旨在为城市教育局提供全面的信息化管理解决方案。平台基于"互联网+智慧教育"理念，实现电子班牌、家校沟通、教育大数据分析等功能，支持多角色用户（管理员、学校管理员、教师、学生、家长）使用。

## 项目架构

```
shixun/
├── backend/              # Spring Boot 后端
│   ├── src/
│   │   ├── main/java/   # Java源代码
│   │   └── resources/   # 配置文件
│   ├── pom.xml          # Maven配置
│   └── README.md        # 后端文档
├── frontend/            # Vue 3 前端
│   ├── src/
│   │   ├── api/        # API接口
│   │   ├── store/      # 状态管理
│   │   ├── router/     # 路由
│   │   ├── views/      # 页面组件
│   │   └── utils/      # 工具函数
│   ├── package.json    # 依赖配置
│   └── README.md       # 前端文档
├── database/            # 数据库
│   ├── schema.sql      # 建表脚本
│   └── ER_DIAGRAM.md   # ER图说明
└── README.md           # 项目总览
```

## 技术栈

### 后端
- **框架**: Spring Boot 3.2.0
- **安全**: Spring Security + JWT
- **ORM**: MyBatis-Plus 3.5.5
- **数据库**: MySQL 8.0
- **缓存**: Redis (可选)
- **文档**: Swagger/OpenAPI

### 前端
- **框架**: Vue 3 (Composition API)
- **UI库**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **图表**: ECharts
- **构建**: Vite

## 功能模块

### 1. 校园门户 (3.1)
- **登录/注册**: 用户认证系统
- **个人信息**: 查看和编辑个人资料
- **班级信息**: 班级花名册查看
- **课程表**: 交互式课程表
- **成绩查询**: 多维度成绩查询
- **通知公告**: 读取状态管理

### 2. 移动端 (3.2)
- **移动登录**: 简化认证流程
- **通知推送**: 实时消息推送
- **家校沟通**: 师生家长三方沟通

### 3. 后台管理 (4.x)
- **文档管理**: 发布、审批、分发、归档
- **学生管理**: 学生信息CRUD
- **成长记录**: 学生成长轨迹
- **办公管理**: 资产、会议、考勤、财务
- **工作流**: 自定义审批流程
- **系统设置**: 全局参数配置

### 4. 大数据分析 (4.4)
- **数据采集**: 学校数据上传
- **数据清洗**: 数据验证处理
- **数据存储**: 大数据存储方案
- **数据分析**: 多维度分析
- **数据可视化**: ECharts图表展示
- **政策参考**: 规划报告生成

### 5. 一卡通系统 (4.5)
- **卡片发行**: 实体/虚拟卡管理
- **门禁控制**: 智能门禁集成
- **支付/考勤**: 消费和签到记录

### 6. 电子班牌 (4.6)
- **班牌显示**: 数字化班级展示
- **内容更新**: 管理员内容发布

### 7. 资源管理 (5.x)
- **教育资源**: 上传、审核、下载、分享
- **教学资源**: 内部资源共享
- **云平台**: 数据聚合、负载均衡

### 8. 系统集成 (6.x)
- **第三方集成**: API对接
- **数据同步**: 实时同步机制
- **API管理**: 接口文档

### 9. 权限管理 (7.x)
- **角色分配**: RBAC模型
- **权限控制**: 细粒度权限

### 10. 系统维护 (8.x)
- **日志管理**: 操作日志、登录日志
- **备份恢复**: 数据备份
- **系统更新**: 版本管理

## 快速开始

### 环境准备

1. **JDK 17+**
2. **Maven 3.8+**
3. **MySQL 8.0+**
4. **Node.js 16+**
5. **Redis 6.0+** (可选)

### 数据库配置

```sql
-- 创建数据库
CREATE DATABASE education_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 导入表结构
mysql -u root -p education_platform < database/schema.sql
```

### 后端部署

```bash
# 进入后端目录
cd backend

# 修改配置 (application.yml)
# 配置数据库连接、Redis等

# 运行项目
mvn spring-boot:run

# 或打包运行
mvn clean package
java -jar target/education-platform-1.0.0.jar
```

后端访问: http://localhost:8080/swagger-ui.html

### 前端部署

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 开发运行
npm run dev

# 构建生产
npm run build
```

前端访问: http://localhost:3000

## 用户角色与权限

| 角色 | 权限说明 |
|------|----------|
| **ADMIN** | 系统管理员，所有权限 |
| **SCHOOL_ADMIN** | 学校管理员，管理本校数据 |
| **TEACHER** | 教师，教学管理、资源上传 |
| **STUDENT** | 学生，查看个人信息、成绩 |
| **PARENT** | 家长，查看孩子信息、家校沟通 |

## API文档

### 认证相关
- `POST /auth/login` - 登录
- `POST /auth/register` - 注册
- `GET /auth/info` - 用户信息

### 校园门户
- `GET /campus/dashboard` - 仪表盘
- `GET /campus/profile` - 个人信息
- `GET /campus/timetable` - 课程表
- `GET /campus/grades` - 成绩查询
- `GET /campus/announcements` - 通知公告

### 后台管理
- `GET /backend/system/settings` - 系统设置
- `GET /backend/student/list` - 学生管理
- `POST /backend/document/publish` - 文档发布
- `GET /backend/asset/list` - 资产管理

### 资源管理
- `POST /resource/upload` - 资源上传
- `GET /resource/list` - 资源列表
- `GET /resource/statistics` - 资源统计

### 大数据分析
- `GET /bigdata/analysis/teacher-distribution` - 教师分布
- `GET /bigdata/visualization/chart` - 图表数据
- `GET /bigdata/policy/report` - 政策报告

## 开发规范

### 代码风格
- **Java**: 遵循阿里巴巴Java开发手册
- **Vue**: 使用ESLint + Prettier
- **提交**: Conventional Commits规范

### 分支管理
- `main` - 主分支（生产环境）
- `develop` - 开发分支
- `feature/*` - 功能分支
- `hotfix/*` - 修复分支

### 测试
- 后端: JUnit 5
- 前端: Vitest/Jest

## 部署方案

### 开发环境
- 后端: `mvn spring-boot:run`
- 前端: `npm run dev`

### 生产环境
1. **Docker容器化**
2. **Nginx反向代理**
3. **MySQL主从复制**
4. **Redis集群**
5. **监控告警**

### Nginx配置示例
```nginx
server {
    listen 80;
    server_name edu-platform.com;

    # 前端
    location / {
        root /var/www/frontend/dist;
        try_files $uri $uri/ /index.html;
    }

    # 后端API
    location /api/v1 {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 性能优化

### 数据库
- 索引优化
- 查询优化
- 分表分库

### 缓存
- Redis缓存热点数据
- 缓存用户信息
- 缓存权限数据

### 前端
- 路由懒加载
- 组件按需加载
- 图片压缩
- CDN加速

## 安全考虑

### 认证授权
- JWT Token加密
- 密码BCrypt加密
- RBAC权限控制
- 接口权限验证

### 数据安全
- SQL注入防护
- XSS攻击防护
- CSRF防护
- 敏感数据脱敏

### 运维安全
- 日志审计
- 访问控制
- 定期备份
- 漏洞扫描

## 维护与支持

### 日常维护
- 日志监控
- 性能监控
- 数据备份
- 系统更新

### 故障处理
- 错误日志分析
- 快速回滚
- 应急预案

## 版本历史

### v1.0.0 (2025-01-01)
- ✅ 核心功能开发完成
- ✅ 用户认证系统
- ✅ 校园门户模块
- ✅ 后台管理模块
- ✅ 大数据分析模块
- ✅ 资源管理模块
- ✅ 数据可视化

## 开发团队

- **架构设计**: Education Platform Team
- **后端开发**: Spring Boot Team
- **前端开发**: Vue.js Team
- **数据库**: DBA Team
- **测试**: QA Team

## License

MIT License

Copyright (c) 2025 Education Platform Team

---

**注意**: 本项目为教育管理系统，包含敏感的学生和教师信息，请确保在安全的环境中部署，并遵循相关数据保护法规。
