-- 题目-知识点关联表
CREATE TABLE `question_knowledge_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `question_id` bigint(20) NOT NULL COMMENT '题目ID',
  `knowledge_point_id` bigint(20) NOT NULL COMMENT '知识点ID',
  `relation_type` tinyint(4) DEFAULT 1 COMMENT '关联类型：1-主要考点 2-次要考点',
  `reason` varchar(200) DEFAULT NULL COMMENT '关联原因',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_question_knowledge` (`question_id`, `knowledge_point_id`),
  KEY `idx_question_id` (`question_id`),
  KEY `idx_knowledge_id` (`knowledge_point_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目知识点关联表';
