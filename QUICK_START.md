# 快速开始指南

## 🎯 5分钟快速运行

### 前置条件
- ✅ JDK 17+
- ✅ MySQL 8.0+
- ✅ Node.js 16+

---

## 步骤 1: 数据库准备 (2分钟)

```bash
# 1. 登录MySQL
mysql -u root -p

# 2. 创建数据库
CREATE DATABASE education_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 3. 导入表结构 (在项目根目录执行)
# 打开新的终端窗口
cd E:\shixun
mysql -u root -p education_platform < database/schema.sql
```

---

## 步骤 2: 启动后端 (1分钟)

```bash
# 1. 进入后端目录
cd E:\shixun\backend

# 2. 修改配置 (application.yml)
# 修改数据库用户名和密码
# spring.datasource.username = your_username
# spring.datasource.password = your_password

# 3. 运行后端
mvn spring-boot:run
```

**验证**: 访问 http://localhost:8080/swagger-ui.html

---

## 步骤 3: 启动前端 (1分钟)

```bash
# 1. 进入前端目录
cd E:\shixun\frontend

# 2. 安装依赖 (第一次运行需要)
npm install

# 3. 运行前端
npm run dev
```

**验证**: 访问 http://localhost:3000

---

## 步骤 4: 登录测试 (1分钟)

### 默认测试账号

**管理员账号:**
- 用户名: `admin`
- 密码: `admin123`

**学生账号:**
- 用户名: `student`
- 密码: `student123`

**教师账号:**
- 用户名: `teacher`
- 密码: `teacher123`

> 💡 **提示**: 首次登录后请修改密码！

---

## 📋 功能体验

### 校园门户
1. **仪表盘**: 查看个人信息和快捷链接
2. **个人信息**: 查看/编辑个人资料
3. **课程表**: 查看课程安排
4. **成绩查询**: 查询成绩
5. **通知公告**: 查看通知

### 后台管理 (管理员)
1. **管理仪表盘**: 系统状态概览
2. **学生管理**: 学生信息管理
3. **文档管理**: 文档发布和审批
4. **资源管理**: 教育资源审核
5. **数据分析**: 图表展示和报告

---

## 🔧 常见问题

### 1. 端口被占用
```bash
# 查看8080端口占用
netstat -ano | findstr :8080

# 查看3000端口占用
netstat -ano | findstr :3000
```

### 2. 数据库连接失败
- 检查MySQL是否启动
- 检查用户名密码是否正确
- 检查数据库名是否正确

### 3. 前端依赖安装失败
```bash
# 清除缓存重试
npm cache clean --force
rm -rf node_modules package-lock.json
npm install
```

---

## 📚 详细文档

- **项目总览**: `README.md`
- **部署说明**: `DEPLOYMENT.md`
- **项目结构**: `PROJECT_STRUCTURE.md`
- **后端文档**: `backend/README.md`
- **前端文档**: `frontend/README.md`

---

## 🚀 下一步

1. **添加测试数据**: 查看 `database/schema.sql` 中的示例数据
2. **自定义配置**: 修改 `application.yml` 和前端环境变量
3. **开发新功能**: 参考现有模块的开发模式
4. **生产部署**: 查看 `DEPLOYMENT.md` 了解完整部署流程

---

## 💡 小贴士

- 💻 **开发环境**: 使用 `mvn spring-boot:run` 和 `npm run dev`
- 📦 **生产环境**: 打包后使用 `java -jar` 和 Nginx
- 🔄 **热更新**: 前端开发时修改代码自动刷新
- 📊 **API文档**: Swagger UI 提供完整的接口文档
- 🔐 **权限控制**: 不同角色看到不同菜单
- 📈 **数据可视化**: 使用 ECharts 展示分析结果

---

**祝您使用愉快！** 🎉

如有问题，请查看详细文档或联系开发团队。
