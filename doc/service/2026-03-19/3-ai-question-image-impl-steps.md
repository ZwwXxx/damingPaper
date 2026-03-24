## 大明刷题 - 题目截图解析 AI 微服务实现步骤（IDEA 实战指引）

### 1. 前置准备

- **JDK 版本**：建议使用 JDK 17 或 21（Spring Boot 3.x 官方推荐至少 17）。
- **构建工具**：Maven（与当前大明刷题后端保持一致）。
- **开发工具**：IntelliJ IDEA（Ultimate 版更方便 Spring 开发）。
- **规划**：本 AI 微服务单独一个 Spring Boot 工程，与现有 `daming-admin` 分开，以 HTTP 接口方式对接。

### 2. 使用 Spring Initializr 创建新项目

1. 打开 **IntelliJ IDEA**，点击：`File -> New -> Project...`
2. 左侧选择 **Spring Initializr**：
   - Service URL：默认即可（start.spring.io）
   - Project：Maven
   - Language：Java
   - Spring Boot：选择 **3.x 最新稳定版本**（例如 3.2.x/3.3.x）。
3. 填写项目信息：
   - Group：`com.dm.quiz`（或你习惯的）
   - Artifact：`dm-ai-question-service`
   - Name：`dm-ai-question-service`
   - Package name：`com.dm.quiz.ai`
4. 选择依赖（Dependencies）：
   - **Spring Web**（必须，用于提供 REST 接口）
   - **Lombok**（建议，简化实体/DTO 编写）
   - **Validation**（Jakarta Bean Validation，用于参数校验，可选）
   - （后续接入大模型时再按需添加 HTTP 客户端依赖，如 OkHttp/WebClient）
5. 点击 **Finish**，IDEA 会自动生成基础的 Spring Boot 3 项目结构。

### 3. 项目基础结构规划

在新项目中，按下面结构规划包（可在 IDEA 中右键包名新建）：

- `com.dm.quiz.ai`
  - `controller`：对外 REST 接口（例如 `QuestionAiController`）
  - `service`：业务逻辑层（例如 `QuestionAiService`）
  - `client`：调用大模型平台的 HTTP 客户端（例如 `LlmClient`）
  - `dto`：请求/响应 DTO（`AiQuestionPayload`、`AiQuestionDecisionResult` 等）
  - `config`：配置类（如大模型客户端配置）

入口类 `DmAiQuestionServiceApplication` 保持在根包 `com.dm.quiz.ai` 下，确保组件扫描能覆盖到子包。

### 4. 定义接口 DTO（与技术方案文档对齐）

在 `dto` 包下，新建 Java 类：

- `AiQuestionPayload`：主项目调用 AI 微服务时的请求体。
- `AiQuestionDecisionResult`：AI 微服务返回给主项目的结果体。
- `ClozeAiResult`：完形填空专用返回体（继承 `AiQuestionDecisionResult`）。
- `ExamStyleExplanation`：应试风格解析结构（内嵌在 `AiQuestionDecisionResult` 中）。

字段内容直接参考 `2-ai-question-image-tech-design.md` 中的接口设计小节，保证命名和类型保持一致。

### 5. 编写 REST Controller

1. 在 `controller` 包下新建类 `QuestionAiController`。
2. 使用注解：
   - `@RestController`
   - `@RequestMapping("/api/ai/question")`
3. 定义接口方法：
   - `@PostMapping("/parse-from-image")`
   - 入参：`@RequestBody AiQuestionPayload payload`
   - 出参：`AiQuestionDecisionResult` 或 `ResponseEntity<AiQuestionDecisionResult>`
4. 方法内部调用 `QuestionAiService`，后者再调用大模型客户端。初期可以先写一个 **假实现（stub）** 返回固定数据，用于联调前后端。

示例轮廓（仅结构，具体实现按业务写）：

```java
@RestController
@RequestMapping("/api/ai/question")
public class QuestionAiController {

    private final QuestionAiService questionAiService;

    public QuestionAiController(QuestionAiService questionAiService) {
        this.questionAiService = questionAiService;
    }

    @PostMapping("/parse-from-image")
    public AiQuestionDecisionResult parseFromImage(@RequestBody AiQuestionPayload payload) {
        return questionAiService.parseFromImage(payload);
    }
}
```

### 6. 编写 Service 与大模型客户端骨架

- 在 `service` 包下创建 `QuestionAiService` 接口/类：
  - 方法：`AiQuestionDecisionResult parseFromImage(AiQuestionPayload payload)`
  - 内部逻辑：
    - 校验 `imageUrl`、`subject` 等基础字段。
    - 组装 Prompt 和图像信息，调用 `LlmClient`。
    - 解析返回的 JSON，映射为 `AiQuestionDecisionResult`。

- 在 `client` 包下创建 `LlmClient`（可以先用假实现）：
  - 方法示例：`String callMultiModal(AiQuestionPayload payload)`
  - 未来再使用 WebClient/OkHttp 实现真正的 HTTP 调用。

初期建议先返回一个固定的 `AiQuestionDecisionResult`，验证：
- 控制器是否能正常接收/返回 JSON；
- 主项目调用这个接口是否通畅。

### 7. 配置 application.yml

在 `src/main/resources/application.yml` 中添加基础配置：

```yaml
server:
  port: 9090  # 与现有 8080 区分开

spring:
  application:
    name: dm-ai-question-service

llm:
  base-url: https://your-llm-endpoint
  api-key: your-api-key-placeholder
  timeout-ms: 5000
```

后续在 `config` 包下读取这些配置，注入到 `LlmClient` 中使用。

### 8. 在 IDEA 中运行与本地验证

1. 在 IDEA 的 **Maven** 面板中，先执行：
   - `clean`
   - `package`（确认项目能成功构建）
2. 找到入口类 `DmAiQuestionServiceApplication`，右键选择：
   - `Run 'DmAiQuestionServiceApplication'`
3. 启动成功后，使用 Postman / IDEA 内置 HTTP Client / curl 测试：

```http
POST http://localhost:9090/api/ai/question/parse-from-image
Content-Type: application/json

{
  "imageUrl": "https://example.com/q.png",
  "subject": "数学",
  "grade": "高三",
  "questionType": "single",
  "difficulty": "medium",
  "source": "2024某省一模",
  "tags": ["导数","函数"],
  "currentColumns": [
    { "id": 1, "name": "导数与函数单调性" },
    { "id": 2, "name": "数列" }
  ]
}
```

4. 确认返回结构中包含：
   - `answer`、`analysis`
   - `recommendedColumnName`、`matchedExistingColumnId`
   - `confidence`、`modelMessage`
   - （可选）`examStyleExplanation`

### 9. 与现有大明刷题项目对接步骤概要

1. 在主项目 `ruoyi-admin` 的 `pom.xml` 中添加调用 HTTP 所需依赖（如 OkHttp/RestTemplate/WebClient），或直接用已有工具类。
2. 在 `dm_questionBank` 模块中新增 `QuestionAiClient`：
   - 读取 AI 微服务地址配置（如 `dm.ai-question-service.url`）。
   - 封装对 `/api/ai/question/parse-from-image` 的调用。
3. 在题目管理 Service 中新增方法：
   - 组装 `AiQuestionPayload`（含题目截图 URL、元信息、专栏列表）。
   - 调用 `QuestionAiClient` 获取 `AiQuestionDecisionResult`。
   - 将结果写入题目实体（答案、解析、专栏、难度）。
4. 在后台 Controller 中提供一个接口给前端按钮调用：
   - 例如：`/quiz/question/aiGenerate`。
   - 内部调用上一步的 Service 方法。

### 10. 后续优化建议

- 接入真实大模型 API，将 `LlmClient` 假实现替换掉。
- 加入日志与调用统计（记录每次 payload 摘要和结果简要信息）。
- 为 `QuestionAiService` 增加单元测试，模拟典型题目场景（单选题、完形填空等）。

