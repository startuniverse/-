# 前端登录问题修复指南

## 🔍 问题分析

### 问题现象
- 前端访问：http://localhost:3000/
- 登录时提示：404 错误，请求地址不存在

### 根本原因
1. **端口不匹配**：
   - 前端配置：8080
   - 后端实际：8081

2. **缺少测试数据**：
   - 数据库中可能没有 admin 用户
   - 或者密码不正确

## ✅ 已完成的修复

### 1. 前端配置修复

#### 文件：`.env`
```env
# 修改前
VITE_API_BASE_URL=http://localhost:8080/api/v1

# 修改后 ✅
VITE_API_BASE_URL=http://localhost:8081/api/v1
```

#### 文件：`vite.config.js`
```javascript
// 修改前
proxy: {
  '/api': {
    target: 'http://localhost:8080',  // ❌
    // ...
  }
}

// 修改后 ✅
proxy: {
  '/api': {
    target: 'http://localhost:8081',  // ✅
    // ...
  }
}
```

#### 文件：`src/utils/request.js`
```javascript
// 修改前
const service = axios.create({
  baseURL: '/api/v1',  // ❌ 硬编码
  // ...
})

// 修改后 ✅
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api/v1',  // ✅ 使用环境变量
  // ...
})
```

### 2. 后端配置确认

#### 确认信息：
- ✅ 后端运行端口：8081
- ✅ 上下文路径：/api/v1
- ✅ 登录接口：POST /api/v1/auth/login
- ✅ 完整地址：http://localhost:8081/api/v1/auth/login

## 📋 下一步操作

### 步骤 1：创建测试用户

在 MySQL 中执行以下 SQL：

```sql
USE education_platform;

-- 创建 admin 用户（密码: admin123）
INSERT INTO user (
    username, password, real_name, phone, email, status, deleted
) VALUES (
    'admin',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBa3BjR0v9bHri',
    '系统管理员',
    '13800138000',
    'admin@education.com',
    1,
    0
);

-- 创建 ADMIN 角色
INSERT INTO role (role_code, role_name, description, status, deleted)
VALUES ('ADMIN', '系统管理员', '系统最高权限', 1, 0);

-- 分配角色
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id
FROM user u, role r
WHERE u.username = 'admin' AND r.role_code = 'ADMIN';
```

或者直接运行：
```bash
mysql -u root -p education_platform < E:\shixun\backend\database\create_admin.sql
```

### 步骤 2：重启前端服务

如果前端正在运行，需要重启以加载新的配置：

```bash
# 停止当前服务 (Ctrl + C)
# 重新启动
cd E:\shixun\frontend
npm run dev
```

### 步骤 3：测试登录

1. 访问：http://localhost:3000/
2. 输入用户名：`admin`
3. 输入密码：`admin123`
4. 点击登录

## 🎯 预期结果

登录成功后应该：
- ✅ 显示登录成功提示
- ✅ 跳转到管理员仪表盘（/admin/dashboard）
- ✅ 在浏览器控制台看到 API 请求成功

## 🔧 如果仍然失败

### 检查 1：后端是否正常运行
```bash
# 访问健康检查
curl http://localhost:8081/api/v1/auth/login
```

### 检查 2：数据库用户数据
```sql
SELECT id, username, password, status FROM user WHERE username = 'admin';
```

### 检查 3：浏览器开发者工具
- 打开 F12 → Network 标签
- 查看登录请求的完整 URL
- 查看请求和响应详情

### 检查 4：前端控制台
- 打开 F12 → Console 标签
- 查看是否有 JavaScript 错误

## 📞 完整的 API 地址

| 功能 | 完整 URL |
|------|---------|
| 登录 | http://localhost:8081/api/v1/auth/login |
| 注册 | http://localhost:8081/api/v1/auth/register |
| 用户信息 | http://localhost:8081/api/v1/auth/info |
| Swagger | http://localhost:8081/swagger-ui.html |

## 💡 提示

- 前端通过 Vite 代理访问后端，实际请求会转发到 8081 端口
- 确保后端 Spring Boot 应用正在运行
- 确保 MySQL 数据库服务正在运行
- 密码 `admin123` 已经过 BCrypt 加密存储
