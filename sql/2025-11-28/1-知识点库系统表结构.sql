-- ========================================
-- 知识点库系统 - 数据库表结构
-- 创建日期: 2025-11-28
-- 说明: 包含知识点管理、学习记录、互动功能等11张表
-- ========================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ========================================
-- 1. 知识点科目表
-- ========================================
DROP TABLE IF EXISTS `knowledge_subject`;
CREATE TABLE `knowledge_subject` (
  `subject_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '科目ID',
  `subject_name` VARCHAR(100) NOT NULL COMMENT '科目名称',
  `subject_code` VARCHAR(50) NOT NULL COMMENT '科目编码',
  `description` TEXT DEFAULT NULL COMMENT '科目描述',
  `icon` VARCHAR(255) DEFAULT NULL COMMENT '科目图标URL',
  `sort_order` INT(11) DEFAULT 0 COMMENT '排序（数字越小越靠前）',
  `status` TINYINT(1) DEFAULT 1 COMMENT '状态（0-停用 1-启用）',
  `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`subject_id`),
  UNIQUE KEY `uk_subject_code` (`subject_code`),
  KEY `idx_status_sort` (`status`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点科目表';

-- ========================================
-- 2. 知识点章节表
-- ========================================
DROP TABLE IF EXISTS `knowledge_chapter`;
CREATE TABLE `knowledge_chapter` (
  `chapter_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '章节ID',
  `subject_id` BIGINT(20) NOT NULL COMMENT '所属科目ID',
  `parent_id` BIGINT(20) DEFAULT 0 COMMENT '父章节ID（0表示一级章节）',
  `chapter_name` VARCHAR(200) NOT NULL COMMENT '章节名称',
  `chapter_code` VARCHAR(50) DEFAULT NULL COMMENT '章节编码',
  `description` TEXT DEFAULT NULL COMMENT '章节描述',
  `sort_order` INT(11) DEFAULT 0 COMMENT '排序',
  `level` INT(11) DEFAULT 1 COMMENT '层级（1-一级 2-二级 3-三级）',
  `status` TINYINT(1) DEFAULT 1 COMMENT '状态（0-停用 1-启用）',
  `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`chapter_id`),
  KEY `idx_subject_id` (`subject_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status_sort` (`status`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点章节表';

-- ========================================
-- 3. 知识点表（核心表）
-- ========================================
DROP TABLE IF EXISTS `knowledge_point`;
CREATE TABLE `knowledge_point` (
  `point_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '知识点ID',
  `subject_id` BIGINT(20) NOT NULL COMMENT '所属科目ID',
  `chapter_id` BIGINT(20) NOT NULL COMMENT '所属章节ID',
  `title` VARCHAR(200) NOT NULL COMMENT '知识点标题',
  `summary` VARCHAR(500) DEFAULT NULL COMMENT '摘要',
  `content` LONGTEXT NOT NULL COMMENT '知识点内容（Markdown格式）',
  `content_html` LONGTEXT DEFAULT NULL COMMENT 'HTML内容（前端渲染用）',
  `difficulty` TINYINT(1) DEFAULT 1 COMMENT '难度等级（1-简单 2-中等 3-困难）',
  `importance` TINYINT(1) DEFAULT 1 COMMENT '重要程度（1-了解 2-理解 3-掌握 4-精通）',
  `author_id` BIGINT(20) NOT NULL COMMENT '作者ID',
  `author_name` VARCHAR(64) NOT NULL COMMENT '作者名称',
  `view_count` INT(11) DEFAULT 0 COMMENT '浏览次数',
  `like_count` INT(11) DEFAULT 0 COMMENT '点赞数',
  `collect_count` INT(11) DEFAULT 0 COMMENT '收藏数',
  `comment_count` INT(11) DEFAULT 0 COMMENT '评论数',
  `learn_count` INT(11) DEFAULT 0 COMMENT '学习人数',
  `is_recommend` TINYINT(1) DEFAULT 0 COMMENT '是否推荐（0-否 1-是）',
  `is_top` TINYINT(1) DEFAULT 0 COMMENT '是否置顶（0-否 1-是）',
  `status` TINYINT(1) DEFAULT 0 COMMENT '状态（0-草稿 1-已发布 2-审核中 3-已下架）',
  `audit_status` TINYINT(1) DEFAULT 0 COMMENT '审核状态（0-待审核 1-通过 2-拒绝）',
  `audit_remark` VARCHAR(500) DEFAULT NULL COMMENT '审核备注',
  `version` INT(11) DEFAULT 1 COMMENT '版本号',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
  PRIMARY KEY (`point_id`),
  KEY `idx_subject_chapter` (`subject_id`, `chapter_id`),
  KEY `idx_author_id` (`author_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time` DESC),
  KEY `idx_like_count` (`like_count` DESC),
  KEY `idx_audit_status` (`audit_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点表';

-- ========================================
-- 4. 知识点标签表
-- ========================================
DROP TABLE IF EXISTS `knowledge_tag`;
CREATE TABLE `knowledge_tag` (
  `tag_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `tag_name` VARCHAR(50) NOT NULL COMMENT '标签名称',
  `tag_color` VARCHAR(20) DEFAULT '#409EFF' COMMENT '标签颜色',
  `use_count` INT(11) DEFAULT 0 COMMENT '使用次数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`tag_id`),
  UNIQUE KEY `uk_tag_name` (`tag_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点标签表';

-- ========================================
-- 5. 知识点标签关联表
-- ========================================
DROP TABLE IF EXISTS `knowledge_point_tag`;
CREATE TABLE `knowledge_point_tag` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `point_id` BIGINT(20) NOT NULL COMMENT '知识点ID',
  `tag_id` BIGINT(20) NOT NULL COMMENT '标签ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_point_tag` (`point_id`, `tag_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点标签关联表';

-- ========================================
-- 6. 知识点附件表
-- ========================================
DROP TABLE IF EXISTS `knowledge_attachment`;
CREATE TABLE `knowledge_attachment` (
  `attachment_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '附件ID',
  `point_id` BIGINT(20) NOT NULL COMMENT '知识点ID',
  `file_name` VARCHAR(255) NOT NULL COMMENT '文件名',
  `file_url` VARCHAR(500) NOT NULL COMMENT '文件URL',
  `file_type` VARCHAR(50) DEFAULT NULL COMMENT '文件类型（image/video/pdf等）',
  `file_size` BIGINT(20) DEFAULT 0 COMMENT '文件大小（字节）',
  `sort_order` INT(11) DEFAULT 0 COMMENT '排序',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`attachment_id`),
  KEY `idx_point_id` (`point_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点附件表';

-- ========================================
-- 7. 知识点与题目关联表
-- ========================================
DROP TABLE IF EXISTS `knowledge_question_relation`;
CREATE TABLE `knowledge_question_relation` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `point_id` BIGINT(20) NOT NULL COMMENT '知识点ID',
  `question_id` BIGINT(20) NOT NULL COMMENT '题目ID',
  `relevance` TINYINT(1) DEFAULT 2 COMMENT '相关度（1-弱相关 2-相关 3-强相关）',
  `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_point_question` (`point_id`, `question_id`),
  KEY `idx_point_id` (`point_id`),
  KEY `idx_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点与题目关联表';

-- ========================================
-- 8. 知识点收藏表
-- ========================================
DROP TABLE IF EXISTS `knowledge_collect`;
CREATE TABLE `knowledge_collect` (
  `collect_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `point_id` BIGINT(20) NOT NULL COMMENT '知识点ID',
  `folder_id` BIGINT(20) DEFAULT 0 COMMENT '收藏夹ID（0表示默认）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`collect_id`),
  UNIQUE KEY `uk_user_point` (`user_id`, `point_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_point_id` (`point_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点收藏表';

-- ========================================
-- 9. 知识点点赞表
-- ========================================
DROP TABLE IF EXISTS `knowledge_like`;
CREATE TABLE `knowledge_like` (
  `like_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `point_id` BIGINT(20) NOT NULL COMMENT '知识点ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`like_id`),
  UNIQUE KEY `uk_user_point` (`user_id`, `point_id`),
  KEY `idx_point_id` (`point_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点点赞表';

-- ========================================
-- 10. 知识点评论表
-- ========================================
DROP TABLE IF EXISTS `knowledge_comment`;
CREATE TABLE `knowledge_comment` (
  `comment_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `point_id` BIGINT(20) NOT NULL COMMENT '知识点ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '评论人ID',
  `user_name` VARCHAR(64) NOT NULL COMMENT '评论人用户名',
  `nick_name` VARCHAR(64) DEFAULT NULL COMMENT '评论人昵称',
  `avatar` VARCHAR(500) DEFAULT NULL COMMENT '评论人头像',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `parent_id` BIGINT(20) DEFAULT 0 COMMENT '父评论ID（0表示一级评论）',
  `reply_to_user_id` BIGINT(20) DEFAULT NULL COMMENT '被回复的用户ID',
  `reply_to_user_name` VARCHAR(64) DEFAULT NULL COMMENT '被回复的用户名',
  `like_count` INT(11) DEFAULT 0 COMMENT '点赞数',
  `status` TINYINT(1) DEFAULT 1 COMMENT '状态（0-删除 1-正常）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`comment_id`),
  KEY `idx_point_id` (`point_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_create_time` (`create_time` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点评论表';

-- ========================================
-- 11. 知识点学习记录表
-- ========================================
DROP TABLE IF EXISTS `knowledge_learn_record`;
CREATE TABLE `knowledge_learn_record` (
  `record_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `point_id` BIGINT(20) NOT NULL COMMENT '知识点ID',
  `learn_duration` INT(11) DEFAULT 0 COMMENT '学习时长（秒）',
  `learn_progress` INT(11) DEFAULT 0 COMMENT '学习进度（0-100）',
  `mastery_level` TINYINT(1) DEFAULT 0 COMMENT '掌握程度（0-未学 1-了解 2-理解 3-掌握 4-精通）',
  `last_learn_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '最后学习时间',
  `learn_count` INT(11) DEFAULT 1 COMMENT '学习次数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '首次学习时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`record_id`),
  UNIQUE KEY `uk_user_point` (`user_id`, `point_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_last_learn_time` (`last_learn_time` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点学习记录表';

-- ========================================
-- 12. 知识点版本历史表
-- ========================================
DROP TABLE IF EXISTS `knowledge_version`;
CREATE TABLE `knowledge_version` (
  `version_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '版本ID',
  `point_id` BIGINT(20) NOT NULL COMMENT '知识点ID',
  `version` INT(11) NOT NULL COMMENT '版本号',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `content` LONGTEXT NOT NULL COMMENT '内容',
  `edit_type` TINYINT(1) DEFAULT 2 COMMENT '编辑类型（1-创建 2-修改 3-删除）',
  `edit_by` VARCHAR(64) DEFAULT NULL COMMENT '编辑者',
  `edit_remark` VARCHAR(500) DEFAULT NULL COMMENT '编辑说明',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`version_id`),
  KEY `idx_point_version` (`point_id`, `version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点版本历史表';

-- ========================================
-- 初始化测试数据
-- ========================================

-- 插入科目数据
INSERT INTO `knowledge_subject` (`subject_name`, `subject_code`, `description`, `sort_order`, `status`, `create_by`) VALUES
('计算机组成原理', 'CO', '介绍计算机系统的组成、工作原理及性能评价', 1, 1, 'admin'),
('数据结构', 'DS', '研究数据的逻辑结构、存储结构及算法', 2, 1, 'admin'),
('操作系统', 'OS', '管理计算机硬件与软件资源的系统软件', 3, 1, 'admin'),
('计算机网络', 'CN', '介绍计算机网络的体系结构、协议及应用', 4, 1, 'admin'),
('数据库原理', 'DB', '数据库系统的概念、设计、管理与应用', 5, 1, 'admin');

-- 插入章节数据（以计算机组成原理为例）
INSERT INTO `knowledge_chapter` (`subject_id`, `parent_id`, `chapter_name`, `sort_order`, `level`, `status`) VALUES
(1, 0, '第1章 计算机系统概述', 1, 1, 1),
(1, 1, '1.1 计算机发展历程', 1, 2, 1),
(1, 1, '1.2 计算机系统层次结构', 2, 2, 1),
(1, 1, '1.3 计算机性能指标', 3, 2, 1),
(1, 0, '第2章 数据的表示和运算', 2, 1, 1),
(1, 5, '2.1 数制与编码', 1, 2, 1),
(1, 5, '2.2 定点数与浮点数', 2, 2, 1),
(1, 0, '第3章 存储系统', 3, 1, 1),
(1, 8, '3.1 存储器概述', 1, 2, 1),
(1, 8, '3.2 主存储器', 2, 2, 1),
(1, 8, '3.3 Cache高速缓存', 3, 2, 1),
(1, 8, '3.4 虚拟存储器', 4, 2, 1);

-- 插入标签数据
INSERT INTO `knowledge_tag` (`tag_name`, `tag_color`, `use_count`) VALUES
('重点', '#f56c6c', 0),
('难点', '#e6a23c', 0),
('高频考点', '#ff6600', 0),
('易错点', '#909399', 0),
('基础', '#67c23a', 0),
('进阶', '#409eff', 0);

-- 插入示例知识点（供测试使用）
INSERT INTO `knowledge_point` (
  `subject_id`, `chapter_id`, `title`, `summary`, `content`, 
  `difficulty`, `importance`, `author_id`, `author_name`, 
  `status`, `audit_status`, `publish_time`
) VALUES
(1, 11, 'Cache映射方式详解', 
 '介绍直接映射、全相联映射、组相联映射三种Cache映射方式的原理、优缺点和应用场景', 
 '## 一、Cache映射概述\n\nCache映射是指主存地址与Cache地址之间的对应关系。主要有三种映射方式：\n\n### 1. 直接映射（Direct Mapping）\n\n**原理**：主存的每个块只能映射到Cache的固定位置。\n\n**映射规则**：\n```\nCache块号 = 主存块号 % Cache块数\n```\n\n**优点**：\n- 实现简单\n- 成本低\n- 查找速度快\n\n**缺点**：\n- 冲突率高\n- 灵活性差\n\n### 2. 全相联映射（Fully Associative Mapping）\n\n**原理**：主存的任何一块都可以映射到Cache的任意位置。\n\n**优点**：\n- 冲突率最低\n- Cache利用率高\n\n**缺点**：\n- 硬件成本高\n- 查找速度慢（需要全部比较）\n\n### 3. 组相联映射（Set Associative Mapping）\n\n**原理**：将Cache分成若干组，主存块可以映射到某一组的任意位置。\n\n**映射规则**：\n```\n组号 = 主存块号 % 组数\n```\n\n**特点**：\n- 是直接映射和全相联映射的折中方案\n- 常见的有2路组相联、4路组相联、8路组相联\n\n## 二、三种映射方式对比\n\n| 映射方式 | 灵活性 | 命中率 | 硬件成本 | 查找速度 |\n|---------|--------|--------|----------|----------|\n| 直接映射 | 低 | 低 | 低 | 快 |\n| 全相联映射 | 高 | 高 | 高 | 慢 |\n| 组相联映射 | 中 | 中 | 中 | 中 |\n\n## 三、实际应用\n\n现代CPU的Cache通常采用**组相联映射**方式，权衡了性能和成本。例如：\n- Intel Core i7：L1 Cache采用8路组相联\n- ARM Cortex-A系列：L2 Cache采用16路组相联\n\n## 四、典型例题\n\n**例题1**：某计算机主存容量为1MB，Cache容量为16KB，块大小为64B，采用4路组相联映射。求：\n1. 主存块数 = ?\n2. Cache块数 = ?\n3. 组数 = ?\n\n**解答**：\n1. 主存块数 = 1MB / 64B = 16K块\n2. Cache块数 = 16KB / 64B = 256块\n3. 组数 = 256块 / 4路 = 64组',
 2, 3, 1, 'admin', 1, 1, NOW()
);

-- 获取刚插入的知识点ID（假设为1）
SET @point_id = LAST_INSERT_ID();

-- 为知识点添加标签
INSERT INTO `knowledge_point_tag` (`point_id`, `tag_id`) VALUES
(@point_id, 1),  -- 重点
(@point_id, 3);  -- 高频考点

-- ========================================
-- 菜单权限配置（后台管理菜单）
-- ========================================

-- 一级菜单：知识点库管理
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) 
VALUES ('知识点库', 0, 6, 'knowledge', NULL, 1, 0, 'M', '0', '0', NULL, 'education', 'admin', NOW(), NULL, NULL, '知识点库管理菜单');

SET @menu_id_knowledge = LAST_INSERT_ID();

-- 二级菜单：科目管理
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`) 
VALUES ('科目管理', @menu_id_knowledge, 1, 'subject', 'knowledge/subject/index', 1, 0, 'C', '0', '0', 'knowledge:subject:list', 'tree-table', 'admin', NOW());

SET @menu_id_subject = LAST_INSERT_ID();

-- 科目管理按钮权限
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`) VALUES
('科目查询', @menu_id_subject, 1, '', NULL, 1, 0, 'F', '0', '0', 'knowledge:subject:query', '#', 'admin', NOW()),
('科目新增', @menu_id_subject, 2, '', NULL, 1, 0, 'F', '0', '0', 'knowledge:subject:add', '#', 'admin', NOW()),
('科目修改', @menu_id_subject, 3, '', NULL, 1, 0, 'F', '0', '0', 'knowledge:subject:edit', '#', 'admin', NOW()),
('科目删除', @menu_id_subject, 4, '', NULL, 1, 0, 'F', '0', '0', 'knowledge:subject:remove', '#', 'admin', NOW());

-- 二级菜单：章节管理
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`) 
VALUES ('章节管理', @menu_id_knowledge, 2, 'chapter', 'knowledge/chapter/index', 1, 0, 'C', '0', '0', 'knowledge:chapter:list', 'tree', 'admin', NOW());

SET @menu_id_chapter = LAST_INSERT_ID();

-- 章节管理按钮权限
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`) VALUES
('章节查询', @menu_id_chapter, 1, '', NULL, 1, 0, 'F', '0', '0', 'knowledge:chapter:query', '#', 'admin', NOW()),
('章节新增', @menu_id_chapter, 2, '', NULL, 1, 0, 'F', '0', '0', 'knowledge:chapter:add', '#', 'admin', NOW()),
('章节修改', @menu_id_chapter, 3, '', NULL, 1, 0, 'F', '0', '0', 'knowledge:chapter:edit', '#', 'admin', NOW()),
('章节删除', @menu_id_chapter, 4, '', NULL, 1, 0, 'F', '0', '0', 'knowledge:chapter:remove', '#', 'admin', NOW());

-- 二级菜单：知识点管理
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`) 
VALUES ('知识点管理', @menu_id_knowledge, 3, 'point', 'knowledge/point/index', 1, 0, 'C', '0', '0', 'knowledge:point:list', 'documentation', 'admin', NOW());

SET @menu_id_point = LAST_INSERT_ID();

-- 知识点管理按钮权限
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`) VALUES
('知识点查询', @menu_id_point, 1, '', NULL, 1, 0, 'F', '0', '0', 'knowledge:point:query', '#', 'admin', NOW()),
('知识点详细', @menu_id_point, 2, '', NULL, 1, 0, 'F', '0', '0', 'knowledge:point:detail', '#', 'admin', NOW()),
('知识点新增', @menu_id_point, 3, '', NULL, 1, 0, 'F', '0', '0', 'knowledge:point:add', '#', 'admin', NOW()),
('知识点修改', @menu_id_point, 4, '', NULL, 1, 0, 'F', '0', '0', 'knowledge:point:edit', '#', 'admin', NOW()),
('知识点删除', @menu_id_point, 5, '', NULL, 1, 0, 'F', '0', '0', 'knowledge:point:remove', '#', 'admin', NOW()),
('知识点审核', @menu_id_point, 6, '', NULL, 1, 0, 'F', '0', '0', 'knowledge:point:audit', '#', 'admin', NOW());

-- 二级菜单：标签管理
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`) 
VALUES ('标签管理', @menu_id_knowledge, 4, 'tag', 'knowledge/tag/index', 1, 0, 'C', '0', '0', 'knowledge:tag:list', 'tag', 'admin', NOW());

SET @menu_id_tag = LAST_INSERT_ID();

-- 标签管理按钮权限
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`) VALUES
('标签查询', @menu_id_tag, 1, '', NULL, 1, 0, 'F', '0', '0', 'knowledge:tag:query', '#', 'admin', NOW()),
('标签新增', @menu_id_tag, 2, '', NULL, 1, 0, 'F', '0', '0', 'knowledge:tag:add', '#', 'admin', NOW()),
('标签修改', @menu_id_tag, 3, '', NULL, 1, 0, 'F', '0', '0', 'knowledge:tag:edit', '#', 'admin', NOW()),
('标签删除', @menu_id_tag, 4, '', NULL, 1, 0, 'F', '0', '0', 'knowledge:tag:remove', '#', 'admin', NOW());

SET FOREIGN_KEY_CHECKS = 1;

-- ========================================
-- 执行完毕
-- ========================================
-- 说明：
-- 1. 已创建12张表（科目、章节、知识点、标签等）
-- 2. 已插入测试数据（5个科目、12个章节、6个标签、1个示例知识点）
-- 3. 已配置后台管理菜单权限
-- 4. 可直接在现有系统中执行此SQL
-- ========================================
