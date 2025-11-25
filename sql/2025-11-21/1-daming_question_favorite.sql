CREATE TABLE IF NOT EXISTS `daming_question_favorite` (
    `favorite_id`      BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `question_id`      BIGINT       NOT NULL COMMENT '题目ID',
    `paper_id`         BIGINT       DEFAULT NULL COMMENT '试卷ID',
    `paper_answer_id`  BIGINT       NOT NULL COMMENT '考试记录ID',
    `subject_id`       INT          DEFAULT NULL COMMENT '科目ID',
    `create_user`      VARCHAR(64)  NOT NULL COMMENT '收藏人账号',
    `remark`           VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `create_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`favorite_id`),
    UNIQUE KEY `uk_user_question` (`create_user`, `question_id`),
    KEY `idx_user_answer` (`create_user`, `paper_answer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目收藏表';


