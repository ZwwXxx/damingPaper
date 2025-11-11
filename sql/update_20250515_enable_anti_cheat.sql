ALTER TABLE `daming_paper`
    ADD COLUMN `enable_anti_cheat` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否开启防作弊' AFTER `paper_type`;

UPDATE `daming_paper`
SET enable_anti_cheat = IFNULL(enable_anti_cheat, 0);
