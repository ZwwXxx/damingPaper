# daming-ai 文本聊天服务说明（Spring AI + 百炼兼容接口）

## 目的

`/ai/chat` 相关会话与发消息能力中，大模型调用已改为 **Spring AI** 的 `OpenAiApi` + `OpenAiChatModel`，不再在业务代码里使用 `HttpURLConnection` 手写 HTTP。装配思路与 `demo/backend/heima-ai` 中 `CommonConfiguration` 的 `OpenAiApi` / `ChatModel` 方式一致；本项目使用标准 `OpenAiChatModel`（未拷贝 heima 中的 `AlibabaOpenAiChatModel`），对接阿里云百炼 **DashScope OpenAI 兼容模式**。

## 模块与类

| 职责 | 类 |
|------|-----|
| 配置 `baseUrl`、`/chat/completions` 路径、超时、`OpenAiChatModel` Bean | `com.daming.damingai.config.QwenOpenAiChatConfiguration`（读 `spring.ai.openai.*` + `ai.qwen` 超时等） |
| 会话落库、RAG 拼接、调用 `qwenChatModel.call(Prompt)` | `com.daming.damingai.service.impl.AiChatServiceImpl` |
| 录题多模态、超时、聊天备用模型名等业务项 | `com.daming.damingai.config.QwenProperties`（`ai.qwen.*`） |

## 配置项（`application.yml`）

**与 heima-ai 一致，连接优先使用 Spring AI 标准前缀：**

- `spring.ai.openai.base-url`：一般为 `https://dashscope.aliyuncs.com/compatible-mode/v1`（千问同样走百炼 OpenAI 兼容端点）。
- `spring.ai.openai.api-key`：与 `DASHSCOPE_API_KEY` 一致（`${DASHSCOPE_API_KEY:}`）。
- `spring.ai.openai.chat.completions-path`：`/chat/completions`（勿使用默认的 `/v1/chat/completions` 拼在已含 `/v1` 的 base 上）。
- `spring.ai.openai.chat.options.model`：默认聊天模型（可与 `ai.qwen.chat-model` 同环境变量对齐）。

**`ai.qwen` 中与连接重复的两项已引用上述变量**（`base-url: ${spring.ai.openai.base-url}`、`api-key: ${spring.ai.openai.api-key}`），供 `QwenOmniClient` 等仍绑定 `QwenProperties` 的代码使用，避免两处手填不一致。

- `ai.qwen.chat-model` / `chat-fallback-model`：`AiChatServiceImpl` 主备模型与 RAG 等逻辑仍读取此处。
- `ai.qwen.timeout-seconds`：通过 `SimpleClientHttpRequestFactory` 作用于 `RestClient` 连接/读超时。

## 依赖

`daming-ai/pom.xml` 中引入 `spring-ai-bom` 与 `spring-ai-openai-spring-boot-starter`，版本属性 `spring-ai.version` 当前与 heima 示例对齐为 **1.0.0-M6**。若与本机 Spring Boot 3.5 解析不兼容，可在 IDEA 中改为 **1.0.4** 等稳定版并重新导入 Maven。

## 与录题（多模态）区分

图片录题等仍走 `QwenOmniClient` / 实时多模态通道；**聊天**仅使用兼容 `/chat/completions` 的文本模型，避免将 `omni-realtime` 用于标准聊天接口导致 500。

## 与 Spring AI 自动装配

启动类已 **`exclude = OpenAiAutoConfiguration.class`**：避免自动创建 `OpenAiEmbeddingModel` 等（向量未接入时不必配 embedding）。连接项仍通过 **`spring.ai.openai.api-key` / `base-url`** 绑定到 `OpenAiConnectionProperties`，由 `QwenOpenAiChatConfiguration` 手动创建 `OpenAiApi` / `OpenAiChatModel`。日后若接入向量，可去掉该排除并启用 embedding，或保留排除、单独声明向量用 Bean。

## 排错

1. **启动报 `OpenAiEmbeddingModel` / API key**：确认已排除 `OpenAiAutoConfiguration`；若改为使用 Spring AI 自动装配，需设置 `spring.ai.openai.api-key` 或关闭 embedding。
2. **401/403**：检查 `DASHSCOPE_API_KEY` 与百炼控制台权限。
3. **路径 404**：确认 `base-url` 与 `completionsPath`（代码中为 `/chat/completions`）与控制台「OpenAI 兼容」文档一致。









































