-- 添加试卷开始时间和截止时间字段
-- 2025-11-22

ALTER TABLE `daming_paper`
ADD COLUMN `start_time` datetime NULL DEFAULT NULL COMMENT '考试开始时间' AFTER `enable_anti_cheat`,
ADD COLUMN `end_time` datetime NULL DEFAULT NULL COMMENT '考试截止时间' AFTER `start_time`;