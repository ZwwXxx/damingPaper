# 🚀 环境配置自动切换 - 快速参考

## 一句话总结
**再也不用手动改配置文件了！启动时自动切换环境配置。**

---

## 📁 配置文件说明

```
ruoyi-admin/src/main/resources/
├── application.yml           # 公共配置（不需要改）
├── application-dev.yml       # 开发环境（已配置好）✅
└── application-prod.yml      # 生产环境（已配置好）✅
```

| 文件 | 作用 | callback | profile | 需要修改 |
|------|------|----------|---------|---------|
| application-dev.yml | 开发环境 | http://10xh9vd648325.vicp.fun | D:/ruoyi/uploadPath | ❌ |
| application-prod.yml | 生产环境 | https://paperback.zww0891.fun | /home/ruoyi/uploadPath | ❌ |

---

## 🎯 快速启动

### Windows 本地开发
```bash
# 方式1：双击启动脚本
bin\run-dev.bat

# 方式2：命令行启动
java -jar ruoyi-admin.jar --spring.profiles.active=dev
```

### Linux 服务器部署
```bash
# 方式1：使用启动脚本（推荐）
chmod +x bin/run-prod.sh
./bin/run-prod.sh

# 方式2：命令行启动
java -jar ruoyi-admin.jar --spring.profiles.active=prod
```

### IDEA 中运行
```
Run/Debug Configurations 
→ Active profiles: dev
→ 点击运行
```

---

## ✅ 验证配置

### 查看启动日志
```log
# 开发环境
The following profiles are active: dev
微信回调地址: http://10xh9vd648325.vicp.fun

# 生产环境  
The following profiles are active: prod
微信回调地址: https://paperback.zww0891.fun
```

### 测试接口
```bash
# 开发环境
curl http://localhost:8080/test/ping

# 生产环境
curl https://paperback.zww0891.fun/test/ping
```

---

## 🔄 切换环境

| 从 | 到 | 操作 |
|----|----|------|
| 开发 | 生产 | 运行 `./bin/run-prod.sh` |
| 生产 | 开发 | 运行 `bin\run-dev.bat` |

**无需修改任何配置文件！**

---

## 📋 自动切换的配置项

| 配置项 | 开发环境 (dev) | 生产环境 (prod) |
|--------|---------------|----------------|
| **微信回调** | http://10xh9vd648325.vicp.fun | https://paperback.zww0891.fun |
| **文件路径** | D:/ruoyi/uploadPath | /home/ruoyi/uploadPath |
| **数据库密码** | damingPaper123456 | damingPaper123456 |
| **授权模式** | base | base |

---

## 🆘 常见问题

### Q: 修改配置后不生效？
**A**: 需要重新打包并重启
```bash
mvn clean package -DskipTests
./bin/run-prod.sh  # 或 bin\run-dev.bat
```

### Q: 如何确认当前环境？
**A**: 查看启动日志中的 `profiles are active`

### Q: 如何添加测试环境？
**A**: 创建 `application-test.yml`，启动时指定 `--spring.profiles.active=test`

---

## 📖 详细文档

- 📄 [环境配置切换指南.md](doc/环境配置切换指南.md) - 完整说明
- 📄 [application.yml配置模板.md](doc/application.yml配置模板.md) - 配置模板
- 📄 [微信授权模式配置说明.md](doc/微信授权模式配置说明.md) - 微信配置

---

## 🎉 优势

✅ **无需手动修改配置**  
✅ **启动时自动切换**  
✅ **降低人为错误**  
✅ **便于团队协作**  
✅ **配置清晰明了**

---

## 📞 快速联系

有问题查看详细文档：`doc/环境配置切换指南.md`
