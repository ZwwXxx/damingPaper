-- 知识点库下只保留「科目管理」「知识点管理」，删除「章节管理」「标签管理」及其按钮
-- 执行后侧边栏仅显示：知识点库 → 科目管理、知识点管理

-- 1) 删除章节管理及其按钮
DELETE FROM sys_menu WHERE perms IN (
  'knowledge:chapter:query', 'knowledge:chapter:add',
  'knowledge:chapter:edit', 'knowledge:chapter:remove'
);
DELETE FROM sys_menu WHERE menu_name = '章节管理';

-- 2) 删除标签管理及其按钮
DELETE FROM sys_menu WHERE perms IN (
  'knowledge:tag:query', 'knowledge:tag:add',
  'knowledge:tag:edit', 'knowledge:tag:remove'
);
DELETE FROM sys_menu WHERE menu_name = '标签管理';
