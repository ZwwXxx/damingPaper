-- 验证热门试卷统计查询
-- 用于排查"暂无数据"问题

-- 1. 检查试卷数据
SELECT '=== 检查试卷表 ===' AS step;
SELECT paper_id, paper_name, del_flag, create_time 
FROM daming_paper 
ORDER BY paper_id 
LIMIT 10;

-- 2. 检查考试记录
SELECT '=== 检查考试记录表 ===' AS step;
SELECT paper_answer_id, paper_id, paper_score, final_score, create_time 
FROM daming_paper_answer 
ORDER BY create_time DESC 
LIMIT 10;

-- 3. 检查最近30天的数据
SELECT '=== 最近30天的考试记录 ===' AS step;
SELECT COUNT(*) AS total_count, MIN(create_time) AS earliest, MAX(create_time) AS latest
FROM daming_paper_answer 
WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 30 DAY);

-- 4. 按试卷统计（不限时间）
SELECT '=== 所有时间的试卷统计 ===' AS step;
SELECT
    p.paper_id AS paperId,
    p.paper_name AS paperName,
    p.del_flag,
    COUNT(pa.paper_answer_id) AS examCount,
    ROUND(AVG(pa.final_score * 100.0 / pa.paper_score), 2) AS avgScorePercent,
    ROUND(AVG(pa.final_score), 2) AS avgScore,
    MAX(pa.paper_score) AS totalScore
FROM daming_paper p
LEFT JOIN daming_paper_answer pa ON p.paper_id = pa.paper_id
WHERE p.del_flag = 0
GROUP BY p.paper_id, p.paper_name, p.del_flag
ORDER BY examCount DESC
LIMIT 5;

-- 5. 按试卷统计（最近30天）
SELECT '=== 最近30天的试卷统计 ===' AS step;
SELECT
    p.paper_id AS paperId,
    p.paper_name AS paperName,
    COUNT(pa.paper_answer_id) AS examCount,
    ROUND(AVG(pa.final_score * 100.0 / pa.paper_score), 2) AS avgScorePercent,
    ROUND(AVG(pa.final_score), 2) AS avgScore,
    MAX(pa.paper_score) AS totalScore
FROM daming_paper p
INNER JOIN daming_paper_answer pa ON p.paper_id = pa.paper_id
WHERE pa.create_time >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
  AND p.del_flag = 0
GROUP BY p.paper_id, p.paper_name
ORDER BY examCount DESC
LIMIT 5;

-- 6. 检查del_flag字段类型
SELECT '=== 检查del_flag字段 ===' AS step;
SELECT paper_id, paper_name, del_flag, TYPEOF(del_flag) AS del_flag_type
FROM daming_paper 
LIMIT 5;

-- 7. 更宽松的查询（去掉时间限制）
SELECT '=== 更宽松的查询（去掉30天限制）===' AS step;
SELECT
    p.paper_id AS paperId,
    p.paper_name AS paperName,
    COUNT(pa.paper_answer_id) AS examCount,
    ROUND(AVG(pa.final_score * 100.0 / NULLIF(pa.paper_score, 0)), 2) AS avgScorePercent,
    ROUND(AVG(pa.final_score), 2) AS avgScore,
    MAX(pa.paper_score) AS totalScore,
    MIN(pa.create_time) AS earliest_exam,
    MAX(pa.create_time) AS latest_exam
FROM daming_paper p
INNER JOIN daming_paper_answer pa ON p.paper_id = pa.paper_id
WHERE p.del_flag = 0 OR p.del_flag = '0'
GROUP BY p.paper_id, p.paper_name
ORDER BY examCount DESC
LIMIT 5;
