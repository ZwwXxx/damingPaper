package com.dm.quiz.service;

import com.dm.quiz.util.MailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * 学生端注册邮箱验证码：Redis 存码 5 分钟；发送窗口 5 分钟内最多成功发送 3 次（自首次成功发送起算 TTL）
 */
@Slf4j
@Service
public class StudentEmailCaptchaService {

    private static final int CODE_EXPIRE_MINUTES = 5;
    private static final int WINDOW_SECONDS = 300;
    private static final int MAX_SENDS_PER_WINDOW = 3;

    private static final String REG_CODE = "student:email:register:code:";
    private static final String REG_WINDOW = "student:email:register:window:";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MailUtil mailUtil;

    @Autowired
    private IDamingUserService damingUserService;

    public String normalizeEmail(String raw) {
        if (raw == null) {
            return null;
        }
        String t = raw.trim().toLowerCase();
        return t.isEmpty() ? null : t;
    }

    public boolean isValidEmailFormat(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * @return 错误信息，null 表示成功
     */
    public String sendRegisterCode(String emailRaw) {
        String email = normalizeEmail(emailRaw);
        if (!isValidEmailFormat(email)) {
            return "邮箱格式不正确";
        }
        if (damingUserService.selectDamingUserByEmail(email) != null
                || damingUserService.selectDamingUserByUserName(email) != null) {
            return "该邮箱已被注册";
        }
        String codeKey = REG_CODE + email;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(codeKey))) {
            return "验证码仍在有效期内，请查看邮箱或稍后再试";
        }
        String windowKey = REG_WINDOW + email;
        Long c = stringRedisTemplate.opsForValue().increment(windowKey);
        if (c != null && c == 1L) {
            stringRedisTemplate.expire(windowKey, WINDOW_SECONDS, TimeUnit.SECONDS);
        }
        if (c != null && c > MAX_SENDS_PER_WINDOW) {
            stringRedisTemplate.opsForValue().decrement(windowKey);
            return "5分钟内发送次数已达上限，请稍后再试";
        }
        String code = String.format("%06d", ThreadLocalRandom.current().nextInt(1_000_000));
        try {
            mailUtil.sendMail(email, "注册验证码", "您的注册验证码是：" + code + "，5分钟内有效。");
            stringRedisTemplate.opsForValue().set(codeKey, code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("发送注册验证码邮件失败: {}", email, e);
            stringRedisTemplate.opsForValue().decrement(windowKey);
            return "邮件发送失败，请稍后重试";
        }
        return null;
    }

    /**
     * @return 错误信息，null 表示校验通过并已删除 Redis 中的验证码
     */
    public String verifyAndConsumeRegisterCode(String emailRaw, String code) {
        if (!StringUtils.hasText(code)) {
            return "请输入邮箱验证码";
        }
        String email = normalizeEmail(emailRaw);
        if (!isValidEmailFormat(email)) {
            return "邮箱格式不正确";
        }
        String key = REG_CODE + email;
        String stored = stringRedisTemplate.opsForValue().get(key);
        if (stored == null) {
            return "验证码已过期或不存在";
        }
        if (!stored.equals(code.trim())) {
            return "验证码错误";
        }
        stringRedisTemplate.delete(key);
        return null;
    }
}
