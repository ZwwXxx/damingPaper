-- ============================================================
-- 生产库：清空「试卷 / 题目 / 内容 / 科目」相关数据，便于从测试库一比一导入
-- ⚠ 执行前务必备份整库；仅在确认可丢弃生产上这些业务数据时使用。
-- ⚠ TRUNCATE 会重置自增计数；若你已从 INT 改为 BIGINT+雪花，自增已无意义。
-- ⚠ 若库里没有某张表，删掉或注释对应行再执行。
-- ============================================================

SET NAMES utf8mb4;

SET FOREIGN_KEY_CHECKS = 0;

-- ---------- 依赖试卷 / 题目的子表（先清空）----------
TRUNCATE TABLE `daming_question_answer`;
TRUNCATE TABLE `daming_paper_answer`;
TRUNCATE TABLE `daming_question_favorite`;

-- 题目 ↔ 知识点（清空后需从源库一并导入，或日后在后台重绑）
TRUNCATE TABLE `knowledge_question_relation`;
TRUNCATE TABLE `question_knowledge_relation`;

-- 专项刷题栏目与题目关联（栏目里有 paper_id 时一并清掉栏目更干净）
TRUNCATE TABLE `daming_practice_column_question`;
TRUNCATE TABLE `daming_practice_column`;

-- ---------- 知识库：知识点本体及挂在 point 上的从表（你已要求清空 knowledge_point_base）----------
TRUNCATE TABLE `knowledge_comment_like`;
TRUNCATE TABLE `knowledge_comment`;
TRUNCATE TABLE `knowledge_attachment`;
TRUNCATE TABLE `knowledge_collect`;
TRUNCATE TABLE `knowledge_learn_record`;
TRUNCATE TABLE `knowledge_like`;
TRUNCATE TABLE `knowledge_point_tag`;
TRUNCATE TABLE `knowledge_version`;
TRUNCATE TABLE `knowledge_point_content`;
TRUNCATE TABLE `knowledge_point_base`;

-- 若知识点引用的「知识库科目」与测试也要一致，可取消下一行注释并同时从测试导入 knowledge_subject
-- TRUNCATE TABLE `knowledge_subject`;

-- 若仍存在「试卷-题目关系表」（老代码生成过可保留；没有该表则注释掉下一行）
-- TRUNCATE TABLE `daming_paper_question`;

-- ---------- 核心四张（与你说的一比一导入对应）----------
TRUNCATE TABLE `daming_paper`;
TRUNCATE TABLE `daming_question`;
TRUNCATE TABLE `daming_content_info`;
TRUNCATE TABLE `daming_subject`;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 后续：从测试库用 Navicat / mysqldump **带主键** 一并导入例如：
--   daming_* 核心四张 + 上面清掉的关联表 + knowledge_point_base / knowledge_point_content 等
--   + knowledge_question_relation、question_knowledge_relation（已与题目顺序一致时再导）。
-- 未删表说明：
-- * daming_animation：题目 animation_id 可能仍引用；要对齐再从测试导或手工处理。
-- * knowledge_tag、knowledge_folder：标签名/用户收藏夹；一般不必删，除非你也要从测试同步。
-- ============================================================
