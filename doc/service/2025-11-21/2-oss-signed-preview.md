## OSS 私有上传与临时签名访问方案

- **背景**：富文本和批改场景的图片统一落 OSS（`daming-paper`），桶为私有，防止外链被刷。
- **核心能力**：
  1. 上传阶段仍走 `/common/upload`，后端使用固定 AK/SK 写入 OSS，保存对象 Key（如 `quiz/paper/answer/.../xxx.jpg`）。
  2. 访问阶段禁止直接暴露 OSS 域名，前端主动调用 `/common/oss/sign` 获取临时 URL（默认 5 分钟有效，服务器可自控过期时间）。
  3. 现有管理员批改页、C 端回顾页都会在渲染 HTML 后扫描 `<img>`，将 Key 转成签名地址并缓存，点击图片走 Element Viewer。
- **配置要点**（`application.yml`）：
  - `aliyun.oss.enabled=true`
  - `endpoint=https://oss-cn-guangzhou.aliyuncs.com`
  - `bucketName=daming-paper`
  - `dir=quiz/paper/answer`
  - `accessKeyId/accessKeySecret` 使用 RAM 子账号

```mermaid
flowchart TD
    A[前端上传图片<br/>/common/upload] --> B[后端 AliOssClient<br/>PutObject 到 OSS 私有桶]
    B --> C[返回对象 Key<br/>存到题干/答案 HTML]
    C --> D[页面渲染前扫描 img]
    D --> E[调用 /common/oss/sign?objectName=Key]
    E --> F[AliOssClient 生成临时 URL<br/>默认有效 300s]
    F --> G[前端替换 img.src=签名链接]
    G --> H[用户可预览/放大，URL 过期需重新签名]
```

```mermaid
sequenceDiagram
    participant P as 批改页/学生端
    participant API as /common/oss/sign
    participant OSS as 阿里云 OSS
    P->>API: objectName=quiz/paper/answer/...jpg
    API->>OSS: GeneratePresignedUrl(bucket,key,expire)
    OSS-->>API: 临时 URL + 过期时间
    API-->>P: {url, expireSeconds}
    P->>P: 缓存 URL，替换 img.src
    Note over P: 过期后重新发起签名请求
```

### 细节 & 注意事项
- **缓存策略**：前端按 Key 本地缓存 `url + expireAt`，提前 5 秒失效，避免频繁请求。
- **历史数据兼容**：若 `<img>` 仍是 `upload/xxx` 等本地路径，会回落到原 `VUE_APP_BASE_API` 直链，不会签名。
- **安全性**：桶保持私有，仅后端掌握 AK/SK；签名 URL 有效期短，可根据业务调节 `expireSeconds` 或做限流日志。
- **后续扩展**：若要下载原始文件或生成 ZIP，可复用 `AliOssClient.generatePresignedUrl`（支持自定义 HTTP Method）或在后端流式转发。

