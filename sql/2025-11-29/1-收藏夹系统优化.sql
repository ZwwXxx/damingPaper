-- 收藏夹系统优化
-- 日期: 2025-11-29
-- 描述: 优化收藏夹系统架构，参考B站等大厂设计

-- 1. 检查收藏夹表是否有默认收藏夹标识字段
-- 如果没有is_default字段，请添加
ALTER TABLE user_collect_folder ADD COLUMN is_default TINYINT(1) DEFAULT 0 COMMENT '是否默认收藏夹';

-- 2. 为每个现有用户创建默认收藏夹（如果还没有）
-- 注意：这个脚本需要根据实际的用户表和收藏夹表结构调整

-- 示例：为没有默认收藏夹的用户创建默认收藏夹
INSERT INTO user_collect_folder (user_id, folder_name, is_default, create_time)
SELECT 
    u.user_id,
    '默认收藏夹' as folder_name,
    1 as is_default,
    NOW() as create_time
FROM sys_user u
WHERE NOT EXISTS (
    SELECT 1 FROM user_collect_folder f 
    WHERE f.user_id = u.user_id AND f.is_default = 1
);

-- 3. 确保每个用户只有一个默认收藏夹
-- 如果有多个默认收藏夹，保留最早创建的那个
UPDATE user_collect_folder f1
SET is_default = 0
WHERE is_default = 1 
AND EXISTS (
    SELECT 1 FROM user_collect_folder f2
    WHERE f2.user_id = f1.user_id 
    AND f2.is_default = 1 
    AND f2.create_time < f1.create_time
);

-- 4. 添加索引优化查询性能
CREATE INDEX idx_user_folder_default ON user_collect_folder(user_id, is_default);

-- 5. 后端API建议
/*
getUserFolders接口应该返回：
{
  "code": 200,
  "data": [
    {
      "folderId": 1,
      "folderName": "默认收藏夹", 
      "isDefault": true,
      "knowledgeCount": 5,
      "createTime": "2025-11-29T10:00:00"
    },
    {
      "folderId": 2,
      "folderName": "前端学习",
      "isDefault": false, 
      "knowledgeCount": 3,
      "createTime": "2025-11-29T11:00:00"
    }
  ]
}

getMyCollects接口：
- 不传folderId参数：返回所有收藏
- 传folderId参数：返回指定收藏夹的收藏
*/

-- 6. 业务规则
/*
1. 用户注册时自动创建默认收藏夹
2. 默认收藏夹不允许删除，但可以重命名
3. 未指定收藏夹的收藏操作自动收藏到默认收藏夹
4. 前端完全依赖后端返回的收藏夹列表，不写死默认收藏夹
*/
