-- 论坛功能数据库表
-- 创建时间: 2025-11-23

-- ========================================
-- 1. 帖子表
-- ========================================
CREATE TABLE `forum_post` (
  `post_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '发帖人ID',
  `user_name` VARCHAR(64) NOT NULL COMMENT '发帖人用户名',
  `nick_name` VARCHAR(64) DEFAULT NULL COMMENT '发帖人昵称',
  `avatar` VARCHAR(500) DEFAULT NULL COMMENT '发帖人头像',
  `title` VARCHAR(200) NOT NULL COMMENT '帖子标题',
  `content` TEXT NOT NULL COMMENT '帖子内容',
  `images` JSON DEFAULT NULL COMMENT '图片列表（JSON数组）',
  `view_count` INT(11) DEFAULT 0 COMMENT '浏览次数',
  `like_count` INT(11) DEFAULT 0 COMMENT '点赞数',
  `comment_count` INT(11) DEFAULT 0 COMMENT '评论数',
  `is_top` TINYINT(1) DEFAULT 0 COMMENT '是否置顶（0否 1是）',
  `is_hot` TINYINT(1) DEFAULT 0 COMMENT '是否热门（0否 1是）',
  `status` TINYINT(1) DEFAULT 1 COMMENT '状态（0删除 1正常 2审核中）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`post_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_is_top` (`is_top`),
  KEY `idx_is_hot` (`is_hot`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛帖子表';

-- ========================================
-- 2. 评论表
-- ========================================
CREATE TABLE `forum_comment` (
  `comment_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `post_id` BIGINT(20) NOT NULL COMMENT '帖子ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '评论人ID',
  `user_name` VARCHAR(64) NOT NULL COMMENT '评论人用户名',
  `nick_name` VARCHAR(64) DEFAULT NULL COMMENT '评论人昵称',
  `avatar` VARCHAR(500) DEFAULT NULL COMMENT '评论人头像',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `parent_id` BIGINT(20) DEFAULT NULL COMMENT '父评论ID（回复评论时使用）',
  `reply_to_user_id` BIGINT(20) DEFAULT NULL COMMENT '被回复的用户ID',
  `reply_to_user_name` VARCHAR(64) DEFAULT NULL COMMENT '被回复的用户名',
  `like_count` INT(11) DEFAULT 0 COMMENT '点赞数',
  `status` TINYINT(1) DEFAULT 1 COMMENT '状态（0删除 1正常）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`comment_id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛评论表';

-- ========================================
-- 3. 点赞表
-- ========================================
CREATE TABLE `forum_like` (
  `like_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `target_id` BIGINT(20) NOT NULL COMMENT '目标ID（帖子ID或评论ID）',
  `target_type` TINYINT(1) NOT NULL COMMENT '目标类型（1帖子 2评论）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`like_id`),
  UNIQUE KEY `uk_user_target` (`user_id`, `target_id`, `target_type`),
  KEY `idx_target` (`target_id`, `target_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛点赞表';

-- ========================================
-- 初始化测试数据
-- ========================================
INSERT INTO `forum_post` (`user_id`, `user_name`, `nick_name`, `avatar`, `title`, `content`, `view_count`, `like_count`, `comment_count`, `is_top`, `is_hot`) VALUES
(1, 'admin', '管理员', 'https://cdn.zww0891.fun/northern-lights-6862969_1920.jpg', '欢迎来到刷题论坛！', '大家好，这里是刷题系统的论坛板块，欢迎大家交流学习心得、分享做题技巧！\n\n有任何问题都可以在这里发帖讨论哦～', 128, 23, 5, 1, 1),
(1, 'admin', '管理员', 'https://cdn.zww0891.fun/northern-lights-6862969_1920.jpg', '如何高效刷题？分享我的经验', '经过一段时间的刷题，我总结了一些经验：\n\n1. **制定计划**：每天固定时间刷题，养成习惯\n2. **循序渐进**：从简单到困难，逐步提升\n3. **总结归纳**：做完题要总结知识点\n4. **温故知新**：定期复习做过的题\n\n大家有什么好的方法也可以分享！', 86, 15, 8, 0, 1),
(1, 'admin', '管理员', 'https://cdn.zww0891.fun/northern-lights-6862969_1920.jpg', '计算机组成原理重点知识点整理', '最近在复习计组，整理了一些重点：\n\n## 1. 冯·诺依曼结构\n- 存储程序工作方式\n- 五大组成部分\n\n## 2. 存储系统\n- Cache映射方式\n- 虚拟存储\n\n希望对大家有帮助！', 52, 10, 3, 0, 0);
