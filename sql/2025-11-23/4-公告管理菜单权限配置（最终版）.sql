-- =============================================
-- 公告管理菜单权限配置SQL（最终版）
-- 创建日期: 2025-11-23
-- 功能说明: 为公告管理模块添加菜单和权限配置（一级菜单）
-- =============================================

-- ============================
-- 1. 插入公告管理菜单（一级菜单）
-- ============================
INSERT INTO `sys_menu` (
    `menu_name`, 
    `parent_id`, 
    `order_num`, 
    `path`, 
    `component`, 
    `query`, 
    `route_name`, 
    `is_frame`, 
    `is_cache`, 
    `menu_type`, 
    `visible`, 
    `status`, 
    `perms`, 
    `icon`, 
    `create_by`, 
    `create_time`, 
    `remark`
) VALUES (
    '公告管理',           -- 菜单名称
    0,                    -- 父菜单ID（0=一级菜单）
    4,                    -- 显示顺序
    'notice',             -- 路由地址
    'quiz/notice/index',  -- 组件路径
    NULL,                 -- 路由参数
    '',                   -- 路由名称
    1,                    -- 是否为外链 (1否)
    0,                    -- 是否缓存 (0缓存)
    'C',                  -- 菜单类型 (C菜单)
    '0',                  -- 菜单状态 (0显示)
    '0',                  -- 状态 (0正常)
    'quiz:notice:list',   -- 权限标识
    'message',            -- 菜单图标
    'admin',              -- 创建者
    NOW(),                -- 创建时间
    '公告管理菜单'        -- 备注
);

-- 获取刚插入的菜单ID
SET @notice_menu_id = LAST_INSERT_ID();

-- ============================
-- 2. 插入按钮权限 - 查询
-- ============================
INSERT INTO `sys_menu` (
    `menu_name`, 
    `parent_id`, 
    `order_num`, 
    `path`, 
    `component`, 
    `menu_type`, 
    `visible`, 
    `status`, 
    `perms`, 
    `icon`, 
    `create_by`, 
    `create_time`
) VALUES (
    '公告查询',
    @notice_menu_id,
    1,
    '',
    NULL,
    'F',
    '0',
    '0',
    'quiz:notice:query',
    '#',
    'admin',
    NOW()
);

-- ============================
-- 3. 插入按钮权限 - 新增
-- ============================
INSERT INTO `sys_menu` (
    `menu_name`, 
    `parent_id`, 
    `order_num`, 
    `path`, 
    `component`, 
    `menu_type`, 
    `visible`, 
    `status`, 
    `perms`, 
    `icon`, 
    `create_by`, 
    `create_time`
) VALUES (
    '公告新增',
    @notice_menu_id,
    2,
    '',
    NULL,
    'F',
    '0',
    '0',
    'quiz:notice:add',
    '#',
    'admin',
    NOW()
);

-- ============================
-- 4. 插入按钮权限 - 修改
-- ============================
INSERT INTO `sys_menu` (
    `menu_name`, 
    `parent_id`, 
    `order_num`, 
    `path`, 
    `component`, 
    `menu_type`, 
    `visible`, 
    `status`, 
    `perms`, 
    `icon`, 
    `create_by`, 
    `create_time`
) VALUES (
    '公告修改',
    @notice_menu_id,
    3,
    '',
    NULL,
    'F',
    '0',
    '0',
    'quiz:notice:edit',
    '#',
    'admin',
    NOW()
);

-- ============================
-- 5. 插入按钮权限 - 删除
-- ============================
INSERT INTO `sys_menu` (
    `menu_name`, 
    `parent_id`, 
    `order_num`, 
    `path`, 
    `component`, 
    `menu_type`, 
    `visible`, 
    `status`, 
    `perms`, 
    `icon`, 
    `create_by`, 
    `create_time`
) VALUES (
    '公告删除',
    @notice_menu_id,
    4,
    '',
    NULL,
    'F',
    '0',
    '0',
    'quiz:notice:remove',
    '#',
    'admin',
    NOW()
);

-- ============================
-- 6. 插入按钮权限 - 发布
-- ============================
INSERT INTO `sys_menu` (
    `menu_name`, 
    `parent_id`, 
    `order_num`, 
    `path`, 
    `component`, 
    `menu_type`, 
    `visible`, 
    `status`, 
    `perms`, 
    `icon`, 
    `create_by`, 
    `create_time`
) VALUES (
    '公告发布',
    @notice_menu_id,
    5,
    '',
    NULL,
    'F',
    '0',
    '0',
    'quiz:notice:publish',
    '#',
    'admin',
    NOW()
);

-- ============================
-- 7. 插入按钮权限 - 下架
-- ============================
INSERT INTO `sys_menu` (
    `menu_name`, 
    `parent_id`, 
    `order_num`, 
    `path`, 
    `component`, 
    `menu_type`, 
    `visible`, 
    `status`, 
    `perms`, 
    `icon`, 
    `create_by`, 
    `create_time`
) VALUES (
    '公告下架',
    @notice_menu_id,
    6,
    '',
    NULL,
    'F',
    '0',
    '0',
    'quiz:notice:unpublish',
    '#',
    'admin',
    NOW()
);

-- ============================
-- 8. 验证插入结果
-- ============================
SELECT 
    menu_id,
    menu_name,
    parent_id,
    order_num,
    path,
    component,
    menu_type,
    perms,
    icon
FROM sys_menu
WHERE menu_name LIKE '%公告%'
ORDER BY parent_id, order_num;

-- ============================
-- 权限分配说明
-- ============================
-- 执行完此SQL后，需要在系统管理界面进行以下操作：
-- 1. 登录系统管理后台
-- 2. 进入【系统管理】->【角色管理】
-- 3. 选择需要分配权限的角色(如:管理员角色)
-- 4. 点击【修改】按钮
-- 5. 在菜单权限树中勾选【公告管理】及其子权限
-- 6. 保存配置
-- 
-- =============================================
