## 批改试卷流程说明

- 入口：教师端 `考试管理 > 试卷批改`，可按试卷名称、考生账号、批改状态（全部/待批改/已完成）筛选。
- 详情弹窗：调 `/quiz/student/paper/answer/getPaperReview/{id}`，包含原始试卷结构 `paperDto` 与答卷 `paperAnswerDto`，主观题以富文本展示学生答案。
- 批改：教师在主观题列表输入得分/评语、填写整体批注 `reviewRemark`，提交调用 `/quiz/student/paper/answer/review`。
- 后端：`DamingPaperAnswerServiceImpl.reviewPaper` 校验状态，累计主观题得分、更新 `finalScore`、设置 `reviewUser/ReviewTime/ReviewRemark`。
- 结果：列表刷新后状态切为“已完成”，考生可在个人记录查看最终得分。

```mermaid
flowchart TD
    A["考生提交试卷<br/>/quiz/student/paper/answer/submitAnswer"] --> B["后台生成客观题得分<br/>reviewStatus=待批改"]
    B --> C["教师在后台列表筛选答卷"]
    C --> D["打开批改弹窗<br/>请求 getPaperReview"]
    D --> E["查看主观题富文本答案<br/>输入题目得分/评语"]
    E --> F["填写整体批注 reviewRemark<br/>点击提交"]
    F --> G["后端 reviewPaper 校验并计算总分"]
    G --> H["更新 DamingPaperAnswer 状态为已完成<br/>写入最终分数、批改信息"]
    H --> I["前端刷新列表状态，考生可在记录页查看"]
```

