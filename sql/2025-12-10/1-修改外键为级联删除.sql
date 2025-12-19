-- 修改试卷答题表外键为级联删除
ALTER TABLE `daming_paper_answer` 
DROP FOREIGN KEY `daming_paper_answer_ibfk_1`;

ALTER TABLE `daming_paper_answer` 
ADD CONSTRAINT `daming_paper_answer_ibfk_1` 
FOREIGN KEY (`paper_id`) REFERENCES `daming_paper` (`paper_id`) 
ON DELETE CASCADE 
ON UPDATE CASCADE;

-- 修改题目答题表外键为级联删除
ALTER TABLE `daming_question_answer` 
DROP FOREIGN KEY `daming_question_answer_ibfk_1`;

ALTER TABLE `daming_question_answer` 
ADD CONSTRAINT `daming_question_answer_ibfk_1` 
FOREIGN KEY (`paper_id`) REFERENCES `daming_paper` (`paper_id`) 
ON DELETE CASCADE 
ON UPDATE CASCADE;

ALTER TABLE `daming_question_answer` 
DROP FOREIGN KEY `daming_question_answer_ibfk_2`;

ALTER TABLE `daming_question_answer` 
ADD CONSTRAINT `daming_question_answer_ibfk_2` 
FOREIGN KEY (`question_id`) REFERENCES `daming_question` (`id`) 
ON DELETE CASCADE 
ON UPDATE CASCADE;
