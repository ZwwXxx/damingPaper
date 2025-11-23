-- 微信扫码登录用户表扩展
-- 为前台用户表daming_user添加微信相关字段
-- 微信扫码登录功能仅供前台用户使用

-- 添加微信openId字段（用于唯一标识微信用户）
ALTER TABLE daming_user ADD COLUMN wx_open_id VARCHAR(64) NULL COMMENT '微信OpenID' AFTER avatar;

-- 添加微信unionId字段（用于跨公众号识别同一用户）
ALTER TABLE daming_user ADD COLUMN wx_union_id VARCHAR(64) NULL COMMENT '微信UnionID' AFTER wx_open_id;

-- 添加微信昵称字段
ALTER TABLE daming_user ADD COLUMN wx_nickname VARCHAR(100) NULL COMMENT '微信昵称' AFTER wx_union_id;

-- 添加微信头像字段
ALTER TABLE daming_user ADD COLUMN wx_avatar VARCHAR(500) NULL COMMENT '微信头像URL' AFTER wx_nickname;

-- 添加微信绑定时间
ALTER TABLE daming_user ADD COLUMN wx_bind_time DATETIME NULL COMMENT '微信绑定时间' AFTER wx_avatar;

-- 为openId字段添加唯一索引
ALTER TABLE daming_user ADD UNIQUE INDEX idx_wx_open_id (wx_open_id);

-- 为unionId字段添加索引
ALTER TABLE daming_user ADD INDEX idx_wx_union_id (wx_union_id);

-- 查看表结构
DESC daming_user;

-- 说明:
-- 1. wx_open_id: 用户在当前公众号下的唯一标识，用于登录验证
-- 2. wx_union_id: 用户在同一开放平台下的唯一标识，可跨公众号识别
-- 3. wx_nickname/wx_avatar: 用于显示用户微信信息
-- 4. wx_bind_time: 记录首次绑定微信的时间
-- 5. 用户可以先用账号密码注册，后续绑定微信；也可以直接微信登录自动创建账号
