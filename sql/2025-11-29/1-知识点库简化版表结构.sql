-- ========================================
-- 知识点库系统 - 简化版数据库表结构
-- 创建日期: 2025-11-29
-- 说明: 简化设计，去除章节概念，采用科目+标签分类
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
  `description` TEXT DEFAULT NULL COMMENT '科目描述',
  `icon` VARCHAR(255) DEFAULT NULL COMMENT '科目图标URL',
  `sort_order` INT(11) DEFAULT 0 COMMENT '排序（数字越小越靠前）',
  `status` TINYINT(1) DEFAULT 1 COMMENT '状态（0-停用 1-启用）',
  `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`subject_id`),
  KEY `idx_status_sort` (`status`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点科目表';

-- ========================================
-- 2. 知识点表（核心表）
-- ========================================
DROP TABLE IF EXISTS `knowledge_point`;
CREATE TABLE `knowledge_point` (
  `point_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '知识点ID',
  `subject_id` BIGINT(20) NOT NULL COMMENT '所属科目ID',
  `title` VARCHAR(200) NOT NULL COMMENT '知识点标题',
  `summary` VARCHAR(500) DEFAULT NULL COMMENT '摘要',
  `content` LONGTEXT NOT NULL COMMENT '知识点内容（Markdown格式）',
  `content_html` LONGTEXT DEFAULT NULL COMMENT 'HTML内容（前端渲染用）',
  `difficulty` TINYINT(1) DEFAULT 1 COMMENT '难度等级（1-简单 2-中等 3-困难）',
  `author_id` BIGINT(20) NOT NULL COMMENT '作者ID',
  `author_name` VARCHAR(64) NOT NULL COMMENT '作者名称',
  `view_count` INT(11) DEFAULT 0 COMMENT '浏览次数',
  `like_count` INT(11) DEFAULT 0 COMMENT '点赞数',
  `collect_count` INT(11) DEFAULT 0 COMMENT '收藏数',
  `comment_count` INT(11) DEFAULT 0 COMMENT '评论数',
  `is_recommend` TINYINT(1) DEFAULT 0 COMMENT '是否推荐（0-否 1-是）',
  `is_top` TINYINT(1) DEFAULT 0 COMMENT '是否置顶（0-否 1-是）',
  `status` TINYINT(1) DEFAULT 1 COMMENT '状态（0-草稿 1-已发布 2-已下架）',
  `audit_status` TINYINT(1) DEFAULT 0 COMMENT '审核状态（0-待审核 1-通过 2-拒绝）',
  `audit_remark` VARCHAR(500) DEFAULT NULL COMMENT '审核备注',
  `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
  PRIMARY KEY (`point_id`),
  KEY `idx_subject_id` (`subject_id`),
  KEY `idx_author_id` (`author_id`),
  KEY `idx_status` (`status`),
  KEY `idx_difficulty` (`difficulty`),
  KEY `idx_create_time` (`create_time` DESC),
  KEY `idx_like_count` (`like_count` DESC),
  KEY `idx_audit_status` (`audit_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点表';

-- ========================================
-- 3. 知识点标签表
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
-- 4. 知识点标签关联表
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
-- 5. 知识点收藏表
-- ========================================
DROP TABLE IF EXISTS `knowledge_collect`;
CREATE TABLE `knowledge_collect` (
  `collect_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `point_id` BIGINT(20) NOT NULL COMMENT '知识点ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`collect_id`),
  UNIQUE KEY `uk_user_point` (`user_id`, `point_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_point_id` (`point_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点收藏表';

-- ========================================
-- 6. 知识点点赞表
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
-- 7. 知识点评论表
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
  `reply_to_nick_name` VARCHAR(64) DEFAULT NULL COMMENT '被回复的昵称',
  `reply_to_comment_id` BIGINT(20) DEFAULT NULL COMMENT '回复目标评论ID',
  `like_count` BIGINT(20) DEFAULT 0 COMMENT '点赞数',
  `status` TINYINT(1) DEFAULT 1 COMMENT '状态（0-删除 1-正常）',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`comment_id`),
  KEY `idx_point_id` (`point_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_create_time` (`create_time` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点评论表';

-- ========================================
-- 8. 知识点评论点赞表
-- ========================================
DROP TABLE IF EXISTS `knowledge_comment_like`;
CREATE TABLE `knowledge_comment_like` (
  `like_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `comment_id` BIGINT(20) NOT NULL COMMENT '评论ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`like_id`),
  UNIQUE KEY `uk_user_comment` (`user_id`, `comment_id`),
  KEY `idx_comment_id` (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点评论点赞表';

-- ========================================
-- 初始化测试数据
-- ========================================

-- 插入科目数据
INSERT INTO `knowledge_subject` (`subject_name`, `description`, `sort_order`, `status`, `create_by`) VALUES
('计算机组成原理', '介绍计算机系统的组成、工作原理及性能评价', 1, 1, 'admin'),
('数据结构', '研究数据的逻辑结构、存储结构及算法', 2, 1, 'admin'),
('操作系统', '管理计算机硬件与软件资源的系统软件', 3, 1, 'admin'),
('计算机网络', '介绍计算机网络的体系结构、协议及应用', 4, 1, 'admin'),
('数据库原理', '数据库系统的概念、设计、管理与应用', 5, 1, 'admin'),
('软件工程', '软件开发方法、项目管理与质量保证', 6, 1, 'admin');

-- 插入标签数据（预定义常用标签）
INSERT INTO `knowledge_tag` (`tag_name`, `tag_color`, `use_count`) VALUES
('重点', '#f56c6c', 0),
('难点', '#e6a23c', 0),
('高频考点', '#ff6600', 0),
('易错点', '#909399', 0),
('基础', '#67c23a', 0),
('进阶', '#409eff', 0),
('面试常考', '#ff0066', 0),
('实战应用', '#00ccff', 0);

-- 插入示例知识点（供测试使用）
INSERT INTO `knowledge_point` (
  `subject_id`, `title`, `summary`, `content`, 
  `difficulty`, `author_id`, `author_name`, 
  `status`, `audit_status`, `publish_time`
) VALUES
(1, 'Cache映射方式详解', 
 '介绍直接映射、全相联映射、组相联映射三种Cache映射方式的原理、优缺点和应用场景', 
 '## 一、Cache映射概述\n\nCache映射是指主存地址与Cache地址之间的对应关系。主要有三种映射方式：\n\n### 1. 直接映射（Direct Mapping）\n\n**原理**：主存的每个块只能映射到Cache的固定位置。\n\n**映射规则**：\n```\nCache块号 = 主存块号 % Cache块数\n```\n\n**优点**：实现简单、成本低、查找速度快\n\n**缺点**：冲突率高、灵活性差\n\n### 2. 全相联映射（Fully Associative Mapping）\n\n**原理**：主存的任何一块都可以映射到Cache的任意位置。\n\n**优点**：冲突率最低、Cache利用率高\n\n**缺点**：硬件成本高、查找速度慢\n\n### 3. 组相联映射（Set Associative Mapping）\n\n**原理**：将Cache分成若干组，主存块可以映射到某一组的任意位置。是直接映射和全相联映射的折中方案。', 
 2, 1, 'admin', 1, 1, NOW()
);

SET @point_id_1 = LAST_INSERT_ID();

-- 为示例知识点添加标签
INSERT INTO `knowledge_point_tag` (`point_id`, `tag_id`) VALUES
(@point_id_1, 1),  -- 重点
(@point_id_1, 3);  -- 高频考点

-- 插入更多测试知识点
INSERT INTO `knowledge_point` (
  `subject_id`, `title`, `summary`, `content`, 
  `difficulty`, `author_id`, `author_name`, 
  `status`, `audit_status`, `publish_time`
) VALUES
-- 数据结构知识点
(2, '二叉树的遍历方法', 
 '详细讲解前序、中序、后序和层序遍历的递归与非递归实现方法',
 '## 二叉树遍历概述\n\n二叉树的遍历是树结构中最基本的操作之一。\n\n### 1. 前序遍历（Pre-order）\n访问顺序：根 → 左 → 右\n\n**递归实现**：\n```java\nvoid preOrder(TreeNode root) {\n    if (root == null) return;\n    System.out.print(root.val + " ");\n    preOrder(root.left);\n    preOrder(root.right);\n}\n```\n\n### 2. 中序遍历（In-order）\n访问顺序：左 → 根 → 右\n\n### 3. 后序遍历（Post-order）\n访问顺序：左 → 右 → 根\n\n### 4. 层序遍历（Level-order）\n使用队列实现按层访问',
 1, 1, 'admin', 1, 1, DATE_SUB(NOW(), INTERVAL 2 DAY)
),
(2, '快速排序算法详解',
 '深入理解快速排序的分治思想、时间复杂度分析及优化方法',
 '## 快速排序原理\n\n快速排序是一种高效的排序算法，采用分治法思想。\n\n### 算法步骤\n1. 选择基准元素（pivot）\n2. 分区操作：比基准小的放左边，大的放右边\n3. 递归地对左右子数组进行快排\n\n### 代码实现\n```java\npublic void quickSort(int[] arr, int left, int right) {\n    if (left >= right) return;\n    int pivot = partition(arr, left, right);\n    quickSort(arr, left, pivot - 1);\n    quickSort(arr, pivot + 1, right);\n}\n```\n\n### 时间复杂度\n- 平均：O(n log n)\n- 最坏：O(n²)\n- 最好：O(n log n)',
 2, 1, 'admin', 1, 1, DATE_SUB(NOW(), INTERVAL 5 DAY)
),
-- 操作系统知识点
(3, '进程与线程的区别',
 '从资源分配、调度、通信等多个角度对比进程和线程的异同',
 '## 进程 vs 线程\n\n### 定义\n- **进程**：资源分配的基本单位\n- **线程**：CPU调度的基本单位\n\n### 主要区别\n\n| 对比项 | 进程 | 线程 |\n|--------|------|------|\n| 资源 | 独立的地址空间 | 共享进程资源 |\n| 开销 | 创建销毁开销大 | 开销小 |\n| 通信 | IPC机制 | 直接读写 |\n| 崩溃影响 | 独立 | 影响整个进程 |\n\n### 使用场景\n- 进程：需要资源隔离的场景\n- 线程：需要频繁切换和通信的场景',
 1, 1, 'admin', 1, 1, DATE_SUB(NOW(), INTERVAL 1 DAY)
),
(3, '死锁的产生与预防',
 '分析死锁产生的四个必要条件及预防、避免、检测和解除死锁的方法',
 '## 死锁概念\n\n多个进程因竞争资源而造成的僵局，若无外力作用，这些进程都将无法继续执行。\n\n### 死锁的四个必要条件\n1. **互斥条件**：资源不能共享\n2. **请求与保持**：持有资源的同时请求新资源\n3. **不可剥夺**：已获得的资源不能强行剥夺\n4. **循环等待**：存在进程资源的循环等待链\n\n### 死锁预防\n破坏四个必要条件之一：\n- 破坏互斥：资源共享（不现实）\n- 破坏请求与保持：一次性申请所有资源\n- 破坏不可剥夺：资源可抢占\n- 破坏循环等待：资源有序分配\n\n### 银行家算法\n著名的死锁避免算法，通过安全性检测避免系统进入不安全状态。',
 3, 1, 'admin', 1, 1, DATE_SUB(NOW(), INTERVAL 3 DAY)
),
-- 计算机网络知识点
(4, 'TCP三次握手与四次挥手',
 '详解TCP连接建立和断开的完整过程及各个状态的含义',
 '## TCP连接管理\n\n### 三次握手（建立连接）\n1. **SYN**：客户端发送SYN=1，seq=x\n2. **SYN+ACK**：服务器回复SYN=1，ACK=1，seq=y，ack=x+1\n3. **ACK**：客户端发送ACK=1，seq=x+1，ack=y+1\n\n### 为什么是三次？\n- 防止旧的重复连接初始化\n- 同步双方的序列号\n- 避免资源浪费\n\n### 四次挥手（断开连接）\n1. **FIN**：主动方发送FIN=1\n2. **ACK**：被动方确认\n3. **FIN**：被动方发送FIN=1\n4. **ACK**：主动方确认\n\n### TIME_WAIT状态\n主动关闭方等待2MSL，确保被动方收到最后的ACK。',
 2, 1, 'admin', 1, 1, DATE_SUB(NOW(), INTERVAL 4 DAY)
),
(4, 'HTTP与HTTPS的区别',
 '对比HTTP和HTTPS的安全性、性能、应用场景等方面的差异',
 '## HTTP vs HTTPS\n\n### 核心区别\n- **HTTP**：超文本传输协议，明文传输\n- **HTTPS**：HTTP + SSL/TLS，加密传输\n\n### HTTPS加密流程\n1. 客户端发起HTTPS请求\n2. 服务器返回数字证书\n3. 客户端验证证书有效性\n4. 生成随机对称密钥\n5. 用服务器公钥加密发送\n6. 服务器私钥解密获得密钥\n7. 后续通信使用对称加密\n\n### 性能对比\n- HTTPS比HTTP慢（加密解密开销）\n- 现代优化：HTTP/2、TLS 1.3大幅提升性能\n\n### 端口\n- HTTP：80\n- HTTPS：443',
 1, 1, 'admin', 1, 1, DATE_SUB(NOW(), INTERVAL 6 DAY)
),
-- 数据库知识点
(5, '数据库事务的ACID特性',
 '详细解释原子性、一致性、隔离性、持久性四大特性及其实现原理',
 '## ACID特性\n\n### 1. 原子性（Atomicity）\n事务中的所有操作要么全部完成，要么全部不完成。\n\n**实现**：通过undo log实现回滚\n\n### 2. 一致性（Consistency）\n事务执行前后，数据库从一个一致性状态转换到另一个一致性状态。\n\n### 3. 隔离性（Isolation）\n并发事务之间相互隔离，互不干扰。\n\n**隔离级别**：\n- READ UNCOMMITTED\n- READ COMMITTED\n- REPEATABLE READ（MySQL默认）\n- SERIALIZABLE\n\n### 4. 持久性（Durability）\n事务提交后，修改永久保存。\n\n**实现**：通过redo log保证',
 2, 1, 'admin', 1, 1, DATE_SUB(NOW(), INTERVAL 7 DAY)
),
(5, 'MySQL索引优化实战',
 '深入讲解B+树索引原理、索引设计原则和常见优化技巧',
 '## MySQL索引优化\n\n### 索引类型\n1. **B+树索引**：最常用，适合范围查询\n2. **哈希索引**：等值查询快，不支持排序\n3. **全文索引**：文本搜索\n\n### 索引设计原则\n1. 选择区分度高的列\n2. 最左前缀原则\n3. 避免索引列上使用函数\n4. 覆盖索引减少回表\n5. 避免使用SELECT *\n\n### 常见优化\n```sql\n-- 复合索引\nCREATE INDEX idx_user ON user(age, city, name);\n\n-- 覆盖索引\nSELECT id, name FROM user WHERE age = 20;\n```\n\n### 索引失效场景\n- 使用!=、<>、NOT IN\n- 使用OR连接\n- 在索引列上做运算',
 3, 1, 'admin', 1, 1, DATE_SUB(NOW(), INTERVAL 8 DAY)
);

-- 获取新插入的知识点ID（避免负数问题）
SET @point_id_2 = @point_id_1 + 1;
SET @point_id_3 = @point_id_1 + 2;
SET @point_id_4 = @point_id_1 + 3;
SET @point_id_5 = @point_id_1 + 4;
SET @point_id_6 = @point_id_1 + 5;
SET @point_id_7 = @point_id_1 + 6;
SET @point_id_8 = @point_id_1 + 7;

-- 为知识点添加标签
INSERT INTO `knowledge_point_tag` (`point_id`, `tag_id`) VALUES
(@point_id_2, 1),  -- 二叉树：重点
(@point_id_2, 5),  -- 二叉树：基础
(@point_id_3, 2),  -- 快排：难点
(@point_id_3, 3),  -- 快排：高频考点
(@point_id_4, 5),  -- 进程线程：基础
(@point_id_4, 7),  -- 进程线程：面试常考
(@point_id_5, 1),  -- 死锁：重点
(@point_id_5, 2),  -- 死锁：难点
(@point_id_6, 1),  -- TCP：重点
(@point_id_6, 7),  -- TCP：面试常考
(@point_id_7, 5),  -- HTTP：基础
(@point_id_7, 8),  -- HTTP：实战应用
-- 第7个知识点是ACID，第8个是索引
-- 需要查看知识点插入顺序来确定正确的ID分配
(@point_id_7, 1),  -- ACID：重点
(@point_id_7, 3),  -- ACID：高频考点  
(@point_id_8, 1),  -- 索引：重点
(@point_id_8, 8);  -- 索引：实战应用

-- 模拟点赞数据（假设用户ID为1和2）
INSERT INTO `knowledge_like` (`user_id`, `point_id`) VALUES
(1, @point_id_1),
(1, @point_id_2),
(1, @point_id_6),
(2, @point_id_1),
(2, @point_id_3),
(2, @point_id_5);

-- 更新知识点的点赞数
UPDATE `knowledge_point` SET `like_count` = 2 WHERE `point_id` = @point_id_1;
UPDATE `knowledge_point` SET `like_count` = 1 WHERE `point_id` IN (@point_id_2, @point_id_3, @point_id_5, @point_id_6);

-- 模拟收藏数据
INSERT INTO `knowledge_collect` (`user_id`, `point_id`) VALUES
(1, @point_id_1),
(1, @point_id_4),
(1, @point_id_6),
(2, @point_id_2),
(2, @point_id_5),
(2, @point_id_7);

-- 更新知识点的收藏数
UPDATE `knowledge_point` SET `collect_count` = 1 WHERE `point_id` IN (@point_id_1, @point_id_2, @point_id_4, @point_id_5, @point_id_6, @point_id_7);

-- 模拟浏览数据
UPDATE `knowledge_point` SET `view_count` = 156 WHERE `point_id` = @point_id_1;
UPDATE `knowledge_point` SET `view_count` = 89 WHERE `point_id` = @point_id_2;
UPDATE `knowledge_point` SET `view_count` = 234 WHERE `point_id` = @point_id_3;
UPDATE `knowledge_point` SET `view_count` = 67 WHERE `point_id` = @point_id_4;
UPDATE `knowledge_point` SET `view_count` = 178 WHERE `point_id` = @point_id_5;
UPDATE `knowledge_point` SET `view_count` = 312 WHERE `point_id` = @point_id_6;
UPDATE `knowledge_point` SET `view_count` = 45 WHERE `point_id` = @point_id_7;
UPDATE `knowledge_point` SET `view_count` = 203 WHERE `point_id` = @point_id_8;

-- 模拟评论数据
INSERT INTO `knowledge_comment` (
  `point_id`, `user_id`, `user_name`, `nick_name`, `avatar`, `content`, `parent_id`, `status`
) VALUES
(@point_id_1, 1, 'student1', '小明', NULL, '讲解得非常清楚，终于理解了Cache映射的区别！', 0, 1),
(@point_id_1, 2, 'student2', '小红', NULL, '组相联映射是最常用的，感谢分享', 0, 1),
(@point_id_3, 1, 'student1', '小明', NULL, '快排的平均时间复杂度是O(n log n)，但最坏情况要注意优化', 0, 1),
(@point_id_6, 2, 'student2', '小红', NULL, 'TIME_WAIT状态在面试中经常被问到', 0, 1);

SET @comment_id_1 = LAST_INSERT_ID();

-- 模拟二级回复
INSERT INTO `knowledge_comment` (
  `point_id`, `user_id`, `user_name`, `nick_name`, `avatar`, `content`, 
  `parent_id`, `reply_to_user_id`, `reply_to_user_name`, `reply_to_nick_name`, `status`
) VALUES
(@point_id_1, 2, 'student2', '小红', NULL, '同感！之前一直搞不懂，现在清楚多了', 
 @comment_id_1, 1, 'student1', '小明', 1);

-- 更新知识点的评论数
UPDATE `knowledge_point` SET `comment_count` = 3 WHERE `point_id` = @point_id_1;
UPDATE `knowledge_point` SET `comment_count` = 1 WHERE `point_id` IN (@point_id_3, @point_id_6);

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

-- 二级菜单：知识点管理
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`) 
VALUES ('知识点管理', @menu_id_knowledge, 2, 'point', 'knowledge/point/index', 1, 0, 'C', '0', '0', 'knowledge:point:list', 'documentation', 'admin', NOW());

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
VALUES ('标签管理', @menu_id_knowledge, 3, 'tag', 'knowledge/tag/index', 1, 0, 'C', '0', '0', 'knowledge:tag:list', 'tag', 'admin', NOW());

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
-- 1. 简化版设计，移除章节表，采用科目+标签模式
-- 2. 已创建8张核心表
-- 3. 预置6个科目、8个常用标签
-- 4. 配置后台管理菜单权限
-- 5. 用户发布时只需选择科目和难度，可自由添加标签
-- ========================================
