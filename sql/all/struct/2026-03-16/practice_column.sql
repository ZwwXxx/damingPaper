/*
  专项刷题栏（栏目）表结构
  - daming_practice_column：栏目定义（是否参与专项刷题筛选）
  - daming_practice_column_question：栏目与题目关联
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `daming_practice_column_question`;
DROP TABLE IF EXISTS `daming_practice_column`;

CREATE TABLE `daming_practice_column` (
  `column_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '栏目ID',
  `column_name` varchar(100) NOT NULL COMMENT '栏目名称',
  `subject_id` int(11) NOT NULL COMMENT '科目ID',
  `group_name` varchar(100) DEFAULT NULL COMMENT '一级分组名称（章节/大类）',
  `group_sort` int(11) NOT NULL DEFAULT 0 COMMENT '一级分组排序（越大越靠前）',
  `description` varchar(500) DEFAULT NULL COMMENT '栏目描述',
  `sort_order` int(11) NOT NULL DEFAULT 0 COMMENT '排序（越大越靠前）',
  `enable_practice` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否参与专项刷题筛选（1是0否）',
  `paper_id` bigint(20) DEFAULT NULL COMMENT '关联试卷ID（首次生成后复用）',
  `create_user` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(64) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`column_id`) USING BTREE,
  KEY `idx_subject_enable_sort` (`subject_id`, `enable_practice`, `sort_order`) USING BTREE,
  KEY `idx_enable_sort` (`enable_practice`, `sort_order`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专项刷题栏目表';

CREATE TABLE `daming_practice_column_question` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `column_id` bigint(20) NOT NULL COMMENT '栏目ID',
  `question_id` bigint(20) NOT NULL COMMENT '题目ID',
  `sort_order` int(11) NOT NULL DEFAULT 0 COMMENT '题目在栏目内排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_column_question` (`column_id`, `question_id`) USING BTREE,
  KEY `idx_column_id` (`column_id`) USING BTREE,
  KEY `idx_question_id` (`question_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专项刷题栏目-题目关联表';

SET FOREIGN_KEY_CHECKS = 1;

