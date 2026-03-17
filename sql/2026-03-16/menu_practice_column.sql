/*
 * 专项专栏管理菜单 SQL（MySQL 8 兼容版）
 * 使用变量避免在 INSERT 中直接子查询 sys_menu，规避 1093 错误。
 * 执行前请确认：sys_menu 中已有 “题目管理” 菜单。
 */

-- 预先取出“题目管理”的 menu_id 作为父节点
SET @p_question_menu_id := (
  SELECT menu_id FROM sys_menu WHERE menu_name = '题目管理' LIMIT 1
);

-- 1. 主菜单：专项专栏管理（挂在“题目管理”下）
INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, is_frame,
  is_cache, menu_type, visible, status, perms, icon, create_by, create_time
) VALUES (
  '专项专栏管理',
  @p_question_menu_id,
  3,
  'practiceColumn',
  'quiz/practice/column/index',
  1,
  0,
  'C',
  '0',
  '0',
  'quiz:practice:column:list',
  'table',
  'admin',
  NOW()
);

-- 记录刚插入的“专项专栏管理”菜单ID
SET @p_practice_menu_id := LAST_INSERT_ID();

-- 2. 按钮：查询
INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, is_frame,
  is_cache, menu_type, visible, status, perms, icon, create_by, create_time
) VALUES (
  '专栏查询',
  @p_practice_menu_id,
  1,
  '',
  '',
  1,
  0,
  'F',
  '0',
  '0',
  'quiz:practice:column:list',
  '#',
  'admin',
  NOW()
);

-- 3. 按钮：新增
INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, is_frame,
  is_cache, menu_type, visible, status, perms, icon, create_by, create_time
) VALUES (
  '专栏新增',
  @p_practice_menu_id,
  2,
  '',
  '',
  1,
  0,
  'F',
  '0',
  '0',
  'quiz:practice:column:add',
  '#',
  'admin',
  NOW()
);

-- 4. 按钮：修改
INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, is_frame,
  is_cache, menu_type, visible, status, perms, icon, create_by, create_time
) VALUES (
  '专栏修改',
  @p_practice_menu_id,
  3,
  '',
  '',
  1,
  0,
  'F',
  '0',
  '0',
  'quiz:practice:column:edit',
  '#',
  'admin',
  NOW()
);

-- 5. 按钮：删除
INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, is_frame,
  is_cache, menu_type, visible, status, perms, icon, create_by, create_time
) VALUES (
  '专栏删除',
  @p_practice_menu_id,
  4,
  '',
  '',
  1,
  0,
  'F',
  '0',
  '0',
  'quiz:practice:column:remove',
  '#',
  'admin',
  NOW()
);

-- 6. 按钮：配置题目（绑定）
INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, is_frame,
  is_cache, menu_type, visible, status, perms, icon, create_by, create_time
) VALUES (
  '专栏配置题目',
  @p_practice_menu_id,
  5,
  '',
  '',
  1,
  0,
  'F',
  '0',
  '0',
  'quiz:practice:column:bind',
  '#',
  'admin',
  NOW()
);

