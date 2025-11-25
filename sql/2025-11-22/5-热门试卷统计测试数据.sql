-- 热门试卷考试统计 - 测试数据生成脚本
-- 创建日期: 2025-11-22
-- 说明: 用于测试"热门试卷考试统计"功能的示例数据

-- ============================
-- 1. 检查现有数据
-- ============================
SELECT '=== 当前试卷数量 ===' AS info;
SELECT COUNT(*) AS paper_count FROM daming_paper WHERE del_flag = 0;

SELECT '=== 当前考试记录数量 ===' AS info;
SELECT COUNT(*) AS exam_count FROM daming_paper_answer;

SELECT '=== 最近30天考试记录 ===' AS info;
SELECT COUNT(*) AS recent_exam_count 
FROM daming_paper_answer 
WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 30 DAY);

-- ============================
-- 2. 插入测试试卷（如果数据库为空）
-- ============================
-- 注意：如果已有试卷数据，可以跳过这一步

-- INSERT INTO daming_paper (paper_name, subject_id, score, paper_info_id, question_count, suggest_time, del_flag, create_time, update_time, paper_type) 
-- VALUES 
-- ('计算机组成原理期中考试', 1, 100, 1, 20, 90, 0, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), 1),
-- ('软件工程期末考试', 1, 150, 2, 30, 120, 0, DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY), 1),
-- ('数据结构基础测试', 1, 80, 3, 15, 60, 0, DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY), 1),
-- ('操作系统原理考试', 1, 120, 4, 25, 100, 0, DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY), 1),
-- ('数据库系统概论测试', 1, 100, 5, 20, 90, 0, DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY), 1);

-- ============================
-- 3. 为现有试卷生成测试考试记录
-- ============================
-- 假设数据库中已有试卷，为每个试卷生成一些考试记录

-- 获取现有试卷
SET @paper_id_1 = (SELECT paper_id FROM daming_paper WHERE del_flag = 0 ORDER BY paper_id LIMIT 1);
SET @paper_id_2 = (SELECT paper_id FROM daming_paper WHERE del_flag = 0 ORDER BY paper_id LIMIT 1 OFFSET 1);
SET @paper_id_3 = (SELECT paper_id FROM daming_paper WHERE del_flag = 0 ORDER BY paper_id LIMIT 1 OFFSET 2);
SET @paper_id_4 = (SELECT paper_id FROM daming_paper WHERE del_flag = 0 ORDER BY paper_id LIMIT 1 OFFSET 3);
SET @paper_id_5 = (SELECT paper_id FROM daming_paper WHERE del_flag = 0 ORDER BY paper_id LIMIT 1 OFFSET 4);

-- 显示选中的试卷ID
SELECT '=== 选中的试卷ID ===' AS info;
SELECT @paper_id_1 AS paper1, @paper_id_2 AS paper2, @paper_id_3 AS paper3, @paper_id_4 AS paper4, @paper_id_5 AS paper5;

-- 为试卷1生成20条考试记录（高分段）
INSERT INTO daming_paper_answer (paper_id, paper_score, final_score, question_count, do_time, review_status, create_time, update_time, create_by)
SELECT 
    @paper_id_1,
    (SELECT score FROM daming_paper WHERE paper_id = @paper_id_1),
    FLOOR((SELECT score FROM daming_paper WHERE paper_id = @paper_id_1) * (0.75 + RAND() * 0.20)), -- 75%-95%
    (SELECT question_count FROM daming_paper WHERE paper_id = @paper_id_1),
    FLOOR(60 + RAND() * 30),
    2,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY),
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY),
    'test_user'
FROM 
    (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION 
     SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION
     SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION
     SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20) AS numbers
WHERE @paper_id_1 IS NOT NULL;

-- 为试卷2生成15条考试记录（中分段）
INSERT INTO daming_paper_answer (paper_id, paper_score, final_score, question_count, do_time, review_status, create_time, update_time, create_by)
SELECT 
    @paper_id_2,
    (SELECT score FROM daming_paper WHERE paper_id = @paper_id_2),
    FLOOR((SELECT score FROM daming_paper WHERE paper_id = @paper_id_2) * (0.60 + RAND() * 0.25)), -- 60%-85%
    (SELECT question_count FROM daming_paper WHERE paper_id = @paper_id_2),
    FLOOR(60 + RAND() * 30),
    2,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY),
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY),
    'test_user'
FROM 
    (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION 
     SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION
     SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15) AS numbers
WHERE @paper_id_2 IS NOT NULL;

-- 为试卷3生成12条考试记录
INSERT INTO daming_paper_answer (paper_id, paper_score, final_score, question_count, do_time, review_status, create_time, update_time, create_by)
SELECT 
    @paper_id_3,
    (SELECT score FROM daming_paper WHERE paper_id = @paper_id_3),
    FLOOR((SELECT score FROM daming_paper WHERE paper_id = @paper_id_3) * (0.65 + RAND() * 0.25)), -- 65%-90%
    (SELECT question_count FROM daming_paper WHERE paper_id = @paper_id_3),
    FLOOR(60 + RAND() * 30),
    2,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY),
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY),
    'test_user'
FROM 
    (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION 
     SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION
     SELECT 11 UNION SELECT 12) AS numbers
WHERE @paper_id_3 IS NOT NULL;

-- 为试卷4生成8条考试记录
INSERT INTO daming_paper_answer (paper_id, paper_score, final_score, question_count, do_time, review_status, create_time, update_time, create_by)
SELECT 
    @paper_id_4,
    (SELECT score FROM daming_paper WHERE paper_id = @paper_id_4),
    FLOOR((SELECT score FROM daming_paper WHERE paper_id = @paper_id_4) * (0.70 + RAND() * 0.20)), -- 70%-90%
    (SELECT question_count FROM daming_paper WHERE paper_id = @paper_id_4),
    FLOOR(60 + RAND() * 30),
    2,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY),
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY),
    'test_user'
FROM 
    (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION 
     SELECT 6 UNION SELECT 7 UNION SELECT 8) AS numbers
WHERE @paper_id_4 IS NOT NULL;

-- 为试卷5生成5条考试记录
INSERT INTO daming_paper_answer (paper_id, paper_score, final_score, question_count, do_time, review_status, create_time, update_time, create_by)
SELECT 
    @paper_id_5,
    (SELECT score FROM daming_paper WHERE paper_id = @paper_id_5),
    FLOOR((SELECT score FROM daming_paper WHERE paper_id = @paper_id_5) * (0.55 + RAND() * 0.30)), -- 55%-85%
    (SELECT question_count FROM daming_paper WHERE paper_id = @paper_id_5),
    FLOOR(60 + RAND() * 30),
    2,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY),
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY),
    'test_user'
FROM 
    (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) AS numbers
WHERE @paper_id_5 IS NOT NULL;

-- ============================
-- 4. 验证插入结果
-- ============================
SELECT '=== 测试数据插入完成 ===' AS info;

SELECT '=== 最新插入的考试记录数量 ===' AS info;
SELECT COUNT(*) AS new_records 
FROM daming_paper_answer 
WHERE create_by = 'test_user';

SELECT '=== 按试卷统计考试人次 ===' AS info;
SELECT 
    p.paper_id,
    p.paper_name,
    COUNT(pa.paper_answer_id) AS exam_count,
    ROUND(AVG(pa.final_score), 2) AS avg_score,
    p.score AS total_score,
    ROUND(AVG(pa.final_score * 100.0 / pa.paper_score), 2) AS avg_percent
FROM daming_paper p
LEFT JOIN daming_paper_answer pa ON p.paper_id = pa.paper_id AND pa.create_time >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
WHERE p.del_flag = 0
GROUP BY p.paper_id, p.paper_name, p.score
ORDER BY exam_count DESC
LIMIT 10;

-- ============================
-- 5. 验证接口SQL
-- ============================
SELECT '=== 验证接口查询SQL ===' AS info;
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

-- ============================
-- 清理测试数据（可选）
-- ============================
-- 如果需要清理测试数据，取消注释以下语句
-- DELETE FROM daming_paper_answer WHERE create_by = 'test_user';
