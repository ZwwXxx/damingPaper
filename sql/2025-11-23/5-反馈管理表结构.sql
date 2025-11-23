-- =============================================
-- 反馈管理表结构
-- 创建日期: 2025-11-23
-- 功能说明: 用户反馈提交和后台管理处理
-- =============================================

-- ============================
-- 1. 创建反馈表
-- ============================
DROP TABLE IF EXISTS `daming_feedback`;

CREATE TABLE `daming_feedback` (
  `feedback_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '反馈ID',
  `user_id` varchar(64) NOT NULL COMMENT '用户ID',
  `user_name` varchar(64) DEFAULT NULL COMMENT '用户名称',
  `feedback_type` tinyint(4) NOT NULL DEFAULT 1 COMMENT '反馈类型：1=功能建议 2=Bug反馈 3=使用问题 4=其他',
  `feedback_title` varchar(200) NOT NULL COMMENT '反馈标题',
  `feedback_content` text NOT NULL COMMENT '反馈内容',
  `contact_info` varchar(200) DEFAULT NULL COMMENT '联系方式（手机/邮箱）',
  `images` varchar(1000) DEFAULT NULL COMMENT '截图附件（多个用逗号分隔）',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '处理状态：0=待处理 1=处理中 2=已处理 3=已关闭',
  `priority` tinyint(4) NOT NULL DEFAULT 1 COMMENT '优先级：1=低 2=中 3=高',
  `handler` varchar(64) DEFAULT NULL COMMENT '处理人',
  `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
  `reply_content` text DEFAULT NULL COMMENT '回复内容',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志：0=正常 1=删除',
  `create_user` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`feedback_id`),
  KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引',
  KEY `idx_status` (`status`) COMMENT '状态索引',
  KEY `idx_create_time` (`create_time`) COMMENT '创建时间索引',
  KEY `idx_feedback_type` (`feedback_type`) COMMENT '反馈类型索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户反馈表';

-- ============================
-- 2. 插入测试数据
-- ============================
INSERT INTO `daming_feedback` (
  `user_id`,
  `user_name`,
  `feedback_type`,
  `feedback_title`,
  `feedback_content`,
  `contact_info`,
  `status`,
  `priority`,
  `create_user`
) VALUES 
(
  'student1',
  '张三',
  1,
  '建议增加错题本导出功能',
  '希望能够将错题本导出为PDF或Word文档，方便线下复习。目前只能在线查看，不太方便。',
  '13800138000',
  1,
  2,
  'student1'
),
(
  'student2',
  '李四',
  2,
  '考试页面刷新后答案丢失',
  '在做题过程中，如果不小心刷新了页面，之前选择的答案会全部丢失，导致需要重新答题。希望能够自动保存答题进度。',
  'lisi@example.com',
  2,
  3,
  'student2'
),
(
  'student3',
  '王五',
  3,
  '如何查看历史考试记录？',
  '我想查看之前所有的考试记录和成绩，但是在个人中心没有找到相关入口，请问应该在哪里查看？',
  '13900139000',
  2,
  1,
  'student3'
),
(
  'student4',
  '赵六',
  1,
  '希望增加夜间模式',
  '晚上学习时屏幕太亮，希望能够增加夜间模式，保护眼睛。',
  'zhaoliu@example.com',
  0,
  1,
  'student4'
);

-- 更新其中一条为已处理状态，添加回复
UPDATE `daming_feedback` 
SET 
  `status` = 2,
  `handler` = 'admin',
  `handle_time` = NOW(),
  `reply_content` = '您好！感谢您的反馈。关于查看历史考试记录，您可以在【个人中心】→【考试记录】中查看所有的考试历史和详细成绩。如有其他问题，欢迎继续反馈！'
WHERE `feedback_title` = '如何查看历史考试记录？';

-- ============================
-- 3. 验证数据
-- ============================
SELECT 
    feedback_id,
    user_name,
    CASE feedback_type
        WHEN 1 THEN '功能建议'
        WHEN 2 THEN 'Bug反馈'
        WHEN 3 THEN '使用问题'
        WHEN 4 THEN '其他'
    END AS type_name,
    feedback_title,
    CASE status
        WHEN 0 THEN '待处理'
        WHEN 1 THEN '处理中'
        WHEN 2 THEN '已处理'
        WHEN 3 THEN '已关闭'
    END AS status_name,
    CASE priority
        WHEN 1 THEN '低'
        WHEN 2 THEN '中'
        WHEN 3 THEN '高'
    END AS priority_name,
    handler,
    handle_time,
    create_time
FROM daming_feedback
WHERE del_flag = '0'
ORDER BY priority DESC, create_time DESC;

-- ============================
-- 4. 查询待处理反馈统计
-- ============================
SELECT 
    CASE feedback_type
        WHEN 1 THEN '功能建议'
        WHEN 2 THEN 'Bug反馈'
        WHEN 3 THEN '使用问题'
        WHEN 4 THEN '其他'
    END AS type_name,
    COUNT(*) AS count
FROM daming_feedback
WHERE status = 0 AND del_flag = '0'
GROUP BY feedback_type
ORDER BY count DESC;

-- =============================================
-- 表结构说明
-- =============================================
-- 
-- 字段说明：
-- 1. feedback_id: 反馈唯一标识
-- 2. user_id: 提交反馈的用户ID
-- 3. user_name: 用户名称（冗余字段，方便查询）
-- 4. feedback_type: 反馈类型（1功能建议 2Bug反馈 3使用问题 4其他）
-- 5. feedback_title: 反馈标题
-- 6. feedback_content: 反馈详细内容
-- 7. contact_info: 联系方式（可选）
-- 8. images: 截图附件路径（可选，多个用逗号分隔）
-- 9. status: 处理状态（0待处理 1处理中 2已处理 3已关闭）
-- 10. priority: 优先级（1低 2中 3高）
-- 11. handler: 处理人员
-- 12. handle_time: 处理时间
-- 13. reply_content: 管理员回复内容
-- 14. del_flag: 软删除标记
-- 
-- 索引说明：
-- 1. idx_user_id: 用户ID索引，查询用户的所有反馈
-- 2. idx_status: 状态索引，查询待处理/已处理反馈
-- 3. idx_create_time: 创建时间索引，按时间排序
-- 4. idx_feedback_type: 反馈类型索引，分类统计
-- 
-- 业务流程：
-- 1. 用户提交反馈（status=0待处理）
-- 2. 管理员接收处理（status=1处理中）
-- 3. 管理员回复处理（status=2已处理，填写reply_content）
-- 4. 用户查看回复
-- 5. 可关闭反馈（status=3已关闭）
-- 
-- =============================================
