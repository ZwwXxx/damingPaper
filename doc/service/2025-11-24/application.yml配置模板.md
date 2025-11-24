# application.yml 配置模板

## 说明
将原 application.yml 中的环境相关配置移除，只保留公共配置。

---

## 需要移除的配置（已移到 profile 文件）

### ❌ 移除这些配置：
```yaml
# ❌ 移除：微信回调地址（已移到 application-dev.yml 和 application-prod.yml）
wx:
  mp:
    callback: https://paperback.zww0891.fun  # 删除这行

# ❌ 移除：文件上传路径（已移到 profile 文件）
ruoyi:
  profile: /home/ruoyi/uploadPath  # 删除这行

# ❌ 移除：数据库密码（已移到 profile 文件）
spring:
  datasource:
    druid:
      master:
        username: root      # 删除这行
        password: damingPaper123456  # 删除这行
```

---

## ✅ 保留的公共配置

### 1. 服务器配置
```yaml
server:
  port: 8080
  servlet:
    context-path: /
  tomcat:
    uri-encoding: UTF-8
    threads:
      max: 800
      min-spare: 30
```

### 2. 微信公众号配置（公共部分）
```yaml
wx:
  mp:
    enabled: true
    # callback: 已移到 application-dev.yml 和 application-prod.yml
    # authScope: 已移到 application-dev.yml 和 application-prod.yml
    
    configs:
      - appId: wxeac644b6acef0405
        secret: 22681f0abf2490d0853b5675905b557b
        token: b3337731ab0711ef8c1fe79208535f88
        aesKey: Hha4Su7QiNUEdj1jow5oQZtcIXVshfUXSClXRCpD1am
```

### 3. 数据库配置（公共部分）
```yaml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/ry-vue?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
    druid:
      master:
        # username: 已移到 application-dev.yml 和 application-prod.yml
        # password: 已移到 application-dev.yml 和 application-prod.yml
        ...其他 druid 配置...
```

### 4. Redis 配置
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: 
    database: 0
```

---

## 完整的目录结构

```
ruoyi-admin/src/main/resources/
├── application.yml           # 主配置（只保留公共配置）
├── application-dev.yml       # 开发环境配置（覆盖主配置）
├── application-prod.yml      # 生产环境配置（覆盖主配置）
└── application-druid.yml     # Druid 数据源配置
```

---

## 配置优先级

```
application-prod.yml  （最高优先级）
    ↓ 覆盖
application-dev.yml
    ↓ 覆盖
application.yml       （最低优先级）
```

**说明**：
- 启动时指定 `--spring.profiles.active=prod`
- Spring Boot 会先加载 `application.yml`
- 然后加载 `application-prod.yml`，覆盖相同的配置项
- 最终生效的是 `application-prod.yml` 中的配置

---

## 修改步骤

### 步骤1：备份原配置
```bash
cp ruoyi-admin/src/main/resources/application.yml \
   ruoyi-admin/src/main/resources/application.yml.backup
```

### 步骤2：编辑 application.yml
移除以下内容：
- `wx.mp.callback`
- `wx.mp.authScope`
- `ruoyi.profile`
- `spring.datasource.druid.master.username`
- `spring.datasource.druid.master.password`

### 步骤3：确认 profile 文件已创建
- ✅ `application-dev.yml`
- ✅ `application-prod.yml`

### 步骤4：测试
```bash
# 开发环境
java -jar ruoyi-admin.jar --spring.profiles.active=dev

# 生产环境
java -jar ruoyi-admin.jar --spring.profiles.active=prod
```

---

## 验证配置

### 开发环境启动后，应该看到：
```log
The following profiles are active: dev
微信回调地址: http://10xh9vd648325.vicp.fun
文件上传路径: D:/ruoyi/uploadPath
```

### 生产环境启动后，应该看到：
```log
The following profiles are active: prod
微信回调地址: https://paperback.zww0891.fun
文件上传路径: /home/ruoyi/uploadPath
```

---

## 总结

**修改前**：
- ✅ application.yml（包含所有配置，需要频繁修改）

**修改后**：
- ✅ application.yml（只包含公共配置，不需要修改）
- ✅ application-dev.yml（开发环境专用，不需要修改）
- ✅ application-prod.yml（生产环境专用，不需要修改）

**结果**：**再也不用改配置文件了！** 🎉
