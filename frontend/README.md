# 教育局综合信息服务平台 - 前端

## 项目简介

城市教育局综合信息服务平台前端项目，基于 Vue 3 + Element Plus + Vite 构建。

## 技术栈

- **框架**: Vue 3 (Composition API)
- **UI库**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **HTTP客户端**: Axios
- **图表**: ECharts
- **构建工具**: Vite

## 项目结构

```
frontend/
├── public/                 # 静态资源
├── src/
│   ├── api/               # API接口
│   │   ├── auth.js        # 认证相关
│   │   ├── campus.js      # 校园门户相关
│   │   └── admin.js       # 后台管理相关
│   ├── store/             # Pinia状态管理
│   │   └── modules/       # store模块
│   ├── router/            # 路由配置
│   ├── utils/             # 工具函数
│   │   └── request.js     # Axios封装
│   ├── views/             # 页面组件
│   │   ├── Login.vue      # 登录页
│   │   ├── Register.vue   # 注册页
│   │   ├── Layout.vue     # 校园门户布局
│   │   ├── campus/        # 校园门户页面
│   │   └── admin/         # 后台管理页面
│   ├── App.vue            # 根组件
│   └── main.js            # 入口文件
├── .env                   # 环境变量
├── .gitignore             # Git忽略文件
├── package.json           # 依赖配置
├── vite.config.js         # Vite配置
└── README.md              # 说明文档
```

## 快速开始

### 环境要求

- Node.js >= 16
- npm >= 8 或 yarn >= 1.22

### 安装依赖

```bash
npm install
# 或
yarn install
```

### 开发运行

```bash
npm run dev
# 或
yarn dev
```

访问 http://localhost:3000

### 构建生产

```bash
npm run build
# 或
yarn build
```

构建产物在 `dist/` 目录

### 预览生产构建

```bash
npm run preview
```

## 功能模块

### 1. 校园门户
- 仪表盘：显示用户信息、快捷链接、通知概览
- 个人信息：查看和编辑个人信息
- 班级信息：查看班级花名册
- 课程表：查看个人课程安排
- 成绩查询：查询和筛选成绩
- 通知公告：查看学校/班级通知

### 2. 后台管理
- 管理仪表盘：系统状态概览
- 学生管理：学生信息CRUD
- 文档管理：文档发布、审批、查询
- 资源管理：教育资源审核、统计
- 数据分析：图表展示、政策报告
- 资产管理：办公资产登记、管理

## API接口

前端通过 Axios 与后端 Spring Boot 服务通信：

- **基础URL**: `http://localhost:8080/api/v1`
- **认证方式**: JWT Token (Bearer)
- **响应格式**:
  ```json
  {
    "code": 200,
    "message": "操作成功",
    "data": {},
    "timestamp": 1234567890
  }
  ```

## 路由说明

- `/login` - 登录页
- `/register` - 注册页
- `/` - 校园门户首页（需要登录）
- `/admin/*` - 后台管理（需要ADMIN角色）

## 开发规范

### 代码风格
- 使用 ESLint + Prettier
- 组件使用 `<script setup>` 语法
- 文件名使用 PascalCase (如: UserList.vue)
- API函数名使用小驼峰 (如: getUserInfo)

### 提交规范
- feat: 新功能
- fix: 修复bug
- docs: 文档更新
- style: 代码格式
- refactor: 重构
- test: 测试相关
- chore: 构建/工具相关

## 部署说明

### Nginx配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        root /var/www/frontend/dist;
        try_files $uri $uri/ /index.html;
    }

    location /api/v1 {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 环境变量

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| VITE_API_BASE_URL | API基础地址 | http://localhost:8080/api/v1 |
| VITE_APP_TITLE | 应用标题 | 城市教育局综合信息服务平台 |
| VITE_APP_VERSION | 应用版本 | 1.0.0 |

## 浏览器支持

- Chrome >= 87
- Firefox >= 78
- Safari >= 14
- Edge >= 88

## License

MIT License
