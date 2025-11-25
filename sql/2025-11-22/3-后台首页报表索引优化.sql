-- 后台首页报表索引优化脚本
-- 创建日期: 2025-11-22
-- 说明: 为报表查询添加必要的索引,提升查询性能

-- ============================
-- 1. 登录日志表索引优化
-- ============================

-- 添加登录时间索引(如果不存在)
-- 用于统计各时间段的登录情况
ALTER TABLE `sys_logininfor` 
ADD INDEX `idx_login_time` (`login_time`) 
COMMENT '登录时间索引';

-- 添加用户名索引(如果不存在)
-- 用于统计唯一登录用户数
ALTER TABLE `sys_logininfor` 
ADD INDEX `idx_user_name` (`user_name`) 
COMMENT '用户名索引';

-- 添加组合索引:状态+登录时间
-- 用于快速筛选成功的登录记录并按时间统计
ALTER TABLE `sys_logininfor` 
ADD INDEX `idx_status_time` (`status`, `login_time`) 
COMMENT '状态和登录时间组合索引';

-- ============================
-- 2. 答卷表索引优化
-- ============================

-- 添加创建时间索引
-- 用于统计各时间段的考试次数
ALTER TABLE `daming_paper_answer` 
ADD INDEX `idx_create_time` (`create_time`) 
COMMENT '创建时间索引';

-- 添加试卷ID索引(已存在,这里注释掉)
-- ALTER TABLE `daming_paper_answer` 
-- ADD INDEX `idx_paper_id` (`paper_id`) 
-- COMMENT '试卷ID索引';

-- 添加创建用户索引
-- 用于按用户统计考试次数
ALTER TABLE `daming_paper_answer` 
ADD INDEX `idx_create_user` (`create_user`) 
COMMENT '创建用户索引';

-- 添加批改状态索引
-- 用于快速统计待批改和已批改的答卷数
ALTER TABLE `daming_paper_answer` 
ADD INDEX `idx_review_status` (`review_status`) 
COMMENT '批改状态索引';

-- 添加组合索引:试卷ID+创建时间
-- 用于统计某试卷在特定时间段的考试次数
ALTER TABLE `daming_paper_answer` 
ADD INDEX `idx_paper_create` (`paper_id`, `create_time`) 
COMMENT '试卷ID和创建时间组合索引';

-- 添加组合索引:科目ID+创建时间
-- 用于按科目统计考试趋势
ALTER TABLE `daming_paper_answer` 
ADD INDEX `idx_subject_create` (`subject_id`, `create_time`) 
COMMENT '科目ID和创建时间组合索引';

-- ============================
-- 3. 题目收藏表索引优化
-- ============================

-- 添加题目ID索引
-- 用于统计某题目的收藏次数(已有唯一索引,包含此字段)
-- ALTER TABLE `daming_question_favorite` 
-- ADD INDEX `idx_question_id` (`question_id`) 
-- COMMENT '题目ID索引';

-- 添加创建时间索引
-- 用于统计某时间段的收藏情况
ALTER TABLE `daming_question_favorite` 
ADD INDEX `idx_create_time` (`create_time`) 
COMMENT '创建时间索引';

-- ============================
-- 4. 题目表索引优化
-- ============================

-- 添加科目ID索引
-- 用于按科目统计题目分布
ALTER TABLE `daming_question` 
ADD INDEX `idx_subject_id` (`subject_id`) 
COMMENT '科目ID索引';

-- 添加题目类型索引
-- 用于按题型统计题目分布
ALTER TABLE `daming_question` 
ADD INDEX `idx_question_type` (`question_type`) 
COMMENT '题目类型索引';

-- 添加组合索引:删除标志+科目ID
-- 用于快速查询未删除的题目并按科目分组
ALTER TABLE `daming_question` 
ADD INDEX `idx_delflag_subject` (`del_flag`, `subject_id`) 
COMMENT '删除标志和科目ID组合索引';

-- ============================
-- 5. 试卷表索引优化
-- ============================

-- 添加科目ID索引
-- 用于按科目统计试卷分布
ALTER TABLE `daming_paper` 
ADD INDEX `idx_subject_id` (`subject_id`) 
COMMENT '科目ID索引';

-- 添加组合索引:删除标志+创建时间
-- 用于统计新增试卷数
ALTER TABLE `daming_paper` 
ADD INDEX `idx_delflag_create` (`del_flag`, `create_time`) 
COMMENT '删除标志和创建时间组合索引';

-- ============================
-- 6. 系统用户表索引优化
-- ============================

-- 添加创建时间索引
-- 用于统计新增用户数
ALTER TABLE `sys_user` 
ADD INDEX `idx_create_time` (`create_time`) 
COMMENT '创建时间索引';

-- 添加组合索引:删除标志+创建时间
-- 用于统计未删除的新增用户
ALTER TABLE `sys_user` 
ADD INDEX `idx_delflag_create` (`del_flag`, `create_time`) 
COMMENT '删除标志和创建时间组合索引';

-- ============================
-- 7. 操作日志表索引优化
-- ============================

-- 添加操作时间索引(已存在,这里注释掉)
-- ALTER TABLE `sys_oper_log` 
-- ADD INDEX `idx_oper_time` (`oper_time`) 
-- COMMENT '操作时间索引';

-- 添加操作人员索引
-- 用于统计某用户的操作记录
ALTER TABLE `sys_oper_log` 
ADD INDEX `idx_oper_name` (`oper_name`) 
COMMENT '操作人员索引';

-- ============================
-- 8. 题目答案表索引优化
-- ============================

-- 添加组合索引:答卷ID+题目ID
-- 用于快速查询某答卷的所有答题记录
ALTER TABLE `daming_question_answer` 
ADD INDEX `idx_paperanswer_question` (`paper_answer_id`, `question_id`) 
COMMENT '答卷ID和题目ID组合索引';

-- 添加创建用户+创建时间组合索引
-- 用于统计用户的答题历史
ALTER TABLE `daming_question_answer` 
ADD INDEX `idx_user_create` (`create_user`, `create_time`) 
COMMENT '创建用户和创建时间组合索引';

-- ============================
-- 索引优化完成
-- ============================

-- 查看表的索引情况
-- SHOW INDEX FROM sys_logininfor;
-- SHOW INDEX FROM daming_paper_answer;
-- SHOW INDEX FROM daming_question_favorite;
-- SHOW INDEX FROM daming_question;
-- SHOW INDEX FROM daming_paper;
-- SHOW INDEX FROM sys_user;
-- SHOW INDEX FROM sys_oper_log;
-- SHOW INDEX FROM daming_question_answer;
