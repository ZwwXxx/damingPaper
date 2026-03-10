-- =============================================
-- 题目表增加动画解析ID + 动画库表
-- 创建日期: 2026-03-04
-- 功能说明:
-- 1) daming_question 增加 animation_id（可选）
-- 2) 新建 daming_animation 动画库表，用于后台统一管理上传的动画HTML页面
-- =============================================

-- ============================
-- 1. 题目表增加动画解析ID
-- ============================
ALTER TABLE `daming_question`
    ADD COLUMN `animation_id` bigint(20) NULL DEFAULT NULL COMMENT '动画解析ID（可选）' AFTER `question_info_id`,
    ADD INDEX `idx_animation_id` (`animation_id`) COMMENT '动画解析ID索引';

-- ============================
-- 2. 创建动画库表
-- ============================
DROP TABLE IF EXISTS `daming_animation`;

CREATE TABLE `daming_animation` (
  `animation_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '动画ID',
  `animation_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '动画名称（通常为原始文件名）',
  `animation_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '动画页面URL',
  `object_name` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '对象存储ObjectName/本地相对路径',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '删除标志：0=正常 1=删除',
  `create_user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`animation_id`) USING BTREE,
  INDEX `idx_del_flag` (`del_flag`) USING BTREE,
  INDEX `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='动画库（题目动画解析HTML）';

