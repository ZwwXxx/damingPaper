-- =============================================
-- 数据大屏菜单调整为一级菜单
-- 创建日期: 2025-11-23
-- 说明: 将"首页统计"从二级菜单提升为一级菜单，改名为"数据大屏"
-- =============================================

-- ============================
-- 1. 查看当前菜单结构
-- ============================
SELECT 
    menu_id,
    menu_name,
    parent_id,
    order_num,
    path,
    component,
    menu_type,
    icon
FROM sys_menu
WHERE menu_name IN ('数据大屏', '首页统计')
ORDER BY parent_id, order_num;

-- ============================
-- 2. 保存"首页统计"的menu_id（用于后续更新权限按钮的parent_id）
-- ============================
SET @old_statistics_menu_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '首页统计' LIMIT 1);

-- ============================
-- 3. 更新"首页统计"菜单（提升为一级菜单并改名）
-- ============================
UPDATE sys_menu 
SET 
    menu_name = '数据大屏',           -- 改名为"数据大屏"
    parent_id = 0,                    -- 设置为顶级菜单（0表示顶级）
    order_num = 1,                    -- 排在最前面
    path = 'dashboard',               -- 路由路径
    component = 'dashboard/index',    -- 组件路径保持不变
    icon = 'dashboard',               -- 使用dashboard图标
    remark = '数据大屏 - 系统数据统计与分析'
WHERE menu_id = @old_statistics_menu_id;

-- ============================
-- 4. 删除旧的"数据大屏"目录菜单
-- ============================
-- 删除原来的"数据大屏"目录（parent_id=0 的那个目录，现在不需要了）
DELETE FROM sys_menu 
WHERE menu_name = '数据大屏' 
  AND parent_id = 0 
  AND menu_type = 'M'
  AND menu_id != @old_statistics_menu_id;

-- 说明：权限按钮（F类型）的parent_id已经指向"首页统计"的menu_id，
-- 现在"首页统计"改名为"数据大屏"后，这些权限按钮自动归属到新的"数据大屏"菜单下

-- ============================
-- 5. 验证修改结果
-- ============================
SELECT 
    menu_id,
    menu_name,
    parent_id,
    order_num,
    path,
    component,
    menu_type,
    icon,
    remark
FROM sys_menu
WHERE menu_name = '数据大屏' OR parent_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '数据大屏' AND parent_id = 0)
ORDER BY parent_id, order_num;

-- ============================
-- 5. 查看完整菜单树结构
-- ============================
SELECT 
    CASE 
        WHEN parent_id = 0 THEN CONCAT('├─ ', menu_name)
        WHEN parent_id IN (SELECT menu_id FROM sys_menu WHERE parent_id = 0) THEN CONCAT('│  ├─ ', menu_name)
        ELSE CONCAT('│  │  ├─ ', menu_name)
    END AS menu_tree,
    menu_id,
    parent_id,
    order_num,
    path,
    component,
    menu_type
FROM sys_menu
WHERE menu_name = '数据大屏' 
   OR parent_id = (SELECT m.menu_id FROM sys_menu m WHERE m.menu_name = '数据大屏' AND m.parent_id = 0)
ORDER BY parent_id, order_num;

-- ============================
-- 说明
-- ============================
-- 修改后的菜单结构：
-- 
-- ├─ 数据大屏 (一级菜单)
-- │  ├─ 概览统计查询 (按钮权限)
-- │  ├─ 用户活跃度查询 (按钮权限)
-- │  ├─ 考试统计查询 (按钮权限)
-- │  ├─ 题目统计查询 (按钮权限)
-- │  ├─ 试卷统计查询 (按钮权限)
-- │  └─ 时间分布查询 (按钮权限)
-- 
-- 执行完此SQL后，刷新浏览器即可看到菜单变化
-- =============================================
