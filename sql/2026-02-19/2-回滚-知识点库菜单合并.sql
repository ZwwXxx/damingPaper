-- 回滚「1-知识点库菜单合并为标签管理.sql」
-- 1) 恢复「标签管理」为原来的组件与路径
UPDATE sys_menu SET component = 'knowledge/tag/index', path = 'tag'
WHERE menu_name = '标签管理' AND component = 'knowledge/index';

-- 2) 恢复被删除的 17 条菜单（科目管理、章节管理、知识点管理及其按钮，parent_id=2146）
INSERT INTO `sys_menu` VALUES (2147, '科目管理', 2146, 1, 'subject', 'knowledge/subject/index', NULL, '', 1, 0, 'C', '0', '0', 'knowledge:subject:list', 'tree-table', 'admin', '2025-11-28 12:18:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2148, '科目查询', 2147, 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'knowledge:subject:query', '#', 'admin', '2025-11-28 12:18:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2149, '科目新增', 2147, 2, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'knowledge:subject:add', '#', 'admin', '2025-11-28 12:18:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2150, '科目修改', 2147, 3, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'knowledge:subject:edit', '#', 'admin', '2025-11-28 12:18:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2151, '科目删除', 2147, 4, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'knowledge:subject:remove', '#', 'admin', '2025-11-28 12:18:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2152, '章节管理', 2146, 2, 'chapter', 'knowledge/chapter/index', NULL, '', 1, 0, 'C', '0', '0', 'knowledge:chapter:list', 'tree', 'admin', '2025-11-28 12:18:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2153, '章节查询', 2152, 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'knowledge:chapter:query', '#', 'admin', '2025-11-28 12:18:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2154, '章节新增', 2152, 2, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'knowledge:chapter:add', '#', 'admin', '2025-11-28 12:18:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2155, '章节修改', 2152, 3, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'knowledge:chapter:edit', '#', 'admin', '2025-11-28 12:18:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2156, '章节删除', 2152, 4, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'knowledge:chapter:remove', '#', 'admin', '2025-11-28 12:18:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2157, '知识点管理', 2146, 3, 'point', 'knowledge/point/index', NULL, '', 1, 0, 'C', '0', '0', 'knowledge:point:list', 'documentation', 'admin', '2025-11-28 12:18:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2158, '知识点查询', 2157, 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'knowledge:point:query', '#', 'admin', '2025-11-28 12:18:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2159, '知识点详细', 2157, 2, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'knowledge:point:detail', '#', 'admin', '2025-11-28 12:18:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2160, '知识点新增', 2157, 3, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'knowledge:point:add', '#', 'admin', '2025-11-28 12:18:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2161, '知识点修改', 2157, 4, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'knowledge:point:edit', '#', 'admin', '2025-11-28 12:18:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2162, '知识点删除', 2157, 5, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'knowledge:point:remove', '#', 'admin', '2025-11-28 12:18:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2163, '知识点审核', 2157, 6, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'knowledge:point:audit', '#', 'admin', '2025-11-28 12:18:20', '', NULL, '');
