# 考试执行模块（EX）流程图

## 模块信息

- **模块编号**：EX
- **模块名称**：考试执行
- **模块简介**：支持在线考试全流程，包括答题、监控、计时、防作弊等执行功能

## 功能列表

| 序号 | 一级功能 | 功能编号 | 二级功能 | 功能编号 |
|------|---------|---------|---------|---------|
| 1 | 在线答题 | DMS-REQ-EX-001 | 答题进度 | DMS-REQ-EX-005 |
| 2 | 考试监控 | DMS-REQ-EX-002 | 异常处理 | DMS-REQ-EX-006 |
| 3 | 计时管理 | DMS-REQ-EX-003 | | |
| 4 | 防作弊 | DMS-REQ-EX-004 | | |

## 功能流程图

```mermaid
graph TD
    Start([考试执行模块入口]) --> EX1[DMS-REQ-EX-001<br/>在线答题]
    Start --> EX2[DMS-REQ-EX-002<br/>考试监控]
    Start --> EX3[DMS-REQ-EX-003<br/>计时管理]
    Start --> EX4[DMS-REQ-EX-004<br/>防作弊]
    Start --> EX5[DMS-REQ-EX-005<br/>答题进度]
    Start --> EX6[DMS-REQ-EX-006<br/>异常处理]

    EX1 --> EX2
    EX1 --> EX3
    EX1 --> EX4
    EX1 --> EX5
    EX2 --> EX6
    EX3 --> EX6
    EX4 --> EX6
    EX5 --> Submit([提交试卷])
    EX6 --> Submit
    Submit --> End([完成])

    style EX1 fill:#fce4ec
    style EX2 fill:#f8bbd0
    style EX3 fill:#f48fb1
    style EX4 fill:#f06292
    style EX5 fill:#ec407a
    style EX6 fill:#e91e63
```

## 功能说明

### 一级功能

1. **DMS-REQ-EX-001：在线答题**
   - 学生在系统中查看题目并作答，支持选择题、填空题、主观题等题型

2. **DMS-REQ-EX-002：考试监控**
   - 教师或管理员实时监控考试过程，查看学生答题状态

3. **DMS-REQ-EX-003：计时管理**
   - 系统自动计时，显示剩余时间，时间到自动交卷

4. **DMS-REQ-EX-004：防作弊**
   - 检测切屏、复制粘贴等异常行为，记录并警告

### 二级功能

5. **DMS-REQ-EX-005：答题进度**
   - 显示学生已答题数、未答题数、标记题目等进度信息

6. **DMS-REQ-EX-006：异常处理**
   - 处理考试过程中的异常情况，如网络中断、系统错误等































