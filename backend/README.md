# 城市教育局综合信息服务平台 - 后端

## 项目简介

基于 Spring Boot 3.x 构建的城市教育局综合信息服务平台后端服务，提供完整的教育管理功能，包括校园门户、后台管理、资源管理、大数据分析等模块。

## 技术栈

- **框架**: Spring Boot 3.1.12
- **安全**: Spring Security + JWT
- **ORM**: MyBatis-Plus 3.5.5
- **数据库**: MySQL 8.0
- **缓存**: Redis (可选)
- **文档**: Swagger/OpenAPI 3
- **工具**: Lombok, Hutool
- **构建**: Maven

## 项目结构

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/education/platform/
│   │   │   ├── config/           # 配置类
│   │   │   ├── controller/       # 控制器
│   │   │   ├── entity/           # 实体类
│   │   │   ├── mapper/           # MyBatis Mapper
│   │   │   ├── service/          # 服务接口
│   │   │   │   └── impl/         # 服务实现
│   │   │   ├── security/         # 安全相关
│   │   │   ├── util/             # 工具类
│   │   │   └── common/           # 公共类
│   │   └── resources/
│   │       ├── mapper/           # MyBatis XML
│   │       ├── application.yml   # 主配置
│   │       └── application-{profile}.yml
│   └── test/                     # 测试
├── database/
│   ├── schema.sql               # 数据库结构
│   └── ER_DIAGRAM.md            # ER图说明
├── pom.xml                      # Maven配置
└── README.md
```

## 数据库设计

### 核心表结构

1. **用户中心**: user, role, permission, user_role, role_permission
2. **学校组织**: school, class, student, teacher, parent
3. **教学管理**: timetable, grade, announcement, student_behavior
4. **后台管理**: document, growth_record, office_asset, meeting, attendance, finance
5. **大数据**: data_collection, data_cleaning, data_analysis
6. **一卡通**: smart_card, access_control, payment_record
7. **资源管理**: educational_resource, resource_share, resource_evaluation
8. **电子班牌**: class_sign, class_sign_content
9. **通讯**: message, notification_push
10. **日志**: operation_log, login_log

详细设计见 `database/schema.sql`

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+ (可选)

> **注意**: 本项目使用 Spring Boot 3.1.12，避免了 Spring Boot 3.2.0 与 MyBatis-Plus 3.5.5 的兼容性问题。

### 数据库准备

1. 创建数据库:
```sql
CREATE DATABASE education_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行建表脚本:
```bash
mysql -u root -p education_platform < database/schema.sql
```

### 配置文件

修改 `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/education_platform?...
    username: your_username
    password: your_password
  redis:
    host: localhost
    port: 6379
```

### 运行项目

```bash
# 编译
mvn clean compile

# 运行
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/education-platform-1.0.0.jar
```

访问 http://localhost:8080/swagger-ui.html 查看API文档

## API接口说明

### 认证模块 (`/auth`)
- `POST /auth/login` - 用户登录
- `POST /auth/register` - 用户注册
- `GET /auth/info` - 获取用户信息

### 校园门户 (`/campus`)
- `GET /campus/dashboard` - 仪表盘
- `GET /campus/profile` - 个人信息
- `GET /campus/class/info` - 班级信息
- `GET /campus/timetable` - 课程表
- `GET /campus/grades` - 成绩查询
- `GET /campus/announcements` - 通知公告

### 后台管理 (`/backend`)
- `GET /backend/system/settings` - 系统设置
- `GET /backend/student/list` - 学生列表
- `POST /backend/document/publish` - 发布文档
- `POST /backend/document/approve` - 审批文档
- `GET /backend/asset/list` - 资产列表

### 资源管理 (`/resource`)
- `POST /resource/upload` - 上传资源
- `POST /resource/review` - 审核资源
- `GET /resource/list` - 资源列表
- `GET /resource/statistics` - 资源统计

### 大数据分析 (`/bigdata`)
- `GET /bigdata/analysis/teacher-distribution` - 教师分布
- `GET /bigdata/visualization/chart` - 图表数据
- `GET /bigdata/policy/report` - 政策报告

## 安全机制

### JWT认证流程
1. 用户登录获取JWT Token
2. 后续请求在Header中携带 `Authorization: Bearer {token}`
3. Spring Security验证Token并解析用户信息
4. 根据RBAC权限控制访问

### RBAC权限模型
- **角色**: ADMIN, SCHOOL_ADMIN, TEACHER, STUDENT, PARENT
- **权限**: 菜单权限、按钮权限、接口权限
- **控制**: 方法级别 `@PreAuthorize` 注解

## 开发规范

### 代码风格
- 使用Lombok减少样板代码
- 统一异常处理
- 统一响应格式 `ApiResult<T>`
- 分页查询使用 `PageResult<T>`

### 命名规范
- 类名: PascalCase
- 方法名: camelCase
- 变量名: camelCase
- 常量: UPPER_SNAKE_CASE
- 数据库字段: snake_case

### 注释规范
- 类和方法必须有Javadoc
- 复杂逻辑需要行内注释
- API接口需要Swagger注解

## 性能优化

### 数据库优化
- 使用MyBatis-Plus分页插件
- 热点数据添加索引
- 大表考虑分表

### 缓存策略
- 使用Redis缓存热点数据
- 缓存用户信息和权限
- 设置合理的过期时间

### 连接池
- 使用HikariCP
- 配置合适的连接数

## 部署说明

### Docker部署

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/education-platform-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 运行命令
```bash
docker build -t education-platform .
docker run -d -p 8080:8080 --name edu-platform education-platform
```

## 测试

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=UserServiceTest
```

## 监控与日志

### 日志配置
- 使用SLF4J + Logback
- 日志级别: DEBUG(开发), INFO(生产)
- 日志文件: logs/education-platform.log

### 监控指标
- JVM内存使用
- 数据库连接池状态
- API响应时间
- 错误率

## 扩展开发

### 添加新模块
1. 创建实体类 (entity/)
2. 创建Mapper接口 (mapper/)
3. 创建Service接口和实现 (service/)
4. 创建Controller (controller/)
5. 添加Swagger注解
6. 编写单元测试

### 自定义配置
在 `application.yml` 中添加自定义配置，使用 `@Value` 注入

## 常见问题

### 1. 跨域问题
已配置CORS，允许所有来源访问

### 2. JWT过期
登录后Token有效期24小时，需要重新登录

### 3. 数据库连接失败
检查MySQL服务是否运行，配置是否正确

### 4. 权限不足
检查用户角色和权限配置

### 5. Spring Boot 3.2.0 启动错误
**问题**: `java.lang.IllegalArgumentException: Invalid value type for attribute 'factoryBeanObjectType': java.lang.String`

**原因**: Spring Boot 3.2.0 与 MyBatis-Plus 3.5.5 的兼容性问题

**解决方案**:
1. 确保 `MybatisPlusConfig.java` 包含 `sqlSessionFactory` Bean 配置
2. 在 `application.yml` 中添加 `type-aliases-super-type: java.lang.Object`
3. 主应用类必须添加 `@MapperScan("com.education.platform.mapper")` 注解

详细修复步骤请参考 `FIX_SPRINGBOOT_320_ISSUES.md`

### 6. Redis 连接失败
**问题**: Redis 服务未启动导致启动失败

**解决方案**:
- 启动 Redis 服务，或
- 在 `application.yml` 中设置 `spring.redis.enabled: false` 临时禁用

### 7. 端口被占用
**问题**: `Port 8080 is already in use`

**解决方案**:
- 修改 `application.yml` 中的 `server.port`
- 或关闭占用8080端口的其他服务

## License

MIT License
