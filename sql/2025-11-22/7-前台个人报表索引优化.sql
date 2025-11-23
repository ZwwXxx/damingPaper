-- 前台个人学习报表 - 数据库索引优化
-- 创建日期: 2025-11-22
-- 说明: 为个人报表查询添加必要的索引以提升性能

-- ============================
-- 1. daming_question_answer 表索引
-- ============================

-- 用户+时间索引（用于查询用户最近做题记录）
ALTER TABLE `daming_question_answer` 
ADD INDEX `idx_user_time` (`create_user`, `create_time`);

-- 用户+题目+是否正确索引（用于错题统计）
ALTER TABLE `daming_question_answer` 
ADD INDEX `idx_user_question_correct` (`create_user`, `question_id`, `is_correct`);

-- ============================
-- 2. daming_paper_answer 表索引
-- ============================

-- 用户+时间索引（用于查询用户考试记录）
ALTER TABLE `daming_paper_answer` 
ADD INDEX `idx_user_time` (`create_user`, `create_time`);

-- 试卷ID索引（用于关联查询）
ALTER TABLE `daming_paper_answer` 
ADD INDEX `idx_paper_id` (`paper_id`);

-- ============================
-- 3. daming_question_favorite 表索引
-- ============================

-- 注意：该表已有唯一索引 uk_user_question(create_user, question_id)
-- 和普通索引 idx_user_answer(create_user, paper_answer_id)
-- 无需额外添加索引

-- ============================
-- 4. daming_question 表索引优化
-- ============================

-- 如果已有科目索引则跳过，否则添加
-- ALTER TABLE `daming_question` 
-- ADD INDEX `idx_subject_id` (`subject_id`);

-- 题目类型索引（用于按题型统计）
ALTER TABLE `daming_question` 
ADD INDEX `idx_question_type` (`question_type`);

-- ============================
-- 5. daming_paper 表索引优化
-- ============================

-- 如果已有科目索引则跳过
-- ALTER TABLE `daming_paper` 
-- ADD INDEX `idx_subject_id` (`subject_id`);

-- 删除标记索引（用于过滤已删除数据）
ALTER TABLE `daming_paper` 
ADD INDEX `idx_del_flag` (`del_flag`);

-- ============================
-- 验证索引创建
-- ============================
SHOW INDEX FROM daming_question_answer;
SHOW INDEX FROM daming_paper_answer;
SHOW INDEX FROM daming_question_favorite;
SHOW INDEX FROM daming_question;
SHOW INDEX FROM daming_paper;
