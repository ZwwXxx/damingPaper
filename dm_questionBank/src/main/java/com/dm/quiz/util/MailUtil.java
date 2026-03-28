package com.dm.quiz.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * QQ 邮箱等 SMTP 发信封装
 */
@Component
public class MailUtil {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromAddress;

    public void sendMail(String to, String subject, String text) {
        if (!StringUtils.hasText(fromAddress)) {
            throw new IllegalStateException(
                    "未配置发件邮箱：请在 application.yml 中设置 spring.mail.username，或设置环境变量 MAIL_USERNAME（QQ 邮箱填完整地址 + SMTP 授权码 spring.mail.password / MAIL_PASSWORD）");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress.trim());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
