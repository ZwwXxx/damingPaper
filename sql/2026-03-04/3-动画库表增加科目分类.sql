-- =============================================
-- 动画库表增加科目分类
-- 创建日期: 2026-03-04
-- 功能说明: 为动画库增加 subject_id，支持按科目分类/筛选
-- =============================================

ALTER TABLE `daming_animation`
    ADD COLUMN `subject_id` int(11) NULL DEFAULT NULL COMMENT '科目ID（用于分类）' AFTER `animation_id`,
    ADD INDEX `idx_subject_id` (`subject_id`) COMMENT '科目ID索引';

