-- 检查当前有哪些用户的数据

-- 1. 查看 daming_question_answer 表中有哪些用户
SELECT DISTINCT create_user, COUNT(*) as count
FROM daming_question_answer
GROUP BY create_user
ORDER BY count DESC
LIMIT 10;

-- 2. 查看 daming_paper_answer 表中有哪些用户
SELECT DISTINCT create_user, COUNT(*) as count
FROM daming_paper_answer
GROUP BY create_user
ORDER BY count DESC
LIMIT 10;

-- 3. 查看 daming_question_favorite 表中有哪些用户
SELECT DISTINCT create_user, COUNT(*) as count
FROM daming_question_favorite
GROUP BY create_user
ORDER BY count DESC
LIMIT 10;

-- 4. 查看 sys_user 表中的用户信息（对比用户名和ID）
SELECT user_id, user_name, nick_name
FROM sys_user
ORDER BY user_id
LIMIT 10;

-- 5. 检查create_user字段存的是用户ID还是用户名
-- 查看一条记录
SELECT create_user, question_id, create_time
FROM daming_question_answer
LIMIT 1;

SELECT create_user, paper_id, create_time
FROM daming_paper_answer
LIMIT 1;
