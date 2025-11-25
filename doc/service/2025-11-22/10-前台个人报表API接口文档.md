# 前台个人学习报表API接口文档

## 📋 文档说明

本文档定义前台学员个人学习报表的所有API接口规范，包括请求参数、响应格式和调用示例。

**项目**: 刷题平台 (daming-admin)  
**模块**: 前台个人学习报表  
**版本**: v1.0  
**创建日期**: 2025-11-22

---

## 🔐 鉴权说明

所有接口均需要用户登录，通过请求头携带 Token：

```
Authorization: Bearer {token}
```

后端通过 `SecurityUtils.getUserId()` 获取当前登录用户ID。

---

## 📡 接口列表

### 1. 获取个人学习概览

**接口地址**: `/api/personal/dashboard/overview`  
**请求方式**: `GET`  
**接口描述**: 获取用户的学习概览数据，包括累计做题数、考试次数、错题数、收藏数等

#### 请求参数
无

#### 响应示例
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "totalQuestions": 256,
    "totalExams": 42,
    "totalWrongQuestions": 38,
    "totalFavorites": 15,
    "weekQuestions": 45,
    "weekExams": 8,
    "totalStudyTime": 1280
  }
}
```

#### 响应字段说明
| 字段 | 类型 | 说明 |
|------|------|------|
| totalQuestions | Long | 累计做题数 |
| totalExams | Long | 累计考试次数 |
| totalWrongQuestions | Long | 累计错题数 |
| totalFavorites | Long | 累计收藏数 |
| weekQuestions | Long | 最近7天做题数 |
| weekExams | Long | 最近7天考试次数 |
| totalStudyTime | Long | 累计学习时长（分钟） |

---

### 2. 获取个人考试趋势

**接口地址**: `/api/personal/dashboard/exam-trend`  
**请求方式**: `GET`  
**接口描述**: 获取用户最近N次考试的成绩趋势

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| limit | Integer | 否 | 返回记录数 | 10 |

#### 响应示例
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "paperAnswerId": 1001,
      "paperId": 25,
      "paperName": "Java基础测试",
      "paperScore": 100,
      "finalScore": 85.0,
      "scorePercent": 85.0,
      "createTime": "2025-11-20 10:30:00",
      "doTime": 45
    },
    {
      "paperAnswerId": 1002,
      "paperId": 26,
      "paperName": "数据结构测试",
      "paperScore": 150,
      "finalScore": 112.5,
      "scorePercent": 75.0,
      "createTime": "2025-11-21 14:20:00",
      "doTime": 60
    }
  ]
}
```

#### 响应字段说明
| 字段 | 类型 | 说明 |
|------|------|------|
| paperAnswerId | Long | 答题记录ID |
| paperId | Long | 试卷ID |
| paperName | String | 试卷名称 |
| paperScore | Integer | 试卷总分 |
| finalScore | Double | 得分 |
| scorePercent | Double | 得分率（百分比） |
| createTime | Date | 考试时间 |
| doTime | Integer | 耗时（分钟） |

---

### 3. 获取个人各科目成绩统计

**接口地址**: `/api/personal/dashboard/subject-score`  
**请求方式**: `GET`  
**接口描述**: 获取用户在各科目的成绩统计，用于雷达图展示

#### 请求参数
无

#### 响应示例
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "subjectId": 1,
      "subjectName": "Java",
      "avgScore": 82.5,
      "avgScorePercent": 82.5,
      "maxScore": 95.0,
      "minScore": 65.0,
      "examCount": 12
    },
    {
      "subjectId": 2,
      "subjectName": "数据结构",
      "avgScore": 78.3,
      "avgScorePercent": 78.3,
      "maxScore": 90.0,
      "minScore": 60.0,
      "examCount": 8
    }
  ]
}
```

#### 响应字段说明
| 字段 | 类型 | 说明 |
|------|------|------|
| subjectId | Long | 科目ID |
| subjectName | String | 科目名称 |
| avgScore | Double | 平均分 |
| avgScorePercent | Double | 平均得分率 |
| maxScore | Double | 最高分 |
| minScore | Double | 最低分 |
| examCount | Long | 考试次数 |

---

### 4. 获取个人错题统计

**接口地址**: `/api/personal/dashboard/wrong-question`  
**请求方式**: `GET`  
**接口描述**: 获取用户的错题统计，包括按科目和题型的分布

#### 请求参数
无

#### 响应示例
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "totalWrong": 38,
    "subjectDistribution": [
      {
        "subjectName": "Java",
        "count": 15
      },
      {
        "subjectName": "数据结构",
        "count": 12
      },
      {
        "subjectName": "操作系统",
        "count": 11
      }
    ],
    "typeDistribution": [
      {
        "typeName": "单选题",
        "count": 12
      },
      {
        "typeName": "多选题",
        "count": 18
      },
      {
        "typeName": "判断题",
        "count": 8
      }
    ]
  }
}
```

#### 响应字段说明
| 字段 | 类型 | 说明 |
|------|------|------|
| totalWrong | Long | 错题总数 |
| subjectDistribution | Array | 按科目分布 |
| subjectDistribution[].subjectName | String | 科目名称 |
| subjectDistribution[].count | Long | 错题数量 |
| typeDistribution | Array | 按题型分布 |
| typeDistribution[].typeName | String | 题型名称 |
| typeDistribution[].count | Long | 错题数量 |

---

### 5. 获取个人学习时间分布

**接口地址**: `/api/personal/dashboard/study-time`  
**请求方式**: `GET`  
**接口描述**: 获取用户的学习时间分布，包括学习天数、连续天数和24小时分布

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| days | Integer | 否 | 统计天数 | 30 |

#### 响应示例
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "studyDays": 22,
    "continuousDays": 5,
    "hourDistribution": [
      { "hour": 8, "count": 12 },
      { "hour": 9, "count": 25 },
      { "hour": 10, "count": 18 },
      { "hour": 14, "count": 30 },
      { "hour": 15, "count": 22 },
      { "hour": 19, "count": 35 },
      { "hour": 20, "count": 28 },
      { "hour": 21, "count": 15 }
    ]
  }
}
```

#### 响应字段说明
| 字段 | 类型 | 说明 |
|------|------|------|
| studyDays | Long | 学习天数 |
| continuousDays | Long | 连续学习天数 |
| hourDistribution | Array | 24小时分布 |
| hourDistribution[].hour | Integer | 小时（0-23） |
| hourDistribution[].count | Long | 活动次数 |

---

## 🔧 调用示例

### JavaScript (Axios)
```javascript
import axios from 'axios'

// 获取学习概览
async function getOverview() {
  const response = await axios.get('/api/personal/dashboard/overview', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  })
  return response.data
}

// 获取考试趋势
async function getExamTrend(limit = 10) {
  const response = await axios.get('/api/personal/dashboard/exam-trend', {
    params: { limit },
    headers: {
      'Authorization': `Bearer ${token}`
    }
  })
  return response.data
}
```

### cURL
```bash
# 获取学习概览
curl -X GET "http://localhost:8080/api/personal/dashboard/overview" \
  -H "Authorization: Bearer YOUR_TOKEN"

# 获取考试趋势
curl -X GET "http://localhost:8080/api/personal/dashboard/exam-trend?limit=10" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 📊 错误码说明

| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| 401 | 未授权 | 检查Token是否有效 |
| 403 | 权限不足 | 确认用户权限 |
| 500 | 服务器错误 | 查看后端日志 |

---

## 🧪 测试建议

1. **单元测试**: 使用 JUnit 测试 Service 层方法
2. **接口测试**: 使用 Postman 或 curl 测试接口
3. **性能测试**: 使用 JMeter 测试并发访问
4. **数据准备**: 使用测试SQL生成模拟数据

---

## 📝 更新日志

| 版本 | 日期 | 更新内容 | 作者 |
|------|------|----------|------|
| v1.0 | 2025-11-22 | 初始版本，定义所有接口 | Cascade |

---

## 📞 联系方式

如有问题，请联系开发团队。
