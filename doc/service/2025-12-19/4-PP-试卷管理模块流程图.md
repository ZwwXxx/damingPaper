# 试卷管理模块（PP）流程图

## 模块信息

- **模块编号**：PP
- **模块名称**：试卷管理
- **模块简介**：处理试卷的创建、编辑、组卷、发布、模板管理等试卷业务逻辑

## 功能列表

| 序号 | 一级功能 | 功能编号 | 二级功能 | 功能编号 |
|------|---------|---------|---------|---------|
| 1 | 手动组卷 | DMS-REQ-PP-001 | 试卷管理 | DMS-REQ-PP-005 |
| 2 | 智能组卷 | DMS-REQ-PP-002 | 试卷分析 | DMS-REQ-PP-006 |
| 3 | 试卷模板 | DMS-REQ-PP-003 | | |
| 4 | 试卷发布 | DMS-REQ-PP-004 | | |

## 功能流程图

```mermaid
graph TD
    Start([试卷管理模块入口]) --> PP1[DMS-REQ-PP-001<br/>手动组卷]
    Start --> PP2[DMS-REQ-PP-002<br/>智能组卷]
    Start --> PP3[DMS-REQ-PP-003<br/>试卷模板]
    Start --> PP4[DMS-REQ-PP-004<br/>试卷发布]
    Start --> PP5[DMS-REQ-PP-005<br/>试卷管理]
    Start --> PP6[DMS-REQ-PP-006<br/>试卷分析]

    PP1 --> PP4
    PP2 --> PP4
    PP3 --> PP1
    PP3 --> PP2
    PP4 --> PP5
    PP4 --> PP6
    PP5 --> PP6
    PP6 --> End([完成])

    style PP1 fill:#e8f5e9
    style PP2 fill:#e8f5e9
    style PP3 fill:#c8e6c9
    style PP4 fill:#a5d6a7
    style PP5 fill:#81c784
    style PP6 fill:#81c784
```

## 功能说明

### 一级功能

1. **DMS-REQ-PP-001：手动组卷**
   - 教师手动从题库中选择题目，配置分值和顺序，组成试卷

2. **DMS-REQ-PP-002：智能组卷**
   - 系统根据设定的条件（难度、知识点、题型等）自动从题库中选择题目组成试卷

3. **DMS-REQ-PP-003：试卷模板**
   - 保存常用的试卷结构作为模板，方便快速创建相似试卷

4. **DMS-REQ-PP-004：试卷发布**
   - 将已创建的试卷发布给学生，设置考试时间、对象等参数

### 二级功能

5. **DMS-REQ-PP-005：试卷管理**
   - 查看、编辑、删除已创建的试卷，管理试卷状态

6. **DMS-REQ-PP-006：试卷分析**
   - 分析试卷的难度分布、知识点覆盖、题型分布等数据































