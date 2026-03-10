-- =============================================
-- 完形填空支持：题目表增加父题ID + 空索引字段
-- 创建日期: 2026-03-09
-- 功能说明:
-- 1) daming_question 增加 parent_id（父题ID，用于完形/阅读等复合题）
-- 2) daming_question 增加 cloze_index（完形第几空，从1开始）
-- =============================================

-- ============================
-- 1. 题目表增加 parent_id、cloze_index
-- ============================
ALTER TABLE `daming_question`
    ADD COLUMN `parent_id` bigint(20) NULL DEFAULT NULL COMMENT '父题ID（完形/阅读等复合题）' AFTER `id`,
    ADD COLUMN `cloze_index` int(11) NULL DEFAULT NULL COMMENT '完形第几空索引（从1开始）' AFTER `question_type`,
    ADD INDEX `idx_parent_id` (`parent_id`) COMMENT '父题ID索引',
    ADD INDEX `idx_cloze_index` (`cloze_index`) COMMENT '完形空索引';

