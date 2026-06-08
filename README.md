# Sky-Lark

Sky-Lark 是一个基于 **Java Spring Boot 3** 和 **Vue 3** 的现代化全栈应用框架，面向追求工程质量与交付效率的团队打造。项目遵循严格的容器化标准，开箱即用，界面简洁美观。

![许可证](https://img.shields.io/badge/license-MIT-blue.svg)
![Docker](https://img.shields.io/badge/docker-compose-green.svg)
![Spring Boot](https://img.shields.io/badge/SpringBoot-3.2-brightgreen.svg)
![Vue](https://img.shields.io/badge/Vue-3.3-4FC08D.svg)

## 📚 文档索引

请阅读 `docs/` 目录下的详细文档以深入了解本项目：

- 🏗️ **[系统架构](docs/architecture.md)**：技术栈、架构图与数据流向
- 🎨 **[详细设计](docs/design.md)**：数据库设计、API 接口定义与前端组件
- 💻 **[开发指南](docs/development.md)**：开发环境、编码规范与常用命令
- 🧪 **[测试指南](docs/testing.md)**：测试策略、验证清单与自动化测试
- 📖 **[用户手册](docs/user_manual.md)**：功能介绍与使用说明

## 🛠 技术栈

| 领域 | 核心技术 | 说明 |
|------|----------|------|
| **前端** | Vue 3、Vite、Pinia、Tailwind CSS | 极速响应的现代化 SPA |
| **后端** | Spring Boot 3、JPA、Java 17 | 稳健的企业级后端 |
| **数据库** | MySQL 8.0 | 可靠的关系型数据存储 |
| **DevOps** | Docker、Docker Compose | 100% 容器化部署 |

## ✨ 界面功能概览

- **顶部导航**：品牌标题、当前模块提示、导航链接（首页/关于）。
- **状态总览**：三张状态卡片展示前端、后端、数据库的运行状态与更新时间。
- **数据列表**：演示数据表格包含 `ID / 名称 / 描述`，支持刷新按钮即时拉取最新记录。
- **交互反馈**：加载时展示加载状态，接口失败显示提示信息。
- **响应式布局**：桌面端三列卡片，移动端自动堆叠为单列，避免横向滚动。

## 🚀 快速启动

### 前置要求
- Docker Desktop 已安装并运行

### 一键启动
1. 在项目根目录执行：
   ```bash
   docker compose up --build
   ```
2. 等待容器启动（首次启动 MySQL 需约 30 秒初始化）。
3. 访问服务：
   - **前端**：http://localhost:3000
   - **后端 API**：http://localhost:8080/api/health
   - **数据库**：localhost:3306（用户名：root / 密码：root）


## 🚀 外部运行方式（非 Docker）

### 1. 环境准备

请确保本地已配置以下环境（Windows 示例路径以参考为主）：

* **JDK 17**: 需配置 `JAVA_HOME` 环境变量。
* **Apache Maven 3.9.11**: 例如 `D:\Apache-maven-3.9.11`，需配置 `MAVEN_HOME`。
* **Apache Tomcat 11.0.14**: 例如 `D:\Apache-tomcat-11.0.14` (仅 War 包部署需要)。
* **Node.js 18+ & npm**: 前端构建需要。
* **MySQL 8.0**: 本地数据库服务。

### 2. 数据库配置

由于脱离 Docker 环境，需手动配置本地数据库：

1. 启动本地 MySQL 服务 (默认端口 3306)。
2. 创建数据库 `skylark_db` (与 `application.yml` 保持一致)。
3. 修改后端配置文件 `backend/src/main/resources/application.yml`，将数据库连接地址中的 host 由 `db` 改为 `localhost`：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/skylark_db?useSSL=false&...
       username: root
       password: root
   ```
4. **初始化数据 (可选)**：
   项目启动时会自动执行 SQL 脚本，若需手动导入，请执行：
   - 表结构：`backend/src/main/resources/schema.sql`
   - 初始数据：`backend/src/main/resources/data.sql`

### 3. 后端运行

#### 方式 A：Jar 包直接运行 (推荐开发调试)
1. 进入后端目录并打包：
   ```bash
   cd backend
   mvn clean package -DskipTests
   ```
2. 运行生成的 Jar 包：
   ```bash
   java -jar target/backend-0.0.1-SNAPSHOT.jar
   ```

#### 方式 B：War 包部署 (传统 Tomcat)
1. 使用 Maven `war` Profile 打包：
   ```bash
   cd backend
   mvn -Pwar clean package -DskipTests
   ```
2. 将生成的 `target/backend-0.0.1-SNAPSHOT.war` 复制到 Tomcat 的 `webapps` 目录：
   ```text
   D:\Apache-tomcat-11.0.14\webapps\
   ```
3. 启动 Tomcat：
   ```bash
   # Windows 命令示例
   D:\Apache-tomcat-11.0.14\bin\startup.bat
   ```

### 4. 前端运行

1. 进入前端目录并安装依赖：
   ```bash
   cd frontend
   npm install
   ```
2. 启动开发服务器：
   ```bash
   npm run dev
   ```
3. 访问服务：
   - **前端**：http://localhost:3000
   - **后端 API**：http://localhost:8080/api/health
   - **数据库**：localhost:3306 (skylark_db)


## 📦 项目结构
```bash
sky-lark/
├── docker-compose.yml   # 核心编排文件
├── docs/                # 项目文档
├── frontend/            # Vue 前端源码
└── backend/             # Spring Boot 后端源码
```

## 🐛 常见问题
- **数据库连接失败？** 请等待 MySQL 容器完全启动。
- **端口冲突？** 请确保本地 3000、8080、3306 端口未被占用。

---
由 Sky-Lark 团队打造
