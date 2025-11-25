-- 后台首页报表菜单权限配置SQL
-- 创建日期: 2025-11-22
-- 说明: 为数据大屏功能添加菜单和权限配置

-- ============================
-- 1. 删除旧菜单(如果存在)
-- ============================
-- 注意: 首次执行可以忽略删除语句，如果是更新菜单则需要先删除

-- DELETE FROM sys_menu WHERE menu_name = '数据大屏' AND parent_id = 0;
-- DELETE FROM sys_menu WHERE menu_name = '首页统计' AND parent_id IN (SELECT menu_id FROM sys_menu WHERE menu_name = '数据大屏');

-- ============================
-- 2. 插入父菜单 - 数据大屏
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
    '数据大屏',           -- 菜单名称
    0,                    -- 父菜单ID (0表示顶级菜单)
    1,                    -- 显示顺序 (排在最前面)
    'dashboard',          -- 路由地址
    NULL,                 -- 组件路径 (目录不需要组件)
    NULL,                 -- 路由参数
    '',                   -- 路由名称
    1,                    -- 是否为外链 (1否)
    0,                    -- 是否缓存 (0缓存)
    'M',                  -- 菜单类型 (M目录)
    '0',                  -- 菜单状态 (0显示)
    '0',                  -- 状态 (0正常)
    NULL,                 -- 权限标识 (目录不需要)
    'dashboard',          -- 菜单图标
    'admin',              -- 创建者
    NOW(),                -- 创建时间
    '数据大屏目录'        -- 备注
);

-- 获取刚插入的父菜单ID (用于后续插入子菜单)
SET @parent_menu_id = LAST_INSERT_ID();

-- ============================
-- 3. 插入子菜单 - 首页统计
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
    '首页统计',           -- 菜单名称
    @parent_menu_id,      -- 父菜单ID (数据大屏)
    1,                    -- 显示顺序
    'index',              -- 路由地址
    'dashboard/index',    -- 组件路径
    NULL,                 -- 路由参数
    '',                   -- 路由名称
    1,                    -- 是否为外链 (1否)
    0,                    -- 是否缓存 (0缓存)
    'C',                  -- 菜单类型 (C菜单)
    '0',                  -- 菜单状态 (0显示)
    '0',                  -- 状态 (0正常)
    'dashboard:index:view', -- 权限标识
    'chart',              -- 菜单图标
    'admin',              -- 创建者
    NOW(),                -- 创建时间
    '首页统计报表'        -- 备注
);

-- 获取首页统计菜单ID
SET @statistics_menu_id = LAST_INSERT_ID();

-- ============================
-- 4. 插入按钮权限 - 概览统计
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
    '概览统计查询',       -- 菜单名称
    @statistics_menu_id,  -- 父菜单ID (首页统计)
    1,                    -- 显示顺序
    '',                   -- 路由地址 (按钮不需要)
    NULL,                 -- 组件路径 (按钮不需要)
    NULL,                 -- 路由参数
    '',                   -- 路由名称
    1,                    -- 是否为外链 (1否)
    0,                    -- 是否缓存 (0缓存)
    'F',                  -- 菜单类型 (F按钮)
    '0',                  -- 菜单状态 (0显示)
    '0',                  -- 状态 (0正常)
    'dashboard:overview:query', -- 权限标识
    '#',                  -- 菜单图标
    'admin',              -- 创建者
    NOW(),                -- 创建时间
    '查询概览统计数据'    -- 备注
);

-- ============================
-- 5. 插入按钮权限 - 用户活跃度
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
    '用户活跃度查询',     -- 菜单名称
    @statistics_menu_id,  -- 父菜单ID (首页统计)
    2,                    -- 显示顺序
    '',                   -- 路由地址
    NULL,                 -- 组件路径
    NULL,                 -- 路由参数
    '',                   -- 路由名称
    1,                    -- 是否为外链 (1否)
    0,                    -- 是否缓存 (0缓存)
    'F',                  -- 菜单类型 (F按钮)
    '0',                  -- 菜单状态 (0显示)
    '0',                  -- 状态 (0正常)
    'dashboard:user-activity:query', -- 权限标识
    '#',                  -- 菜单图标
    'admin',              -- 创建者
    NOW(),                -- 创建时间
    '查询用户活跃度数据' -- 备注
);

-- ============================
-- 6. 插入按钮权限 - 考试统计
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
    '考试统计查询',       -- 菜单名称
    @statistics_menu_id,  -- 父菜单ID (首页统计)
    3,                    -- 显示顺序
    '',                   -- 路由地址
    NULL,                 -- 组件路径
    NULL,                 -- 路由参数
    '',                   -- 路由名称
    1,                    -- 是否为外链 (1否)
    0,                    -- 是否缓存 (0缓存)
    'F',                  -- 菜单类型 (F按钮)
    '0',                  -- 菜单状态 (0显示)
    '0',                  -- 状态 (0正常)
    'dashboard:exam-statistics:query', -- 权限标识
    '#',                  -- 菜单图标
    'admin',              -- 创建者
    NOW(),                -- 创建时间
    '查询考试统计数据'    -- 备注
);

-- ============================
-- 7. 插入按钮权限 - 题目统计
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
    '题目统计查询',       -- 菜单名称
    @statistics_menu_id,  -- 父菜单ID (首页统计)
    4,                    -- 显示顺序
    '',                   -- 路由地址
    NULL,                 -- 组件路径
    NULL,                 -- 路由参数
    '',                   -- 路由名称
    1,                    -- 是否为外链 (1否)
    0,                    -- 是否缓存 (0缓存)
    'F',                  -- 菜单类型 (F按钮)
    '0',                  -- 菜单状态 (0显示)
    '0',                  -- 状态 (0正常)
    'dashboard:question-statistics:query', -- 权限标识
    '#',                  -- 菜单图标
    'admin',              -- 创建者
    NOW(),                -- 创建时间
    '查询题目统计数据'    -- 备注
);

-- ============================
-- 8. 插入按钮权限 - 试卷统计
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
    '试卷统计查询',       -- 菜单名称
    @statistics_menu_id,  -- 父菜单ID (首页统计)
    5,                    -- 显示顺序
    '',                   -- 路由地址
    NULL,                 -- 组件路径
    NULL,                 -- 路由参数
    '',                   -- 路由名称
    1,                    -- 是否为外链 (1否)
    0,                    -- 是否缓存 (0缓存)
    'F',                  -- 菜单类型 (F按钮)
    '0',                  -- 菜单状态 (0显示)
    '0',                  -- 状态 (0正常)
    'dashboard:paper-statistics:query', -- 权限标识
    '#',                  -- 菜单图标
    'admin',              -- 创建者
    NOW(),                -- 创建时间
    '查询试卷统计数据'    -- 备注
);

-- ============================
-- 9. 插入按钮权限 - 时间分布
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
    '时间分布查询',       -- 菜单名称
    @statistics_menu_id,  -- 父菜单ID (首页统计)
    6,                    -- 显示顺序
    '',                   -- 路由地址
    NULL,                 -- 组件路径
    NULL,                 -- 路由参数
    '',                   -- 路由名称
    1,                    -- 是否为外链 (1否)
    0,                    -- 是否缓存 (0缓存)
    'F',                  -- 菜单类型 (F按钮)
    '0',                  -- 菜单状态 (0显示)
    '0',                  -- 状态 (0正常)
    'dashboard:time-distribution:query', -- 权限标识
    '#',                  -- 菜单图标
    'admin',              -- 创建者
    NOW(),                -- 创建时间
    '查询时间分布数据'    -- 备注
);

-- ============================
-- 菜单配置完成
-- ============================

-- 查看插入的菜单
SELECT 
    menu_id,
    menu_name,
    parent_id,
    order_num,
    path,
    component,
    menu_type,
    perms,
    icon,
    create_time
FROM sys_menu
WHERE menu_name IN ('数据大屏', '首页统计', '概览统计查询', '用户活跃度查询', '考试统计查询', '题目统计查询', '试卷统计查询', '时间分布查询')
ORDER BY parent_id, order_num;

-- ============================
-- 权限分配说明
-- ============================
-- 执行完此SQL后，需要在系统管理界面进行以下操作：
-- 1. 登录系统管理后台
-- 2. 进入【系统管理】->【角色管理】
-- 3. 选择需要分配权限的角色(如:管理员角色)
-- 4. 点击【修改】按钮
-- 5. 在菜单权限树中勾选【数据大屏】及其子菜单
-- 6. 保存配置
-- 
-- 或者执行以下SQL直接为管理员角色(role_id=1)分配权限：
-- INSERT INTO sys_role_menu (role_id, menu_id) 
-- SELECT 1, menu_id FROM sys_menu 
-- WHERE menu_name IN ('数据大屏', '首页统计', '概览统计查询', '用户活跃度查询', '考试统计查询', '题目统计查询', '试卷统计查询', '时间分布查询');
