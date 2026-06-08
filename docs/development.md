# Sky-Lark 开发指南

## 1. 环境准备

在开始开发前，请确保您的本地环境已安装以下工具：

- **Docker Desktop**：用于容器化运行环境（必选）
- **Node.js 20+**：用于前端本地开发（可选，推荐）
- **JDK 17**：用于后端本地开发（可选，推荐）
- **Maven 3.9.11**：用于后端依赖管理（可选）

## 2. 快速启动

### 2.1 全栈一键运行（推荐）

最简单的方式是使用 Docker Compose 启动整个环境。

```bash
# 在项目根目录执行
docker compose up --build
```

- 前端地址：`http://localhost:3000`
- 后端 API：`http://localhost:8080`
- 数据库：`localhost:3306`

### 2.2 本地混合开发模式

如果您希望在本地调试代码，可以分别启动服务。

#### 后端本地启动
```bash
cd backend
mvn spring-boot:run
```

#### 前端本地启动
```bash
cd frontend
npm install
npm run dev
```

## 3. 开发规范

### 3.1 后端规范
- **包命名**：`com.skylark.*`（如 `controller`、`service`、`entity`）
- **API 路径**：所有 API 必须以 `/api` 开头
- **日志**：使用 `@Slf4j` 注解，禁止使用 `System.out.println`
- **DTO**：推荐使用 DTO（数据传输对象）进行前后端数据交互，避免直接暴露 Entity

### 3.2 前端规范
- **组件命名**：使用 PascalCase（如 `AppLayout.vue`）
- **状态管理**：优先使用 Pinia Store 管理全局状态
- **样式**：优先使用 Tailwind CSS 工具类，复杂样式可写在 `<style scoped>` 中

## 4. 前端页面结构说明

为了方便定位与开发，前端页面功能模块可按如下拆分：

- **顶部导航**：项目名称、路由入口、当前页提示。
- **状态卡片区**：三张卡片分别展示前端/后端/数据库状态。
- **数据表格区**：显示 `ID / 名称 / 描述`，提供刷新按钮。
- **反馈区域**：加载、错误、空状态统一在组件内处理。

## 5. 常见命令

| 操作 | 命令 | 说明 |
|------|------|------|
| 重建镜像 | `docker compose build --no-cache` | 清除缓存并重新构建 |
| 停止服务 | `docker compose down` | 停止并移除容器 |
| 查看日志 | `docker compose logs -f backend` | 实时查看后端日志 |
| 进入数据库 | `docker exec -it skylark-db mysql -uroot -proot` | 进入 MySQL 容器 |
