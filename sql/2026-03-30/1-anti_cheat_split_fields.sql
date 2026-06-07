-- 1) 给 daming_paper 增加防作弊子功能字段
ALTER TABLE daming_paper
  ADD COLUMN enable_anti_cheat_cut_screen TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否开启防作弊-防止切页/切屏检测',
  ADD COLUMN enable_anti_cheat_single_question TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否开启防作弊-单题展示模式',
  ADD COLUMN enable_anti_cheat_disable_actions TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否开启防作弊-禁用右键/复制/快捷键等操作',
  ADD COLUMN enable_anti_cheat_dev_tools_detection TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否开启防作弊-开发者工具检测',
  ADD COLUMN enable_anti_cheat_browser_env_detection TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否开启防作弊-浏览器环境检测（可疑扩展/自动化工具）',
  ADD COLUMN enable_anti_cheat_shuffle TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否开启防作弊-题目乱序/顺序锁定';

-- 2) 迁移旧数据：
--    旧版 enable_anti_cheat=1 时，把所有子开关都置为 1
UPDATE daming_paper
SET
  enable_anti_cheat_cut_screen = enable_anti_cheat,
  enable_anti_cheat_single_question = enable_anti_cheat,
  enable_anti_cheat_disable_actions = enable_anti_cheat,
  enable_anti_cheat_dev_tools_detection = enable_anti_cheat,
  enable_anti_cheat_browser_env_detection = enable_anti_cheat,
  enable_anti_cheat_shuffle = enable_anti_cheat;

