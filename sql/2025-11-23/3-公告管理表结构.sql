-- =============================================
-- 公告管理表结构
-- 创建日期: 2025-11-23
-- 功能说明: 用于后台管理系统发布和管理前台考试系统的公告
-- =============================================

-- ============================
-- 1. 创建公告表
-- ============================
DROP TABLE IF EXISTS `daming_notice`;

CREATE TABLE `daming_notice` (
  `notice_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(200) NOT NULL COMMENT '公告标题',
  `notice_content` text NOT NULL COMMENT '公告内容',
  `notice_type` tinyint(4) NOT NULL DEFAULT 1 COMMENT '公告类型：1=通知 2=公告 3=活动',
  `is_top` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否置顶：0=否 1=是',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '发布状态：0=草稿 1=已发布',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `sort_order` int(11) NOT NULL DEFAULT 0 COMMENT '排序序号（越大越靠前）',
  `view_count` int(11) NOT NULL DEFAULT 0 COMMENT '浏览次数',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志：0=正常 1=删除',
  `create_user` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`),
  KEY `idx_status_isTop_sort` (`status`, `is_top`, `sort_order`) COMMENT '发布状态+置顶+排序索引',
  KEY `idx_create_time` (`create_time`) COMMENT '创建时间索引',
  KEY `idx_publish_time` (`publish_time`) COMMENT '发布时间索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告管理表';

-- ============================
-- 2. 插入测试数据
-- ============================
INSERT INTO `daming_notice` (
  `notice_title`, 
  `notice_content`, 
  `notice_type`, 
  `is_top`, 
  `status`, 
  `publish_time`, 
  `sort_order`,
  `create_user`,
  `remark`
) VALUES 
(
  '系统维护通知',
  '尊敬的用户，系统将于本周六（2025年11月23日）凌晨2:00-4:00进行系统维护升级，期间将暂停服务，请各位用户合理安排考试时间。给您带来的不便敬请谅解！',
  1,
  1,
  1,
  NOW(),
  100,
  'admin',
  '系统维护公告'
),
(
  '新功能上线公告',
  '亲爱的用户们，我们很高兴地宣布，数据大屏功能正式上线！您可以在个人中心查看详细的学习数据统计，包括考试成绩趋势、错题分析、学习时间分布等，快来体验吧！',
  2,
  1,
  1,
  NOW(),
  90,
  'admin',
  '新功能发布'
),
(
  '考试规则说明',
  '为了保证考试的公平公正，请各位考生注意以下事项：\n1. 考试过程中请独立完成，不得借助任何外部资源\n2. 每道题目都有时间限制，请合理分配答题时间\n3. 提交后不可修改答案，请仔细检查后再提交\n4. 系统会自动记录考试过程，请诚信考试',
  1,
  0,
  1,
  NOW(),
  80,
  'admin',
  '考试规则'
),
(
  '学习资料更新',
  '本周新增了100道Java高级编程题目和50道数据库优化题目，欢迎大家前往题库练习。同时，我们还整理了常见错题解析文档，可在错题本中查看详细解答。',
  3,
  0,
  1,
  NOW(),
  70,
  'admin',
  '题库更新'
);

-- ============================
-- 3. 验证数据
-- ============================
SELECT 
    notice_id,
    notice_title,
    notice_type,
    CASE notice_type
        WHEN 1 THEN '通知'
        WHEN 2 THEN '公告'
        WHEN 3 THEN '活动'
    END AS type_name,
    is_top,
    status,
    CASE status
        WHEN 0 THEN '草稿'
        WHEN 1 THEN '已发布'
    END AS status_name,
    publish_time,
    sort_order,
    view_count,
    create_time
FROM daming_notice
WHERE del_flag = '0'
ORDER BY is_top DESC, sort_order DESC, create_time DESC;

-- =============================================
-- 表结构说明
-- =============================================
-- 
-- 字段说明：
-- 1. notice_id: 公告唯一标识
-- 2. notice_title: 公告标题，最多200字符
-- 3. notice_content: 公告内容，支持富文本
-- 4. notice_type: 公告类型（1通知 2公告 3活动）
-- 5. is_top: 是否置顶，置顶的公告优先显示
-- 6. status: 发布状态（0草稿 1已发布），只有已发布的才在前台显示
-- 7. publish_time: 发布时间，可以设置定时发布
-- 8. sort_order: 排序序号，数值越大越靠前
-- 9. view_count: 浏览次数统计
-- 10. del_flag: 软删除标记
-- 
-- 索引说明：
-- 1. idx_status_isTop_sort: 组合索引，优化前台查询（已发布+置顶+排序）
-- 2. idx_create_time: 创建时间索引，优化后台管理查询
-- 3. idx_publish_time: 发布时间索引，支持定时发布功能
-- 
-- =============================================
