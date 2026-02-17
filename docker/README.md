# Docker 部署说明（两仓库分开时使用）

**本项目仅保留此一处 Docker 配置**，位于 **daming-admin 仓库**内（`daming-admin/docker/`）。部署时需与 **daming-front** 仓库在同一层目录使用。

## 在 Linux 上的操作

```bash
# 1. 建一个根目录
mkdir -p paperProject && cd paperProject

# 2. 克隆两个仓库（目录名固定）
git clone <daming-admin 仓库地址> daming-admin
git clone <daming-front 仓库地址> daming-front

# 3. 把 docker 移到与两个仓库同级
mv daming-admin/docker .

# 4. 一键启动
cd docker && docker-compose up -d
```

最终结构：`paperProject/daming-admin`、`paperProject/daming-front`、`paperProject/docker`。

访问：管理后台 http://localhost:8081，学生端 http://localhost:8082，后端 http://localhost:8080。

---

## 镜像与构建说明

| 服务         | 来源           | 说明 |
|--------------|----------------|------|
| mysql、redis | 从镜像仓库拉取 | 使用官方镜像 mysql:8.0、redis:7-alpine，需能访问镜像站（国内建议配镜像加速）。 |
| backend      | 本地构建       | 用 `daming-admin/Dockerfile` 在本地打 JAR 并构建镜像，**不会**从仓库拉取。 |
| ruoyi-ui      | 本地构建       | 用 `docker/ruoyi-ui/Dockerfile` 在本地用源码构建，**不会**从仓库拉取。 |
| daming-front  | 本地构建       | 用 `docker/daming-front/Dockerfile` 在本地用源码构建，**不会**从仓库拉取。 |

`docker-compose.yml` 里已为 backend、ruoyi-ui、daming-front 设置 **`pull_policy: never`**，Compose 只会用本地已有镜像或按 Dockerfile 本地构建，不会去拉 `paper-backend:latest` 等名字的镜像。若你看到这三个服务一直在「Pulling」，请确认使用的是带 `pull_policy: never` 的 compose 配置。

---

## 拉取镜像失败（connection refused / timeout）

报错里出现 `registry-1.docker.io`、`connection refused`、`Client.Timeout exceeded` 时，多半是**访问 Docker Hub 被墙或很慢**（国内服务器常见）。需要给 Docker 配置**镜像加速**。

**在 Linux 服务器上执行：**

1. 编辑或新建 Docker 配置：
   ```bash
   mkdir -p /etc/docker
   nano /etc/docker/daemon.json
   ```

2. 写入以下内容（任选一个或几个镜像源）：
   ```json
   {
     "registry-mirrors": [
       "https://docker.1ms.run",
       "https://docker.xuanyuan.me"
     ],
     "insecure-registries": []
   }
   ```
   其他可选：阿里云（需登录控制台获取专属地址）、腾讯云、中科大等，可自行搜索「Docker 镜像加速 国内」。

3. 保存后重启 Docker 并再试：
   ```bash
   systemctl daemon-reload
   systemctl restart docker
   cd /root/paperProject/docker && docker-compose up -d
   ```

4. 若仍拉不到，可先在本机或能访问外网的机器**构建并导出镜像**，再传到服务器 `docker load` 使用（适合完全无法连 Docker Hub 的环境）。

**说明**：上述镜像加速只影响 **mysql、redis** 等从仓库拉取的镜像。**backend、ruoyi-ui、daming-front** 由本地 Dockerfile 构建，不依赖 Docker Hub，只要 Maven/Node 能下依赖即可。

---

## 构建报错：compose build requires buildx 0.17.0 or later

出现该提示说明当前环境的 Docker Buildx 版本过旧，Compose 构建时自动退出。可任选一种方式解决。

**方式一：不用 Buildx，改用 Compose 自带构建（优先试）**

执行前加环境变量，再启动：

```bash
export COMPOSE_DOCKER_CLI_BUILD=0
export DOCKER_BUILDKIT=0
cd /root/paperProject/docker && docker-compose up -d --build
```

若希望长期生效，可写入当前用户环境（如 `~/.bashrc`）：

```bash
echo 'export COMPOSE_DOCKER_CLI_BUILD=0' >> ~/.bashrc
echo 'export DOCKER_BUILDKIT=0' >> ~/.bashrc
source ~/.bashrc
```

**方式二：升级 Buildx 到 0.17+**

```bash
# 建插件目录
mkdir -p ~/.docker/cli-plugins

# 下载 buildx 0.17+（示例 0.19.3，可按需改版本）
curl -L "https://github.com/docker/buildx/releases/download/v0.19.3/buildx-v0.19.3.linux-amd64" -o ~/.docker/cli-plugins/docker-buildx
chmod +x ~/.docker/cli-plugins/docker-buildx

# 验证
docker buildx version
```

再执行 `docker-compose up -d --build`。

---

## 其他说明

- **compose 版本**：当前 `docker-compose.yml` 已去掉 `version` 字段，避免新版 Compose 的 obsolete 警告。
- **首次启动**：会先拉取 mysql、redis 镜像（若未配加速可能较慢），同时本地构建 backend、ruoyi-ui、daming-front，三者不会出现「Pulling」阶段。

---

## 域名与宝塔反向代理（不指 dist 文件夹）

前端现在跑在 Docker 容器里，**主机上没有 dist 目录可指**，不能用「网站 → 根目录选 dist」的方式。改用 **Nginx 反向代理**：域名解析到服务器 IP，宝塔里把该域名的请求**转发到容器端口**即可。

### 1. 端口对应关系

| 服务       | 容器端口 | 说明           |
|------------|----------|----------------|
| 管理后台   | 8081     | ruoyi-ui       |
| 学生端     | 8082     | daming-front   |
| 后端 API   | 8080     | Spring Boot    |

### 2. 在宝塔里怎么做

- **添加网站**：域名照常填（如 `admin.xxx.com`、`www.xxx.com`），**根目录随便选一个即可**（例如 `/www/wwwroot/placeholder`），因为不会用这个目录，只用下面的代理。
- **设置反向代理**：  
  宝塔 → 网站 → 对应站点 → **设置** → **反向代理** → **添加反向代理**：
  - **代理名称**：随意（如「学生端」）
  - **目标 URL**：`http://127.0.0.1:8082`（学生端）或 `http://127.0.0.1:8081`（管理后台）
  - **发送域名**：`$host` 或留空  
  保存后，访问该域名即等于访问容器里的 Nginx（80 已由宝塔占用的前提下，容器只暴露 8081/8082，由宝塔 80 转过去）。

### 3. 若一个域名要同时做「学生端 + 后端 API」

例如希望 `https://paper.xxx.com` 打开学生端，且前端请求 `/prod-api` 也走同一域名：  
在宝塔该站点的 **Nginx 配置**里用类似下面（或宝塔「反向代理」里配置「高级」自定义）：

```nginx
location / {
    proxy_pass http://127.0.0.1:8082;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
}

location /prod-api/ {
    proxy_pass http://127.0.0.1:8080/;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
}

location /ws/ {
    proxy_pass http://127.0.0.1:8080/ws/;
    proxy_http_version 1.1;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection "upgrade";
    proxy_set_header Host $host;
}
```

这样：**不指任何文件夹**，只通过 80 端口和上述 `proxy_pass` 把请求转到 Docker 的 8082（前端）和 8080（后端）。

### 4. HTTPS

在宝塔里为该站点申请 SSL（Let’s Encrypt 等），证书配在宝塔 Nginx 上即可，代理到 127.0.0.1 仍是 HTTP，对外是 HTTPS。
