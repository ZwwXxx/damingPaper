# 启动失败：Druid 配置未加载解决方案

## 错误现象

```log
Could not resolve placeholder 'spring.datasource.druid.initialSize' 
in value "${spring.datasource.druid.initialSize}"
```

**完整错误堆栈**：
```
Error creating bean with name 'druidProperties': 
Injection of autowired dependencies failed; 
nested exception is java.lang.IllegalArgumentException: 
Could not resolve placeholder 'spring.datasource.druid.initialSize'
```

---

## 错误原因

当你在 IDEA 中指定 `Active profiles: dev` 或命令行使用 `--spring.profiles.active=dev` 时：

```yaml
# application.yml 中的配置
spring:
  profiles:
    active: druid  # ❌ 被命令行参数覆盖了
```

**结果**：
- ✅ `application.yml` 被加载
- ✅ `application-dev.yml` 被加载
- ❌ `application-druid.yml` **没有**被加载（被覆盖）

**导致**：Druid 数据源配置缺失，启动失败。

---

## 配置加载机制

### 错误的配置（会覆盖）

```yaml
# application.yml
spring:
  profiles:
    active: druid  # ❌ 会被命令行参数覆盖
```

```bash
# 启动时指定
--spring.profiles.active=dev

# 实际加载：
application.yml + application-dev.yml
# ❌ application-druid.yml 没有被加载
```

### 正确的配置（会合并）

**方式1：使用 include（推荐）**
```yaml
# application.yml
spring:
  profiles:
    include: druid  # ✅ 总是包含，不会被覆盖
```

```bash
# 启动时指定
--spring.profiles.active=dev

# 实际加载：
application.yml + application-druid.yml + application-dev.yml
# ✅ 三个都加载了
```

**方式2：同时指定多个 profile**
```bash
# 启动时指定多个（逗号分隔）
--spring.profiles.active=druid,dev

# 实际加载：
application.yml + application-druid.yml + application-dev.yml
# ✅ 三个都加载了
```

---

## 解决方案（3种方法）

### 方法1：修改 application.yml（推荐）⭐⭐⭐

**修改主配置文件**：
```yaml
# application.yml
spring:
  profiles:
    # active: druid  # ❌ 删除这行
    include: druid   # ✅ 改为 include
```

**优点**：
- ✅ 一次修改，永久生效
- ✅ IDEA 和命令行启动都支持
- ✅ 不需要修改启动命令

**操作步骤**：
1. 打开 `ruoyi-admin/src/main/resources/application.yml`
2. 找到 `spring.profiles.active: druid`
3. 改为 `spring.profiles.include: druid`
4. 保存并重启

---

### 方法2：IDEA 配置多个 Profile（推荐）⭐⭐

**在 IDEA 中配置**：
```
Run/Debug Configurations
→ Active profiles: druid,dev  ← 逗号分隔多个 profile
→ 点击 OK
→ 重新启动
```

**优点**：
- ✅ 不需要修改配置文件
- ✅ 灵活，可以随时调整

**缺点**：
- ⚠️ 只适用于 IDEA
- ⚠️ 命令行启动需要单独配置

---

### 方法3：修改启动脚本（推荐）⭐⭐

**开发环境（run-dev.bat）**：
```bash
# 修改前
--spring.profiles.active=dev

# 修改后
--spring.profiles.active=druid,dev
```

**生产环境（run-prod.sh）**：
```bash
# 修改前
SPRING_OPTS="--spring.profiles.active=prod"

# 修改后
SPRING_OPTS="--spring.profiles.active=druid,prod"
```

**优点**：
- ✅ 启动脚本统一管理
- ✅ 不容易出错

**缺点**：
- ⚠️ IDEA 中还需要单独配置

---

## 推荐方案对比

| 方案 | 适用场景 | 优先级 | 操作步骤 |
|------|---------|--------|---------|
| **方法1：修改 yml** | 所有场景 | ⭐⭐⭐ | 修改一次，永久生效 |
| 方法2：IDEA 配置 | IDEA 开发 | ⭐⭐ | 每次新建配置时设置 |
| 方法3：修改脚本 | 命令行启动 | ⭐⭐ | 修改启动脚本 |

**最佳实践**：
1. ✅ **优先使用方法1**（修改 yml），一劳永逸
2. ✅ 如果不想改 yml，使用方法2+方法3组合

---

## 快速解决（立即可用）

### 立即解决（30秒）

**在 IDEA 中**：
```
1. 打开 Run/Debug Configurations
2. Active profiles 改为: druid,dev
3. 点击运行
```

**验证**：
```log
# 启动日志应该显示
The following profiles are active: druid,dev
```

---

### 永久解决（1分钟）

**修改 application.yml**：
```yaml
# 找到这行
spring:
  profiles:
    active: druid

# 改为
spring:
  profiles:
    include: druid
```

**然后**：
- IDEA 中 Active profiles 只需填：`dev`
- 命令行启动：`--spring.profiles.active=dev`

**验证**：
```log
# 启动日志应该显示
The following profiles are active: druid,dev
```

---

## 配置加载顺序图

### 错误的加载（会报错）

```
启动命令: --spring.profiles.active=dev
    ↓
application.yml
    ├─ spring.profiles.active: druid  ← ❌ 被覆盖
    └─ 其他配置 ✅
    ↓
application-dev.yml  ✅
    ↓
❌ application-druid.yml 未加载
    ↓
💥 启动失败：找不到 druid 配置项
```

### 正确的加载（方法1）

```
启动命令: --spring.profiles.active=dev
    ↓
application.yml
    ├─ spring.profiles.include: druid  ← ✅ 不会被覆盖
    └─ 其他配置 ✅
    ↓
application-druid.yml  ✅ (被 include 引入)
    ↓
application-dev.yml  ✅
    ↓
✅ 启动成功
```

### 正确的加载（方法2）

```
启动命令: --spring.profiles.active=druid,dev
    ↓
application.yml  ✅
    ↓
application-druid.yml  ✅ (命令行指定)
    ↓
application-dev.yml  ✅ (命令行指定)
    ↓
✅ 启动成功
```

---

## 验证配置是否生效

### 查看启动日志

**正确的日志**：
```log
# 应该同时显示 druid 和 dev
The following profiles are active: druid,dev

# 或者（如果用 include）
The following profiles are active: dev
The following 1 profile is active: "druid" (default)
```

**错误的日志**：
```log
# 只显示 dev，缺少 druid
The following profiles are active: dev
```

### 访问 Druid 监控

启动成功后，访问：
```
http://localhost:8080/druid/
用户名: ruoyi
密码: 123456
```

如果能访问，说明 `application-druid.yml` 已成功加载。

---

## 相关配置文件

### application.yml（主配置）
```yaml
spring:
  profiles:
    include: druid  # ✅ 使用 include
    # active: druid  # ❌ 不要用 active
```

### application-druid.yml（数据源配置）
```yaml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    druid:
      master:
        url: jdbc:mysql://localhost:3306/...
        initialSize: 5
        minIdle: 10
        # ... 其他 druid 配置
```

### application-dev.yml（开发环境）
```yaml
spring:
  datasource:
    druid:
      master:
        username: root
        password: damingPaper123456
```

---

## 常见问题

### Q1: 为什么 active 会被覆盖，include 不会？
**A**: 
- `active`: 指定激活哪个 profile，命令行参数会**替换**它
- `include`: 指定包含哪个 profile，命令行参数会**合并**它

### Q2: 可以同时使用 active 和 include 吗？
**A**: 可以，但不推荐。推荐只用 `include`。

```yaml
spring:
  profiles:
    include: druid  # 总是包含
    # active: dev   # 默认激活（可以不写）
```

### Q3: 启动脚本已修改，但 IDEA 启动还是报错？
**A**: IDEA 的配置是独立的，需要单独修改：
```
Run/Debug Configurations → Active profiles: druid,dev
```

### Q4: 生产环境也需要改吗？
**A**: 需要。生产环境启动时也要指定：
```bash
--spring.profiles.active=druid,prod
```

---

## 总结

**问题**：`application-druid.yml` 没有被加载

**原因**：`spring.profiles.active` 被命令行参数覆盖

**解决**：
1. ⭐⭐⭐ 修改 yml：`active` → `include`（推荐）
2. ⭐⭐ 同时指定多个 profile：`druid,dev`
3. ⭐⭐ 修改启动脚本

**验证**：
```log
The following profiles are active: druid,dev
```

**以后启动**：
- IDEA: Active profiles: `druid,dev`
- 命令行: `--spring.profiles.active=druid,dev`
- 或者修改 yml 使用 `include`，只需指定 `dev` 或 `prod`
