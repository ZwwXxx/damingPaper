-- 论坛管理菜单权限配置
-- 执行时间：2025-11-24
-- 说明：为后台管理系统添加论坛管理菜单和权限配置

-- 1. 添加论坛管理父菜单
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) 
VALUES 
('论坛管理', 0, 5, 'forum', NULL, 1, 0, 'M', '0', '0', '', 'message', 'admin', NOW(), '', NULL, '论坛内容管理');

-- 获取刚插入的父菜单ID（假设为@forumMenuId，实际使用时需要查询最后插入的ID）
SET @forumMenuId = LAST_INSERT_ID();

-- 2. 添加帖子管理菜单
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) 
VALUES 
('帖子管理', @forumMenuId, 1, 'post', 'quiz/forum/post/index', 1, 0, 'C', '0', '0', 'quiz:forum:list', 'post', 'admin', NOW(), '', NULL, '论坛帖子管理');

-- 获取帖子管理菜单ID
SET @postMenuId = LAST_INSERT_ID();

-- 3. 添加帖子管理按钮权限
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) 
VALUES 
('帖子查询', @postMenuId, 1, '#', '', 1, 0, 'F', '0', '0', 'quiz:forum:query', '#', 'admin', NOW(), '', NULL, ''),
('帖子编辑', @postMenuId, 2, '#', '', 1, 0, 'F', '0', '0', 'quiz:forum:edit', '#', 'admin', NOW(), '', NULL, ''),
('帖子删除', @postMenuId, 3, '#', '', 1, 0, 'F', '0', '0', 'quiz:forum:remove', '#', 'admin', NOW(), '', NULL, ''),
('帖子导出', @postMenuId, 4, '#', '', 1, 0, 'F', '0', '0', 'quiz:forum:export', '#', 'admin', NOW(), '', NULL, '');

-- 4. 添加评论管理菜单
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) 
VALUES 
('评论管理', @forumMenuId, 2, 'comment', 'quiz/forum/comment/index', 1, 0, 'C', '0', '0', 'quiz:forum:list', 'comment', 'admin', NOW(), '', NULL, '论坛评论管理');

-- 获取评论管理菜单ID
SET @commentMenuId = LAST_INSERT_ID();

-- 5. 添加评论管理按钮权限
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) 
VALUES 
('评论查询', @commentMenuId, 1, '#', '', 1, 0, 'F', '0', '0', 'quiz:forum:query', '#', 'admin', NOW(), '', NULL, ''),
('评论删除', @commentMenuId, 2, '#', '', 1, 0, 'F', '0', '0', 'quiz:forum:remove', '#', 'admin', NOW(), '', NULL, ''),
('评论导出', @commentMenuId, 3, '#', '', 1, 0, 'F', '0', '0', 'quiz:forum:export', '#', 'admin', NOW(), '', NULL, '');

-- 6. 为管理员角色分配论坛管理权限（假设管理员角色ID为1）
-- 注意：需要根据实际的角色ID调整
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, `menu_id` FROM `sys_menu` 
WHERE `menu_id` = @forumMenuId 
   OR `parent_id` = @forumMenuId 
   OR `parent_id` = @postMenuId 
   OR `parent_id` = @commentMenuId;

-- 执行说明：
-- 1. 请确保数据库中已存在 sys_menu 和 sys_role_menu 表
-- 2. 执行前请备份数据库
-- 3. 如果需要为其他角色分配权限，请修改最后一条SQL中的 role_id
-- 4. 执行完成后，需要重新登录系统以刷新权限

-- 查询验证（可选）
-- SELECT * FROM sys_menu WHERE menu_name LIKE '论坛%' OR menu_name LIKE '帖子%' OR menu_name LIKE '评论%';
