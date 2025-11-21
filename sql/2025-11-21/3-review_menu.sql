-- 批改试卷菜单（试卷管理子菜单）
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`,
                        `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`,
                        `perms`, `icon`, `create_by`, `create_time`, `remark`)
VALUES (2110, '批改试卷', 2018, 3, 'review', 'quiz/paperReview/index',
        '', 1, '0', 'C', '0', '0', 'quiz:paperReview:list', 'form', 'admin', NOW(), '老师批改入口');

-- 权限按钮：查询
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`,
                        `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`,
                        `perms`, `icon`, `create_by`, `create_time`, `remark`)
VALUES (2111, '批改查询', 2110, 1, '#', '', '',
        1, '0', 'F', '0', '0', 'quiz:paperReview:query', '#', 'admin', NOW(), '');

-- 权限按钮：评分（提交批改结果）
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`,
                        `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`,
                        `perms`, `icon`, `create_by`, `create_time`, `remark`)
VALUES (2112, '提交评分', 2110, 2, '#', '', '',
        1, '0', 'F', '0', '0', 'quiz:paperReview:score', '#', 'admin', NOW(), '');


