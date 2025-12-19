/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306_1
 Source Server Type    : MySQL
 Source Server Version : 50744 (5.7.44-log)
 Source Host           : localhost:3306
 Source Schema         : ry-vue

 Target Server Type    : MySQL
 Target Server Version : 50744 (5.7.44-log)
 File Encoding         : 65001

 Date: 11/12/2025 09:30:03
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for knowledge_attachment
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_attachment`;
CREATE TABLE `knowledge_attachment`  (
  `attachment_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '附件ID',
  `point_id` bigint(20) NOT NULL COMMENT '知识点ID',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件名',
  `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件URL',
  `file_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文件类型（image/video/pdf等）',
  `file_size` bigint(20) NULL DEFAULT 0 COMMENT '文件大小（字节）',
  `sort_order` int(11) NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`attachment_id`) USING BTREE,
  INDEX `idx_point_id`(`point_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '知识点附件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of knowledge_attachment
-- ----------------------------

-- ----------------------------
-- Table structure for knowledge_collect
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_collect`;
CREATE TABLE `knowledge_collect`  (
  `collect_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `point_id` bigint(20) NOT NULL COMMENT '知识点ID',
  `folder_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '收藏夹ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`collect_id`) USING BTREE,
  UNIQUE INDEX `uk_user_point`(`user_id`, `point_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_point_id`(`point_id`) USING BTREE,
  INDEX `idx_folder_id`(`folder_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '知识点收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of knowledge_collect
-- ----------------------------
INSERT INTO `knowledge_collect` VALUES (1, 1, 1, 1, '2025-11-29 11:29:16');
INSERT INTO `knowledge_collect` VALUES (2, 1, 4, 1, '2025-11-29 11:29:16');
INSERT INTO `knowledge_collect` VALUES (3, 1, 6, 1, '2025-11-29 11:29:16');
INSERT INTO `knowledge_collect` VALUES (4, 2, 2, 0, '2025-11-29 11:29:16');
INSERT INTO `knowledge_collect` VALUES (5, 2, 5, 0, '2025-11-29 11:29:16');
INSERT INTO `knowledge_collect` VALUES (6, 2, 7, 0, '2025-11-29 11:29:16');
INSERT INTO `knowledge_collect` VALUES (9, 8, 3, 3, '2025-11-29 19:38:46');

-- ----------------------------
-- Table structure for knowledge_comment
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_comment`;
CREATE TABLE `knowledge_comment`  (
  `comment_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `point_id` bigint(20) NOT NULL COMMENT '知识点ID',
  `user_id` bigint(20) NOT NULL COMMENT '评论人ID',
  `user_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '评论人用户名',
  `nick_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '评论人昵称',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '评论人头像',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '评论内容',
  `parent_id` bigint(20) NULL DEFAULT 0 COMMENT '父评论ID（0表示一级评论）',
  `reply_to_user_id` bigint(20) NULL DEFAULT NULL COMMENT '被回复的用户ID',
  `reply_to_user_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '被回复的用户名',
  `reply_to_nick_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '被回复的昵称',
  `reply_to_comment_id` bigint(20) NULL DEFAULT NULL COMMENT '回复目标评论ID',
  `like_count` bigint(20) NULL DEFAULT 0 COMMENT '点赞数',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态（0-删除 1-正常）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`comment_id`) USING BTREE,
  INDEX `idx_point_id`(`point_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '知识点评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of knowledge_comment
-- ----------------------------
INSERT INTO `knowledge_comment` VALUES (1, 1, 1, 'student1', '小明', NULL, '讲解得非常清楚，终于理解了Cache映射的区别！', 0, NULL, NULL, NULL, NULL, 0, 1, '', '2025-11-29 11:29:16', '', '2025-11-29 11:29:16');
INSERT INTO `knowledge_comment` VALUES (2, 1, 2, 'student2', '小红', NULL, '组相联映射是最常用的，感谢分享', 0, NULL, NULL, NULL, NULL, 0, 1, '', '2025-11-29 11:29:16', '', '2025-11-29 11:29:16');
INSERT INTO `knowledge_comment` VALUES (3, 3, 1, 'student1', '小明', NULL, '快排的平均时间复杂度是O(n log n)，但最坏情况要注意优化', 0, NULL, NULL, NULL, NULL, 0, 1, '', '2025-11-29 11:29:16', '', '2025-11-29 11:29:16');
INSERT INTO `knowledge_comment` VALUES (4, 6, 2, 'student2', '小红', NULL, 'TIME_WAIT状态在面试中经常被问到', 0, NULL, NULL, NULL, NULL, 0, 1, '', '2025-11-29 11:29:16', '', '2025-11-29 11:29:16');
INSERT INTO `knowledge_comment` VALUES (5, 1, 2, 'student2', '小红', NULL, '同感！之前一直搞不懂，现在清楚多了', 1, 1, 'student1', '小明', NULL, 0, 1, '', '2025-11-29 11:29:16', '', '2025-11-29 11:29:16');
INSERT INTO `knowledge_comment` VALUES (6, 12, 8, 'test666', 'testzww', 'https://daming-paper.oss-cn-guangzhou.aliyuncs.com/quiz/paper/answer/2025/11/24/5d9d1e6218f64117831b42d0546bf1de.jpg', '写的太好了哥们', 0, NULL, NULL, NULL, NULL, 0, 1, 'test666', '2025-11-29 21:21:07', '', '2025-11-29 21:21:07');
INSERT INTO `knowledge_comment` VALUES (7, 15, 8, 'test666', 'testzww', 'https://daming-paper.oss-cn-guangzhou.aliyuncs.com/quiz/paper/answer/2025/11/29/4db7f9a2283b4ecb811a593b3f5a8d70.jpg', '牛逼', 0, NULL, NULL, NULL, NULL, 0, 1, 'test666', '2025-11-29 22:06:25', '', '2025-11-29 22:06:25');
INSERT INTO `knowledge_comment` VALUES (8, 15, 3, 'zww', '大米面膜', 'https://daming-paper.oss-cn-guangzhou.aliyuncs.com/quiz/paper/answer/2025/11/29/aea28aaf7fcb42569f4492b58c68281b.jpg', '厉害了', 7, 8, 'test666', 'testzww', 7, 0, 1, 'zww', '2025-11-29 22:06:51', '', '2025-11-29 22:06:51');
INSERT INTO `knowledge_comment` VALUES (9, 15, 8, 'test666', 'testzww', 'https://daming-paper.oss-cn-guangzhou.aliyuncs.com/quiz/paper/answer/2025/11/29/4db7f9a2283b4ecb811a593b3f5a8d70.jpg', '666', 7, 8, 'test666', 'testzww', 7, 0, 1, 'test666', '2025-11-29 22:06:56', '', '2025-11-29 22:06:56');
INSERT INTO `knowledge_comment` VALUES (10, 15, 3, 'zww', '大米面膜', 'https://daming-paper.oss-cn-guangzhou.aliyuncs.com/quiz/paper/answer/2025/11/29/aea28aaf7fcb42569f4492b58c68281b.jpg', '做的太好了', 7, 8, 'test666', 'testzww', 9, 0, 1, 'zww', '2025-11-29 22:07:06', '', '2025-11-29 22:07:06');

-- ----------------------------
-- Table structure for knowledge_comment_like
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_comment_like`;
CREATE TABLE `knowledge_comment_like`  (
  `like_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `comment_id` bigint(20) NOT NULL COMMENT '评论ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`like_id`) USING BTREE,
  UNIQUE INDEX `uk_user_comment`(`user_id`, `comment_id`) USING BTREE,
  INDEX `idx_comment_id`(`comment_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '知识点评论点赞表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of knowledge_comment_like
-- ----------------------------

-- ----------------------------
-- Table structure for knowledge_folder
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_folder`;
CREATE TABLE `knowledge_folder`  (
  `folder_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '收藏夹ID',
  `folder_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '收藏夹名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '收藏夹描述',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `is_default` tinyint(1) NULL DEFAULT 0 COMMENT '是否默认收藏夹(1是0否)',
  `is_public` tinyint(1) NULL DEFAULT 0 COMMENT '是否公开(1是0否)',
  `collect_count` int(11) NULL DEFAULT 0 COMMENT '收藏数量',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`folder_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_user_default`(`user_id`, `is_default`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '知识点收藏夹表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of knowledge_folder
-- ----------------------------
INSERT INTO `knowledge_folder` VALUES (2, '前端学习', '前端相关知识点收藏', 8, 0, 1, 0, '2025-11-29 14:09:37', '2025-11-29 18:50:20');
INSERT INTO `knowledge_folder` VALUES (3, '算法练习', '数据结构与算法相关', 8, 0, 0, 1, '2025-11-29 14:09:37', '2025-11-29 19:38:46');
INSERT INTO `knowledge_folder` VALUES (4, '默认收藏夹', '系统自动创建的默认收藏夹', 8, 1, 0, 0, '2025-11-29 16:01:15', '2025-11-29 22:09:22');
INSERT INTO `knowledge_folder` VALUES (5, '888', '', 8, 0, 0, 0, '2025-11-29 18:30:53', '2025-11-29 21:28:29');
INSERT INTO `knowledge_folder` VALUES (6, '计组', '', 8, 0, 0, 0, '2025-11-29 22:09:20', '2025-11-29 22:10:06');
INSERT INTO `knowledge_folder` VALUES (7, '计网', '', 8, 0, 0, 0, '2025-11-29 22:09:45', '2025-11-29 22:09:45');

-- ----------------------------
-- Table structure for knowledge_learn_record
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_learn_record`;
CREATE TABLE `knowledge_learn_record`  (
  `record_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `point_id` bigint(20) NOT NULL COMMENT '知识点ID',
  `learn_duration` int(11) NULL DEFAULT 0 COMMENT '学习时长（秒）',
  `learn_progress` int(11) NULL DEFAULT 0 COMMENT '学习进度（0-100）',
  `mastery_level` tinyint(1) NULL DEFAULT 0 COMMENT '掌握程度（0-未学 1-了解 2-理解 3-掌握 4-精通）',
  `last_learn_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后学习时间',
  `learn_count` int(11) NULL DEFAULT 1 COMMENT '学习次数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '首次学习时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`record_id`) USING BTREE,
  UNIQUE INDEX `uk_user_point`(`user_id`, `point_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_last_learn_time`(`last_learn_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '知识点学习记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of knowledge_learn_record
-- ----------------------------

-- ----------------------------
-- Table structure for knowledge_like
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_like`;
CREATE TABLE `knowledge_like`  (
  `like_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `point_id` bigint(20) NOT NULL COMMENT '知识点ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`like_id`) USING BTREE,
  UNIQUE INDEX `uk_user_point`(`user_id`, `point_id`) USING BTREE,
  INDEX `idx_point_id`(`point_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '知识点点赞表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of knowledge_like
-- ----------------------------
INSERT INTO `knowledge_like` VALUES (1, 1, 1, '2025-11-29 11:29:16');
INSERT INTO `knowledge_like` VALUES (2, 1, 2, '2025-11-29 11:29:16');
INSERT INTO `knowledge_like` VALUES (3, 1, 6, '2025-11-29 11:29:16');
INSERT INTO `knowledge_like` VALUES (4, 2, 1, '2025-11-29 11:29:16');
INSERT INTO `knowledge_like` VALUES (5, 2, 3, '2025-11-29 11:29:16');
INSERT INTO `knowledge_like` VALUES (6, 2, 5, '2025-11-29 11:29:16');
INSERT INTO `knowledge_like` VALUES (11, 8, 1, '2025-11-29 14:05:19');

-- ----------------------------
-- Table structure for knowledge_point_back
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_point_back`;
CREATE TABLE `knowledge_point_back`  (
  `point_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '知识点ID',
  `subject_id` bigint(20) NOT NULL COMMENT '所属科目ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '知识点标题',
  `summary` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '摘要',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '知识点内容（Markdown格式）',
  `content_html` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'HTML内容（前端渲染用）',
  `difficulty` tinyint(1) NULL DEFAULT 1 COMMENT '难度等级（1-简单 2-中等 3-困难）',
  `author_id` bigint(20) NOT NULL COMMENT '作者ID',
  `author_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '作者名称',
  `view_count` int(11) NULL DEFAULT 0 COMMENT '浏览次数',
  `like_count` int(11) NULL DEFAULT 0 COMMENT '点赞数',
  `collect_count` int(11) NULL DEFAULT 0 COMMENT '收藏数',
  `comment_count` int(11) NULL DEFAULT 0 COMMENT '评论数',
  `is_recommend` tinyint(1) NULL DEFAULT 0 COMMENT '是否推荐（0-否 1-是）',
  `is_top` tinyint(1) NULL DEFAULT 0 COMMENT '是否置顶（0-否 1-是）',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态（0-草稿 1-已发布 2-已下架）',
  `audit_status` tinyint(1) NULL DEFAULT 0 COMMENT '审核状态（0-待审核 1-通过 2-拒绝）',
  `audit_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '审核备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  PRIMARY KEY (`point_id`) USING BTREE,
  INDEX `idx_subject_id`(`subject_id`) USING BTREE,
  INDEX `idx_author_id`(`author_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_difficulty`(`difficulty`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_like_count`(`like_count`) USING BTREE,
  INDEX `idx_audit_status`(`audit_status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '知识点表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of knowledge_point_back
-- ----------------------------
INSERT INTO `knowledge_point_back` VALUES (1, 1, 'Cache映射方式详解', '介绍直接映射、全相联映射、组相联映射三种Cache映射方式的原理、优缺点和应用场景', '## 一、Cache映射概述\n\nCache映射是指主存地址与Cache地址之间的对应关系。主要有三种映射方式：\n\n### 1. 直接映射（Direct Mapping）\n\n**原理**：主存的每个块只能映射到Cache的固定位置。\n\n**映射规则**：\n```\nCache块号 = 主存块号 % Cache块数\n```\n\n**优点**：实现简单、成本低、查找速度快\n\n**缺点**：冲突率高、灵活性差\n\n### 2. 全相联映射（Fully Associative Mapping）\n\n**原理**：主存的任何一块都可以映射到Cache的任意位置。\n\n**优点**：冲突率最低、Cache利用率高\n\n**缺点**：硬件成本高、查找速度慢\n\n### 3. 组相联映射（Set Associative Mapping）\n\n**原理**：将Cache分成若干组，主存块可以映射到某一组的任意位置。是直接映射和全相联映射的折中方案。', NULL, 2, 1, 'admin', 163, 3, 1, 3, 0, 0, 1, 1, NULL, NULL, '2025-11-29 11:29:16', NULL, '2025-11-29 15:45:41', '2025-11-29 11:29:16');
INSERT INTO `knowledge_point_back` VALUES (2, 2, '二叉树的遍历方法', '详细讲解前序、中序、后序和层序遍历的递归与非递归实现方法', '## 二叉树遍历概述\n\n二叉树的遍历是树结构中最基本的操作之一。\n\n### 1. 前序遍历（Pre-order）\n访问顺序：根 → 左 → 右\n\n**递归实现**：\n```java\nvoid preOrder(TreeNode root) {\n    if (root == null) return;\n    System.out.print(root.val + \" \");\n    preOrder(root.left);\n    preOrder(root.right);\n}\n```\n\n### 2. 中序遍历（In-order）\n访问顺序：左 → 根 → 右\n\n### 3. 后序遍历（Post-order）\n访问顺序：左 → 右 → 根\n\n### 4. 层序遍历（Level-order）\n使用队列实现按层访问', NULL, 1, 1, 'admin', 128, 1, 1, 0, 0, 0, 1, 1, NULL, NULL, '2025-11-29 11:29:16', NULL, '2025-11-29 14:02:41', '2025-11-27 11:29:16');
INSERT INTO `knowledge_point_back` VALUES (3, 2, '快速排序算法详解', '深入理解快速排序的分治思想、时间复杂度分析及优化方法', '## 快速排序原理\n\n快速排序是一种高效的排序算法，采用分治法思想。\n\n### 算法步骤\n1. 选择基准元素（pivot）\n2. 分区操作：比基准小的放左边，大的放右边\n3. 递归地对左右子数组进行快排\n\n### 代码实现\n```java\npublic void quickSort(int[] arr, int left, int right) {\n    if (left >= right) return;\n    int pivot = partition(arr, left, right);\n    quickSort(arr, left, pivot - 1);\n    quickSort(arr, pivot + 1, right);\n}\n```\n\n### 时间复杂度\n- 平均：O(n log n)\n- 最坏：O(n²)\n- 最好：O(n log n)', NULL, 2, 1, 'admin', 235, 1, 1, 1, 0, 0, 1, 1, NULL, NULL, '2025-11-29 11:29:16', NULL, '2025-11-29 19:38:46', '2025-11-24 11:29:16');
INSERT INTO `knowledge_point_back` VALUES (4, 3, '进程与线程的区别', '从资源分配、调度、通信等多个角度对比进程和线程的异同', '## 进程 vs 线程\n\n### 定义\n- **进程**：资源分配的基本单位\n- **线程**：CPU调度的基本单位\n\n### 主要区别\n\n| 对比项 | 进程 | 线程 |\n|--------|------|------|\n| 资源 | 独立的地址空间 | 共享进程资源 |\n| 开销 | 创建销毁开销大 | 开销小 |\n| 通信 | IPC机制 | 直接读写 |\n| 崩溃影响 | 独立 | 影响整个进程 |\n\n### 使用场景\n- 进程：需要资源隔离的场景\n- 线程：需要频繁切换和通信的场景', NULL, 1, 1, 'admin', 67, 0, 1, 0, 0, 0, 1, 1, NULL, NULL, '2025-11-29 11:29:16', NULL, '2025-11-29 11:29:16', '2025-11-28 11:29:16');
INSERT INTO `knowledge_point_back` VALUES (5, 3, '死锁的产生与预防', '分析死锁产生的四个必要条件及预防、避免、检测和解除死锁的方法', '## 死锁概念\n\n多个进程因竞争资源而造成的僵局，若无外力作用，这些进程都将无法继续执行。\n\n### 死锁的四个必要条件\n1. **互斥条件**：资源不能共享\n2. **请求与保持**：持有资源的同时请求新资源\n3. **不可剥夺**：已获得的资源不能强行剥夺\n4. **循环等待**：存在进程资源的循环等待链\n\n### 死锁预防\n破坏四个必要条件之一：\n- 破坏互斥：资源共享（不现实）\n- 破坏请求与保持：一次性申请所有资源\n- 破坏不可剥夺：资源可抢占\n- 破坏循环等待：资源有序分配\n\n### 银行家算法\n著名的死锁避免算法，通过安全性检测避免系统进入不安全状态。', NULL, 3, 1, 'admin', 179, 1, 1, 0, 0, 0, 1, 1, NULL, NULL, '2025-11-29 11:29:16', NULL, '2025-11-29 17:18:16', '2025-11-26 11:29:16');
INSERT INTO `knowledge_point_back` VALUES (6, 4, 'TCP三次握手与四次挥手', '详解TCP连接建立和断开的完整过程及各个状态的含义', '## TCP连接管理\n\n### 三次握手（建立连接）\n1. **SYN**：客户端发送SYN=1，seq=x\n2. **SYN+ACK**：服务器回复SYN=1，ACK=1，seq=y，ack=x+1\n3. **ACK**：客户端发送ACK=1，seq=x+1，ack=y+1\n\n### 为什么是三次？\n- 防止旧的重复连接初始化\n- 同步双方的序列号\n- 避免资源浪费\n\n### 四次挥手（断开连接）\n1. **FIN**：主动方发送FIN=1\n2. **ACK**：被动方确认\n3. **FIN**：被动方发送FIN=1\n4. **ACK**：主动方确认\n\n### TIME_WAIT状态\n主动关闭方等待2MSL，确保被动方收到最后的ACK。', NULL, 2, 1, 'admin', 320, 1, 2, 1, 0, 0, 1, 1, NULL, NULL, '2025-11-29 11:29:16', NULL, '2025-11-29 19:30:10', '2025-11-25 11:29:16');
INSERT INTO `knowledge_point_back` VALUES (7, 4, 'HTTP与HTTPS的区别', '对比HTTP和HTTPS的安全性、性能、应用场景等方面的差异', '## HTTP vs HTTPS\n\n### 核心区别\n- **HTTP**：超文本传输协议，明文传输\n- **HTTPS**：HTTP + SSL/TLS，加密传输\n\n### HTTPS加密流程\n1. 客户端发起HTTPS请求\n2. 服务器返回数字证书\n3. 客户端验证证书有效性\n4. 生成随机对称密钥\n5. 用服务器公钥加密发送\n6. 服务器私钥解密获得密钥\n7. 后续通信使用对称加密\n\n### 性能对比\n- HTTPS比HTTP慢（加密解密开销）\n- 现代优化：HTTP/2、TLS 1.3大幅提升性能\n\n### 端口\n- HTTP：80\n- HTTPS：443', NULL, 1, 1, 'admin', 45, 0, 1, 0, 0, 0, 1, 1, NULL, NULL, '2025-11-29 11:29:16', NULL, '2025-11-29 11:29:16', '2025-11-23 11:29:16');
INSERT INTO `knowledge_point_back` VALUES (8, 5, '数据库事务的ACID特性', '详细解释原子性、一致性、隔离性、持久性四大特性及其实现原理', '## ACID特性\n\n### 1. 原子性（Atomicity）\n事务中的所有操作要么全部完成，要么全部不完成。\n\n**实现**：通过undo log实现回滚\n\n### 2. 一致性（Consistency）\n事务执行前后，数据库从一个一致性状态转换到另一个一致性状态。\n\n### 3. 隔离性（Isolation）\n并发事务之间相互隔离，互不干扰。\n\n**隔离级别**：\n- READ UNCOMMITTED\n- READ COMMITTED\n- REPEATABLE READ（MySQL默认）\n- SERIALIZABLE\n\n### 4. 持久性（Durability）\n事务提交后，修改永久保存。\n\n**实现**：通过redo log保证', NULL, 2, 1, 'admin', 203, 0, 0, 0, 0, 0, 1, 1, NULL, NULL, '2025-11-29 11:29:16', NULL, '2025-11-29 11:29:16', '2025-11-22 11:29:16');
INSERT INTO `knowledge_point_back` VALUES (9, 5, 'MySQL索引优化实战', '深入讲解B+树索引原理、索引设计原则和常见优化技巧', '## MySQL索引优化\n\n### 索引类型\n1. **B+树索引**：最常用，适合范围查询\n2. **哈希索引**：等值查询快，不支持排序\n3. **全文索引**：文本搜索\n\n### 索引设计原则\n1. 选择区分度高的列\n2. 最左前缀原则\n3. 避免索引列上使用函数\n4. 覆盖索引减少回表\n5. 避免使用SELECT *\n\n### 常见优化\n```sql\n-- 复合索引\nCREATE INDEX idx_user ON user(age, city, name);\n\n-- 覆盖索引\nSELECT id, name FROM user WHERE age = 20;\n```\n\n### 索引失效场景\n- 使用!=、<>、NOT IN\n- 使用OR连接\n- 在索引列上做运算', NULL, 3, 1, 'admin', 0, 0, 0, 0, 0, 0, 1, 1, NULL, NULL, '2025-11-29 11:29:16', NULL, '2025-11-29 11:29:16', '2025-11-21 11:29:16');
INSERT INTO `knowledge_point_back` VALUES (11, 1, 'lllll', 'lll', '**指令由哪两部分组成**\n\n**考点：指令格式**\n\n- **两部分：** 操作码 + 操作数\n- **一句话：** 操作码说明“干什么”，操作数说明“对谁干”。\n- **例子：** 指令 `ADD R1, R2, R3`**操作码：** `ADD`（执行加法操作）**操作数：** `R1, R2, R3`（把寄存器R2和R3的值相加，结果存到R1）\n\n\n\n', NULL, 1, 8, 'testzww', 4, 0, 0, 0, 0, 0, 1, 0, NULL, NULL, '2025-11-29 15:46:10', NULL, '2025-11-29 15:53:31', NULL);
INSERT INTO `knowledge_point_back` VALUES (12, 8, '指令由哪两部分组成', '考点：指令格式 - 两部分： 操作码 + 操作数 - 一句话： 操作码说明“干什么”，操作数说明“对谁干”。 - 例子： 指令 ADD R1, R2, R3操作码： ADD（执行加法操作）操作数： R1, R2, R3（把寄存器R2和R3的值相加，结果存到R1）', '**考点：指令格式**\n\n- **两部分：** 操作码 + 操作数\n- **一句话：** 操作码说明“干什么”，操作数说明“对谁干”。\n- **例子：** 指令 `ADD R1, R2, R3`**操作码：** `ADD`（执行加法操作）**操作数：** `R1, R2, R3`（把寄存器R2和R3的值相加，结果存到R1）\n\n', NULL, 1, 8, 'testzww', 13, 0, 1, 0, 0, 0, 1, 0, NULL, NULL, '2025-11-29 17:42:22', NULL, '2025-11-29 19:32:51', NULL);

-- ----------------------------
-- Table structure for knowledge_point_base
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_point_base`;
CREATE TABLE `knowledge_point_base`  (
  `point_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '知识点ID',
  `subject_id` bigint(20) NOT NULL COMMENT '科目ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
  `summary` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '摘要(限制500字)',
  `difficulty` tinyint(4) NULL DEFAULT 1 COMMENT '难度等级(1简单 2中等 3困难)',
  `author_id` bigint(20) NULL DEFAULT NULL COMMENT '作者用户ID',
  `author_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '作者姓名',
  `view_count` int(11) NULL DEFAULT 0 COMMENT '浏览次数',
  `like_count` int(11) NULL DEFAULT 0 COMMENT '点赞数',
  `collect_count` int(11) NULL DEFAULT 0 COMMENT '收藏数',
  `comment_count` int(11) NULL DEFAULT 0 COMMENT '评论数',
  `is_recommend` tinyint(4) NULL DEFAULT 0 COMMENT '是否推荐(0否 1是)',
  `is_top` tinyint(4) NULL DEFAULT 0 COMMENT '是否置顶(0否 1是)',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态(0草稿 1正常 2下架)',
  `audit_status` tinyint(4) NULL DEFAULT 0 COMMENT '审核状态(0待审核 1通过 2拒绝)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  PRIMARY KEY (`point_id`) USING BTREE,
  INDEX `idx_subject`(`subject_id`) USING BTREE,
  INDEX `idx_author`(`author_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_view_count`(`view_count`) USING BTREE,
  INDEX `idx_like_count`(`like_count`) USING BTREE,
  INDEX `idx_base_status_time`(`status`, `create_time`) USING BTREE,
  INDEX `idx_base_subject_status`(`subject_id`, `status`) USING BTREE,
  INDEX `idx_base_author_status`(`author_id`, `status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '知识点基础信息表(热数据)' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of knowledge_point_base
-- ----------------------------
INSERT INTO `knowledge_point_base` VALUES (1, 1, 'Cache映射方式详解', '介绍直接映射、全相联映射、组相联映射三种Cache映射方式的原理、优缺点和应用场景', 2, 1, 'admin', 163, 3, 1, 3, 0, 0, 1, 1, NULL, '2025-11-29 11:29:16', NULL, '2025-11-29 15:45:41', '2025-11-29 11:29:16');
INSERT INTO `knowledge_point_base` VALUES (2, 2, '二叉树的遍历方法', '详细讲解前序、中序、后序和层序遍历的递归与非递归实现方法', 1, 1, 'admin', 128, 1, 1, 0, 0, 0, 1, 1, NULL, '2025-11-29 11:29:16', NULL, '2025-11-29 14:02:41', '2025-11-27 11:29:16');
INSERT INTO `knowledge_point_base` VALUES (3, 2, '快速排序算法详解', '深入理解快速排序的分治思想、时间复杂度分析及优化方法', 2, 1, 'admin', 235, 1, 1, 1, 0, 0, 1, 1, NULL, '2025-11-29 11:29:16', NULL, '2025-11-29 19:38:46', '2025-11-24 11:29:16');
INSERT INTO `knowledge_point_base` VALUES (4, 3, '进程与线程的区别', '从资源分配、调度、通信等多个角度对比进程和线程的异同', 1, 1, 'admin', 67, 0, 1, 0, 0, 0, 1, 1, NULL, '2025-11-29 11:29:16', NULL, '2025-11-29 11:29:16', '2025-11-28 11:29:16');
INSERT INTO `knowledge_point_base` VALUES (5, 3, '死锁的产生与预防', '分析死锁产生的四个必要条件及预防、避免、检测和解除死锁的方法', 3, 1, 'admin', 179, 1, 1, 0, 0, 0, 1, 1, NULL, '2025-11-29 11:29:16', NULL, '2025-11-29 17:18:16', '2025-11-26 11:29:16');
INSERT INTO `knowledge_point_base` VALUES (6, 4, 'TCP三次握手与四次挥手', '详解TCP连接建立和断开的完整过程及各个状态的含义', 2, 1, 'admin', 322, 1, 1, 1, 0, 0, 1, 1, NULL, '2025-11-29 11:29:16', NULL, '2025-11-29 22:10:06', '2025-11-25 11:29:16');
INSERT INTO `knowledge_point_base` VALUES (7, 4, 'HTTP与HTTPS的区别', '对比HTTP和HTTPS的安全性、性能、应用场景等方面的差异', 1, 1, 'admin', 45, 0, 1, 0, 0, 0, 1, 1, NULL, '2025-11-29 11:29:16', NULL, '2025-11-29 11:29:16', '2025-11-23 11:29:16');
INSERT INTO `knowledge_point_base` VALUES (8, 5, '数据库事务的ACID特性', '详细解释原子性、一致性、隔离性、持久性四大特性及其实现原理', 2, 1, 'admin', 203, 0, 0, 0, 0, 0, 1, 1, NULL, '2025-11-29 11:29:16', NULL, '2025-11-29 11:29:16', '2025-11-22 11:29:16');
INSERT INTO `knowledge_point_base` VALUES (9, 5, 'MySQL索引优化实战', '深入讲解B+树索引原理、索引设计原则和常见优化技巧', 3, 1, 'admin', 0, 0, 0, 0, 0, 0, 1, 1, NULL, '2025-11-29 11:29:16', NULL, '2025-11-29 11:29:16', '2025-11-21 11:29:16');
INSERT INTO `knowledge_point_base` VALUES (15, 8, '指令由哪两部分组成', '考点：指令格式 - 两部分： 操作码 + 操作数 - 一句话： 操作码说明“干什么”，操作数说明“对谁干”。 - 例子： 指令 ADD R1, R2, R3操作码： ADD（执行加法操作）操作数： R1, R2, R3（把寄存器R2和R3的值相加，结果存到R1）', 1, 8, 'testzww', 0, 0, 0, 0, 0, 0, 1, 0, NULL, '2025-11-29 21:53:45', NULL, NULL, NULL);
INSERT INTO `knowledge_point_base` VALUES (16, 11, '微服务搭建', '0）版本说明 - JDK 19 - springboot 3.0 - springCloud 2022 1）新建父子项目 1.0）创建空Maven项目，引入依赖 !image-20251010094000654 1.1）修改本地maven文件夹地址 !image-20251010094141146...', 1, 8, 'testzww', 0, 0, 0, 0, 0, 0, 1, 0, NULL, '2025-11-29 22:02:53', NULL, NULL, NULL);
INSERT INTO `knowledge_point_base` VALUES (17, 8, '冯·诺依曼体系结构核心思想', '考点：冯·诺依曼体系结构核心思想    五大部件： 运算器、控制器、存储器、输入设备、输出设备。    一句话： “存储程序”，即程序和数据都以二进制的形式放在同一个存储器中。 *   例子： 你电脑开机流程：     1.  输入设备（键盘鼠标）接受指令。', 1, 8, 'testzww', 0, 0, 0, 0, 0, 0, 1, 0, NULL, '2025-11-30 12:47:55', NULL, NULL, NULL);
INSERT INTO `knowledge_point_base` VALUES (18, 8, '2.1）指令集体系结构分类（ISA - Instruction Set Architecture）', '考点：指令集体系结构分类（ISA - Instruction Set Architecture） 这题考的是CPU处理数据时，数据存放在哪里的4种不同方式。 --- 核心概念 问题：CPU做运算时，操作数（比如A、B这些数）存在哪？', 1, 8, 'testzww', 0, 0, 0, 0, 0, 0, 1, 0, NULL, '2025-11-30 14:59:17', NULL, '2025-11-30 15:55:58', NULL);
INSERT INTO `knowledge_point_base` VALUES (19, 8, '2.2）指令集架构设计（CISC vs RISC、指令格式、面向目标程序优化）', '!image-20251130150457756 通用寄存器型指令集的3种结构 RR结构（Register-Register，寄存器-寄存器） - 全称：Register-Register - 大白话：数据只能在寄存器之间操作，比如 ADD R1, R2, R3（把R2和R3加起来放到R1） -...', 1, 8, 'testzww', 0, 0, 0, 0, 0, 0, 1, 0, NULL, '2025-11-30 15:29:46', NULL, '2025-11-30 15:55:49', NULL);
INSERT INTO `knowledge_point_base` VALUES (20, 8, '2.3）指令寻址方式（操作数获取方法）', '寄存器寻址 - 大白话：数据直接在CPU内部的小盒子（寄存器）里 - 实例：Add R4, R3  - 含义：把R3寄存器的值加到R4寄存器上 - 特点：最快，因为数据就在CPU内部 立即值寻址（Immediate Addressing） - 大白话：数据直接写在指令里，是个固定数字 -...', 1, 8, 'testzww', 0, 0, 0, 0, 0, 0, 1, 0, NULL, '2025-11-30 15:48:27', NULL, '2025-11-30 15:55:40', NULL);
INSERT INTO `knowledge_point_base` VALUES (21, 8, '2.4）指令集设计基本要求、控制流指令、基本汇编指令', '指令集结构的4个基本要求 完整性 - 大白话：指令要够全，什么活都能干 - 解释：加减乘除、内存操作、跳转判断，该有的都得有，不能缺功能 规整性  - 大白话：指令格式要统一，不能乱七八糟 - 解释：同类指令的写法要一致，程序员容易记忆和使用 高效率 - 大白话：常用的指令要快，执行效率要高 -...', 1, 8, 'testzww', 0, 0, 0, 0, 0, 0, 1, 0, NULL, '2025-11-30 15:54:56', NULL, '2025-11-30 15:55:34', NULL);
INSERT INTO `knowledge_point_base` VALUES (22, 8, '3.1）什么是流水线？和流水线的特点', '!image-20251130155658249 什么是流水线？ 大白话：把复杂工作拆成多个简单步骤，每个步骤专人负责，大家同时干活 生活类比： - 像汽车装配线：A工人装轮子，B工人装发动机，C工人装座椅 - 不用等一辆车完全装完再装下一辆，而是同时装多辆车的不同部分 -...', 1, 8, 'testzww', 0, 0, 0, 0, 0, 0, 1, 0, NULL, '2025-11-30 16:06:22', NULL, '2025-11-30 16:31:27', NULL);
INSERT INTO `knowledge_point_base` VALUES (23, 8, '3.2）流水线的分类 ', '单功能流水线 vs 多功能流水线： - 单功能例子：专门做浮点加法的流水线，只能处理 3.14 + 2.5 这种运算 - 多功能例子：可以做加法、乘法、除法的流水线，既能算 3+5，又能算 3×5 静态流水线 vs 动态流水线： - 静态例子：汽车装配线，永远是装轮子→装发动机→装座椅，顺序不能变...', 1, 8, 'testzww', 0, 0, 0, 0, 0, 0, 1, 0, NULL, '2025-11-30 17:01:11', NULL, NULL, NULL);
INSERT INTO `knowledge_point_base` VALUES (24, 8, '3.3）流水线性能', '!image-20251130171310690 吞吐量TP计算 例子：3级流水线处理5个任务，每段用时2ns >TP的全称：Throughput（吞吐量） > >- Throughput：大白话就是\"通过量\"，表示单位时间内能处理多少个任务 >- 例子：工厂每小时生产100个产品，TP =...', 3, 8, 'testzww', 0, 0, 0, 0, 0, 0, 1, 0, NULL, '2025-11-30 17:13:50', NULL, '2025-12-01 16:57:49', NULL);
INSERT INTO `knowledge_point_base` VALUES (25, 8, '3.4）流水线相关', '什么是相关？ 大白话：两条指令之间有\"扯不清的关系\"，后面的指令依赖前面指令的结果 生活例子： - 你要先烧水，再泡茶 → 泡茶依赖烧水的结果 - 这两个动作就是\"相关\"的 三种相关类型 数据相关（真数据相关）：写后读（RAW） RAW全称：Read After Write（写后读）...', 1, 8, 'testzww', 0, 0, 0, 0, 0, 0, 1, 0, NULL, '2025-12-01 17:55:10', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for knowledge_point_content
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_point_content`;
CREATE TABLE `knowledge_point_content`  (
  `content_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '内容ID',
  `point_id` bigint(20) NOT NULL COMMENT '知识点ID(外键)',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '知识点内容(Markdown)',
  `content_html` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '知识点内容(HTML渲染后)',
  `audit_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '审核备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`content_id`) USING BTREE,
  UNIQUE INDEX `point_id`(`point_id`) USING BTREE,
  INDEX `idx_point_id`(`point_id`) USING BTREE,
  CONSTRAINT `knowledge_point_content_ibfk_1` FOREIGN KEY (`point_id`) REFERENCES `knowledge_point_base` (`point_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '知识点内容详情表(冷数据)' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of knowledge_point_content
-- ----------------------------
INSERT INTO `knowledge_point_content` VALUES (1, 1, '## 一、Cache映射概述\n\nCache映射是指主存地址与Cache地址之间的对应关系。主要有三种映射方式：\n\n### 1. 直接映射（Direct Mapping）\n\n**原理**：主存的每个块只能映射到Cache的固定位置。\n\n**映射规则**：\n```\nCache块号 = 主存块号 % Cache块数\n```\n\n**优点**：实现简单、成本低、查找速度快\n\n**缺点**：冲突率高、灵活性差\n\n### 2. 全相联映射（Fully Associative Mapping）\n\n**原理**：主存的任何一块都可以映射到Cache的任意位置。\n\n**优点**：冲突率最低、Cache利用率高\n\n**缺点**：硬件成本高、查找速度慢\n\n### 3. 组相联映射（Set Associative Mapping）\n\n**原理**：将Cache分成若干组，主存块可以映射到某一组的任意位置。是直接映射和全相联映射的折中方案。', NULL, NULL, '2025-11-29 20:13:04', NULL);
INSERT INTO `knowledge_point_content` VALUES (2, 2, '## 二叉树遍历概述\n\n二叉树的遍历是树结构中最基本的操作之一。\n\n### 1. 前序遍历（Pre-order）\n访问顺序：根 → 左 → 右\n\n**递归实现**：\n```java\nvoid preOrder(TreeNode root) {\n    if (root == null) return;\n    System.out.print(root.val + \" \");\n    preOrder(root.left);\n    preOrder(root.right);\n}\n```\n\n### 2. 中序遍历（In-order）\n访问顺序：左 → 根 → 右\n\n### 3. 后序遍历（Post-order）\n访问顺序：左 → 右 → 根\n\n### 4. 层序遍历（Level-order）\n使用队列实现按层访问', NULL, NULL, '2025-11-29 20:13:04', NULL);
INSERT INTO `knowledge_point_content` VALUES (3, 3, '## 快速排序原理\n\n快速排序是一种高效的排序算法，采用分治法思想。\n\n### 算法步骤\n1. 选择基准元素（pivot）\n2. 分区操作：比基准小的放左边，大的放右边\n3. 递归地对左右子数组进行快排\n\n### 代码实现\n```java\npublic void quickSort(int[] arr, int left, int right) {\n    if (left >= right) return;\n    int pivot = partition(arr, left, right);\n    quickSort(arr, left, pivot - 1);\n    quickSort(arr, pivot + 1, right);\n}\n```\n\n### 时间复杂度\n- 平均：O(n log n)\n- 最坏：O(n²)\n- 最好：O(n log n)', NULL, NULL, '2025-11-29 20:13:04', NULL);
INSERT INTO `knowledge_point_content` VALUES (4, 4, '## 进程 vs 线程\n\n### 定义\n- **进程**：资源分配的基本单位\n- **线程**：CPU调度的基本单位\n\n### 主要区别\n\n| 对比项 | 进程 | 线程 |\n|--------|------|------|\n| 资源 | 独立的地址空间 | 共享进程资源 |\n| 开销 | 创建销毁开销大 | 开销小 |\n| 通信 | IPC机制 | 直接读写 |\n| 崩溃影响 | 独立 | 影响整个进程 |\n\n### 使用场景\n- 进程：需要资源隔离的场景\n- 线程：需要频繁切换和通信的场景', NULL, NULL, '2025-11-29 20:13:04', NULL);
INSERT INTO `knowledge_point_content` VALUES (5, 5, '## 死锁概念\n\n多个进程因竞争资源而造成的僵局，若无外力作用，这些进程都将无法继续执行。\n\n### 死锁的四个必要条件\n1. **互斥条件**：资源不能共享\n2. **请求与保持**：持有资源的同时请求新资源\n3. **不可剥夺**：已获得的资源不能强行剥夺\n4. **循环等待**：存在进程资源的循环等待链\n\n### 死锁预防\n破坏四个必要条件之一：\n- 破坏互斥：资源共享（不现实）\n- 破坏请求与保持：一次性申请所有资源\n- 破坏不可剥夺：资源可抢占\n- 破坏循环等待：资源有序分配\n\n### 银行家算法\n著名的死锁避免算法，通过安全性检测避免系统进入不安全状态。', NULL, NULL, '2025-11-29 20:13:04', NULL);
INSERT INTO `knowledge_point_content` VALUES (6, 6, '## TCP连接管理\n\n### 三次握手（建立连接）\n1. **SYN**：客户端发送SYN=1，seq=x\n2. **SYN+ACK**：服务器回复SYN=1，ACK=1，seq=y，ack=x+1\n3. **ACK**：客户端发送ACK=1，seq=x+1，ack=y+1\n\n### 为什么是三次？\n- 防止旧的重复连接初始化\n- 同步双方的序列号\n- 避免资源浪费\n\n### 四次挥手（断开连接）\n1. **FIN**：主动方发送FIN=1\n2. **ACK**：被动方确认\n3. **FIN**：被动方发送FIN=1\n4. **ACK**：主动方确认\n\n### TIME_WAIT状态\n主动关闭方等待2MSL，确保被动方收到最后的ACK。', NULL, NULL, '2025-11-29 20:13:04', NULL);
INSERT INTO `knowledge_point_content` VALUES (7, 7, '## HTTP vs HTTPS\n\n### 核心区别\n- **HTTP**：超文本传输协议，明文传输\n- **HTTPS**：HTTP + SSL/TLS，加密传输\n\n### HTTPS加密流程\n1. 客户端发起HTTPS请求\n2. 服务器返回数字证书\n3. 客户端验证证书有效性\n4. 生成随机对称密钥\n5. 用服务器公钥加密发送\n6. 服务器私钥解密获得密钥\n7. 后续通信使用对称加密\n\n### 性能对比\n- HTTPS比HTTP慢（加密解密开销）\n- 现代优化：HTTP/2、TLS 1.3大幅提升性能\n\n### 端口\n- HTTP：80\n- HTTPS：443', NULL, NULL, '2025-11-29 20:13:04', NULL);
INSERT INTO `knowledge_point_content` VALUES (8, 8, '## ACID特性\n\n### 1. 原子性（Atomicity）\n事务中的所有操作要么全部完成，要么全部不完成。\n\n**实现**：通过undo log实现回滚\n\n### 2. 一致性（Consistency）\n事务执行前后，数据库从一个一致性状态转换到另一个一致性状态。\n\n### 3. 隔离性（Isolation）\n并发事务之间相互隔离，互不干扰。\n\n**隔离级别**：\n- READ UNCOMMITTED\n- READ COMMITTED\n- REPEATABLE READ（MySQL默认）\n- SERIALIZABLE\n\n### 4. 持久性（Durability）\n事务提交后，修改永久保存。\n\n**实现**：通过redo log保证', NULL, NULL, '2025-11-29 20:13:04', NULL);
INSERT INTO `knowledge_point_content` VALUES (9, 9, '## MySQL索引优化\n\n### 索引类型\n1. **B+树索引**：最常用，适合范围查询\n2. **哈希索引**：等值查询快，不支持排序\n3. **全文索引**：文本搜索\n\n### 索引设计原则\n1. 选择区分度高的列\n2. 最左前缀原则\n3. 避免索引列上使用函数\n4. 覆盖索引减少回表\n5. 避免使用SELECT *\n\n### 常见优化\n```sql\n-- 复合索引\nCREATE INDEX idx_user ON user(age, city, name);\n\n-- 覆盖索引\nSELECT id, name FROM user WHERE age = 20;\n```\n\n### 索引失效场景\n- 使用!=、<>、NOT IN\n- 使用OR连接\n- 在索引列上做运算', NULL, NULL, '2025-11-29 20:13:04', NULL);
INSERT INTO `knowledge_point_content` VALUES (18, 15, '**考点：指令格式**\n\n- **两部分：** 操作码 + 操作数\n- **一句话：** 操作码说明“干什么”，操作数说明“对谁干”。\n- **例子：** 指令 `ADD R1, R2, R3`**操作码：** `ADD`（执行加法操作）**操作数：** `R1, R2, R3`（把寄存器R2和R3的值相加，结果存到R1）\n\n\n\n', NULL, NULL, '2025-11-29 21:53:45', NULL);
INSERT INTO `knowledge_point_content` VALUES (19, 16, '# 0）版本说明\n\n- JDK 19\n- springboot 3.0\n- springCloud 2022\n\n# 1）新建父子项目\n\n## 1.0）创建空Maven项目，引入依赖\n\n![image-20251010094000654](https://cdn.zww0891.fun/image-20251010094000654.png)\n\n## 1.1）修改本地maven文件夹地址\n\n![image-20251010094141146](https://cdn.zww0891.fun/image-20251010094141146.png)\n\n## 1.2）配置父项目\n\n配置父项目pom.xml文件\n\n- 修改打包方式，\n- 增加模块标签，后续引入子服务\n- 增加dependencyManagement标签，后续管理子服务依赖版本\n\n![](https://cdn.zww0891.fun/image-20251010095011306.png)\n\n```xml\n<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n    <modelVersion>4.0.0</modelVersion>\n\n    <groupId>com.zww0891</groupId>\n    <artifactId>springCloudAlibaba</artifactId>\n    <version>1.0-SNAPSHOT</version>\n    <!-- 1.修改打包方式 -->\n    <packaging>pom</packaging>\n    <!-- 2.添加子模块 -->\n    <modules>\n\n    </modules>\n    <properties>\n        <maven.compiler.source>19</maven.compiler.source>\n        <maven.compiler.target>19</maven.compiler.target>\n        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n    </properties>\n    <!--  3.管理子模块的依赖版本 -->\n    <dependencyManagement>\n        <dependencies>\n            <!-- springcloud的依赖-->\n            <dependency>\n                <groupId>org.springframework.cloud</groupId>\n                <artifactId>spring-cloud-dependencies</artifactId>\n                <version>2022.0.0</version>\n                <type>pom</type>\n                <scope>import</scope>\n            </dependency>\n\n            <!--spring-cloud-alibaba-->\n            <dependency>\n                <groupId>com.alibaba.cloud</groupId>\n                <artifactId>spring-cloud-alibaba-dependencies</artifactId>\n                <version>2022.0.0.0-RC1</version>\n                <type>pom</type>\n                <scope>import</scope>\n            </dependency>\n\n            <!-- springboot的依赖-->\n            <dependency>\n                <groupId>org.springframework.boot</groupId>\n                <artifactId>spring-boot-dependencies</artifactId>\n                <version>3.0.0</version>\n                <type>pom</type>\n                <scope>import</scope>\n            </dependency>\n        </dependencies>\n    </dependencyManagement>\n</project>\n\n```\n\nSpring Cloud 版本对照表 https://baijiahao.baidu.com/s?id=1771358714767361684&wfr=spider&for=pc\n\n## 1.3）配置子项目\n\n### 创建springBoot项目\n\n删除父项目的src文件，创建springboot子项目\n\n![image-20251010100137135](https://cdn.zww0891.fun/image-20251010100137135.png)\n\n### 修改pom.xml\n\n修改父模块的版本，组id和工程id\n\n![image-20251010183417259](https://cdn.zww0891.fun/image-20251010183417259.png)\n\n引入spring  web 依赖，不指定版本号，由父模块管理\n\n```xml\n<dependency>\n    <groupId>org.springframework.boot</groupId>\n    <artifactId>spring-boot-starter-web</artifactId>\n</dependency>\n```\n\n\n\n### 修改application.yml\n\n指定端口号\n\n![image-20251010183742535](https://cdn.zww0891.fun/image-20251010183742535.png)\n\n### 加入父模块里的子模块里\n\n![image-20251010184233865](https://cdn.zww0891.fun/image-20251010184233865.png)\n\n写一个测试接口，使用postman或rest插件测试\n\n![image-20251010185620435](https://cdn.zww0891.fun/image-20251010185620435.png)\n\n同样的操作在复刻一个子项目，\n\n方便后续\n\n- openFeign远程调用\n- 网关路由\n- 多实例负载均衡\n\n# 2）配置Nacos注册中心\n\n## 2.0）组件对应版本\n\n[spring cloud alibaba 版本对应关系 – 每天进步一点点](https://www.longkui.site/program/java/spring-cloud-alibaba/6051/)\n\n![image-20251010185957136](https://cdn.zww0891.fun/image-20251010185957136.png)\n\nnacos**下载地址**\n\n[发布历史 | Nacos 官网](https://nacos.io/download/release-history/?spm=5238cd80.2ef5001f.0.0.3f613b7c790pld)\n\n## 2.1）修改nacos配置文件\n\n\"E:\\nacos-server-2.2.1\\nacos\\conf\\application.properties\"\n\n![image-20251010191854631](https://cdn.zww0891.fun/image-20251010191854631.png)\n\n**修改三个鉴权相关的配置**\n\n![image-20251010191458536](https://cdn.zww0891.fun/image-20251010191458536.png)\n\n```\nnacos.core.auth.server.identity.key=chenXuYuanJiage\nnacos.core.auth.server.identity.value=chenXuYuanJiage\nnacos.core.auth.plugin.nacos.token.secret.key=YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY=\n```\n\n>nacos.core.auth.plugin.nacos.token.secret.key要是32字节进行base64编码后的值\n>\n>![image-20251010192201001](https://cdn.zww0891.fun/image-20251010192201001.png)\n>\n>1. `nacos.core.auth.server.identity.key`/`value`：集群模式下 Nacos 节点的 “身份凭证”，确保只有合法节点能互相通信（如同步数据、检测心跳）；\n>2. `nacos.core.auth.plugin.nacos.token.secret.key`：生成、验证客户端 “访问令牌（Token）” 的密钥，控制客户端能否合法访问 Nacos。\n\n## 2.2）以单机模式启动\n\n```shell\nE:\\nacos-server-2.2.1\\nacos\\bin>\nstartup.cmd -m standalone\n```\n\n![image-20251010192258256](https://cdn.zww0891.fun/image-20251010192258256.png)\n\n## 2.3）访问前端页面\n\nhttp://localhost:8848/nacos\n\n账号密码均为nacos\n\n## 2.4）spring项目服务发现和配置注册\n\n**添加依赖**\n\n```xml\n<!-- nacos-->\n<dependency>\n    <groupId>com.alibaba.cloud</groupId>\n    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>\n</dependency>\n<dependency>\n    <groupId>com.alibaba.cloud</groupId>\n    <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>\n</dependency>\n```\n\n**配置文件**\n\n服务名和nacos地址\n\n这里把服务器的端口号配置在nacos配置中心，后续不需重新启动项目就能加载最新配置\n\n```yml\n# ====================================================================\n# Order Service 订单服务配置文件\n# ====================================================================\n\n# 服务器配置\n#server:\n#  port: 6660  # 订单服务端口号\n\n# Spring Boot 配置\nspring:\n  application:\n    name: order-service  # 服务名称，用于服务注册与发现\n  \n  # 配置导入设置（Spring Cloud 2020+ 新特性）\n  config:\n    # 从Nacos配置中心导入配置文件\n    # optional: 表示配置文件不存在时不会导致启动失败\n    # nacos: 指定配置中心类型为Nacos\n    # order-service-dev.yml: 配置文件名（开发环境）\n    import: optional:nacos:order-service-dev.yml\n  \n  # Spring Cloud 配置\n  cloud:\n    nacos:\n      # 服务发现配置\n      discovery:\n        server-addr: localhost:8848  # Nacos服务器地址\n      \n      # 配置中心配置\n      config:\n        server-addr: localhost:8848  # Nacos配置中心地址\n        file-extension: yml          # 配置文件扩展名\n\n```\n\n![image-20251011144221688](https://cdn.zww0891.fun/image-20251011144221688.png)\n\n然后要开启此热重载功能还需要加@RefreshScope注解\n\n## 2.5）测试热重载配置文件\n\n测试能否不重启服务达到配置生效的效果\n\n![](https://cdn.zww0891.fun/image-20251011144823945.png)\n\n![image-20251011145025922](https://cdn.zww0891.fun/image-20251011145025922.png)\n\n# 3）OpenFeign远程调用\n\n## 3.1）创建common模块存放实体\n\n把启动类和配置文件删掉，指创实体包，当前模块指定父模块，父模块引入当前模块，其他模块引入该模块依赖\n\n![image-20251011145821592](https://cdn.zww0891.fun/image-20251011145821592.png)\n\n![image-20251011150400564](https://cdn.zww0891.fun/image-20251011150400564.png)\n\n## 3.2）设置FeignClient接口\n\n在需要调用的地方设置client和依赖，如这里payment需要调用order的接口，那就在payment服务设置接口\n\n1.添加依赖\n\n2.feign客户端添加注解@FeignClient(value = \"order-service\") //value填远程调用的服务名，然后设置远程调用的接口方法签名（无函数体）\n\n3.启动类加注解@EnableFeignClients\n\n4.autowired注入接口并调用\n\n==1.==\n\n```xml\n<!-- openfeign-->\n            <dependency>\n                <groupId>org.springframework.cloud</groupId>\n                <artifactId>spring-cloud-starter-openfeign</artifactId>\n            </dependency>\n            <dependency>\n                <groupId>org.springframework.cloud</groupId>\n                <artifactId>spring-cloud-starter-loadbalancer</artifactId>\n            </dependency>\n```\n\n==2.==\n\n```java\n@FeignClient(value = \"order-service\")\npublic interface OrderServiceFeignClient {\n    @PostMapping(\"/payForOrder\")\n    public void payForOrder(Order orderId);\n}\n\n```\n\n==3.==\n\n```java\n@SpringBootApplication\n@EnableFeignClients\npublic class PaymentServiceApplication {\n\n    public static void main(String[] args) {\n        SpringApplication.run(PaymentServiceApplication.class, args);\n    }\n\n}\n```\n\n==4.==\n\n```java\npackage com.zww0891.paymentservice.controller;\n\nimport com.zww0891.commonmodule.entity.Order;\nimport com.zww0891.paymentservice.feignClient.OrderServiceFeignClient;\nimport org.springframework.beans.factory.annotation.Autowired;\nimport org.springframework.web.bind.annotation.GetMapping;\nimport org.springframework.web.bind.annotation.RequestParam;\nimport org.springframework.web.bind.annotation.RestController;\n\n@RestController\npublic class TestController {\n    @Autowired\n    private OrderServiceFeignClient orderServiceFeignClient;\n\n    @GetMapping(\"/payment-test\")\n    public String test() {\n        return \"payment-test\";\n    }\n\n    @GetMapping(\"/payment-order\")\n    public String  payMoney (@RequestParam String orderId) {\n        System.out.println(\"模拟支付成功...\");\n        // 调用订单服务的添加订单\n        Order order = new Order(666L, \"苹果\");\n 		orderServiceFeignClient.payForOrder(order);\n        return \"ok\";\n    }\n\n}\n\n```\n\n## 3.3）测试调用\n\n![image-20251011153953714](https://cdn.zww0891.fun/image-20251011153953714.png)\n\n# 4）多实例\n\n**概述**\n\n用httpClient发请求跨服务器调用太麻烦，用接口方法调用方法的方式符合习惯\n\n## 4.1）新增启动配置\n\n![image-20251011154321208](https://cdn.zww0891.fun/image-20251011154321208.png)\n\n## 4.2）新增配置文件\n\n![](https://cdn.zww0891.fun/image-20251011154718073.png)\n\n**然后修改一下配置文件再启动，方便指向远程nacos的yml配置文件**\n\n![image-20251011154829927](https://cdn.zww0891.fun/image-20251011154829927.png)\n\n**修改控制器，标识是新的实例**\n\n![image-20251011154932946](https://cdn.zww0891.fun/image-20251011154932946.png)\n\n**最后启动，nacos查看和发请求测试**\n\n![image-20251011155021526](https://cdn.zww0891.fun/image-20251011155021526.png)\n\n这里端口太大报错了，可以搞小一点\n\n![image-20251011155053971](https://cdn.zww0891.fun/image-20251011155053971.png)\n\n\n\n![image-20251011155459361](https://cdn.zww0891.fun/image-20251011155459361.png)\n\n# 5）网关\n\n**概述**\n\n前端遇到多个服务所要写不同的请求端口 \n\nhttp://localhost:6601/order-test\n\nhttp://localhost:6602/order-test\n\nhttp://localhost:6603/order-test.....\n\n非常麻烦，用了网关后就能和之前单体服务的请求路径一样了\n\nhttp://localhost:网关端口/服务名/order-test.....\n\n这样一来后续还能负载均衡，同个服务根据调度规则分发请求\n\n- 统一入口端口，内部转发各个服务器\n\n## 5.1）配置过程\n\n1.新建网关服务（省略...）\n\n2.引入网关,nacos发现和配置依赖\n\n```xml\n        <!-- nacos-->\n        <dependency>\n            <groupId>com.alibaba.cloud</groupId>\n            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>\n        </dependency>\n        <dependency>\n            <groupId>com.alibaba.cloud</groupId>\n            <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>\n        </dependency>\n\n        <!-- gateway-->\n        <dependency>\n            <groupId>org.springframework.cloud</groupId>\n            <artifactId>spring-cloud-starter-gateway</artifactId>\n        </dependency>\n        <dependency>\n            <groupId>org.springframework.cloud</groupId>\n            <artifactId>spring-cloud-starter-loadbalancer</artifactId>\n        </dependency>\n```\n\n3.服务注册nacos\n\n**application.yml**\n\n```yml\n# ====================================================================\n# Gateway Service 网关服务配置文件\n# ====================================================================\n\n# 服务器配置\n#server:\n#  port: 8800  # 网关服务端口号\n\n# Spring Boot 配置\nspring:\n  application:\n    name: gateway-service  # 服务名称，用于服务注册与发现\n\n  # 配置导入设置（Spring Cloud 2020+ 新特性）\n  config:\n    # 从Nacos配置中心导入配置文件\n    # optional: 表示配置文件不存在时不会导致启动失败\n    # nacos: 指定配置中心类型为Nacos\n    # gateway-service-dev.yml: 配置文件名（开发环境）\n    import: optional:nacos:gateway-service-dev.yml\n\n  # Spring Cloud 配置\n  cloud:\n    nacos:\n      # 服务发现配置\n      discovery:\n        server-addr: localhost:8848  # Nacos服务器地址\n\n      # 配置中心配置\n      config:\n        server-addr: localhost:8848  # Nacos配置中心地址\n        file-extension: yml          # 配置文件扩展名\n\n```\n\n**gateway-service-dev.yml**\n\n```yml\n# 服务器配置\nserver:\n  port: 8800  # 网关服务端口号\n```\n\n![image-20251011160829773](https://cdn.zww0891.fun/image-20251011160829773.png)\n\n4.nacos配置网关路由，规则  /服务ID/xxxx->具体服务\n\n```yml\nserver:\n  port: 8800\nspring:\n  redis:\n    host: localhost\n    port: 6379\n    #password: 123456  \n  cloud:\n    sentinel:\n      transport:\n        dashboard: localhost:8858\n        port: 8719  \n    gateway:\n      default-filters:\n        - AddRequestHeader=origin,gateway # 添加名为origin的请求头，值为gateway\n      globalcors:\n        cors-configurations:\n          \'[/**]\': # 匹配所有请求\n            allowedOrigins: \"*\" #跨域处理 允许所有的域\n            allowedMethods: # 支持的方法\n              - GET\n              - POST\n              - PUT\n              - DELETE\n      discovery:\n        locator:\n          enabled: true #开启注册中心路由功能\n      routes:  # 路由\n        - id: order-service #路由ID，没有固定要求，但是要保证唯一，建议配合服务名\n          uri: lb://order-service # 匹配提供服务的路由地址\n          predicates: # 断言\n            - Path=/order/** # 断言，路径相匹配进行路由\n        - id: payment-service #路由ID，没有固定要求，但是要保证唯一，建议配合服务名\n          uri: lb://payment-service # 匹配提供服务的路由地址\n          predicates: # 断言\n            - Path=/payment/** # 断言，路径相匹配进行路由\nconfig:\n  redisTimeout: 60\n```\n\n## 5.2）测试网关路由\n\n```java\n	/**\n     * TestController\n		测试网关路由\n     */\n@GetMapping(\"/order/add\")\npublic String addOrder() {\n    return \"成功根据网关路由到对应的服务！\";\n}\n```\n\n使用网关端口发送请求，查看是否有转发\n\n![image-20251011161916279](https://cdn.zww0891.fun/image-20251011161916279.png)\n\n# 6）网关鉴权\n\n## 6.1）思路\n\n1.写user-service服务，写登录方法拿token（固定返回一个，然后存到redis里）\n\n1.实现过滤器，重写方法，所有请求经过该方法\n\n2.请求获取请求头，拿到token与redis里的进行比对校验，过期则拦截，反之放行\n\n3.加入gateway路由规则\n\n## 6.2）相关依赖/配置文件\n\n### jwt（common-module）\n\n```xml\n<!-- jwt -->\n<dependency>\n    <groupId>com.auth0</groupId>\n    <artifactId>java-jwt</artifactId>\n    <version>4.4.0</version>\n</dependency>\n```\n\n### redis（common-module）\n\n```xml\n<dependency>\n    <groupId>org.springframework.boot</groupId>\n    <artifactId>spring-boot-starter-data-redis</artifactId>\n</dependency>\n```\n\n### json序列器（common-module）\n\n```xml\n<!-- jackson -->\n        <dependency>\n            <groupId>com.fasterxml.jackson.core</groupId>\n            <artifactId>jackson-databind</artifactId>\n        </dependency>\n        <dependency>\n            <groupId>com.fasterxml.jackson.core</groupId>\n            <artifactId>jackson-core</artifactId>\n        </dependency>\n        <dependency>\n            <groupId>com.fasterxml.jackson.core</groupId>\n            <artifactId>jackson-annotations</artifactId>\n        </dependency>\n```\n\n\n\n### user-service配置文件\n\n**application.yml**\n\n```yml\nspring:\n  application:\n    name: user-service\n  config:\n    import: nacos:user-service-dev.yml\n  cloud:\n    nacos:\n      discovery:\n        server-addr: localhost:8848\n      config:\n        server-addr: localhost:8848\n        file-extension: yml\n\n```\n\n**user-service-dev.yml**\n\n```yml\nserver:\n  port: 5500\nspring:\n  cloud:\n    sentinel:\n      transport:\n        dashboard: localhost:8080\n        port: 8719\n  redis:\n    host: localhost\n    port: 6379\n    #password: 123456\nredis:\n  timeout: 60\n```\n\n**gateway-service-dev.yml**\n\n```yml\n- id: user-service #路由ID，没有固定要求，但是要保证唯一，建议配合服务名\n          uri: lb://user-service # 匹配提供服务的路由地址\n          predicates: # 断言\n            - Path=/user/** # 断言，路径相匹配进行路由\n```\n\n### redisConfig\n\n```\n\n```\n\n### gateway和userservice服务pom引入common\n\n```xml\n <!-- 公共模块依赖 -->\n        <dependency>\n            <groupId>com.zww0891</groupId>\n            <artifactId>common-module</artifactId>\n            <version>0.0.1-SNAPSHOT</version>\n        </dependency>\n```\n\n\n\n## 6.4）测试\n\n\n\n## 6.3）相关类\n\n### jwtUtil（common->util）\n\n```java\npackage com.ushio.commonmodule.entity.util;\n\n\nimport com.auth0.jwt.JWT;\nimport com.auth0.jwt.JWTCreator;\nimport com.auth0.jwt.JWTVerifier;\nimport com.auth0.jwt.algorithms.Algorithm;\nimport com.auth0.jwt.interfaces.Claim;\nimport com.auth0.jwt.interfaces.DecodedJWT;\n\nimport java.util.Date;\nimport java.util.HashMap;\nimport java.util.Map;\n\npublic class JwtUtil {\n    //用户的用户名\n    private static final String USERNAME = \"admin\";\n\n    //用于签名加密的密钥，为一个字符串（需严格保密）\n    private static final String KEY = \"token_key\";\n\n    private static final int TOKEN_TIME_OUT = 1000 * 60 * 60;\n\n    public static String getToken(String userId) {\n\n        //获取jwt生成器\n        JWTCreator.Builder jwtBuilder = JWT.create();\n\n        //由于该生成器设置Header的参数为一个<String, Object>的Map,\n        //所以我们提前准备好\n        Map<String, Object> headers = new HashMap<>();\n\n        headers.put(\"typ\", \"jwt\");   //设置token的type为jwt\n        headers.put(\"alg\", \"hs256\");  //表明加密的算法为HS256\n\n        //开始生成token\n        //我们将之前准备好的header设置进去\n        String token = jwtBuilder.withHeader(headers)\n\n                //接下来为设置PayLoad,Claim中的键值对可自定义\n\n                //设置用户id\n                .withClaim(\"userId\", userId)\n\n                //token失效时间，这里为一消失后失效\n                .withExpiresAt(new Date(System.currentTimeMillis() + TOKEN_TIME_OUT))\n                //设置该jwt的发行时间，一般为当前系统时间\n                .withIssuedAt(new Date(System.currentTimeMillis()))\n\n                //token的发行者（可自定义）\n                .withIssuer(USERNAME)\n\n                //进行签名，选择加密算法，以一个字符串密钥为参数\n                .sign(Algorithm.HMAC256(KEY));\n\n        //token生成完毕，可以发送给客户端了，前端可以使用\n        //localStorage.setItem(\"your_token\", token)进行存储，在\n        //下次请求时携带发送给服务器端进行验证\n        System.out.println(token);\n        return token;\n    }\n\n\n    public static boolean verify(String token) {\n\n        /*从请求头中获取token（具体要看你的token放在了请求的哪里，\n           这里以放在请求头举例）\n        */\n        //String token = request.getHeader(\"token\");\n\n        /*判断token是否存在，若不存在，验证失败，\n            并进行验证失败的逻辑操作（例如跳转到登录界面，\n            或拒绝访问等等）*/\n        if (token == null) return false;\n\n        /*获取jwt的验证器对象，传入的算法参数以及密钥字符串（KEY）必须\n        和加密时的相同*/\n        JWTVerifier require = JWT.require(Algorithm.HMAC256(KEY)).build();\n\n        DecodedJWT decode;\n        try {\n\n            /*开始进行验证，该函数会验证此token是否遭到修改，\n                以及是否过期，验证成功会生成一个解码对象\n                ，如果token遭到修改或已过期就会\n                抛出异常，我们用try-catch抓一下*/\n            decode = require.verify(token);\n\n        } catch (Exception e) {\n\n            //抛出异常，验证失败\n            return false;\n        }\n\n        //若验证成功，就可获取其携带的信息进行其他操作\n\n        //可以一次性获取所有的自定义参数，返回Map集合\n        Map<String, Claim> claims = decode.getClaims();\n        if (claims == null) return false;\n        claims.forEach((k, v) -> System.out.println(k + \" \" + v.asString()));\n\n        //也可以根据自定义参数的键值来获取\n        if (decode.getClaim(\"userId\") == null) return false;\n        System.out.println(decode.getClaim(\"userId\").toString());\n\n        //获取发送者，没有设置则为空\n        System.out.println(decode.getIssuer());\n\n        //获取过期时间\n        System.out.println(decode.getExpiresAt());\n\n        //获取主题，没有设置则为空\n        System.out.println(decode.getSubject());\n        return true;\n    }\n}\n```\n\n### UserController\n\n```java\n@RestController\n@RequestMapping(\"/user\")\npublic class UserController {\n    @Autowired\n    private RedisTemplate redisTemplate;\n\n    @Value(\"${redis.timeout}\")\n    private long redisTimeout ;\n\n    @PostMapping(\"/login\")\n    public String login() {\n        //1.TODO:账号密码校验成功...\n        // 2.生成token存入redis\n        String userId = \"666\";\n        String token = JwtUtil.getToken(userId);\n        redisTemplate.opsForValue().set(token,userId,redisTimeout, TimeUnit.SECONDS);\n        // 3.返回token\n        return token;\n    }\n}\n\n```\n\n### GlobalFilterConfig\n\n```java\npackage com.zww0891.gatewayservice.config;\n\nimport org.springframework.beans.factory.annotation.Autowired;\nimport org.springframework.cloud.gateway.filter.GatewayFilterChain;\nimport org.springframework.cloud.gateway.filter.GlobalFilter;\nimport org.springframework.core.Ordered;\nimport org.springframework.core.io.buffer.DataBuffer;\nimport org.springframework.data.redis.core.RedisTemplate;\nimport org.springframework.http.HttpStatus;\nimport org.springframework.http.server.reactive.ServerHttpRequest;\nimport org.springframework.http.server.reactive.ServerHttpResponse;\nimport org.springframework.stereotype.Component;\nimport org.springframework.web.server.ServerWebExchange;\nimport reactor.core.publisher.Mono;\n\nimport java.nio.charset.StandardCharsets;\nimport java.util.HashSet;\nimport java.util.Set;\n\n@Component\npublic class GlobalFilterConfig implements GlobalFilter, Ordered {\n    @Autowired\n    private RedisTemplate<String, Object> redisTemplate;\n\n    // 白名单路径集合，可根据实际需求添加更多路径\n    private static final Set<String> WHITE_LIST_PATHS = new HashSet<String>() {{\n        add(\"/user/login\");\n        add(\"/user/register\");\n        add(\"/api/public/health\");\n        add(\"/swagger-ui.html\");\n        add(\"/swagger-resources\");\n        add(\"/v3/api-docs\");\n        // 可以继续添加其他需要放行的路径\n    }};\n\n    @Override\n    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {\n        // 1. 接口白名单放行\n        ServerHttpRequest request = exchange.getRequest();\n        String path = request.getURI().getPath();\n\n        // 检查当前路径是否在白名单中\n        if (WHITE_LIST_PATHS.contains(path)) {\n            return chain.filter(exchange);\n        }\n\n        // 2. 从请求头获取Token\n        String token = request.getHeaders().getFirst(\"Access-Token\");\n\n        // 3. Token不存在的情况\n        if (token == null || token.trim().isEmpty()) {\n            return buildErrorResponse(exchange, HttpStatus.UNAUTHORIZED, \"Token不能为空\");\n        }\n\n        // 4. 验证Token是否在Redis中存在\n        System.out.println(\"🔍 Gateway验证Token: \" + token);\n        boolean hasKey = redisTemplate.hasKey(token);\n        System.out.println(\"🔍 Redis中是否存在该Token: \" + hasKey);\n        \n        if (!hasKey) {\n            System.out.println(\"❌ Token验证失败，Token不存在或已过期\");\n            return buildErrorResponse(exchange, HttpStatus.UNAUTHORIZED, \"无效的Token或Token已过期\");\n        }\n        \n        System.out.println(\"✅ Token验证通过，继续请求\");\n\n        // 5. Token验证通过，继续执行后续过滤器链\n        return chain.filter(exchange);\n    }\n\n    /**\n     * 构建错误响应\n     */\n    private Mono<Void> buildErrorResponse(ServerWebExchange exchange, HttpStatus status, String message) {\n        ServerHttpResponse response = exchange.getResponse();\n        response.setStatusCode(status);\n        response.getHeaders().add(\"Content-Type\", \"application/json;charset=UTF-8\");\n\n        String json = \"{\\\"code\\\":\" + status.value() + \",\\\"message\\\":\\\"\" + message + \"\\\"}\";\n        DataBuffer buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));\n\n        return response.writeWith(Mono.just(buffer));\n    }\n\n    @Override\n    public int getOrder() {\n        return 0;\n    }\n}\n\n```\n\n### RedisConfig\n\n>user和gateway 的服务 在启动类要记得扫描\n>\n>```java\n>@SpringBootApplication(scanBasePackages = {\"com.zww0891.gatewayservice\", \"com.zww0891.commonmodule\"})\n>```\n>\n>\n\n```java\npackage com.zww0891.commonmodule.config;\n\nimport com.fasterxml.jackson.annotation.JsonAutoDetect;\nimport com.fasterxml.jackson.annotation.PropertyAccessor;\nimport com.fasterxml.jackson.databind.ObjectMapper;\nimport com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;\nimport org.springframework.context.annotation.Bean;\nimport org.springframework.context.annotation.Configuration;\nimport org.springframework.data.redis.connection.RedisConnectionFactory;\nimport org.springframework.data.redis.core.RedisTemplate;\nimport org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;\nimport org.springframework.data.redis.serializer.StringRedisSerializer;\n\n@Configuration\npublic class RedisConfig {\n\n    @Bean\n    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {\n        RedisTemplate<String, Object> template = new RedisTemplate<>();\n\n        template.setConnectionFactory(redisConnectionFactory);\n\n        //配置序列化方式 - 使用Spring Boot 3.0推荐的方式\n        // 创建自定义的ObjectMapper\n        ObjectMapper objectMapper = new ObjectMapper();\n        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public\n        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);\n        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会抛出异常\n        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);\n        \n        // 使用自定义ObjectMapper创建序列化器，避免使用已弃用的setObjectMapper方法\n        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);\n\n        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();\n        \n        // key采用String的序列化方式\n        template.setKeySerializer(stringRedisSerializer);\n        // hash key采用String的序列化方式\n        template.setHashKeySerializer(stringRedisSerializer);\n        // value采用Jackson的序列化方式\n        template.setValueSerializer(jackson2JsonRedisSerializer);\n        // hash value也采用Jackson的序列化方式\n        template.setHashValueSerializer(jackson2JsonRedisSerializer);\n        \n        template.afterPropertiesSet();\n        return template;\n    }\n\n}\n\n```\n\n\n\n\n\n# 7）Sentinel熔断降级\n\n## 7.1）控制台安装\n\n版本对应1.8.6\n\n![image-20251011163151475](https://cdn.zww0891.fun/image-20251011163151475.png)\n\n[Releases · alibaba/Sentinel](https://github.com/alibaba/Sentinel/releases)\n\n**启动命令**\n\n```\njava -jar jar包名\n```\n\n**前端入口**\n\n[Sentinel Dashboard](http://localhost:8858/#/login)\n\n密码和账号都是sentinel\n\n>只有有请求才会有服务显示\n\n## 7.2）依赖\n\n```xml\n<!-- sentinel -->\n        <dependency>\n            <groupId>com.alibaba.cloud</groupId>\n            <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>\n        </dependency>\n\n```\n\n网关服务需额外加的依赖，控制路由级别的熔断降级 \n\n```xml\n		<!---->\n        <dependency>\n            <groupId>com.alibaba.cloud</groupId>\n            <artifactId>spring-cloud-alibaba-sentinel-gateway</artifactId>\n        </dependency>\n\n```\n\n\n\n## 7.3）yml配置文件\n\n从属cloud下的组件，配置再cloud下\n\n![image-20251011165437681](https://cdn.zww0891.fun/image-20251011165437681.png)\n\n```yml\nspring:\n  redis:\n    host: localhost\n    port: 6379\n  cloud:\n    sentinel:\n      transport:\n        dashboard: localhost:8858 #看板的前端地址\n        port: 8719  #后台地址\n```\n\n\n\n## 7.4）网关/非网关服务配置流控规则\n\n**网关留空流控规则**\n\n![image-20251012160227835](https://cdn.zww0891.fun/image-20251012160227835.png)\n\napi类型可以按route id一个大类来分，也可以新建api分组对某个接口限流\n\nburst size就是上下浮动数\n\n匀速排队就是这个请求排队超时后直接中断\n\n**非网关流控规则**\n\n![image-20251012160926427](https://cdn.zww0891.fun/image-20251012160926427.png)\n\nwarm up就是前几个请求在规定的时间完成，慢慢的增加，前几个控制完速度后后续就不控制速度了\n\n好比100个请求，阈值为10，预热时长为3，那么前10个限制在3秒内才能发完，后90个无所谓\n\n## 7.5）熔断\n\n![image-20251012161630336](https://cdn.zww0891.fun/image-20251012161630336.png)\n\n慢调用比例，为0.5就是10个里有50%个慢调用超过设置的最大RT，则熔断\n\n请求数和统计时长就是规定时间内必须要达到指定的请求数量才生效\n\n\n\n```java\n    /**\n     * 慢调用\n     */\n    @GetMapping(\"/order/timeout\")\n    public void orderTimeout() {\n        try {\n            Thread.sleep(10000);\n        } catch (InterruptedException e) {\n            throw new RuntimeException(e);\n        }\n    }\n// 用于存储异常信息的列表\n    private List<Exception> exceptionList = new ArrayList<>();\n    // 异常计数器\n    private int exceptionCount = 0;\n    // 最大记录异常数量\n    private static final int MAX_EXCEPTION_COUNT = 5;\n   /**\n     * 异常比例 - 只记录前5个发生的异常\n     */\n    @GetMapping(\"/order/exception\")\n    public void exceptionProportion() {\n        try {\n            // 这里会产生算术异常\n            int i = 10 / 0;\n        } catch (Exception e) {\n            // 只记录前5个异常\n            if (exceptionCount < MAX_EXCEPTION_COUNT) {\n                exceptionList.add(e);\n                exceptionCount++;\n                System.out.println(\"已记录第\" + exceptionCount + \"个异常: \" + e.getMessage());\n            } else {\n                System.out.println(\"已达到最大异常记录数量(\" + MAX_EXCEPTION_COUNT + \")，不再记录新异常\");\n            }\n        }\n    }\n```\n\n## 7.6）热点参数\n\n![image-20251012165132100](https://cdn.zww0891.fun/image-20251012165132100.png)\n\n![image-20251012165115073](https://cdn.zww0891.fun/image-20251012165115073.png)\n\n- 资源名用@SentinelResource注解标明\n\n- 参数索引0为第一个参数\n\n\n\n意思就是传这个参数的请求多了就降级走别的方法（参数要一致，且要多一个BLockException）\n\n![image-20251012165511534](https://cdn.zww0891.fun/image-20251012165511534.png)\n\n还可以对参数值设置条件\n\n# 8）授权\n\n说白了就是只允许达到某个条件的请求进行请求\n\n## 8.1）网关服务统一加上请求头\n\n![image-20251012170754359](https://cdn.zww0891.fun/image-20251012170754359.png)\n\n```yml\ndefault-filters:\n        - AddRequestHeader=origin,gateway # 添加名为origin的请求头，值为gateway\n```\n\n## 8.2）提取请求头的值与授权的值比对\n\n在需要限制请求的服务里加入\n\n```java\npackage com.ushio.userservice.coinfig;\n\nimport com.alibaba.cloud.commons.lang.StringUtils;\nimport com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;\nimport jakarta.servlet.http.HttpServletRequest;\nimport org.springframework.stereotype.Component;\n\n/**\n * Sentinel 请求来源解析器\n * 用于解析HTTP请求的来源，为Sentinel流量控制提供请求来源标识\n * 主要用于实现基于来源的流量控制和熔断降级\n */\n@Component\npublic class HeaderOriginParser implements RequestOriginParser {\n    \n    /**\n     * 解析请求来源\n     * 从HTTP请求头中获取\"origin\"字段作为请求来源标识\n     * \n     * @param httpServletRequest HTTP请求对象\n     * @return 请求来源标识，如果origin为空则返回\"blank\"\n     */\n    @Override\n    public String parseOrigin(HttpServletRequest httpServletRequest) {\n        // 从请求头中获取origin字段\n        String origin = httpServletRequest.getHeader(\"origin\");\n        \n        // 如果origin为空，返回默认标识\"blank\"\n        if(StringUtils.isEmpty(origin)) {\n            return \"blank\";\n        }\n        \n        // 返回实际的请求来源\n        return origin;\n    }\n}\n\n\n```\n\n返回实际来源，与sentinel控制台的比对，是否在白名单或者黑名单里\n\n![image-20251012171127498](https://cdn.zww0891.fun/image-20251012171127498.png)\n\n## 8.3）测试授权\n\n可以根据网关访问\n\n![image-20251012171152383](https://cdn.zww0891.fun/image-20251012171152383.png)\n\n不可直接访问\n\n![image-20251012171237844](https://cdn.zww0891.fun/image-20251012171237844.png)\n\n不过你也可以伪造，因此这个条件最好设置苛刻一点\n\n![image-20251012171306848](https://cdn.zww0891.fun/image-20251012171306848.png)\n\n# 9）ELK搭建', NULL, NULL, '2025-11-29 22:02:53', NULL);
INSERT INTO `knowledge_point_content` VALUES (20, 17, '**考点：冯·诺依曼体系结构核心思想**\n\n*   **五大部件：** 运算器、控制器、存储器、输入设备、输出设备。\n*   **一句话：** “存储程序”，即程序和数据都以二进制的形式放在同一个存储器中。\n*   **例子：** 你电脑开机流程：\n    1.  **输入设备**（键盘鼠标）接受指令。\n    2.  **存储器**（内存）从硬盘加载操作系统程序和数据。\n    3.  **控制器**（CPU一部分）从内存逐条读取指令。\n    4.  **运算器**（CPU另一部分）执行计算（如1+1）。\n    5.  **输出设备**（显示器）显示结果。', NULL, NULL, '2025-11-30 12:47:55', NULL);
INSERT INTO `knowledge_point_content` VALUES (21, 18, '### 考点：指令集体系结构分类（ISA - Instruction Set Architecture）\n![](https://cdn.zww0891.fun/image-20251130150612514-17644863949521.png)\n这题考的是**CPU处理数据时，数据存放在哪里的4种不同方式**。\n\n---\n\n### 核心概念\n\n**问题**：CPU做运算时，操作数（比如A、B这些数）存在哪？\n\n**4种方案**：\n\n### **(a) 堆栈结构（Stack）**\n\n- **数据存哪**：栈顶（TOS = Top of Stack）\n- **怎么用**：\n  - `PUSH A` - 把A压进栈\n  - `PUSH B` - 把B压进栈\n  - `ADD` - 自动取栈顶两个数相加\n  - `POP C` - 结果弹出给C\n- **大白话**：像叠盘子，只能操作最上面的\n\n### **(b) 累加器结构（Accumulator）**\n\n- **数据存哪**：累加器（黑色方块，专门存中间结果）\n- **怎么用**：\n  - `LOAD A` - 把A装进累加器\n  - `ADD B` - 累加器的值加上B\n  - `STORE C` - 累加器结果存到C\n- **大白话**：有个专用的草稿纸，所有计算都在这张纸上做\n\n### **(c) 通用寄存器-内存型（RM = Register-Memory）**\n\n- **数据存哪**：寄存器R1 + 内存\n- **怎么用**：\n  - `LOAD R1,A` - 把A装进R1\n  - `ADD R1,B` - R1加上内存里的B\n  - `STORE R1,C` - R1结果存到C\n- **大白话**：有多个小抽屉（寄存器），可以直接从大仓库（内存）拿东西算\n\n### **(d) 通用寄存器-寄存器型（RR = Register-Register）**\n\n- **数据存哪**：全在寄存器R1、R2、R3\n- **怎么用**：\n  - `LOAD R1,A` - A→R1\n  - `LOAD R2,B` - B→R2\n  - `ADD R3, R1, R2` - R1+R2→R3\n  - `STORE C` - R3→C\n- **大白话**：所有计算只在小抽屉之间搬，最快但抽屉数量有限\n\n---\n\n### 关键区别\n\n| 类型   | 操作数位置               | 指令数 | 速度 |\n| ------ | ------------------------ | ------ | ---- |\n| 堆栈   | 隐式（栈顶）             | 最少   | 慢   |\n| 累加器 | 1个固定位置              | 少     | 较慢 |\n| RM     | 一个在寄存器，一个在内存 | 中等   | 中等 |\n| RR     | 全在寄存器               | 多     | 最快 |\n\n**现代CPU多用RR结构**（比如x86的EAX、EBX等寄存器）。\n\n', NULL, NULL, '2025-11-30 14:59:17', '2025-11-30 15:55:58');
INSERT INTO `knowledge_point_content` VALUES (22, 19, '![image-20251130150457756](https://cdn.zww0891.fun/image-20251130150457756.png)\n\n### 通用寄存器型指令集的3种结构\n\n**RR结构（Register-Register，寄存器-寄存器）**\n\n- 全称：Register-Register\n- 大白话：数据只能在寄存器之间操作，比如 `ADD R1, R2, R3`（把R2和R3加起来放到R1）\n- 特点：最快，但指令数量多（因为要先把数据从内存搬到寄存器）\n\n**RM结构（Register-Memory，寄存器-内存）**\n\n- 全称：Register-Memory  \n- 大白话：一个操作数在寄存器，另一个可以直接从内存拿，比如 `ADD R1, [内存地址]`\n- 特点：折中方案，指令少一些但速度也还行\n\n**MM结构（Memory-Memory，内存-内存）**\n\n- 全称：Memory-Memory\n- 大白话：操作数直接从内存到内存，不用经过寄存器\n- 特点：指令最少，但最慢（内存访问慢）\n\n### 指令中的操作数类型\n\n**常数**：直接写在指令里的数字，比如 `ADD R1, 5`（把5加到R1上）\n\n**寄存器操作数**：数据在CPU内部的小格子（寄存器）里，读取超快\n\n**存储器操作数**：数据在内存（RAM）里，需要通过地址去取\n\n### CISC和RISC的设计理念\n\n**CISC（Complex Instruction Set Computer，复杂指令集计算机）**\n\n- 全称：Complex Instruction Set Computer\n- 大白话：**指令功能超强，一条指令能干很多事**\n- 设计思路：让硬件干更多活，程序员写代码更轻松\n- 举例：一条指令能完成\"从内存读数据→计算→写回内存\"全流程\n- 问题：**指令太复杂，硬件执行慢，CPI（每条指令的时钟周期）大**\n\n**RISC（Reduced Instruction Set Computer，精简指令集计算机）**\n\n- 全称：Reduced Instruction Set Computer\n- 大白话：**指令功能简单，但执行超快**\n- 设计思路：指令越简单，硬件执行越快\n- 特点：指令数量少、功能简单、大部分一个时钟周期完成\n- 举例：要完成上面的事需要3条指令：`LOAD`→`ADD`→`STORE`\n\n### CISC指令集的优化方法（面向目标程序）\n\n**核心思想**：分析程序运行情况，找出**高频指令**和**耗时指令**，然后针对性优化\n\n**两种优化策略**：\n\n1. **使用频度高的指令→用硬件加速**\n   - 发现某条指令天天用，就把它做成专门的硬件电路\n   - 好处：执行超快\n\n2. **使用频度高的指令串→合并成一条新指令**\n   - 发现某几条指令总是一起出现（比如\"数组访问\"总是要算地址→读数据），就把它们合并成一条超级指令\n   - 好处：减少指令数量，程序更短，执行更快\n\n**效果**：\n\n- 程序执行时间变短\n- 程序代码长度变短（因为一条指令能干多件事）', NULL, NULL, '2025-11-30 15:29:46', '2025-11-30 15:55:49');
INSERT INTO `knowledge_point_content` VALUES (23, 20, '![image-20251130154856577](https://cdn.zww0891.fun/image-20251130154856577.png)\n### 寄存器寻址\n\n- 大白话：**数据直接在CPU内部的小盒子（寄存器）里**\n- 实例：`Add R4, R3` \n- 含义：把R3寄存器的值加到R4寄存器上\n- 特点：最快，因为数据就在CPU内部\n\n### 立即值寻址（Immediate Addressing）\n\n- 大白话：**数据直接写在指令里，是个固定数字**\n- 实例：`Add R4, #3`\n- 含义：把数字3直接加到R4寄存器上\n- 特点：数据是\"写死\"的，不能变\n\n### 偏移寻址（Displacement Addressing）\n\n- 大白话：**基地址+偏移量=真实地址**，像门牌号+楼层号\n- 实例：`Add R4, 100(R1)`\n- 含义：从内存地址（R1寄存器的值+100）取数据，加到R4上\n- 应用：访问数组、结构体成员\n\n### 寄存器间接寻址（Register Indirect Addressing）\n\n- 大白话：**寄存器里存的不是数据，而是数据的地址**，像地址本\n- 实例：`Add R4, (R1)`\n- 含义：R1寄存器存的是内存地址，从那个地址取数据加到R4上\n- 特点：多了一次\"查地址本\"的步骤\n\n### 索引寻址（Indexed Addressing）\n\n- 大白话：**基址+两个偏移量**，像\"几号楼+几单元+几层\"\n- 实例：`Add R3, (R1 + R2)`\n- 含义：从内存地址（R1+R2）取数据，加到R3上\n- 应用：二维数组访问，R1是行基址，R2是列偏移\n\n\n\n>### 索引寻址的二维数组访问详解\n>\n>**生活中的类比**：\n>\n>- 想象一个**小区停车场**，有很多排车位\n>- 每排有很多个车位\n>- 你要找到具体的车位，需要：**第几排 + 这排的第几个位置**\n>\n>**技术原理**：\n>假设有个二维数组 `int arr[3][4]`（3行4列的整数数组）\n>\n>```\n>内存布局：\n>行0: [10][20][30][40]  ← 内存地址1000开始\n>行1: [50][60][70][80]  ← 内存地址1016开始  \n>行2: [90][91][92][93]  ← 内存地址1032开始\n>```\n>\n>**访问 arr[1][2]（第1行第2列的元素70）**：\n>\n>1. **R1存行基址**：\n>   - 数组起始地址：1000\n>   - 第1行的起始地址 = 1000 + 1×(4×4) = 1016\n>   - R1 = 1016\n>\n>2. **R2存列偏移**：\n>   - 第2列的偏移 = 2×4 = 8字节\n>   - R2 = 8\n>\n>3. **最终地址**：\n>   - 地址 = R1 + R2 = 1016 + 8 = 1024\n>   - 从地址1024取出数据70\n>\n>**为什么这样设计**：\n>\n>- **R1（行基址）**：帮你快速跳到正确的\"排\"\n>- **R2（列偏移）**：帮你在这排里找到正确的\"位置\"\n>- **一条指令搞定**：不用先算行地址，再算列地址\n>\n>**实际应用场景**：\n>\n>```c\n>// C代码：访问二维数组\n>int matrix[10][20];\n>int value = matrix[i][j];\n>\n>// 对应的汇编可能是：\n>// R1 = 数组基址 + i*20*4  (行基址)\n>// R2 = j*4                (列偏移)  \n>// Add R3, (R1 + R2)       (取数据)\n>```\n>\n>**总结**：索引寻址就是用两个寄存器配合，一个定位\"大概位置\"，另一个定位\"精确偏移\"，特别适合处理表格类数据。\n>\n>\n\n### 直接绝对寻址（Direct Absolute Addressing）\n\n- 大白话：**直接给出内存的具体门牌号**\n- 实例：`Add R1, (1001)`\n- 含义：从内存地址1001取数据，加到R1上\n- 特点：地址是写死的，通常用于访问全局变量\n\n### 存储器间接寻址（Memory Indirect Addressing）\n\n- 大白话：**地址的地址**，像\"地址本的地址\"，要查两次\n- 实例：`Add R1, @(R3)`\n- 含义：R3指向的内存位置存的是另一个地址，从那个地址取数据\n- 应用：指针的指针，函数指针表\n\n### 自增寻址（Post-increment Addressing）\n\n- 大白话：**用完地址后自动+1，像自动翻页**\n- 实例：`Add R1, (R2)+`\n- 含义：\n  - 从R2指向的地址取数据加到R1\n  - 然后R2自动加上数据长度d（比如4字节）\n- 应用：遍历数组，每次处理下一个元素\n\n### 自减寻址（Pre-decrement Addressing）\n\n- 大白话：**用地址前先-1，像先退一格再使用**\n- 实例：`Add R1, -(R2)`\n- 含义：\n  - R2先减去数据长度d\n  - 然后从新地址取数据加到R1\n- 应用：堆栈操作，压栈时使用\n\n### 缩放寻址（Scaled Addressing）\n\n- 大白话：**索引值要乘个倍数**，像\"每层有d个房间\"\n- 实例：`Add R1, 100(R2)[R3]`\n- 含义：从地址（R1 + 100 + R2 + R3×d）取数据\n- 应用：访问不同大小的数据类型数组\n- 解释：d是数据类型大小，int是4，char是1\n\n**重点总结**：\n\n- 寄存器寻址最快\n- 立即值寻址数据固定\n- 间接寻址要多查一次内存，较慢\n- 自增/自减寻址适合循环操作\n- 缩放寻址处理不同大小的数据类型\n\n', NULL, NULL, '2025-11-30 15:48:27', '2025-11-30 15:55:40');
INSERT INTO `knowledge_point_content` VALUES (24, 21, '### 指令集结构的4个基本要求\n\n**完整性**\n\n- 大白话：**指令要够全，什么活都能干**\n- 解释：加减乘除、内存操作、跳转判断，该有的都得有，不能缺功能\n\n**规整性** \n\n- 大白话：**指令格式要统一，不能乱七八糟**\n- 解释：同类指令的写法要一致，程序员容易记忆和使用\n\n**高效率**\n\n- 大白话：**常用的指令要快，执行效率要高**\n- 解释：经常用的指令优先优化，让程序跑得快\n\n**兼容性**\n\n- 大白话：**新CPU要能运行老程序**\n- 解释：升级硬件时，以前的软件还能用，不用重新开发\n\n### 控制流指令（改变程序执行顺序的指令）\n\n**条件分支**：\n\n- 大白话：**\"如果...就...\"的判断跳转**\n- 例子：if (a > b) 跳到某个地方\n\n**跳转**：\n\n- 大白话：**无条件直接跳到指定位置**\n- 例子：goto语句，直接跳转\n\n**过程调用**：\n\n- 大白话：**调用函数，执行完再回来**\n- 例子：调用一个计算函数\n\n**过程返回**：\n\n- 大白话：**函数执行完，回到调用的地方继续**\n- 例子：函数的return语句\n\n### 基本汇编指令\n\n**LOAD指令**：\n\n- 大白话：**从内存搬数据到寄存器**，像\"从仓库拿货到柜台\"\n- 作用：把存储器里的数据加载到寄存器，方便CPU处理\n\n**STORE指令**：\n\n- 大白话：**从寄存器写数据到内存**，像\"把柜台的货存到仓库\"  \n- 作用：把寄存器的数据保存到存储器里，进行持久存储\n\n**MOV指令（Move传送指令）**：\n\n- 全称：Move\n- 大白话：**数据搬家，从一个地方移到另一个地方**\n- 功能：可以在寄存器之间、寄存器与内存之间、立即数与寄存器/内存之间传送数据\n\n**PUSH指令（入栈指令）**：\n\n- 全称：Push onto the stack\n- 大白话：**把数据压到堆栈顶部**，像\"往盒子里放东西\"\n- 工作原理：\n  - 堆栈指针（SP，Stack Pointer）会自动调整\n  - 16位数据：SP减2\n  - 32位数据：SP减4\n  - 这样确保新数据放在正确位置，不会覆盖旧数据\n\n**堆栈的概念**：\n\n- 大白话：**后进先出的数据结构**，像一摞盘子，最后放的最先拿出来\n- SP（Stack Pointer）：**堆栈指针**，永远指向堆栈顶部，告诉CPU下一个数据放哪里', NULL, NULL, '2025-11-30 15:54:56', '2025-11-30 15:55:34');
INSERT INTO `knowledge_point_content` VALUES (25, 22, '\n\n![image-20251130155658249](https://cdn.zww0891.fun/image-20251130155658249.png)\n\n**大白话**：**把复杂工作拆成多个简单步骤，每个步骤专人负责，大家同时干活**\n\n**生活类比**：\n\n- 像汽车装配线：A工人装轮子，B工人装发动机，C工人装座椅\n- 不用等一辆车完全装完再装下一辆，而是**同时装多辆车的不同部分**\n- 这样效率比一个人装完整辆车高很多\n\n**计算机中**：\n\n- 把一条指令的执行过程分解成多个子过程\n- 每个子过程由专门的功能部件完成\n- 多条指令可以**同时在不同阶段执行**\n\n### 5级流水线的各个阶段\n\n**IF（Instruction Fetch，取指令）**：\n\n- 全称：Instruction Fetch\n- 大白话：**从内存里拿指令**，像从菜谱上看下一道菜怎么做\n\n**ID（Instruction Decode，译码）**：\n\n- 全称：Instruction Decode  \n- 大白话：**理解指令的意思**，像看懂菜谱说的是炒菜还是煮菜\n\n**EXE（Execute，执行）**：\n\n- 全称：Execute\n- 大白话：**真正干活**，进行计算、比较等操作，像真正开始炒菜\n\n**MEM（Memory Access，存储器访问）**：\n\n- 全称：Memory Access\n- 大白话：**读写内存**，需要的话从内存取数据或存数据\n\n**WB（Write Back，写回）**：\n\n- 全称：Write Back\n- 大白话：**把结果保存起来**，把计算结果写回寄存器\n\n\n\n>### 5级流水线各阶段举例\n>\n>**例子：执行指令 `ADD R1, R2, R3`（R1 = R2 + R3）**\n>\n>**IF阶段**：\n>\n>- 从内存地址1000取出指令 `ADD R1, R2, R3`\n>\n>**ID阶段**：\n>\n>- 解析指令：这是加法运算\n>- 确定操作数：需要读R2和R3寄存器，结果写入R1\n>\n>**EXE阶段**：\n>\n>- 从R2取值：5\n>- 从R3取值：3  \n>- 计算：5 + 3 = 8\n>\n>**MEM阶段**：\n>\n>- 这条ADD指令不需要访问内存（直接跳过）\n>- 如果是LOAD指令，这里会读内存\n>\n>**WB阶段**：\n>\n>- 把计算结果8写入R1寄存器\n>\n>**时间线展示**：\n>\n>```\n>时钟1: ADD指令在IF阶段\n>时钟2: ADD指令在ID阶段，下一条指令在IF阶段  \n>时钟3: ADD指令在EXE阶段，下一条在ID，第3条在IF\n>时钟4: ADD指令在MEM阶段...\n>时钟5: ADD指令在WB阶段完成\n>```\n>\n>**核心**：一条指令需要5个时钟周期完成，但每个时钟周期都能开始一条新指令！\n\n### 流水线的特点\n\n**并行处理**：\n\n- **同一时刻可以处理多条指令**\n- 第1条指令在执行时，第2条在译码，第3条在取指令\n\n>### 并行处理举例\n>\n>**3条指令**：\n>\n>1. `ADD R1, R2, R3`\n>2. `SUB R4, R5, R6` \n>3. `MUL R7, R8, R9`\n>\n>**时钟周期3的状态**：\n>\n>| 指令    | 当前阶段    | 在做什么             |\n>| ------- | ----------- | -------------------- |\n>| ADD指令 | **EXE阶段** | 正在计算 R2+R3       |\n>| SUB指令 | **ID阶段**  | 正在解析减法指令     |\n>| MUL指令 | **IF阶段**  | 正在从内存取乘法指令 |\n>\n>**关键点**：\n>\n>- **同一时刻**，CPU的3个不同部件在干3件不同的事\n>- 像3个工人在流水线上：工人A在炒菜，工人B在洗菜，工人C在买菜\n>- **效率提升**：本来要9个时钟周期完成3条指令，现在7个时钟周期就完成了\n>\n>**简单理解**：CPU不再\"一心一意\"处理一条指令，而是\"一心三用\"同时推进多条指令！\n\n**各段时间相等**：\n\n- **每个阶段用时差不多**，否则会有瓶颈\n- 就像流水线上每个工位的工作时间要平衡\n\n**连续不断**：\n\n- **需要源源不断的任务输入**\n- 如果没有新指令，流水线就空了，效率下降\n\n**流水线寄存器**：\n\n- **每个阶段后面都有缓存器**\n- 临时存储这一级的结果，传给下一级\n\n**适合重复任务**：\n\n- **大量相似指令处理效率最高**\n- 就像生产线适合批量生产同类产品\n\n**需要通畅和排空**：\n\n- **通畅**：流水线刚开始时，要几个时钟周期才能满载\n- **排空**：流水线结束时，要等最后几条指令完成\n\n**流水线的瓶颈**：\n\n- 最慢的那个阶段决定整体速度\n- 如果某个阶段特别慢，其他阶段要等它', NULL, NULL, '2025-11-30 16:06:22', '2025-11-30 16:31:27');
INSERT INTO `knowledge_point_content` VALUES (26, 23, '**单功能流水线 vs 多功能流水线**：\n\n- **单功能例子**：专门做浮点加法的流水线，只能处理 `3.14 + 2.5` 这种运算\n- **多功能例子**：可以做加法、乘法、除法的流水线，既能算 `3+5`，又能算 `3×5`\n\n**静态流水线 vs 动态流水线**：\n\n- **静态例子**：汽车装配线，永远是装轮子→装发动机→装座椅，顺序不能变\n- **动态例子**：智能厨房，根据菜品不同可以调整：炒菜（洗→切→炒），煮面（烧水→下面→调味）\n\n**部件级、处理器级、处理机间流水线**：\n\n- **部件级例子**：CPU内部的5级流水线（IF→ID→EXE→MEM→WB）\n- **处理器级例子**：4核CPU，第1核处理指令1，第2核处理指令2\n- **处理机间例子**：谷歌搜索，服务器A爬网页，服务器B建索引，服务器C排序\n\n**线性流水线 vs 非线性流水线**：\n\n- **线性例子**：快餐店：点餐→制作→打包→取餐，一条直线\n- **非线性例子**：洗衣服：洗→烘干→折叠，但如果没干净要回到\"洗\"这步\n\n**按序流水线 vs 乱序流水线**：\n\n- **按序例子**：银行排队，必须按号码顺序办业务\n- **乱序例子**：餐厅出菜，哪道菜先做好先上，不按点菜顺序', NULL, NULL, '2025-11-30 17:01:11', NULL);
INSERT INTO `knowledge_point_content` VALUES (27, 24, '### 时空图\n\n1.画xy轴，y段x为Δt\n\n2.展开连加/乘号表示多轮，x轴写入输出，y轴填充段的单元时间占位方块\n\n3.最后一个段右边的边画箭头，表示这一轮的计算输出\n\n4.同轮内部串行，轮外并行，轮与轮之间隔得Δt为瓶颈段大小，也就是最大的耗时时间，按此规则完成其他几轮操作\n\n5.执行第二个类型的操作（加/乘）如果是静态流水线，则在最后一轮任务完成后才能执行，才能有新的段占位方块\n\n6.如果数据间没有依赖关系，则并行，有依赖则要在该数据输出后才能进行新的一轮计算\n\n7.最后可以根据图表进行计算吞吐率，效率，加速比\n\n>\n>\n>**大白版本**\n>\n>### 绘图步骤\n>\n>**步骤1：画坐标轴**\n>\n>- 横轴(x)：时间Δt\n>- 纵轴(y)：流水线各段（从下到上：取指→译码→执行）\n>\n>**步骤2：填充方块**\n>\n>- 在对应时间和段的位置画方块\n>- 方块内标注任务编号\n>\n>**步骤3：标记输出**\n>\n>- 在最后一个段的右边画箭头\n>- 箭头表示该任务完成输出\n>\n>**步骤4：确定任务间隔**\n>\n>- 同轮内部：串行执行（一个任务的各段按顺序）\n>- 轮外并行：多个任务同时在不同段\n>- 间隔时间：由瓶颈段（最慢的段）决定  （==**不同轮的同段的最左边线相隔的距离**==）\n>\n>**步骤5：处理静态流水线**\n>\n>- 完成第一类操作后才能开始第二类操作\n>- 必须等流水线排空\n>\n>**步骤6：处理数据依赖**\n>\n>- 无依赖：可以并行启动\n>- 有依赖：必须等待前置任务输出后才能开始\n>\n>**步骤7：性能计算**\n>\n>- 根据图表计算：吞吐量、效率、加速比\n>\n>### 关键点\n>\n>- **瓶颈段 = 最大的Δt** → 决定任务启动间隔\n>- **数据依赖 = 必须等待** → 影响任务启动时间\n>- **静态流水线 = 必须排空** → 不同操作不能混合\n\n### 吞吐率，加速比，效率\n\nTP=总任务/总时间\n\n加速比=总方块数（各类型的段数*任务数相加）/总时间\n\n效率=总方块数（各类型的段数\\*任务数相加）/总时间*总段数\n\n\n\n### 例题\n\n**1.静态**\n\n![image-20251201164809188](https://cdn.zww0891.fun/image-20251201164809188.png)\n\n**2.动态**\n\n![image-20251201164737438](https://cdn.zww0891.fun/image-20251201164737438.png)', NULL, NULL, '2025-11-30 17:13:50', '2025-12-01 16:57:49');
INSERT INTO `knowledge_point_content` VALUES (28, 25, '\n\n### 什么是相关？\n\n**大白话**：两条指令之间有\"扯不清的关系\"，后面的指令依赖前面指令的结果\n\n**生活例子**：\n\n- 你要先烧水，再泡茶 → 泡茶依赖烧水的结果\n- 这两个动作就是\"相关\"的\n\n### 三种相关类型\n\n#### 数据相关（真数据相关）：写后读（RAW）\n\n**RAW全称**：**Read After Write（写后读）**\n\n**大白话**：后面的指令要读前面指令写的数据\n\n**代码段1例子**：\n\n```assembly\nL.D  F0, 0(R1)      # 第1条：把内存数据加载到F0（写F0）\nADD.D F4, F0, F2    # 第2条：用F0来计算（读F0）\n```\n\n**相关分析**：\n\n- 第1条指令**写F0**\n- 第2条指令**读F0**\n- 必须等第1条写完F0，第2条才能读F0\n- 否则读到的是旧数据，结果错误！\n\n**生活例子**：\n\n- 厨师A炒好菜放盘子里（写盘子）\n- 服务员B端盘子上菜（读盘子）\n- B必须等A炒好才能端，否则端空盘子\n\n---\n\n#### 名相关：WAR（读后写）和WAW（写后写）\n\n**名相关大白话**：两条指令用了同一个\"名字\"（寄存器），但其实没真正的数据依赖，只是名字冲突\n\n##### WAR（读后写）\n\n**WAR全称**：**Write After Read（读后写）**\n\n**大白话**：后面的指令要写，前面的指令要读，用的是同一个寄存器\n\n**代码段2例子**：\n\n```assembly\nDIV.D F2, F6, F4     # 第1条：读F6（用F6来计算）\nADD.D F6, F0, F12    # 第2条：写F6（把结果存到F6）\n```\n\n**相关分析**：\n\n- 第1条**读F6**\n- 第2条**写F6**\n- 如果第2条先执行，F6被改了，第1条读到的就是错的\n\n**生活例子**：\n\n- 学生A抄黑板上的题目（读黑板）\n- 老师B要擦黑板写新题（写黑板）\n- B必须等A抄完才能擦，否则A抄错\n\n##### WAW（写后写）\n\n**WAW全称**：**Write After Write（写后写）**\n\n**大白话**：两条指令都要写同一个寄存器\n\n**代码段2例子**：\n\n```assembly\nDIV.D F2, F6, F4     # 第1条：写F2\nADD.D S₁, F0, F12    # 第2条：也写F2（如果S₁就是F2）\n```\n\n**相关分析**：\n\n- 两条指令都写F2\n- 如果顺序乱了，最终F2的值就错了\n- 必须保证最后写的是第2条的结果\n\n**生活例子**：\n\n- 两个人都要往同一个文件里写内容\n- 后写的会覆盖先写的\n- 顺序错了，最终内容就错了\n\n---\n\n#### 控制相关\n\n**大白话**：**分支指令（if判断、跳转）导致的相关**\n\n**解释**：\n\n- 分支指令会根据条件决定跳不跳\n- 后面的指令能不能执行，取决于分支结果\n- 在分支执行完之前，不知道该不该执行后面的指令\n\n**代码段2和3的控制相关**：\n\n```assembly\nDIV.D F2, F6, F4      # 除法指令\nADD.D S₁, F0, F12     # 如果S₁是条件码，影响分支\nSUB.D F8, S₁, F14     # 根据S₁的值决定是否执行\n```\n\n**相关分析**：\n\n- S₁（条件码）决定后面指令执不执行\n- 必须等ADD.D算完S₁，才知道SUB.D要不要做\n\n**生活例子**：\n\n- 看天气决定出不出门（分支判断）\n- 如果出门，就要穿衣服、带伞（后续指令）\n- 不看天气就不知道该不该穿衣服\n\n---\n\n### 三种相关对比表\n\n| 相关类型          | 全称              | 前指令   | 后指令   | 问题           | 例子      |\n| ----------------- | ----------------- | -------- | -------- | -------------- | --------- |\n| **数据相关(RAW)** | Read After Write  | 写X      | 读X      | 读到旧值       | 写F0→读F0 |\n| **名相关(WAR)**   | Write After Read  | 读X      | 写X      | 读到新值       | 读F6→写F6 |\n| **名相关(WAW)**   | Write After Write | 写X      | 写X      | 最终值错       | 写F2→写F2 |\n| **控制相关**      | Control Hazard    | 分支指令 | 普通指令 | 不知道执不执行 | 判断→执行 |\n\n---\n\n### 关键理解\n\n**数据相关（RAW）**：真的需要数据，必须等\n**名相关（WAR/WAW）**：只是名字冲突，可以通过重命名解决\n**控制相关**：不知道走哪条路，需要分支预测\n\n**解决方法预告**：\n\n- RAW：插入气泡、数据前推\n- WAR/WAW：寄存器重命名\n- 控制相关：分支预测、延迟槽', NULL, NULL, '2025-12-01 17:55:10', NULL);

-- ----------------------------
-- Table structure for knowledge_point_tag
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_point_tag`;
CREATE TABLE `knowledge_point_tag`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `point_id` bigint(20) NOT NULL COMMENT '知识点ID',
  `tag_id` bigint(20) NOT NULL COMMENT '标签ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_point_tag`(`point_id`, `tag_id`) USING BTREE,
  INDEX `idx_tag_id`(`tag_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '知识点标签关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of knowledge_point_tag
-- ----------------------------
INSERT INTO `knowledge_point_tag` VALUES (1, 1, 1, '2025-11-29 11:29:16');
INSERT INTO `knowledge_point_tag` VALUES (2, 1, 3, '2025-11-29 11:29:16');
INSERT INTO `knowledge_point_tag` VALUES (3, 2, 1, '2025-11-29 11:29:16');
INSERT INTO `knowledge_point_tag` VALUES (4, 2, 5, '2025-11-29 11:29:16');
INSERT INTO `knowledge_point_tag` VALUES (5, 3, 2, '2025-11-29 11:29:16');
INSERT INTO `knowledge_point_tag` VALUES (6, 3, 3, '2025-11-29 11:29:16');
INSERT INTO `knowledge_point_tag` VALUES (7, 4, 5, '2025-11-29 11:29:16');
INSERT INTO `knowledge_point_tag` VALUES (8, 4, 7, '2025-11-29 11:29:16');
INSERT INTO `knowledge_point_tag` VALUES (9, 5, 1, '2025-11-29 11:29:16');
INSERT INTO `knowledge_point_tag` VALUES (10, 5, 2, '2025-11-29 11:29:16');
INSERT INTO `knowledge_point_tag` VALUES (11, 6, 1, '2025-11-29 11:29:16');
INSERT INTO `knowledge_point_tag` VALUES (12, 6, 7, '2025-11-29 11:29:16');
INSERT INTO `knowledge_point_tag` VALUES (13, 7, 5, '2025-11-29 11:29:16');
INSERT INTO `knowledge_point_tag` VALUES (14, 7, 8, '2025-11-29 11:29:16');
INSERT INTO `knowledge_point_tag` VALUES (15, 7, 1, '2025-11-29 11:29:16');
INSERT INTO `knowledge_point_tag` VALUES (16, 7, 3, '2025-11-29 11:29:16');
INSERT INTO `knowledge_point_tag` VALUES (17, 8, 1, '2025-11-29 11:29:16');
INSERT INTO `knowledge_point_tag` VALUES (18, 8, 8, '2025-11-29 11:29:16');

-- ----------------------------
-- Table structure for knowledge_question_relation
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_question_relation`;
CREATE TABLE `knowledge_question_relation`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `point_id` bigint(20) NOT NULL COMMENT '知识点ID',
  `question_id` bigint(20) NOT NULL COMMENT '题目ID',
  `relevance` tinyint(1) NULL DEFAULT 2 COMMENT '相关度（1-弱相关 2-相关 3-强相关）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_point_question`(`point_id`, `question_id`) USING BTREE,
  INDEX `idx_point_id`(`point_id`) USING BTREE,
  INDEX `idx_question_id`(`question_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '知识点与题目关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of knowledge_question_relation
-- ----------------------------

-- ----------------------------
-- Table structure for knowledge_subject
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_subject`;
CREATE TABLE `knowledge_subject`  (
  `subject_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '科目ID',
  `subject_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '科目名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '科目描述',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '科目图标URL',
  `sort_order` int(11) NULL DEFAULT 0 COMMENT '排序（数字越小越靠前）',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态（0-停用 1-启用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`subject_id`) USING BTREE,
  INDEX `idx_status_sort`(`status`, `sort_order`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '知识点科目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of knowledge_subject
-- ----------------------------
INSERT INTO `knowledge_subject` VALUES (1, '计算机组成原理', '介绍计算机系统的组成、工作原理及性能评价', NULL, 1, 1, 'admin', '2025-11-29 11:29:16', NULL, '2025-11-29 11:29:16');
INSERT INTO `knowledge_subject` VALUES (2, '数据结构', '研究数据的逻辑结构、存储结构及算法', NULL, 2, 1, 'admin', '2025-11-29 11:29:16', NULL, '2025-11-29 11:29:16');
INSERT INTO `knowledge_subject` VALUES (3, '操作系统', '管理计算机硬件与软件资源的系统软件', NULL, 3, 1, 'admin', '2025-11-29 11:29:16', NULL, '2025-11-29 11:29:16');
INSERT INTO `knowledge_subject` VALUES (4, '计算机网络', '介绍计算机网络的体系结构、协议及应用', NULL, 4, 1, 'admin', '2025-11-29 11:29:16', NULL, '2025-11-29 11:29:16');
INSERT INTO `knowledge_subject` VALUES (5, '数据库原理', '数据库系统的概念、设计、管理与应用', NULL, 5, 1, 'admin', '2025-11-29 11:29:16', NULL, '2025-11-29 11:29:16');
INSERT INTO `knowledge_subject` VALUES (6, '软件工程', '软件开发方法、项目管理与质量保证', NULL, 6, 1, 'admin', '2025-11-29 11:29:16', NULL, '2025-11-29 11:29:16');
INSERT INTO `knowledge_subject` VALUES (7, 'lll', '', NULL, 0, 1, 'test666', '2025-11-29 15:27:25', NULL, '2025-11-29 15:27:25');
INSERT INTO `knowledge_subject` VALUES (8, '计算机系统结构', '', NULL, 0, 1, 'test666', '2025-11-29 17:35:01', NULL, '2025-11-29 17:35:01');
INSERT INTO `knowledge_subject` VALUES (9, 'markdown', NULL, NULL, 0, 1, 'test666', '2025-11-29 21:45:40', NULL, '2025-11-29 21:45:40');
INSERT INTO `knowledge_subject` VALUES (11, '微服务', NULL, NULL, 0, 1, 'test666', '2025-11-29 22:02:47', NULL, '2025-11-29 22:02:47');

-- ----------------------------
-- Table structure for knowledge_tag
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_tag`;
CREATE TABLE `knowledge_tag`  (
  `tag_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `tag_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标签名称',
  `tag_color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '#409EFF' COMMENT '标签颜色',
  `use_count` int(11) NULL DEFAULT 0 COMMENT '使用次数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`tag_id`) USING BTREE,
  UNIQUE INDEX `uk_tag_name`(`tag_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '知识点标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of knowledge_tag
-- ----------------------------
INSERT INTO `knowledge_tag` VALUES (1, '重点', '#f56c6c', 0, '2025-11-29 11:29:16');
INSERT INTO `knowledge_tag` VALUES (2, '难点', '#e6a23c', 0, '2025-11-29 11:29:16');
INSERT INTO `knowledge_tag` VALUES (3, '高频考点', '#ff6600', 0, '2025-11-29 11:29:16');
INSERT INTO `knowledge_tag` VALUES (4, '易错点', '#909399', 0, '2025-11-29 11:29:16');
INSERT INTO `knowledge_tag` VALUES (5, '基础', '#67c23a', 0, '2025-11-29 11:29:16');
INSERT INTO `knowledge_tag` VALUES (6, '进阶', '#409eff', 0, '2025-11-29 11:29:16');
INSERT INTO `knowledge_tag` VALUES (7, '面试常考', '#ff0066', 0, '2025-11-29 11:29:16');
INSERT INTO `knowledge_tag` VALUES (8, '实战应用', '#00ccff', 0, '2025-11-29 11:29:16');

-- ----------------------------
-- Table structure for knowledge_version
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_version`;
CREATE TABLE `knowledge_version`  (
  `version_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '版本ID',
  `point_id` bigint(20) NOT NULL COMMENT '知识点ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '内容',
  `edit_type` tinyint(1) NULL DEFAULT 2 COMMENT '编辑类型（1-创建 2-修改 3-删除）',
  `edit_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '编辑者',
  `edit_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '编辑说明',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`version_id`) USING BTREE,
  INDEX `idx_point_version`(`point_id`, `version`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '知识点版本历史表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of knowledge_version
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
