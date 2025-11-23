package com.ruoyi.common.config;

import cn.hutool.json.JSONUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 微信公众号配置属性类
 * 
 * @author ruoyi
 */
@Data
@ConfigurationProperties(prefix = "wx.mp")
public class WxMpProperties {
    /**
     * 多个公众号配置信息
     */
    private List<MpConfig> configs;

    /**
     * 回调地址
     */
    private String callback;

    @Data
    public static class MpConfig {
        /**
         * 设置微信公众号的appid
         */
        private String appId;

        /**
         * 设置微信公众号的app secret
         */
        private String secret;

        /**
         * 设置微信公众号的token
         */
        private String token;

        /**
         * 设置微信公众号的EncodingAESKey
         */
        private String aesKey;
    }

    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }
}
