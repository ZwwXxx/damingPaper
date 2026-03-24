# 百炼调用参数与 Spring AI 对接模板

## 1. 文档目标

提供一套可直接用于 `daming-ai` 的对接模板，包含：

- 百炼请求参数建议；
- Spring AI 服务分层建议；
- 与当前后端联调的接口定义；
- 异常处理、重试与日志字段建议；
- 最小可运行代码骨架（便于你快速开工）。

## 2. 目录与模块建议

建议在 `daming-ai` 中按如下分层：

- `controller`：对外接口（创建任务、查询结果）
- `service`：业务编排（任务状态流转）
- `client`：百炼调用封装
- `prompt`：系统提示词与模板管理
- `schema`：JSON Schema 与结构化校验
- `repository`：任务持久化

## 3. 环境变量与配置建议

```yaml
ai:
  qwen:
    base-url: https://dashscope.aliyuncs.com/compatible-mode/v1
    api-key: ${DASHSCOPE_API_KEY}
    model: qwen3-omni-flash-realtime-2025-09-15
    stream: true
    enable-thinking: false
    modalities: ["text"]
    timeout-seconds: 60
  task:
    max-retry: 2
    default-conf-threshold: 0.75
```

说明：

- `api-key` 使用环境变量，不写死在仓库；
- `model` 可配置化，便于后续切换；
- `default-conf-threshold` 用于低置信度强制人工复核。

## 4. 百炼请求参数建议

## 4.1 请求核心参数（业务建议）

- 模型：使用 `qwen3-omni-flash-realtime-2025-09-15`（你当前选型）；
- 输入：`题图 + 候选专栏/知识点 + JSON Schema + 用户提示词`；
- 输出：建议先只要文本 `modalities=["text"]`，用于结构化 JSON 回填；
- 温度：`0.1 ~ 0.3`（提高稳定性）；
- `stream` 必须为 `true`（Omni 系列硬性要求）；
- 最大输出 token：按 Schema 上限给足，但避免过大。

## 4.2 请求体结构示意（伪示例）

```json
{
  "model": "qwen3-omni-flash-realtime-2025-09-15",
  "temperature": 0.2,
  "stream": true,
  "stream_options": {
    "include_usage": true
  },
  "extra_body": {
    "enable_thinking": false
  },
  "modalities": ["text"],
  "messages": [
    {
      "role": "system",
      "content": [
        { "type": "text", "text": "系统提示词（第1份文档13.1）" }
      ]
    },
    {
      "role": "user",
      "content": [
        { "type": "image_url", "image_url": { "url": "https://xxx/question.png" } },
        { "type": "text", "text": "用户提示词模板（第1份文档13.2）+ 候选ID列表" }
      ]
    }
  ]
}
```

> 说明：Omni 走流式返回，生产侧需要将增量文本拼接后再做 JSON 解析与 Schema 校验。

## 5. AI 服务接口定义（对当前后端）

## 5.1 创建任务

- `POST /ai/question-image/task`

请求示例：

```json
{
  "imageUrl": "https://oss.xxx.com/question/20260321/abc.png",
  "columnCandidates": [
    { "id": 1, "name": "计算机网络" },
    { "id": 2, "name": "操作系统" }
  ],
  "pointCandidates": [
    { "id": 27, "name": "CIDR与子网划分", "columnId": 1 },
    { "id": 28, "name": "路由协议基础", "columnId": 1 }
  ],
  "confidenceThreshold": 0.75
}
```

响应示例：

```json
{
  "taskId": "20260321_9f8e2a",
  "status": "PROCESSING"
}
```

## 5.2 查询任务

- `GET /ai/question-image/task/{taskId}`

响应示例：

```json
{
  "taskId": "20260321_9f8e2a",
  "status": "SUCCESS",
  "result": {
    "questionType": "SINGLE",
    "stemText": "......",
    "options": [
      { "key": "A", "text": "......" },
      { "key": "B", "text": "......" },
      { "key": "C", "text": "......" },
      { "key": "D", "text": "......" }
    ],
    "answer": {
      "option": "B",
      "finalAnswer": "选 B。"
    },
    "analysis": "......",
    "examTips": {
      "step1": "......",
      "step2": "......",
      "step3": "......"
    },
    "difficultyTag": "EASY_70",
    "knowledgeColumnId": 1,
    "knowledgePointId": 27,
    "knowledgeSummary": "本题属于计算机网络-子网划分。",
    "confidence": 0.88,
    "needsHumanReview": false
  }
}
```

## 6. 任务状态机建议

- `PENDING`：任务已创建，未开始调用模型；
- `PROCESSING`：模型处理中；
- `SUCCESS`：解析成功；
- `FAILED`：解析失败；
- `REVIEW_REQUIRED`：结果可用但需人工重点复核。

状态流转建议：

- 默认：`PENDING -> PROCESSING -> SUCCESS/FAILED`
- 若 `confidence < threshold`：`SUCCESS -> REVIEW_REQUIRED`

## 7. Spring AI 代码骨架（模板）

## 7.1 DTO（示意）

```java
public class CreateQuestionImageTaskReq {
    private String imageUrl;
    private List<CandidateDTO> columnCandidates;
    private List<CandidatePointDTO> pointCandidates;
    private BigDecimal confidenceThreshold;
}
```

## 7.2 Controller（示意）

```java
@RestController
@RequestMapping("/ai/question-image")
public class QuestionImageController {

    @PostMapping("/task")
    public AjaxResult createTask(@RequestBody CreateQuestionImageTaskReq req) {
        String taskId = questionImageService.createTask(req);
        return AjaxResult.success(Map.of("taskId", taskId, "status", "PROCESSING"));
    }

    @GetMapping("/task/{taskId}")
    public AjaxResult getTask(@PathVariable String taskId) {
        return AjaxResult.success(questionImageService.getTask(taskId));
    }
}
```

## 7.3 Service 编排（示意）

```java
public interface QuestionImageService {
    String createTask(CreateQuestionImageTaskReq req);
    TaskResultVO getTask(String taskId);
}
```

```java
@Service
public class QuestionImageServiceImpl implements QuestionImageService {

    @Override
    public String createTask(CreateQuestionImageTaskReq req) {
        // 1) 落库 PENDING
        // 2) 异步执行调用百炼
        // 3) 返回 taskId
        return TaskIdUtil.nextId();
    }

    @Override
    public TaskResultVO getTask(String taskId) {
        // 查任务表并返回结果
        return new TaskResultVO();
    }
}
```

## 7.4 百炼客户端封装（示意）

```java
public interface BailianClient {
    String parseQuestionImage(String imageUrl, String systemPrompt, String userPrompt, String schemaJson);
}
```

```java
@Component
public class BailianClientImpl implements BailianClient {

    @Override
    public String parseQuestionImage(String imageUrl, String systemPrompt, String userPrompt, String schemaJson) {
        // 1) 构造 messages（image + text）
        // 2) 设置 response_format=json_schema
        // 3) 调用百炼接口
        // 4) 返回模型原始 JSON 字符串
        return "{}";
    }
}
```

## 8. 输出校验与降级策略

- 先做 JSON 反序列化；
- 再做 Schema 校验（字段完整性、枚举值、ID 合法性）；
- 若 `knowledgeColumnId/knowledgePointId` 不在候选内，直接标记 `needsHumanReview=true`；
- 若解析失败：
  - 第一次：自动重试 1 次（可切换更稳模型）；
  - 第二次失败：置 `FAILED` 并记录错误码。

## 9. 日志与监控字段（必须）

- `taskId`
- `model`
- `latencyMs`
- `promptVersion`
- `inputImageSize`
- `tokenUsage`（若可取）
- `status`
- `errorCode` / `errorMessage`
- `confidence`

## 10. 联调检查清单

- 百炼密钥是否生效；
- 图片 URL 是否可被模型访问；
- Schema 严格模式是否开启；
- 候选知识点 ID 是否正确传入；
- 低置信度是否触发人工复核；
- 当前后端表单回填字段是否一一对应。

## 11. 建议实施顺序

1. 先打通单题任务创建与查询；  
2. 再接入百炼并拿到结构化 JSON；  
3. 再补 Schema 校验与重试机制；  
4. 最后联调后台回填与人工确认入库。

## 12. 官方文档精简总结与勘误

### 12.1 精简总结（按你当前场景）

- 鉴权：使用环境变量 `DASHSCOPE_API_KEY`；
- 基础地址（北京）：`https://dashscope.aliyuncs.com/compatible-mode/v1`；
- Omni 仅支持流式：`stream=true` 是必须项；
- 单条 `user.content` 里只能是“文本 + 一种其他模态”（图片/音频/视频三选一）；
- 你做图片录题时，建议输出只用 `modalities=["text"]`；
- 若开启 `enable_thinking=true`，不要请求音频输出；
- 我们业务以结构化回填为主，建议固定 `enable_thinking=false` 提高稳定性与响应速度。

### 12.2 本文档已修正的错误点

1. 模型名修正：  
   从 `qwen-vl-max` 改为 `qwen3-omni-flash-realtime-2025-09-15`。

2. 流式要求补齐：  
   增加 `stream=true`、`stream_options.include_usage=true`，避免非流式报错。

3. 模态策略修正：  
   录题业务从 `["text","audio"]` 调整为 `["text"]`，减少不必要开销和复杂度。

4. 思考模式参数补齐：  
   增加 `extra_body.enable_thinking=false`，与结构化输出场景对齐。

5. 响应处理口径明确：  
   强调“先拼接流式增量，再进行 JSON 反序列化与 Schema 校验”。

### 12.3 实施注意

- `qwen3-omni-flash-realtime-2025-09-15` 为快照版本，建议保留可配置回退模型（例如 `qwen3-omni-flash`）；
- 图片 URL 需公网可访问，或使用 Base64 data URL；
- 若后续要语音播报，再单独开启 `modalities=["text","audio"]` 和 `audio` 参数，不要与当前录题链路耦合。

