package com.ruoyi.web.config;

import com.ruoyi.common.config.WxMpProperties;
import com.ruoyi.web.handler.ScanHandler;
import lombok.AllArgsConstructor;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

import static me.chanjar.weixin.common.api.WxConsts.EventType.SCAN;
import static me.chanjar.weixin.common.api.WxConsts.EventType.SUBSCRIBE;
import static me.chanjar.weixin.common.api.WxConsts.XmlMsgType.EVENT;

/**
 * 微信公众号配置类
 * 只有在配置了wx.mp.enabled=true时才会加载
 * 
 * @author ruoyi
 */
@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(WxMpProperties.class)
@ConditionalOnProperty(prefix = "wx.mp", name = "enabled", havingValue = "true", matchIfMissing = false)
public class WxMpConfiguration {
    
    private static final Logger log = LoggerFactory.getLogger(WxMpConfiguration.class);
    
    private final ScanHandler scanHandler;
    private final WxMpProperties properties;
    
    /**
     * 启动时输出微信配置信息
     */
    @PostConstruct
    public void logWxConfig() {
        log.info("========================================");
        log.info("微信公众号配置加载成功");
        log.info("========================================");
        log.info("✅ 回调地址(callback): {}", properties.getCallback());
        log.info("✅ 授权模式(authScope): {}", properties.getAuthScope());
        log.info("✅ 公众号数量: {}", properties.getConfigs() != null ? properties.getConfigs().size() : 0);
        if (properties.getConfigs() != null && !properties.getConfigs().isEmpty()) {
            properties.getConfigs().forEach(config -> {
                log.info("   - AppId: {}", config.getAppId());
                log.info("     Token: {}", maskString(config.getToken()));
                log.info("     Secret: {}", maskString(config.getSecret()));
            });
        }
        log.info("========================================");
    }
    
    /**
     * 脱敏显示敏感信息
     */
    private String maskString(String str) {
        if (str == null || str.length() <= 8) {
            return "****";
        }
        return str.substring(0, 4) + "****" + str.substring(str.length() - 4);
    }

    /**
     * 微信公众号服务配置
     */
    @Bean
    public WxMpService wxMpService() {
        // 这里是为了适配多个公众号的情况
        final List<WxMpProperties.MpConfig> configs = properties.getConfigs();
        if (configs == null) {
            throw new RuntimeException("微信相关配置有误，请仔细核对自己的公众号参数");
        }

        WxMpService service = new WxMpServiceImpl();
        // 将配置列表转换为Map结构，以appId作为key，配置对象作为value
        // 这样微信SDK可以根据appId快速找到对应的公众号配置
        service.setMultiConfigStorages(configs
                .stream().map(a -> {
                    // 为每个公众号创建配置对象
                    WxMpDefaultConfigImpl configStorage;
                    configStorage = new WxMpDefaultConfigImpl();

                    configStorage.setAppId(a.getAppId());
                    configStorage.setSecret(a.getSecret());
                    configStorage.setToken(a.getToken());
                    configStorage.setAesKey(a.getAesKey());
                    return configStorage;
                }).collect(Collectors.toMap(
                        WxMpDefaultConfigImpl::getAppId,  // Key映射器: 使用appId作为Map的键
                        a -> a,                           // Value映射器: 使用配置对象本身作为Map的值
                        (o, n) -> o                       // 合并函数: 遇到重复的appId时，保留第一个配置，忽略后面的
                )));
        return service;
    }

    /**
     * 微信消息路由配置
     */
    @Bean
    public WxMpMessageRouter messageRouter(WxMpService wxMpService) {
        final WxMpMessageRouter newRouter = new WxMpMessageRouter(wxMpService);
        
        // ⭐ 扫码关注事件（首次关注）
        newRouter.rule().async(false).msgType(EVENT).event(SUBSCRIBE).handler(this.scanHandler).end();
        
        // ⭐ 扫码事件（已关注用户）
        newRouter.rule().async(false).msgType(EVENT).event(SCAN).handler(this.scanHandler).end();
        
        return newRouter;
    }
}
