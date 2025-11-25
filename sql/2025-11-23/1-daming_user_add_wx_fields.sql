-- 给 daming_user 表添加微信相关字段
-- 日期: 2025-11-23
-- 功能: 支持微信扫码登录

ALTER TABLE `daming_user`
ADD COLUMN `wx_open_id` VARCHAR(50) NULL COMMENT '微信OpenID' AFTER `avatar`,
ADD COLUMN `wx_union_id` VARCHAR(50) NULL COMMENT '微信UnionID' AFTER `wx_open_id`,
ADD UNIQUE INDEX `idx_wx_open_id` (`wx_open_id`) USING BTREE COMMENT '微信OpenID唯一索引';

-- 注释说明：
-- wx_open_id: 微信用户的唯一标识（针对当前公众号）
-- wx_union_id: 微信用户的唯一标识（针对同一开放平台下的所有应用）
-- 添加唯一索引确保一个微信号只能对应一个账号
