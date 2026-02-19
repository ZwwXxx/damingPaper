-- 删除「章节管理」菜单及其下属按钮（章节查询/新增/修改/删除）
-- 先删子权限，再删主菜单

DELETE FROM sys_menu WHERE perms IN (
  'knowledge:chapter:query',
  'knowledge:chapter:add',
  'knowledge:chapter:edit',
  'knowledge:chapter:remove'
);

DELETE FROM sys_menu WHERE menu_name = '章节管理';
