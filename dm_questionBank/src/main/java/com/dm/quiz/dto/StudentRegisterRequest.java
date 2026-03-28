package com.dm.quiz.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

/**
 * 前台注册：邮箱 + 昵称 + 密码 + 邮箱验证码；后端将 user_name 设为规范化邮箱。
 * <p>兼容历史请求体：登录账号字段曾用 username / userName，与 email 同义，反序列化时写入 email。</p>
 */
@Data
public class StudentRegisterRequest {
    @JsonAlias({ "username", "userName", "user_name" })
    private String email;
    private String nickName;
    private String password;
    @JsonAlias({ "code", "email_code" })
    private String emailCode;
}
