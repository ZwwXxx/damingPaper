-- 题目表增加“原卷题号”字段（用于按原卷顺序组卷）
-- 说明：
-- - origin_order：原卷题号（对于“父题不编号、从首子题开始编号”的复合题，可填写首子题题号）
-- - 允许为空：不填则按默认逻辑/手动排序

ALTER TABLE `daming_question`
  ADD COLUMN `origin_order` INT NULL DEFAULT NULL COMMENT '原卷题号（复合题填首子题题号）' AFTER `cloze_index`;

