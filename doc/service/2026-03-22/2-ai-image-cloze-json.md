# 图片智能录题：完形填空 / 多子题 JSON 约定与后台回填

## 背景

原先 `QwenOmniClientImpl` 的提示词仅覆盖**单题**结构（`options` + `answer`）。管理端「题目编辑」已支持 **完形填空（父题 + 多子题）**（`questionType = 6`，`clozeChildren`），因此模型输出与前端回填需同时支持：

- 一篇材料 / 一大题干 + **多个客观子题**（每空一道单选 / 多选 / 判断）；
- 子题**无单独题干**时（典型软考/行测完形）：只填选项与标准答案；
- 未来 **英语完形** 等：子题可带**独立题干**（模型写入 `questionTitle`）。

## 题型枚举（模型输出）

`questionType` 在原有基础上增加：

| 模型值 | 后台 `questionType` |
|--------|---------------------|
| `CLOZE` | `6` 完形填空父题 |

其余仍为：`SINGLE`、`MULTIPLE`、`JUDGE`、`FILL_BLANK`、`SUBJECTIVE`、`UNKNOWN`。

## JSON 顶层字段（在原有字段上扩展）

```json
{
  "questionType": "CLOZE",
  "stemText": "完整文章或题干，可含 {1}{2} 占位",
  "subQuestions": [
    {
      "index": 1,
      "questionTitle": "",
      "questionType": "SINGLE",
      "options": [
        { "key": "A", "text": "选项A全文" },
        { "key": "B", "text": "..." }
      ],
      "answer": { "option": "B" }
    },
    {
      "index": 2,
      "questionTitle": "（英语完形示例）Which word fits blank 2?",
      "questionType": "SINGLE",
      "options": [],
      "answer": { "option": "C" }
    }
  ],
  "subAnswers": {
    "1": "B",
    "2": "C"
  },
  "analysis": "整体解析",
  "difficultyTag": "EASY_70",
  "knowledgePointId": 0,
  "knowledgeColumnId": 0
}
```

### `subQuestions`（推荐）

- 每个元素对应 **一个空 / 一道子题**，按 `index` 从 **1** 递增。
- **`questionTitle`**：无单独提问行时填空字符串 `""`；有子题叙述时写富文本可编辑内容（与后台「子题题干」一致）。
- **`questionType`**：`SINGLE` | `MULTIPLE` | `JUDGE`，与后台子题题型一致。
- **`options`**：该子题选项；可省略或置空数组，前端会用默认 A–D 空壳，由人工补全。
- **`answer.option`**：单选 / 判断为单个字母；多选可为 `"AC"` 等形式（与现有多选题一致）。

### `subAnswers`（简写）

- 形如 **`{"1":"B","2":"C"}`**：键为**空序号字符串**，值为选项字母。
- 用于各空选项布局相同、模型只可靠识别**答案字母**时；**可与 `subQuestions` 并存**——若某子题缺少 `answer`，则用 `subAnswers` 同序号合并。

### 仅 `subAnswers`、无 `subQuestions`

- 模型至少返回 `questionType: CLOZE`、`stemText`、`subAnswers`；前端按序号生成子题，默认 **单选题 + 默认四项选项**，仅填 `correct`。

## 提示词位置

- `daming-ai`：`com.daming.damingai.service.impl.QwenOmniClientImpl` 中 `USER_PROMPT_PREFIX` / `USER_PROMPT_PREFIX_NO_ASSIGN`（与 `pointCandidates` 无关分支均已同步）。

## 管理端回填逻辑

- 文件：`daming-admin/ruoyi-ui/src/views/quiz/question/single/index.vue`
- `mapAiQuestionType`：`CLOZE` → `6`
- `applyQuestionTypeDefaults`：题型 `6` 时清空父题 `items/correct`，并初始化 `clozeChildren`（非保留答案场景）
- `applyAiResultToForm`：完形走 `applyAiClozeResult`，组装 `formData.clozeChildren`（与手工「添加子题」结构一致）
- 父题 `questionTitle` 仍用 `buildStemWithImageFirst`（图 + `stemText`）

## 与人工录题流程对齐

1. 先选题型为 **完形填空**（或由 AI 识别为 `CLOZE` 自动切换）。
2. 上传题干图 → **父题题干**回填文章；**子题列表**逐个出现。
3. 子题**无题干**时：子题「题干」编辑器为空，仅检查选项与答案。
4. 有英语类独立提问时：在对应子题「子题题干」中显示模型返回的 `questionTitle`。

## 测试建议

- 纯单题截图：应仍为 `SINGLE`，不应出现 `clozeChildren`。
- 一篇多空截图：应识别为 `CLOZE`，子题数量与空数一致。
- 仅返回 `subAnswers` 的 JSON：应生成对应个数子题且默认选项占位。

---

## 真题与原卷题号 `originOrder`

当题目编辑页 **「是否真题」= 真题** 时，创建图片录题任务会附带 `isRealQuestion=true`，`QwenOmniClientImpl` 在提示词末尾追加 **REAL EXAM MODE**，要求模型从卷面识别 **题序/原卷题号**（如「第12题」、12.、Question 12），并在 JSON 中输出：

```json
"originOrder": 12
```

不可见或无法判断时可为 `null`。

**完形 + 真题**：`originOrder` **只填卷面上「第一空 / 首子题」对应的题序**（例如卷面印「21～25」则填 **21**）。后台会按该首题号对后续子题 **+1 递增**，因此模型**不要**填最后一空的题号。

前端在 `applyAiResultToForm` 中通过 `applyAiOriginOrder` 写入表单字段 **`originOrder`**（界面「原卷题号」）。非真题不传该模式，仍可保留 JSON 字段为 `null`。

### 模型提示（daming-ai `QwenOmniClientImpl`）

- 要求先 **数清卷面空格数 N**，若 N≥2：`questionType=CLOZE`，且 **`subQuestions` 数组长度必须等于 N**，禁止只输出 1 条子题。
- **`subAnswers`** 必须含 `"1"`…`"N"` 全部键；各空若共用同一组 A/B/C/D，应在 **每个** `subQuestions[i].options` 中重复完整选项文本。

### 前端兜底（与模型误判）

- 若 **`subQuestions` 条数 ≥ 2** 或 **`subAnswers` 键数量 ≥ 2**，即使 `questionType` 误为 `SINGLE`，也会**强制按完形**（题型 6）回填子题。
- **`subAnswers` 与 `subQuestions` 合并**：若子题数组不全但 `subAnswers` 有多键，会为缺省序号补子题。
- **自动分配专栏前**会先 **`loadPracticeOptions()`**，避免专项分组未加载导致 `pointCandidates` 为空、`knowledgePointId` 无法匹配。
