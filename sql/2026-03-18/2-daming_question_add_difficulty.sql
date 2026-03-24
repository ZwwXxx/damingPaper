-- 题目表增加“难度”字段
-- 说明：
-- - difficulty：1=简单，2=中等，3=困难

ALTER TABLE `daming_question`
  ADD COLUMN `difficulty` TINYINT NOT NULL DEFAULT 1 COMMENT '难度：1=简单，2=中等，3=困难' AFTER `score`;

-- 常用筛选索引：按科目+难度筛题/组卷
CREATE INDEX `idx_question_subject_difficulty` ON `daming_question` (`subject_id`, `difficulty`);

