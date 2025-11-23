# 后台首页报表API接口文档

## 文档信息

- **创建日期**: 2025-11-22
- **版本**: v1.0
- **基础路径**: `/dashboard`
- **认证方式**: Bearer Token

---

## 接口列表

### 1. 获取首页概览统计数据

#### 1.1 基本信息

- **接口地址**: `/dashboard/overview`
- **请求方式**: GET
- **权限标识**: `dashboard:overview:query`

#### 1.2 请求参数

无

#### 1.3 返回示例

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "totalUsers": 1250,
    "totalUsersGrowth": "+15",
    "totalQuestions": 5430,
    "totalQuestionsGrowth": "+8",
    "totalPapers": 230,
    "totalPapersGrowth": "+2",
    "totalExams": 12800,
    "totalExamsGrowth": "+156"
  }
}
```

#### 1.4 返回字段说明

| 字段名 | 类型 | 说明 |
|-------|------|------|
| totalUsers | Long | 总用户数 |
| totalUsersGrowth | String | 用户增长数(昨日) |
| totalQuestions | Long | 总题目数 |
| totalQuestionsGrowth | String | 题目增长数(昨日) |
| totalPapers | Long | 总试卷数 |
| totalPapersGrowth | String | 试卷增长数(昨日) |
| totalExams | Long | 总考试次数 |
| totalExamsGrowth | String | 考试次数增长数(昨日) |

---

### 2. 获取用户活跃度统计数据

#### 2.1 基本信息

- **接口地址**: `/dashboard/user-activity`
- **请求方式**: GET
- **权限标识**: `dashboard:user-activity:query`

#### 2.2 请求参数

无

#### 2.3 返回示例

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "todayLogins": 120,
    "weekLogins": 580,
    "monthLogins": 1050,
    "newUsers": 45,
    "loginTrend": [
      {
        "date": "2025-10-23",
        "logins": 85,
        "uniqueUsers": 62
      },
      {
        "date": "2025-10-24",
        "logins": 92,
        "uniqueUsers": 68
      }
    ]
  }
}
```

#### 2.4 返回字段说明

| 字段名 | 类型 | 说明 |
|-------|------|------|
| todayLogins | Long | 今日登录用户数 |
| weekLogins | Long | 本周登录用户数 |
| monthLogins | Long | 本月登录用户数 |
| newUsers | Long | 本月新增用户数 |
| loginTrend | Array | 登录趋势数据(最近30天) |
| loginTrend[].date | String | 日期(yyyy-MM-dd) |
| loginTrend[].logins | Long | 登录人次 |
| loginTrend[].uniqueUsers | Long | 唯一用户数 |

---

### 3. 获取考试数据统计

#### 3.1 基本信息

- **接口地址**: `/dashboard/exam-statistics`
- **请求方式**: GET
- **权限标识**: `dashboard:exam-statistics:query`

#### 3.2 请求参数

无

#### 3.3 返回示例

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "todayExams": 156,
    "weekExams": 820,
    "monthExams": 2340,
    "avgScore": 78.5,
    "passRate": 85.2,
    "excellentRate": 32.6,
    "examTrend": [
      {
        "date": "2025-10-23",
        "count": 45,
        "avgScore": 76.8
      },
      {
        "date": "2025-10-24",
        "count": 52,
        "avgScore": 79.2
      }
    ],
    "reviewProgress": {
      "pending": 45,
      "reviewed": 180,
      "rate": 80.0
    }
  }
}
```

#### 3.4 返回字段说明

| 字段名 | 类型 | 说明 |
|-------|------|------|
| todayExams | Long | 今日考试人次 |
| weekExams | Long | 本周考试人次 |
| monthExams | Long | 本月考试人次 |
| avgScore | Double | 平均分 |
| passRate | Double | 及格率(%) |
| excellentRate | Double | 优秀率(%) |
| examTrend | Array | 考试趋势数据(最近30天) |
| examTrend[].date | String | 日期(yyyy-MM-dd) |
| examTrend[].count | Long | 考试人次 |
| examTrend[].avgScore | Double | 平均分 |
| reviewProgress | Object | 批改进度 |
| reviewProgress.pending | Long | 待批改数 |
| reviewProgress.reviewed | Long | 已批改数 |
| reviewProgress.rate | Double | 批改率(%) |

---

### 4. 获取题目统计数据

#### 4.1 基本信息

- **接口地址**: `/dashboard/question-statistics`
- **请求方式**: GET
- **权限标识**: `dashboard:question-statistics:query`

#### 4.2 请求参数

无

#### 4.3 返回示例

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "subjectDistribution": [
      {
        "subjectName": "计算机组成",
        "count": 1250
      },
      {
        "subjectName": "软件工程",
        "count": 980
      }
    ],
    "typeDistribution": [
      {
        "type": 1,
        "typeName": "单选题",
        "count": 2100
      },
      {
        "type": 2,
        "typeName": "多选题",
        "count": 1580
      }
    ],
    "topFavorites": [
      {
        "questionId": 123,
        "preview": "操作系统中的进程调度算法有哪些？",
        "favCount": 45
      }
    ]
  }
}
```

#### 4.4 返回字段说明

| 字段名 | 类型 | 说明 |
|-------|------|------|
| subjectDistribution | Array | 科目分布 |
| subjectDistribution[].subjectName | String | 科目名称 |
| subjectDistribution[].count | Long | 题目数量 |
| typeDistribution | Array | 题型分布 |
| typeDistribution[].type | Integer | 题型编号(1-单选 2-多选 3-判断 4-填空 5-简答) |
| typeDistribution[].typeName | String | 题型名称 |
| typeDistribution[].count | Long | 题目数量 |
| topFavorites | Array | 热门收藏题目TOP10 |
| topFavorites[].questionId | Long | 题目ID |
| topFavorites[].preview | String | 题目内容预览(前50字符) |
| topFavorites[].favCount | Long | 收藏次数 |

---

### 5. 获取试卷统计数据

#### 5.1 基本信息

- **接口地址**: `/dashboard/paper-statistics`
- **请求方式**: GET
- **权限标识**: `dashboard:paper-statistics:query`

#### 5.2 请求参数

无

#### 5.3 返回示例

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "topPapers": [
      {
        "paperId": 25,
        "paperName": "计算机组成期中考试",
        "examCount": 320,
        "avgScore": 81.5,
        "subjectName": "计算机组成"
      }
    ],
    "usageRate": 78.5,
    "publishedCount": 230,
    "usedCount": 181
  }
}
```

#### 5.4 返回字段说明

| 字段名 | 类型 | 说明 |
|-------|------|------|
| topPapers | Array | 热门试卷TOP10 |
| topPapers[].paperId | Long | 试卷ID |
| topPapers[].paperName | String | 试卷名称 |
| topPapers[].examCount | Long | 考试人次 |
| topPapers[].avgScore | Double | 平均分 |
| topPapers[].subjectName | String | 科目名称 |
| usageRate | Double | 试卷使用率(%) |
| publishedCount | Long | 已发布试卷数 |
| usedCount | Long | 已使用试卷数 |

---

### 6. 获取时间分布数据

#### 6.1 基本信息

- **接口地址**: `/dashboard/time-distribution`
- **请求方式**: GET
- **权限标识**: `dashboard:time-distribution:query`

#### 6.2 请求参数

无

#### 6.3 返回示例

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "hourlyDistribution": [
      {
        "hour": 0,
        "count": 5
      },
      {
        "hour": 1,
        "count": 2
      },
      {
        "hour": 8,
        "count": 45
      },
      {
        "hour": 9,
        "count": 68
      }
    ]
  }
}
```

#### 6.4 返回字段说明

| 字段名 | 类型 | 说明 |
|-------|------|------|
| hourlyDistribution | Array | 24小时分布数据 |
| hourlyDistribution[].hour | Integer | 小时(0-23) |
| hourlyDistribution[].count | Long | 考试人次 |

---

## 公共说明

### 响应状态码

| 状态码 | 说明 |
|-------|------|
| 200 | 请求成功 |
| 401 | 未授权,需要登录 |
| 403 | 权限不足 |
| 500 | 服务器内部错误 |

### 响应格式

所有接口均返回统一的响应格式:

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {}
}
```

### 请求头

| 参数名 | 必填 | 说明 |
|-------|------|------|
| Authorization | 是 | Bearer Token |
| Content-Type | 是 | application/json |

---

## 错误码说明

| 错误码 | 说明 | 解决方案 |
|-------|------|---------|
| 401 | Token过期或无效 | 重新登录获取Token |
| 403 | 权限不足 | 联系管理员分配权限 |
| 500 | 服务器错误 | 查看服务器日志排查问题 |

---

## 使用示例

### JavaScript (Axios)

```javascript
// 获取首页概览统计数据
axios.get('/dashboard/overview', {
  headers: {
    'Authorization': 'Bearer ' + token
  }
})
.then(response => {
  console.log(response.data);
})
.catch(error => {
  console.error(error);
});
```

### Java (RestTemplate)

```java
RestTemplate restTemplate = new RestTemplate();
HttpHeaders headers = new HttpHeaders();
headers.set("Authorization", "Bearer " + token);

HttpEntity<String> entity = new HttpEntity<>(headers);
ResponseEntity<String> response = restTemplate.exchange(
    "http://localhost:8080/dashboard/overview",
    HttpMethod.GET,
    entity,
    String.class
);

System.out.println(response.getBody());
```

### cURL

```bash
curl -X GET "http://localhost:8080/dashboard/overview" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json"
```

---

## 注意事项

1. **权限配置**: 确保在系统菜单管理中配置了相应的权限标识
2. **数据缓存**: 部分统计数据可能有5分钟的缓存延迟
3. **性能考虑**: 建议在非高峰期调用统计接口,避免影响系统性能
4. **时区**: 所有时间字段均使用服务器时区
5. **数据准确性**: 统计数据基于数据库实时查询,确保数据库索引已正确创建

---

## 更新日志

| 版本 | 日期 | 更新内容 | 修改人 |
|------|------|----------|--------|
| v1.0 | 2025-11-22 | 初始版本,创建所有报表接口 | Cascade |

---

## 附录

### A. 题型编号对照表

| 编号 | 题型名称 |
|------|---------|
| 1 | 单选题 |
| 2 | 多选题 |
| 3 | 判断题 |
| 4 | 填空题 |
| 5 | 简答题 |

### B. 批改状态对照表

| 状态值 | 说明 |
|-------|------|
| 0 | 无需批改 |
| 1 | 待批改 |
| 2 | 已批改 |

### C. 及格/优秀分数线

| 等级 | 分数线 |
|------|--------|
| 及格 | 60分 |
| 优秀 | 85分 |

---

## 联系方式

如有疑问,请联系开发团队。
