ALTER TABLE `daming_question_answer`
    ADD COLUMN `review_status` TINYINT DEFAULT 0 COMMENT '批改状态 0-无需 1-待批改 2-已批改' AFTER `item_order`,
    ADD COLUMN `review_comment` VARCHAR(255) DEFAULT NULL COMMENT '批改备注' AFTER `review_status`;

ALTER TABLE `daming_paper_answer`
    ADD COLUMN `objective_score` INT DEFAULT 0 COMMENT '客观题得分' AFTER `final_score`,
    ADD COLUMN `subjective_score` INT DEFAULT 0 COMMENT '主观题得分' AFTER `objective_score`,
    ADD COLUMN `review_status` TINYINT DEFAULT 0 COMMENT '批改状态 0-无需 1-待批改 2-已批改' AFTER `subjective_score`,
    ADD COLUMN `review_user` VARCHAR(64) DEFAULT NULL COMMENT '批改老师' AFTER `review_status`,
    ADD COLUMN `review_time` DATETIME DEFAULT NULL COMMENT '批改时间' AFTER `review_user`,
    ADD COLUMN `review_remark` VARCHAR(255) DEFAULT NULL COMMENT '批改备注' AFTER `review_time`;

