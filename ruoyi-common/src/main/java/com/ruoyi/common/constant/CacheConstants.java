package com.ruoyi.common.constant;

/**
 * 缓存的key 常量
 * 
 * @author ruoyi
 */
public class CacheConstants
{
    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * 参数管理 cache key
     */
    public static final String SYS_CONFIG_KEY = "sys_config:";

    /**
     * 字典管理 cache key
     */
    public static final String SYS_DICT_KEY = "sys_dict:";

    /**
     * 防重提交 redis key
     */
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:";

    /**
     * 限流 redis key
     */
    public static final String RATE_LIMIT_KEY = "rate_limit:";

    /**
     * 登录账户密码错误次数 redis key
     */
    public static final String PWD_ERR_CNT_KEY = "pwd_err_cnt:";

    // ========== 前台身份模块（与后台 login_tokens 隔离，便于独立练手/重构） ==========
    /** 前台 token：front:token:{token} -> 前台会话对象 */
    public static final String FRONT_TOKEN_KEY = "front:token:";
    /** 前台短信验证码：front:sms:login:{phone} */
    public static final String FRONT_SMS_LOGIN_KEY = "front:sms:login:";
    /** 前台短信防刷限流：front:sms:limit:{phone} */
    public static final String FRONT_SMS_LIMIT_KEY = "front:sms:limit:";
}
