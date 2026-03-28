package com.ruoyi.web.controller.quiz.student;



import com.dm.quiz.dto.EmailSendBody;

import com.dm.quiz.service.StudentEmailCaptchaService;

import com.ruoyi.common.core.domain.AjaxResult;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;



/**

 * 学生端：仅注册邮箱验证码发送（登录统一为密码，支持邮箱或历史用户名）

 */

@Slf4j

@RestController

@RequestMapping("/quiz/student/user/email")

public class StudentEmailAuthController {



    @Autowired

    private StudentEmailCaptchaService emailCaptchaService;



    @PostMapping("/sendRegisterCode")

    public AjaxResult sendRegisterCode(@RequestBody(required = false) EmailSendBody body) {

        String err = emailCaptchaService.sendRegisterCode(body != null ? body.getEmail() : null);

        if (err != null) {

            return AjaxResult.error(err);

        }

        return AjaxResult.success("验证码已发送，请查收邮箱");

    }

}


