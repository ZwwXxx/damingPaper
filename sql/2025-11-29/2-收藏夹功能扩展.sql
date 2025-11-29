-- ========================================
-- 收藏夹功能扩展SQL
-- 创建时间：2025-11-29
-- 说明：添加收藏夹管理功能，支持分类收藏知识点
-- ========================================

-- ========================================
-- 1. 创建收藏夹表
-- ========================================
DROP TABLE IF EXISTS `knowledge_folder`;
CREATE TABLE `knowledge_folder` (
  `folder_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '收藏夹ID',
  `folder_name` VARCHAR(100) NOT NULL COMMENT '收藏夹名称',
  `description` TEXT COMMENT '收藏夹描述',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `is_default` TINYINT(1) DEFAULT 0 COMMENT '是否默认收藏夹(1是0否)',
  `is_public` TINYINT(1) DEFAULT 0 COMMENT '是否公开(1是0否)',
  `collect_count` INT(11) DEFAULT 0 COMMENT '收藏数量',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`folder_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_user_default` (`user_id`, `is_default`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点收藏夹表';

-- ========================================
-- 2. 修改收藏表，添加folder_id字段
-- ========================================
ALTER TABLE `knowledge_collect` ADD COLUMN `folder_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '收藏夹ID' AFTER `point_id`;
ALTER TABLE `knowledge_collect` ADD KEY `idx_folder_id` (`folder_id`);

-- ========================================
-- 3. 初始化默认收藏夹数据
-- ========================================
-- 为每个用户创建默认收藏夹
INSERT INTO `knowledge_folder` (`folder_name`, `description`, `user_id`, `is_default`, `is_public`, `collect_count`) VALUES
('默认收藏夹', '系统自动创建的默认收藏夹', 1, 1, 0, 0),
('前端学习', '前端相关知识点收藏', 1, 0, 1, 0),
('算法练习', '数据结构与算法相关', 1, 0, 0, 0);

-- 获取默认收藏夹ID
SET @default_folder_id = (SELECT folder_id FROM knowledge_folder WHERE user_id = 1 AND is_default = 1);

-- 将现有收藏记录关联到默认收藏夹
UPDATE `knowledge_collect` SET `folder_id` = @default_folder_id WHERE `user_id` = 1;

-- 更新默认收藏夹的收藏数量
UPDATE `knowledge_folder` SET `collect_count` = (
    SELECT COUNT(*) FROM `knowledge_collect` WHERE `folder_id` = @default_folder_id
) WHERE `folder_id` = @default_folder_id;
