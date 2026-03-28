-- 前台用户表增加邮箱字段（唯一，可空；用于邮箱验证码登录与邮箱注册）
ALTER TABLE `daming_user`
    ADD COLUMN `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱' AFTER `user_name`;

ALTER TABLE `daming_user`
    ADD UNIQUE INDEX `uk_daming_user_email`(`email`) USING BTREE;
