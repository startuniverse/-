# 在线学习培训模块 - 使用指南

## 📖 本文档用途

本文档帮助你快速了解和使用**在线学习培训模块**。

---

## 🎯 快速导航

### 我是新手，想快速开始
👉 请阅读：**培训模块快速开始.md**

### 我想了解技术细节
👉 请阅读：**在线学习培训模块实现总结.md**

### 我要部署上线
👉 请阅读：**部署检查清单.md**

### 我想查看完整项目信息
👉 请阅读：**项目完成总结.md**

---

## 📋 模块概述

这是一个完整的在线学习培训系统，包含：

### 功能模块
1. **课程管理** - 创建、发布、管理培训课程
2. **学员报名** - 在线报名、查看进度
3. **在线考试** - 多种题型、自动评分
4. **证书管理** - 结业证书、下载
5. **数据统计** - 实时数据、可视化

### 用户角色
- **学生/教师** - 浏览、报名、学习、考试
- **管理员** - 课程管理、数据统计

---

## 🚀 5分钟快速体验

### 第1步：创建数据库
```bash
mysql -u root -p education_platform < E:\shixun\database\schema.sql
```

### 第2步：启动后端
```bash
cd E:\shixun\backend
mvn spring-boot:run
```

### 第3步：启动前端
```bash
cd E:\shixun\frontend
npm run dev
```

### 第4步：测试
1. 浏览器访问 `http://localhost:5173`
2. 登录管理员账号
3. 进入"培训管理"
4. 创建一个课程
5. 登录学生账号
6. 报名并参加考试

---

## 📁 文件结构

```
E:\shixun\
│
├── 数据库表结构
│   └── database/schema.sql                    (新增8张表)
│
├── 后端代码
│   ├── backend/src/main/java/com/education/platform/
│   │   ├── entity/Training*.java             (4个实体类)
│   │   ├── mapper/Training*.java             (4个Mapper)
│   │   └── controller/TrainingController.java (1个控制器)
│   └── 已集成到现有项目
│
├── 前端代码
│   ├── frontend/src/
│   │   ├── api/training.js                   (API文件)
│   │   ├── views/campus/TrainingCourses.vue (学生课程)
│   │   ├── views/campus/MyTraining.vue       (我的培训)
│   │   ├── views/admin/TrainingManagement.vue (管理后台)
│   │   ├── router/index.js                   (已更新路由)
│   │   └── views/Layout.vue                  (已更新菜单)
│   └── 已集成到现有项目
│
└── 文档
    ├── 培训模块快速开始.md                    (快速开始)
    ├── 在线学习培训模块实现总结.md            (技术文档)
    ├── 部署检查清单.md                        (部署指南)
    ├── 项目完成总结.md                        (项目概览)
    └── 培训模块README.md                      (本文档)
```

---

## 🎓 核心概念

### 数据库表（8张）
1. **training_course** - 课程信息
2. **training_enrollment** - 学员报名
3. **training_chapter** - 章节内容（可扩展）
4. **training_learning_record** - 学习记录（可扩展）
5. **training_exam** - 考试配置（可扩展）
6. **training_question** - 题库（可扩展）
7. **training_exam_record** - 考试记录
8. **training_evaluation** - 课程评价

### API接口（12个）
**学生端：**
- 课程列表、详情
- 报名、我的课程
- 考试提交、记录
- 评价、评分统计

**管理员端：**
- 课程管理
- 数据统计

### 前端页面（3个）
- **TrainingCourses.vue** - 课程浏览
- **MyTraining.vue** - 我的培训（含考试）
- **TrainingManagement.vue** - 管理后台

---

## 🔍 功能速查表

| 功能 | 路径 | 角色 |
|------|------|------|
| 浏览课程 | `/campus/training` | 学生/教师 |
| 我的课程 | `/campus/my-training` | 学生/教师 |
| 培训管理 | `/admin/training` | 管理员 |

---

## 💡 常见问题

### Q: 数据库表没创建？
A: 运行 `mysql -u root -p education_platform < E:\shixun\database\schema.sql`

### Q: 页面不显示？
A: 检查路由和菜单是否已更新

### Q: 接口404？
A: 确保后端已启动，检查端口8080

### Q: 权限错误？
A: 确保用户角色正确，检查localStorage

---

## 📞 获取帮助

### 文档优先
1. 查看 **培训模块快速开始.md**
2. 查看 **在线学习培训模块实现总结.md**
3. 查看 **部署检查清单.md**

### 代码参考
- 对比 `ResourceController.java` 和 `TrainingController.java`
- 对比 `Resources.vue` 和 `TrainingCourses.vue`

---

## ✅ 检查清单

部署前确认：
- [ ] 数据库8张表已创建
- [ ] 后端12个接口可用
- [ ] 前端3个页面存在
- [ ] 路由配置正确
- [ ] 菜单显示正常

---

## 🎉 开始使用

你已经准备就绪！

**下一步：**
1. 阅读 **培训模块快速开始.md**
2. 按步骤操作
3. 体验完整功能

**祝你使用愉快！** 🚀

---

**版本：** 1.0
**日期：** 2026-01-08
**状态：** ✅ 完成
