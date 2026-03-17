-- 知识点附件去重改造脚本
-- 日期：2026-03-16

-- 1. 给 knowledge_attachment 增加文件内容哈希字段（用于全局去重）
ALTER TABLE knowledge_attachment 
    ADD COLUMN file_hash varchar(64) NULL COMMENT '文件内容哈希(MD5)';

-- 2. 同一知识点下，同名附件唯一（防止同一知识点重复上传同名文件）
ALTER TABLE knowledge_attachment 
    ADD UNIQUE INDEX uk_point_file_name (point_id, file_name);

-- 3. 为哈希查询增加索引（提升按 hash 查找的性能）
ALTER TABLE knowledge_attachment 
    ADD INDEX idx_file_hash (file_hash);

