-- ========================================
-- 微信扫码登录状态追踪表
-- 用于记录每次扫码的完整生命周期
-- ========================================

DROP TABLE IF EXISTS `wx_scan_log`;
CREATE TABLE `wx_scan_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `scene_str` varchar(64) NOT NULL COMMENT '场景值字符串（业务唯一标识）',
  `scene_id` int(11) NOT NULL COMMENT '场景值数字（微信二维码场景值）',
  `qrcode_url` varchar(500) NOT NULL COMMENT '二维码ticket（用于获取二维码图片）',
  `qrcode_image_url` varchar(500) NULL COMMENT '二维码图片URL',
  `session_id` varchar(64) NOT NULL COMMENT 'WebSocket会话ID',
  `ip_address` varchar(50) NULL COMMENT '客户端IP地址',
  `user_agent` varchar(500) NULL COMMENT '客户端User-Agent',
  
  -- 状态字段
  `scan_status` tinyint(2) NOT NULL DEFAULT 0 COMMENT '扫码状态: 0-待扫码 1-已扫码 2-已授权 3-登录成功 4-已过期 5-已取消',
  
  -- 用户信息（扫码后填充）
  `wx_open_id` varchar(64) NULL COMMENT '微信OpenID',
  `wx_union_id` varchar(64) NULL COMMENT '微信UnionID',
  `wx_nickname` varchar(100) NULL COMMENT '微信昵称',
  `daming_user_id` int(11) NULL COMMENT '关联的前台用户ID（登录成功后填充）',
  
  -- 时间戳（完整的状态时间线）
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '二维码生成时间',
  `scan_time` datetime NULL COMMENT '用户扫码时间',
  `auth_time` datetime NULL COMMENT '用户授权时间',
  `login_time` datetime NULL COMMENT '登录成功时间',
  `expire_time` datetime NOT NULL COMMENT '二维码过期时间（默认5分钟）',
  
  -- 其他字段
  `redirect_url` varchar(500) NULL COMMENT '登录成功后重定向URL（用于登录后返回原页面）',
  `error_msg` varchar(500) NULL COMMENT '错误信息（登录失败时记录）',
  `remark` varchar(500) NULL COMMENT '备注',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_scene_str` (`scene_str`) COMMENT '场景值字符串唯一索引',
  UNIQUE KEY `uk_scene_id` (`scene_id`) COMMENT '场景值数字唯一索引',
  KEY `idx_session_id` (`session_id`) COMMENT 'WebSocket会话索引',
  KEY `idx_wx_open_id` (`wx_open_id`) COMMENT '微信OpenID索引',
  KEY `idx_scan_status` (`scan_status`) COMMENT '状态索引',
  KEY `idx_create_time` (`create_time`) COMMENT '创建时间索引（用于清理过期数据）',
  KEY `idx_expire_time` (`expire_time`) COMMENT '过期时间索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='微信扫码登录状态追踪表';

-- ========================================
-- 状态说明
-- ========================================
-- 0-待扫码: 二维码已生成，等待用户扫描
-- 1-已扫码: 用户已扫描二维码（SCAN事件触发）
-- 2-已授权: 用户点击授权按钮（获取到code）
-- 3-登录成功: 已生成Token，登录成功
-- 4-已过期: 二维码已过期（超过expire_time）
-- 5-已取消: 用户取消登录或关闭页面

-- ========================================
-- 完整的状态流转
-- ========================================
-- 前端打开登录页 → WebSocket连接 
-- → 后端生成二维码 (status=0, 记录scene_str和session_id)
-- → 用户扫码 (status=1, 记录scan_time和wx_open_id)
-- → 用户授权 (status=2, 记录auth_time)
-- → 登录成功 (status=3, 记录login_time和daming_user_id)

-- ========================================
-- 索引设计说明
-- ========================================
-- 1. uk_scene_str: 保证一个场景值只能生成一次，防止重复
-- 2. idx_session_id: 根据WebSocket会话快速查询二维码状态
-- 3. idx_wx_open_id: 根据微信用户快速查询扫码历史
-- 4. idx_scan_status: 状态查询优化（如查询所有已过期的记录）
-- 5. idx_create_time/idx_expire_time: 定时清理任务优化

-- ========================================
-- 查询示例
-- ========================================

-- 1. 查询某个二维码的状态
-- SELECT * FROM wx_scan_log WHERE scene_str = 'xxx';

-- 2. 查询某个用户的扫码历史
-- SELECT * FROM wx_scan_log WHERE wx_open_id = 'xxx' ORDER BY create_time DESC;

-- 3. 查询5分钟内未扫码的二维码（待扫码超时）
-- SELECT * FROM wx_scan_log WHERE scan_status = 0 AND expire_time < NOW();

-- 4. 统计今日扫码次数
-- SELECT scan_status, COUNT(*) as count FROM wx_scan_log 
-- WHERE DATE(create_time) = CURDATE() GROUP BY scan_status;

-- 5. 查询某个session的最新二维码
-- SELECT * FROM wx_scan_log WHERE session_id = 'xxx' ORDER BY create_time DESC LIMIT 1;

-- ========================================
-- 清理策略
-- ========================================
-- 建议定时清理7天前的记录，保留最近数据用于审计和统计
-- DELETE FROM wx_scan_log WHERE create_time < DATE_SUB(NOW(), INTERVAL 7 DAY);
