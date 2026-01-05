# 在线考试与学生成绩管理系统 - 功能模块流程图

## 系统功能模块结构图

```mermaid
graph TD
    Start([系统入口]) --> US[US: 用户管理模块]
    Start --> QB[QB: 题库管理模块]
    Start --> PP[PP: 试卷管理模块]
    Start --> EX[EX: 考试执行模块]

    %% 用户管理模块
    US --> US1[DMS-REQ-US-001<br/>用户注册]
    US --> US2[DMS-REQ-US-002<br/>用户登录]
    US --> US3[DMS-REQ-US-003<br/>密码管理]
    US --> US4[DMS-REQ-US-004<br/>权限管理]
    US --> US5[DMS-REQ-US-005<br/>个人信息]
    US --> US6[DMS-REQ-US-006<br/>用户统计]

    %% 题库管理模块
    QB --> QB1[DMS-REQ-QB-001<br/>题目创建]
    QB --> QB2[DMS-REQ-QB-002<br/>题目编辑]
    QB --> QB3[DMS-REQ-QB-003<br/>题目分类]
    QB --> QB4[DMS-REQ-QB-004<br/>题目审核]
    QB --> QB5[DMS-REQ-QB-005<br/>批量导入]
    QB --> QB6[DMS-REQ-QB-006<br/>题库统计]

    %% 试卷管理模块
    PP --> PP1[DMS-REQ-PP-001<br/>手动组卷]
    PP --> PP2[DMS-REQ-PP-002<br/>智能组卷]
    PP --> PP3[DMS-REQ-PP-003<br/>试卷模板]
    PP --> PP4[DMS-REQ-PP-004<br/>试卷发布]
    PP --> PP5[DMS-REQ-PP-005<br/>试卷管理]
    PP --> PP6[DMS-REQ-PP-006<br/>试卷分析]

    %% 考试执行模块
    EX --> EX1[DMS-REQ-EX-001<br/>在线答题]
    EX --> EX2[DMS-REQ-EX-002<br/>考试监控]
    EX --> EX3[DMS-REQ-EX-003<br/>计时管理]
    EX --> EX4[DMS-REQ-EX-004<br/>防作弊]
    EX --> EX5[DMS-REQ-EX-005<br/>答题进度]
    EX --> EX6[DMS-REQ-EX-006<br/>异常处理]

    %% 模块间流程关系
    US2 --> PP4
    QB4 --> PP1
    QB4 --> PP2
    PP4 --> EX1
    EX1 --> EX2
    EX1 --> EX3
    EX1 --> EX4
    EX6 --> End([结束])

    style US fill:#e1f5ff
    style QB fill:#fff4e1
    style PP fill:#e8f5e9
    style EX fill:#fce4ec
```

## 功能模块详细说明

### 模块1：用户管理（US）

- **DMS-REQ-US-001**：用户注册
- **DMS-REQ-US-002**：用户登录
- **DMS-REQ-US-003**：密码管理
- **DMS-REQ-US-004**：权限管理
- **DMS-REQ-US-005**：个人信息
- **DMS-REQ-US-006**：用户统计

### 模块2：题库管理（QB）

- **DMS-REQ-QB-001**：题目创建
- **DMS-REQ-QB-002**：题目编辑
- **DMS-REQ-QB-003**：题目分类
- **DMS-REQ-QB-004**：题目审核
- **DMS-REQ-QB-005**：批量导入
- **DMS-REQ-QB-006**：题库统计

### 模块3：试卷管理（PP）

- **DMS-REQ-PP-001**：手动组卷
- **DMS-REQ-PP-002**：智能组卷
- **DMS-REQ-PP-003**：试卷模板
- **DMS-REQ-PP-004**：试卷发布
- **DMS-REQ-PP-005**：试卷管理
- **DMS-REQ-PP-006**：试卷分析

### 模块4：考试执行（EX）

- **DMS-REQ-EX-001**：在线答题
- **DMS-REQ-EX-002**：考试监控
- **DMS-REQ-EX-003**：计时管理
- **DMS-REQ-EX-004**：防作弊
- **DMS-REQ-EX-005**：答题进度
- **DMS-REQ-EX-006**：异常处理

## 模块间业务流程

1. **用户登录**（US-002）→ **试卷发布**（PP-004）→ **在线答题**（EX-001）
2. **题目审核**（QB-004）→ **手动/智能组卷**（PP-001/PP-002）→ **试卷发布**（PP-004）
3. **在线答题**（EX-001）过程中同时进行 **考试监控**（EX-002）、**计时管理**（EX-003）、**防作弊**（EX-004）
4. 如遇异常情况，触发 **异常处理**（EX-006）流程















