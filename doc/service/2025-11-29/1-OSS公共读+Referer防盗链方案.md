# OSS公共读+Referer防盗链方案

## 实施日期
2025-11-29

## 现象描述

### 可以访问的情况 ✅
- **网站内嵌图片**：`<img src="https://daming-paper.oss-cn-guangzhou.aliyuncs.com/xxx.jpg">` → 正常显示
- **前端axios请求**：带有正确的Referer → 正常访问

### 不能访问的情况 ❌  
- **浏览器地址栏直接访问**：复制URL到浏览器地址栏 → 返回错误
  ```xml
  <Error>
    <Code>NoSuchKey</Code>
    <Message>The specified key does not exist.</Message>
  </Error>
  ```
- **Postman/curl等工具**：无Referer或Referer不在白名单 → 拒绝访问

## 原因分析

### OSS配置方案
```
Bucket权限：公共读（public-read）
    ↓
防盗链配置：Referer白名单
    ↓
工作原理：
  - 请求包含合法Referer → 允许访问
  - 请求无Referer或Referer非法 → 拒绝访问
```

### 访问流程对比

#### ✅ 网站访问（成功）
```
浏览器 → 访问网站（http://localhost:8080）
  ↓
网站加载图片：<img src="https://daming-paper.oss-cn-guangzhou.aliyuncs.com/xxx.jpg">
  ↓
HTTP请求头：
  Referer: http://localhost:8080/xxx
  ↓
OSS检查Referer → 在白名单内 → 返回图片 ✅
```

#### ❌ 直接访问（失败）
```
浏览器 → 地址栏输入：https://daming-paper.oss-cn-guangzhou.aliyuncs.com/xxx.jpg
  ↓
HTTP请求头：
  Referer: (空) 或 Referer: https://daming-paper.oss-cn-guangzhou.aliyuncs.com/
  ↓
OSS检查Referer → 不在白名单或为空 → 拒绝访问 ❌
```

## 当前配置详情

### 1. OSS Bucket设置

#### 读写权限
```
权限类型：公共读（public-read）
- 任何人都可以读取bucket中的文件
- 只有bucket拥有者可以写入
```

#### Referer防盗链白名单
阿里云OSS控制台 → Bucket列表 → daming-paper → 访问控制 → 防盗链

**推测的白名单配置：**
```
允许空Referer：否
Referer白名单：
  - http://localhost:8080
  - http://localhost:8081  
  - http://127.0.0.1:8080
  - http://your-domain.com
  - https://your-domain.com
```

### 2. 后端实现

#### 上传接口
**文件：** `CommonController.java`

```java
@PostMapping("/upload")
public AjaxResult uploadFile(MultipartFile file) {
    if (useOss()) {
        OssUploadResult result = aliOssClient.upload(file);
        // 返回完整的OSS URL（无需签名）
        return AjaxResult.success()
            .put("fileName", result.getObjectName())
            .put("url", result.getUrl());
    }
    // ...
}
```

**返回示例：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "fileName": "quiz/paper/answer/2025/11/29/abc123.jpg",
  "url": "https://daming-paper.oss-cn-guangzhou.aliyuncs.com/quiz/paper/answer/2025/11/29/abc123.jpg"
}
```

#### 无需签名URL
- ✅ 直接返回普通OSS URL
- ✅ 无需调用`generatePresignedUrl()`
- ✅ URL永久有效（依赖Referer验证安全性）

### 3. 前端实现

#### 图片显示
```vue
<template>
  <!-- 直接使用返回的URL -->
  <img :src="imageUrl" />
</template>

<script>
export default {
  data() {
    return {
      imageUrl: 'https://daming-paper.oss-cn-guangzhou.aliyuncs.com/quiz/paper/answer/2025/11/29/abc123.jpg'
    }
  }
}
</script>
```

#### 自动携带Referer
浏览器会自动在请求头中添加：
```
GET https://daming-paper.oss-cn-guangzhou.aliyuncs.com/xxx.jpg
Headers:
  Referer: http://localhost:8080/xxx  // 自动添加
```

## 方案优势

### ✅ 优点

1. **简单易用**
   - 无需后端生成签名URL
   - URL永久有效，无需刷新
   - 前端直接使用返回的URL

2. **防盗链保护**
   - 阻止其他网站直接引用图片
   - 防止恶意刷流量
   - 节省OSS费用

3. **性能优秀**
   - 无签名生成开销
   - CDN加速友好
   - 浏览器可缓存

4. **开发效率高**
   - 代码简单
   - 无需处理URL过期问题
   - 易于调试

### ⚠️ 注意事项

1. **Referer可伪造**
   - 技术人员可以伪造Referer绕过限制
   - 不适合高安全性要求的场景
   - 建议配合其他安全措施

2. **白名单维护**
   - 新增域名需要更新白名单
   - 开发/测试/生产环境需分别配置
   - 移动端APP可能需要特殊处理

3. **调试限制**
   - Postman等工具需手动设置Referer
   - 浏览器地址栏无法直接访问
   - 需要了解防盗链机制

## 配置步骤

### 阿里云OSS控制台配置

#### 步骤1：设置Bucket权限
```
OSS控制台 → Bucket列表 → daming-paper → 权限管理 → 读写权限
选择：公共读
```

#### 步骤2：配置防盗链
```
OSS控制台 → Bucket列表 → daming-paper → 访问控制 → 防盗链 → 设置

Referer类型：白名单
允许空Referer：❌ 不允许

Referer白名单：
  http://localhost:8080
  http://localhost:8081
  http://127.0.0.1:8080
  https://your-production-domain.com
  https://www.your-production-domain.com
```

#### 步骤3：可选 - 配置CORS
如果前端跨域访问图片：
```
OSS控制台 → Bucket列表 → daming-paper → 访问控制 → 跨域设置

来源：*（或具体域名）
允许Methods：GET, POST, PUT, DELETE, HEAD
允许Headers：*
暴露Headers：ETag, x-oss-request-id
```

## 使用场景

### 适用场景 ✅
- 论坛/社区图片
- 用户头像
- 文章配图
- 试卷答案图片
- 反馈截图

### 不适用场景 ❌
- 需要严格权限控制（用签名URL）
- 临时分享（用签名URL + 短期有效）
- 敏感数据（用私有权限 + 签名URL）
- 付费内容（用私有权限 + 权限验证）

## 测试验证

### 测试1：网站内访问
```bash
# 启动前端
npm run dev

# 访问包含图片的页面
http://localhost:8080/forum

# 预期结果：图片正常显示 ✅
```

### 测试2：直接URL访问
```bash
# 复制图片URL到浏览器地址栏
https://daming-paper.oss-cn-guangzhou.aliyuncs.com/quiz/paper/answer/2025/11/29/abc123.jpg

# 预期结果：拒绝访问或NoSuchKey错误 ❌
```

### 测试3：Postman测试
```
GET https://daming-paper.oss-cn-guangzhou.aliyuncs.com/quiz/paper/answer/2025/11/29/abc123.jpg

不设置Referer：
  预期结果：拒绝访问 ❌

设置Referer头：
  Headers:
    Referer: http://localhost:8080
  预期结果：返回图片 ✅
```

### 测试4：跨域访问
```javascript
// 在浏览器Console中测试
fetch('https://daming-paper.oss-cn-guangzhou.aliyuncs.com/quiz/paper/answer/2025/11/29/abc123.jpg')
  .then(res => res.blob())
  .then(blob => console.log('成功', blob))
  .catch(err => console.log('失败', err))

// 预期结果：
// - 同源：成功 ✅
// - 跨域且配置CORS：成功 ✅  
// - 跨域未配置CORS：失败 ❌
```

## 故障排查

### 问题1：网站内图片也无法显示
**原因：** Referer不在白名单或白名单配置错误

**解决：**
1. F12打开开发者工具 → Network
2. 找到图片请求，查看Request Headers中的Referer
3. 确保该Referer在OSS白名单中

### 问题2：部分浏览器无法显示
**原因：** 浏览器安全策略（如HTTPS → HTTP混合内容）

**解决：**
- 确保网站和OSS都使用HTTPS
- 检查浏览器控制台的Mixed Content警告

### 问题3：移动端APP无法加载
**原因：** APP请求可能不带Referer

**解决：**
```
方案A：白名单中允许空Referer（降低安全性）
方案B：APP端手动设置Referer头
方案C：使用签名URL方案
```

## 安全增强建议

### 1. 添加IP黑名单
OSS控制台 → 访问控制 → 防盗链 → IP黑名单

### 2. 设置访问频率限制
通过CDN配置访问频率限制，防止恶意刷量

### 3. 监控异常访问
定期查看OSS访问日志，发现异常及时处理

### 4. 结合CDN
```
用户 → CDN → OSS
     ↓
   缓存热点资源
   减少OSS访问
   降低成本
```

## 与旧方案对比

| 对比项 | 旧方案（签名URL） | 新方案（Referer防盗链） |
|-------|-----------------|----------------------|
| **安全性** | ⭐⭐⭐⭐⭐ 高（签名验证） | ⭐⭐⭐ 中（Referer可伪造） |
| **性能** | ⭐⭐⭐ 中（需生成签名） | ⭐⭐⭐⭐⭐ 高（无额外开销） |
| **易用性** | ⭐⭐⭐ 中（需处理过期） | ⭐⭐⭐⭐⭐ 高（永久有效） |
| **防盗链** | ⭐⭐⭐⭐⭐ 强（时效限制） | ⭐⭐⭐⭐ 较强（域名限制） |
| **适用场景** | 敏感数据、临时分享 | 公开资源、用户内容 |

## 迁移指南

### 从签名URL方案迁移

#### 1. 修改OSS配置
- 权限：私有 → 公共读
- 防盗链：无 → 配置白名单

#### 2. 简化后端代码
```java
// 旧代码（可删除）
String signedUrl = ossSignUrlHelper.convertToSignedUrl(objectName);

// 新代码（直接使用）
String url = buildFileUrl(objectName);
```

#### 3. 前端无需修改
直接使用返回的URL即可

## 总结

当前使用的**OSS公共读+Referer防盗链方案**是一个：
- ✅ 简单高效
- ✅ 性能优秀
- ✅ 成本较低
- ⚠️ 安全性中等

的图片存储方案，适合大多数用户生成内容（UGC）场景。

**核心机制：**
```
网站访问 → 带Referer → 在白名单 → 允许 ✅
直接访问 → 无Referer → 不在白名单 → 拒绝 ❌
```

**建议：**
- 敏感数据：继续使用签名URL方案
- 公开内容：使用当前Referer方案
- 定期监控：OSS访问日志，及时发现异常

---

**实施人员：** Cascade AI  
**生效日期：** 2025-11-29
