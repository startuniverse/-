# 部署和运行说明

## 系统要求

### 硬件要求
- **CPU**: 2核+
- **内存**: 4GB+
- **磁盘**: 50GB+
- **网络**: 可访问互联网（用于下载依赖）

### 软件要求
- **操作系统**: Linux (推荐) / Windows / macOS
- **JDK**: 17+
- **Maven**: 3.8+
- **MySQL**: 8.0+
- **Node.js**: 16+
- **Redis**: 6.0+ (可选，用于缓存和会话)

## 一、环境准备

### 1. 安装JDK 17

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install openjdk-17-jdk
java -version  # 验证安装
```

**Windows:**
- 下载: https://adoptium.net/temurin/releases/
- 安装并配置环境变量 `JAVA_HOME`

### 2. 安装Maven

**Linux:**
```bash
sudo apt install maven
mvn -version
```

**Windows:**
- 下载: https://maven.apache.org/download.cgi
- 配置环境变量 `MAVEN_HOME`

### 3. 安装MySQL 8.0

**Linux:**
```bash
sudo apt install mysql-server
sudo mysql_secure_installation
```

**Windows:**
- 下载: https://dev.mysql.com/downloads/mysql/
- 安装并设置root密码

### 4. 安装Node.js

**Linux:**
```bash
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs
node -v
npm -v
```

**Windows:**
- 下载: https://nodejs.org/
- 安装LTS版本

### 5. 安装Redis (可选)

**Linux:**
```bash
sudo apt install redis-server
sudo systemctl start redis
sudo systemctl enable redis
redis-cli ping  # 应返回 PONG
```

**Windows:**
- 下载: https://github.com/microsoftarchive/redis/releases
- 或使用WSL2安装Linux版本

## 二、数据库配置

### 1. 创建数据库

```sql
-- 登录MySQL
mysql -u root -p

-- 创建数据库
CREATE DATABASE education_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户（可选）
CREATE USER 'edu_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON education_platform.* TO 'edu_user'@'localhost';
FLUSH PRIVILEGES;
```

### 2. 导入表结构

```bash
# 在项目根目录执行
mysql -u root -p education_platform < database/schema.sql
```

### 3. 验证表创建

```sql
USE education_platform;
SHOW TABLES;
-- 应看到30+张表
```

## 三、后端部署

### 1. 修改配置文件

编辑 `backend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/education_platform?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=Asia/Shanghai
    username: root  # 修改为你的数据库用户名
    password: root  # 修改为你的数据库密码

  redis:
    host: localhost
    port: 6379
    password:      # 如果有密码则填写
```

### 2. 编译后端

```bash
cd backend
mvn clean compile
```

### 3. 运行后端

**方式一：直接运行**
```bash
mvn spring-boot:run
```

**方式二：打包后运行**
```bash
mvn clean package
java -jar target/education-platform-1.0.0.jar
```

### 4. 验证后端

访问: http://localhost:8080/swagger-ui.html

如果看到Swagger界面，说明后端启动成功。

## 四、前端部署

### 1. 安装依赖

```bash
cd frontend
npm install
```

如果下载慢，可以使用淘宝镜像：
```bash
npm config set registry https://registry.npmmirror.com
npm install
```

### 2. 配置环境变量

编辑 `frontend/.env` (开发环境) 或 `frontend/.env.production` (生产环境):

```env
VITE_API_BASE_URL=http://localhost:8080/api/v1
VITE_APP_TITLE=城市教育局综合信息服务平台
```

### 3. 运行开发服务器

```bash
npm run dev
```

访问: http://localhost:3000

### 4. 构建生产版本

```bash
npm run build
```

构建产物在 `dist/` 目录。

## 五、生产环境部署

### 1. 使用Nginx部署前端

**安装Nginx:**
```bash
# Ubuntu/Debian
sudo apt install nginx

# 启动
sudo systemctl start nginx
sudo systemctl enable nginx
```

**配置Nginx:**

编辑 `/etc/nginx/sites-available/edu-platform`:

```nginx
server {
    listen 80;
    server_name your-domain.com;  # 修改为你的域名或IP

    # 前端静态文件
    location / {
        root /var/www/edu-platform/frontend/dist;
        try_files $uri $uri/ /index.html;
        index index.html;
    }

    # 后端API代理
    location /api/v1 {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Swagger文档（仅开发环境开放）
    location /swagger-ui {
        proxy_pass http://localhost:8080/swagger-ui;
        allow 127.0.0.1;  # 仅本地访问
        deny all;
    }
}
```

**启用配置:**
```bash
sudo ln -s /etc/nginx/sites-available/edu-platform /etc/nginx/sites-enabled/
sudo nginx -t  # 测试配置
sudo systemctl reload nginx
```

### 2. 使用Systemd管理后端服务

创建服务文件 `/etc/systemd/system/edu-platform.service`:

```ini
[Unit]
Description=Education Platform Backend
After=network.target

[Service]
Type=simple
User=your-user
WorkingDirectory=/path/to/shixun/backend
ExecStart=/usr/bin/java -jar /path/to/shixun/backend/target/education-platform-1.0.0.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

**启用服务:**
```bash
sudo systemctl daemon-reload
sudo systemctl enable edu-platform
sudo systemctl start edu-platform
sudo systemctl status edu-platform
```

### 3. 使用Docker部署

**创建Dockerfile (后端):**

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/education-platform-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**创建Dockerfile (前端):**

```dockerfile
FROM nginx:alpine
COPY dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
```

**构建和运行:**
```bash
# 后端
docker build -t edu-platform-backend .
docker run -d -p 8080:8080 --name edu-backend edu-platform-backend

# 前端
docker build -t edu-platform-frontend .
docker run -d -p 80:80 --name edu-frontend edu-platform-frontend
```

## 六、初始化数据

### 1. 创建管理员账号

```bash
# 访问注册页面或使用SQL插入
INSERT INTO user (username, password, real_name, status)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBa3BjR0v9bYRi', '系统管理员', 1);

INSERT INTO user_role (user_id, role_id) VALUES (1, 1);
```

**默认密码**: admin123 (已加密)

### 2. 添加测试数据

```sql
-- 添加学校
INSERT INTO school (school_code, school_name, school_type) VALUES ('S001', '实验中学', 'secondary');

-- 添加班级
INSERT INTO class (school_id, class_name, class_code, grade) VALUES (1, '三年级一班', 'C001', '三年级');

-- 添加学生
INSERT INTO student (user_id, student_number, class_id) VALUES (2, '2024001', 1);
```

## 七、常见问题

### 1. 数据库连接失败
**原因**: 数据库未启动或配置错误
**解决**:
```bash
sudo systemctl status mysql
# 检查application.yml中的用户名密码
```

### 2. 端口被占用
**查看占用端口的进程:**
```bash
# Linux
sudo lsof -i :8080
sudo lsof -i :3000

# Windows
netstat -ano | findstr :8080
```

**杀死进程:**
```bash
kill -9 <PID>
```

### 3. 跨域问题
**解决**: 后端已配置CORS，如仍有问题，检查：
- 前端API地址配置
- Nginx代理配置

### 4. 前端依赖安装失败
**解决**:
```bash
# 清除缓存
npm cache clean --force

# 删除node_modules并重装
rm -rf node_modules package-lock.json
npm install
```

### 5. Swagger无法访问
**解决**:
- 检查后端是否运行
- 检查URL: http://localhost:8080/swagger-ui.html
- 生产环境可能需要配置访问权限

## 八、性能优化

### 1. JVM调优
```bash
java -Xms2g -Xmx2g -jar app.jar
```

### 2. MySQL优化
```sql
-- 优化配置
[mysqld]
innodb_buffer_pool_size = 2G
max_connections = 500
```

### 3. Redis配置
```yaml
spring:
  redis:
    timeout: 5000ms
    lettuce:
      pool:
        max-active: 20
        max-idle: 10
        min-idle: 5
```

## 九、监控与维护

### 1. 日志查看
```bash
# 后端日志
tail -f logs/education-platform.log

# Nginx日志
tail -f /var/log/nginx/access.log
tail -f /var/log/nginx/error.log
```

### 2. 备份策略
```bash
# 数据库备份
mysqldump -u root -p education_platform > backup_$(date +%Y%m%d).sql

# 代码备份
tar -czf edu-platform-backup.tar.gz /path/to/shixun
```

### 3. 健康检查
```bash
# 检查后端
curl http://localhost:8080/actuator/health

# 检查前端
curl -I http://localhost:3000
```

## 十、安全建议

1. **修改默认密码**
2. **配置防火墙**
3. **启用HTTPS**
4. **定期更新系统**
5. **备份重要数据**
6. **限制Swagger访问**
7. **监控异常登录**

## 技术支持

如有问题，请查看：
- 项目文档: README.md
- 后端文档: backend/README.md
- 前端文档: frontend/README.md
- API文档: http://localhost:8080/swagger-ui.html

---

**版本**: v1.0.0
**更新日期**: 2025-01-01
**维护团队**: Education Platform Team
