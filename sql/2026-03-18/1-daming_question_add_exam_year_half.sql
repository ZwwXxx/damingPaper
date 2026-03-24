-- 题目表增加“题目年份 + 考试批次（上/下半年，可为空）”
-- 说明：
-- - exam_year：题目所属年份（用于按年份筛题/组卷）
-- - exam_half：考试批次，1=上半年，2=下半年；为空表示该年份仅考一次

ALTER TABLE `daming_question`
  ADD COLUMN `exam_year` INT NULL DEFAULT NULL COMMENT '题目年份（用于按年份筛题/组卷）' AFTER `subject_id`,
  ADD COLUMN `exam_half` TINYINT NULL DEFAULT NULL COMMENT '考试批次：1=上半年，2=下半年；为空表示该年份仅考一次' AFTER `exam_year`;

-- 常用筛选索引：按年份/批次筛题
CREATE INDEX `idx_exam_year_half` ON `daming_question` (`exam_year`, `exam_half`);

