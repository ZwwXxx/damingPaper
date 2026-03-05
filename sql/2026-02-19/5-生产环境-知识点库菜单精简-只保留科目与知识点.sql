-- ============================================================
-- 生产环境执行：知识点库菜单精简（2026-02-19）
-- 效果：知识点库下只保留「科目管理」「知识点管理」，删除「章节管理」「标签管理」及其按钮
-- 执行后侧边栏：知识点库 → 科目管理、知识点管理
-- ============================================================

-- 1) 删除章节管理及其按钮（按权限标识删除，避免遗漏子菜单）
DELETE FROM sys_menu WHERE perms IN (
  'knowledge:chapter:query', 'knowledge:chapter:add',
  'knowledge:chapter:edit', 'knowledge:chapter:remove',
  'knowledge:chapter:list'
);
DELETE FROM sys_menu WHERE menu_name = '章节管理';

-- 2) 删除标签管理及其按钮
DELETE FROM sys_menu WHERE perms IN (
  'knowledge:tag:query', 'knowledge:tag:add',
  'knowledge:tag:edit', 'knowledge:tag:remove',
  'knowledge:tag:list'
);
DELETE FROM sys_menu WHERE menu_name = '标签管理';

-- 说明：若需清理角色菜单关联表 sys_role_menu 中已删除菜单的引用，可在执行上述删除后，
-- 于系统管理 → 角色管理 中重新分配菜单，或由 DBA 根据实际 menu_id 清理。
