-- 试卷表增加题号规则字段
-- 1 = 按题型分组编号
-- 2 = 按加入顺序全局编号（默认）

ALTER TABLE `daming_paper`
    ADD COLUMN `number_mode` tinyint(4) NULL DEFAULT 2 COMMENT '题号规则：1按题型分组 2按加入顺序全局编号' AFTER `end_time`;

