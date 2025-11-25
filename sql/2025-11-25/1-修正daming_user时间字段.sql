-- 修正daming_user表的时间字段配置
-- 修正日期：2025-11-25
-- 问题：create_time和update_time字段在新增数据时为NULL

USE `ry-vue`; -- 数据库名包含连字符需要用反引号包围

-- 修正create_time字段：插入时自动设置当前时间，更新时不变
ALTER TABLE `daming_user` 
MODIFY COLUMN `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';

-- 修正update_time字段：插入和更新时都自动设置当前时间  
ALTER TABLE `daming_user` 
MODIFY COLUMN `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- 为现有的NULL记录补充时间数据
UPDATE `daming_user` 
SET 
    `create_time` = COALESCE(`create_time`, NOW()),
    `update_time` = COALESCE(`update_time`, NOW())
WHERE `create_time` IS NULL OR `update_time` IS NULL;

-- 验证修复结果
SELECT user_id, user_name, create_time, update_time 
FROM `daming_user` 
ORDER BY user_id DESC 
LIMIT 10;
