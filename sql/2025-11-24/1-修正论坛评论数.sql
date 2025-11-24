-- 修正论坛帖子的评论数
-- 说明：将所有帖子的 comment_count 更新为实际的评论数量（一级评论 + 二级评论）
-- 执行时间：2025-11-24
-- 作者：系统管理员

-- 1. 更新所有帖子的评论数为实际值
UPDATE forum_post fp
SET comment_count = (
    SELECT COUNT(*)
    FROM forum_comment fc
    WHERE fc.post_id = fp.post_id
      AND fc.status = 1  -- 只统计正常状态的评论
)
WHERE fp.post_id IN (SELECT DISTINCT post_id FROM forum_comment);

-- 2. 将没有评论的帖子评论数设为0
UPDATE forum_post
SET comment_count = 0
WHERE post_id NOT IN (SELECT DISTINCT post_id FROM forum_comment WHERE status = 1);

-- 3. 查看修正后的结果（前10条）
SELECT 
    fp.post_id,
    fp.title,
    fp.comment_count AS '帖子显示评论数',
    (SELECT COUNT(*) FROM forum_comment fc WHERE fc.post_id = fp.post_id AND fc.status = 1) AS '实际评论数'
FROM forum_post fp
ORDER BY fp.create_time DESC
LIMIT 10;

-- 4. 验证是否还有不一致的数据
SELECT 
    COUNT(*) AS '不一致的帖子数量'
FROM forum_post fp
WHERE fp.comment_count != (
    SELECT COUNT(*) 
    FROM forum_comment fc 
    WHERE fc.post_id = fp.post_id AND fc.status = 1
);
