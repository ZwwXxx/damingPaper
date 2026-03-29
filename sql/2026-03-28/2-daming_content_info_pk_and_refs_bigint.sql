-- ============================================================
-- 内容表 daming_content_info 主键 + 引用列：支持雪花 ID
-- 依赖：建议与 1-daming_paper_question_pk_bigint_no_autoinc.sql 同库执行完后，
--       或与之合并；本脚本不要求先改试卷/题目主键，可与 1 任意先后（无正式 FK）。
-- 执行前请备份。
-- ============================================================

SET NAMES utf8mb4;

-- 1) 引用 content 主键的外键列先加宽（现网多为 INT，值兼容 BIGINT）
ALTER TABLE `daming_paper`
  MODIFY COLUMN `paper_info_id` BIGINT NOT NULL COMMENT '关联 daming_content_info.id（试卷结构 JSON 等）';

ALTER TABLE `daming_question`
  MODIFY COLUMN `question_info_id` BIGINT NULL DEFAULT NULL COMMENT '关联 daming_content_info.id（题干 JSON）';

-- 2) 内容表主键：BIGINT、非自增（新行由应用生成 id）
ALTER TABLE `daming_content_info`
  MODIFY COLUMN `id` BIGINT NOT NULL COMMENT '内容主键（应用生成，如雪花）';

-- InnoDB 上去掉 AUTO_INCREMENT 后，SHOW CREATE TABLE 中不应再出现 AUTO_INCREMENT= 旧值；
-- 若仍显示旧计数可忽略，或 DBA 可手动整理表定义（一般不必）。

-- ============================================================
-- 说明：
-- * 老数据 id 仍是小整数，无需改行数据。
-- * 新插入须在 insertContentInfo 等逻辑里显式写入雪花 id（与试卷/题目一致）。
-- * JSON 里的 paperQuestionVMS[].id 存的是题目主键；题目主键已 BIGINT 时，JSON 数字即可。
-- * 若其他表还有 content_id / xxx_info_id 指向本表而仍为 INT，请同样 MODIFY 为 BIGINT。
-- ============================================================
