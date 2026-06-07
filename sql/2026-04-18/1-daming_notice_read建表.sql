-- =============================================
-- 用户公告阅读状态表（若依库 ry-vue）
-- 说明：解决 NoticeController.getLatest 等接口访问 daming_notice_read 表不存在的问题
-- 历史脚本：sql/2025-01-15/1-用户公告阅读状态表.sql（若已执行可忽略本文件）
-- =============================================

CREATE TABLE IF NOT EXISTS `daming_notice_read` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `notice_id` bigint(20) NOT NULL COMMENT '公告ID',
  `read_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_notice` (`user_id`, `notice_id`) COMMENT '用户-公告唯一索引',
  KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引',
  KEY `idx_notice_id` (`notice_id`) COMMENT '公告ID索引',
  KEY `idx_read_time` (`read_time`) COMMENT '阅读时间索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户公告阅读状态表';
