-- =============================================
-- 用户公告阅读状态表结构
-- 创建日期: 2025-01-15
-- 功能说明: 记录用户对公告的阅读状态，用于判断未读公告
-- =============================================

-- ============================
-- 1. 创建用户公告阅读状态表
-- ============================
DROP TABLE IF EXISTS `daming_notice_read`;

CREATE TABLE `daming_notice_read` (
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

-- ============================
-- 2. 插入测试数据（可选）
-- ============================
-- INSERT INTO `daming_notice_read` (`user_id`, `notice_id`, `read_time`) VALUES
-- (1, 1, NOW()),
-- (1, 2, NOW());








