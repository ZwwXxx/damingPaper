-- =============================================
-- 修复用户数统计 - SQL变更记录
-- 创建日期: 2025-11-23
-- 问题描述: 后台管理首页报表只统计了sys_user表，缺少daming_user表
-- 修复内容: 合并两张表的用户数统计
-- =============================================

-- 说明：
-- 本次修改已直接在 DashboardMapper.xml 中实现
-- 此SQL仅作为变更记录和测试验证使用

-- =============================================
-- 1. 测试原SQL（仅统计sys_user）
-- =============================================

-- 原SQL - 总用户数
SELECT COUNT(*)
FROM sys_user
WHERE del_flag = '0';

-- 原SQL - 新增用户数
SELECT COUNT(*)
FROM sys_user
WHERE del_flag = '0'
  AND create_time >= '2025-11-01'
  AND create_time < '2025-11-30';


-- =============================================
-- 2. 测试新SQL（合并两张表）
-- =============================================

-- 新SQL - 总用户数（sys_user + daming_user）
SELECT COUNT(*) FROM (
    SELECT user_id FROM sys_user WHERE del_flag = '0'
    UNION ALL
    SELECT user_id FROM daming_user WHERE del_flag = '0'
) AS all_users;

-- 新SQL - 新增用户数
SELECT COUNT(*) FROM (
    SELECT user_id, create_time FROM sys_user WHERE del_flag = '0'
    UNION ALL
    SELECT user_id, create_time FROM daming_user WHERE del_flag = '0'
) AS all_users
WHERE create_time >= '2025-11-01'
  AND create_time < '2025-11-30';


-- =============================================
-- 3. 数据对比验证
-- =============================================

-- 分别查看两张表的用户数
SELECT 
    '管理员用户' AS user_type,
    COUNT(*) AS user_count
FROM sys_user 
WHERE del_flag = '0'

UNION ALL

SELECT 
    '学生用户' AS user_type,
    COUNT(*) AS user_count
FROM daming_user 
WHERE del_flag = '0'

UNION ALL

SELECT 
    '总计' AS user_type,
    COUNT(*) AS user_count
FROM (
    SELECT user_id FROM sys_user WHERE del_flag = '0'
    UNION ALL
    SELECT user_id FROM daming_user WHERE del_flag = '0'
) AS all_users;


-- =============================================
-- 4. 验证字段一致性
-- =============================================

-- 检查sys_user表结构
DESC sys_user;

-- 检查daming_user表结构
DESC daming_user;

-- 确认两张表都有以下字段：
-- - user_id (用户ID)
-- - del_flag (删除标志)
-- - create_time (创建时间)


-- =============================================
-- 5. 性能优化建议
-- =============================================

-- 如果数据量较大，建议添加索引
-- CREATE INDEX idx_del_flag_create_time ON sys_user(del_flag, create_time);
-- CREATE INDEX idx_del_flag_create_time ON daming_user(del_flag, create_time);


-- =============================================
-- 修改的文件清单
-- =============================================
-- DashboardMapper.xml
--   - countTotalUsers (第8-14行)
--   - countNewUsers (第17-25行)
-- =============================================
