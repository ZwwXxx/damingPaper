-- =============================================
-- 动画管理菜单权限
-- 创建日期: 2026-03-04
-- 功能说明: 在「题库管理」下新增「动画管理」菜单，用于回显/管理上传的动画解析页面
-- =============================================

-- 题库管理父菜单ID（已有）：2000
SET @questionBankMenuId = 2000;

-- 1) 新增「动画管理」菜单
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`,
                        `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('动画管理', @questionBankMenuId, 3, 'animation', 'quiz/animation/index', '', 1, 0,
        'C', '0', '0', 'quiz:animation:list', 'video-camera', 'admin', NOW(), '', NULL, '题目动画解析库管理');

SET @animationMenuId = LAST_INSERT_ID();

-- 2) 按钮权限（查询/上传/删除/导出）
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`,
                        `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES
('动画管理查询', @animationMenuId, 1, '#', '', '', 1, 0, 'F', '0', '0', 'quiz:animation:query', '#', 'admin', NOW(), '', NULL, ''),
('动画管理上传', @animationMenuId, 2, '#', '', '', 1, 0, 'F', '0', '0', 'quiz:animation:upload', '#', 'admin', NOW(), '', NULL, ''),
('动画管理删除', @animationMenuId, 3, '#', '', '', 1, 0, 'F', '0', '0', 'quiz:animation:remove', '#', 'admin', NOW(), '', NULL, ''),
('动画管理导出', @animationMenuId, 4, '#', '', '', 1, 0, 'F', '0', '0', 'quiz:animation:export', '#', 'admin', NOW(), '', NULL, '');

-- 3) 给管理员角色授权（默认管理员角色ID=1）
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, `menu_id`
FROM `sys_menu`
WHERE `menu_id` = @animationMenuId OR `parent_id` = @animationMenuId;

