-- ============================================================
-- 试卷 / 题目主键：INT AUTO_INCREMENT -> BIGINT（非自增，应用侧雪花赋值）
-- 当前仓库 ry-vue 结构中外键名见 daming_paper_answer、daming_question_answer。
-- 执行前请先备份；若约束名不一致，先查：
--   SELECT CONSTRAINT_NAME, TABLE_NAME
--   FROM information_schema.KEY_COLUMN_USAGE
--   WHERE TABLE_SCHEMA = DATABASE()
--     AND REFERENCED_TABLE_NAME IN ('daming_paper', 'daming_question');
-- ============================================================

SET NAMES utf8mb4;

-- 1) 去掉外键（先子表，名称以你库为准）
ALTER TABLE `daming_paper_answer` DROP FOREIGN KEY `daming_paper_answer_ibfk_1`;
ALTER TABLE `daming_question_answer` DROP FOREIGN KEY `daming_question_answer_ibfk_1`;
ALTER TABLE `daming_question_answer` DROP FOREIGN KEY `daming_question_answer_ibfk_2`;

-- 2) 子表外键列改为 BIGINT（与父表一致）
ALTER TABLE `daming_paper_answer`
  MODIFY COLUMN `paper_id` BIGINT NOT NULL;

ALTER TABLE `daming_question_answer`
  MODIFY COLUMN `paper_id` BIGINT NOT NULL,
  MODIFY COLUMN `question_id` BIGINT NOT NULL;

-- 3) 父表主键：BIGINT + 取消自增（不写 AUTO_INCREMENT 即去掉自增）
ALTER TABLE `daming_paper`
  MODIFY COLUMN `paper_id` BIGINT NOT NULL COMMENT '试卷主键（应用生成，如雪花）';

ALTER TABLE `daming_question`
  MODIFY COLUMN `id` BIGINT NOT NULL COMMENT '题目主键（应用生成，如雪花）';

-- 4) 恢复外键
ALTER TABLE `daming_paper_answer`
  ADD CONSTRAINT `daming_paper_answer_ibfk_1`
    FOREIGN KEY (`paper_id`) REFERENCES `daming_paper` (`paper_id`)
    ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `daming_question_answer`
  ADD CONSTRAINT `daming_question_answer_ibfk_1`
    FOREIGN KEY (`paper_id`) REFERENCES `daming_paper` (`paper_id`)
    ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `daming_question_answer`
  ADD CONSTRAINT `daming_question_answer_ibfk_2`
    FOREIGN KEY (`question_id`) REFERENCES `daming_question` (`id`)
    ON DELETE CASCADE ON UPDATE CASCADE;

-- ============================================================
-- 未包含在本脚本中、但你库若仍为 INT 时建议一并改为 BIGINT 的列（无外键时可直接 MODIFY）：
--   daming_paper.paper_info_id        （指向 daming_content_info.id，新卷/新内容用雪花时需加宽）
--   daming_question.question_info_id  （题干正文 content 主键，新题用雪花时建议同样 BIGINT）
-- JSON（content）内的题目 id 与上表一致即可，无需 ALTER。
-- 已 BIGINT 的子表：daming_practice_column.paper_id、daming_practice_column_question.question_id、
--   daming_question_favorite 等一般已兼容，若你环境为 INT 请自行 MODIFY 为 BIGINT。
-- ============================================================
