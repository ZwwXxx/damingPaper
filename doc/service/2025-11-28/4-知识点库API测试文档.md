# 知识点库系统 - API接口测试文档

> **文档版本**: v1.0  
> **创建日期**: 2025-11-28  
> **测试工具**: Postman / Apifox / 浏览器

---

## 📋 接口列表

### 1. 查询科目列表

**接口地址**: `GET /student/knowledge/subjects`

**请求参数**: 无

**成功响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "subjectId": 1,
      "subjectName": "计算机组成原理",
      "subjectCode": "CO",
      "description": "介绍计算机系统的组成、工作原理及性能评价",
      "icon": null,
      "sortOrder": 1,
      "status": 1
    },
    {
      "subjectId": 2,
      "subjectName": "数据结构",
      "subjectCode": "DS",
      "description": "研究数据的逻辑结构、存储结构及算法",
      "sortOrder": 2,
      "status": 1
    }
  ]
}
```

**测试命令（curl）**:
```bash
curl -X GET "http://localhost:8080/student/knowledge/subjects"
```

---

### 2. 查询章节树

**接口地址**: `GET /student/knowledge/chapters/{subjectId}`

**路径参数**:
- `subjectId`: 科目ID（例如：1）

**成功响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "chapterId": 1,
      "subjectId": 1,
      "parentId": 0,
      "chapterName": "第1章 计算机系统概述",
      "sortOrder": 1,
      "level": 1,
      "children": [
        {
          "chapterId": 2,
          "subjectId": 1,
          "parentId": 1,
          "chapterName": "1.1 计算机发展历程",
          "sortOrder": 1,
          "level": 2,
          "children": []
        }
      ]
    }
  ]
}
```

**测试命令（curl）**:
```bash
curl -X GET "http://localhost:8080/student/knowledge/chapters/1"
```

---

### 3. 查询知识点列表（分页）

**接口地址**: `GET /student/knowledge/points`

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| subjectId | Long | 否 | 科目ID |
| chapterId | Long | 否 | 章节ID |
| title | String | 否 | 标题关键词 |
| difficulty | Integer | 否 | 难度（1-简单 2-中等 3-困难） |

**成功响应示例**:
```json
{
  "code": 200,
  "msg": "查询成功",
  "total": 1,
  "rows": [
    {
      "pointId": 1,
      "subjectId": 1,
      "chapterId": 11,
      "title": "Cache映射方式详解",
      "summary": "介绍直接映射、全相联映射、组相联映射三种Cache映射方式的原理、优缺点和应用场景",
      "difficulty": 2,
      "importance": 3,
      "authorName": "admin",
      "viewCount": 0,
      "likeCount": 0,
      "collectCount": 0,
      "commentCount": 0,
      "status": 1,
      "subjectName": "计算机组成原理",
      "chapterName": "Cache高速缓存",
      "createTime": "2025-11-28 12:00:00"
    }
  ]
}
```

**测试命令（curl）**:
```bash
# 查询所有知识点（分页）
curl -X GET "http://localhost:8080/student/knowledge/points?pageNum=1&pageSize=10"

# 按科目查询
curl -X GET "http://localhost:8080/student/knowledge/points?subjectId=1"

# 按章节查询
curl -X GET "http://localhost:8080/student/knowledge/points?chapterId=11"

# 按标题搜索
curl -X GET "http://localhost:8080/student/knowledge/points?title=Cache"
```

---

### 4. 获取知识点详情

**接口地址**: `GET /student/knowledge/point/{pointId}`

**路径参数**:
- `pointId`: 知识点ID（例如：1）

**成功响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "pointId": 1,
    "subjectId": 1,
    "chapterId": 11,
    "title": "Cache映射方式详解",
    "summary": "介绍直接映射、全相联映射、组相联映射三种Cache映射方式的原理、优缺点和应用场景",
    "content": "## 一、Cache映射概述\n\nCache映射是指主存地址与Cache地址之间的对应关系...",
    "contentHtml": "<h2>一、Cache映射概述</h2><p>Cache映射是指...</p>",
    "difficulty": 2,
    "importance": 3,
    "authorId": 1,
    "authorName": "admin",
    "viewCount": 1,
    "likeCount": 0,
    "collectCount": 0,
    "commentCount": 0,
    "status": 1,
    "subjectName": "计算机组成原理",
    "chapterName": "Cache高速缓存",
    "createTime": "2025-11-28 12:00:00"
  }
}
```

**特殊说明**: 调用此接口会自动增加浏览次数（`viewCount`）

**测试命令（curl）**:
```bash
curl -X GET "http://localhost:8080/student/knowledge/point/1"
```

---

### 5. 获取热门知识点

**接口地址**: `GET /student/knowledge/point/hot`

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| limit | Integer | 否 | 数量限制，默认10 |

**排序规则**: 按浏览次数、点赞数降序

**成功响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "pointId": 1,
      "title": "Cache映射方式详解",
      "viewCount": 1280,
      "likeCount": 58,
      "subjectName": "计算机组成原理"
    }
  ]
}
```

**测试命令（curl）**:
```bash
# 获取前10个热门知识点
curl -X GET "http://localhost:8080/student/knowledge/point/hot"

# 获取前5个热门知识点
curl -X GET "http://localhost:8080/student/knowledge/point/hot?limit=5"
```

---

### 6. 获取最新知识点

**接口地址**: `GET /student/knowledge/point/latest`

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| limit | Integer | 否 | 数量限制，默认10 |

**排序规则**: 按创建时间降序

**测试命令（curl）**:
```bash
curl -X GET "http://localhost:8080/student/knowledge/point/latest?limit=10"
```

---

### 7. 获取推荐知识点

**接口地址**: `GET /student/knowledge/point/recommend`

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| limit | Integer | 否 | 数量限制，默认10 |

**筛选条件**: 只返回标记为"推荐"的知识点（`is_recommend = 1`）

**测试命令（curl）**:
```bash
curl -X GET "http://localhost:8080/student/knowledge/point/recommend?limit=10"
```

---

## 🧪 测试步骤

### 1. 启动项目

```bash
cd daming-admin
mvn clean package
java -jar ruoyi-admin/target/ruoyi-admin.jar
```

或者在IDE中直接运行 `RuoYiApplication.java`

### 2. 访问测试

**方式一：使用浏览器**
- 打开浏览器访问：`http://localhost:8080/student/knowledge/subjects`
- 应该能看到JSON格式的科目列表

**方式二：使用Postman**
1. 新建请求
2. 方法选择 `GET`
3. 输入URL：`http://localhost:8080/student/knowledge/subjects`
4. 点击 `Send`

**方式三：使用curl命令**
```bash
curl -X GET "http://localhost:8080/student/knowledge/subjects"
```

### 3. 验证数据

检查返回的数据是否包含之前SQL插入的测试数据：
- ✅ 5个科目（计算机组成原理、数据结构等）
- ✅ 12个章节
- ✅ 1个示例知识点（Cache映射方式详解）

---

## 📊 测试用例

### 测试用例1：查询科目列表

**预期结果**:
- 返回5个科目
- 按 `sortOrder` 排序
- 只返回 `status = 1` 的科目

**验证点**:
```bash
curl -X GET "http://localhost:8080/student/knowledge/subjects"
# 检查返回的data数组长度是否为5
```

### 测试用例2：查询章节树

**预期结果**:
- 返回树形结构
- 一级章节包含 `children` 数组
- 按 `sortOrder` 排序

**验证点**:
```bash
curl -X GET "http://localhost:8080/student/knowledge/chapters/1"
# 检查第一个章节是否有children字段
```

### 测试用例3：查询知识点列表（分页）

**预期结果**:
- 返回分页数据（`total` 和 `rows`）
- 只返回 `status = 1` 的知识点
- 包含科目名称和章节名称

**验证点**:
```bash
curl -X GET "http://localhost:8080/student/knowledge/points?pageNum=1&pageSize=10"
# 检查返回的total字段和rows数组
```

### 测试用例4：增加浏览次数

**预期结果**:
- 第一次访问知识点详情，`viewCount` 为 1
- 第二次访问，`viewCount` 为 2

**验证步骤**:
```bash
# 第一次访问
curl -X GET "http://localhost:8080/student/knowledge/point/1"
# 记录 viewCount

# 第二次访问
curl -X GET "http://localhost:8080/student/knowledge/point/1"
# viewCount 应该增加了1
```

---

## ⚠️ 常见问题

### 1. 启动失败：找不到Mapper

**错误信息**: `org.apache.ibatis.binding.BindingException: Invalid bound statement`

**解决方案**:
- 检查 `mapper/system` 目录下是否存在对应的XML文件
- 检查XML文件中的 `namespace` 是否正确
- 重新编译项目：`mvn clean package`

### 2. 返回空数据

**原因**: SQL脚本可能没有正确执行

**解决方案**:
```sql
-- 检查数据是否存在
SELECT * FROM knowledge_subject;
SELECT * FROM knowledge_chapter WHERE subject_id = 1;
SELECT * FROM knowledge_point;
```

如果数据不存在，重新执行SQL脚本：
```bash
mysql -u root -p ry-vue < sql/2025-11-28/1-知识点库系统表结构.sql
```

### 3. 404错误

**原因**: 接口路径错误或Controller未加载

**解决方案**:
- 检查Controller类上的 `@RestController` 和 `@RequestMapping` 注解
- 检查包扫描路径是否正确
- 查看启动日志，确认Controller是否被加载

### 4. 500错误

**原因**: 业务逻辑异常或数据库连接问题

**解决方案**:
- 查看后台日志获取详细错误信息
- 检查数据库连接配置
- 检查SQL语句是否正确

---

## 🎯 下一步

接口测试通过后，可以进行：

1. **前端集成**
   - 在前端项目中调用这些接口
   - 实现知识点列表页面
   - 实现知识点详情页面

2. **功能扩展**
   - 添加点赞功能
   - 添加收藏功能
   - 添加评论功能

3. **性能优化**
   - 添加Redis缓存
   - 优化SQL查询
   - 添加索引

---

## 📝 接口清单总结

| 序号 | 接口名称 | 接口地址 | 方法 | 说明 |
|------|---------|---------|------|------|
| 1 | 查询科目列表 | `/student/knowledge/subjects` | GET | 返回所有启用的科目 |
| 2 | 查询章节树 | `/student/knowledge/chapters/{subjectId}` | GET | 返回指定科目的章节树 |
| 3 | 查询知识点列表 | `/student/knowledge/points` | GET | 分页查询，支持筛选 |
| 4 | 获取知识点详情 | `/student/knowledge/point/{pointId}` | GET | 返回详细信息，增加浏览数 |
| 5 | 获取热门知识点 | `/student/knowledge/point/hot` | GET | 按浏览量排序 |
| 6 | 获取最新知识点 | `/student/knowledge/point/latest` | GET | 按时间排序 |
| 7 | 获取推荐知识点 | `/student/knowledge/point/recommend` | GET | 返回推荐的知识点 |

---

> **测试完成标准**:  
> ✅ 所有7个接口都能正常返回数据  
> ✅ 分页功能正常  
> ✅ 筛选功能正常  
> ✅ 浏览次数能正常增加
