package com.ruoyi.web.config;

import com.ruoyi.web.handler.WxLoginWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/**
 * WebSocket配置类
 * 配置微信扫码登录的WebSocket端点
 * 
 * @author ruoyi
 */
@Configuration
@EnableWebSocket
// 临时注释掉条件注解，测试WebSocket是否能正常工作
// @ConditionalOnProperty(prefix = "wx.mp", name = "enabled", havingValue = "true", matchIfMissing = false)
public class WebSocketConfig implements WebSocketConfigurer {

    private static final Logger log = LoggerFactory.getLogger(WebSocketConfig.class);

    private final WxLoginWebSocketHandler wxLoginWebSocketHandler;

    public WebSocketConfig(WxLoginWebSocketHandler wxLoginWebSocketHandler) {
        this.wxLoginWebSocketHandler = wxLoginWebSocketHandler;
        log.info("========================================");
        log.info("✅ WebSocketConfig 配置类已加载！");
        log.info("========================================");
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 注册WebSocket处理器，路径为 /ws/login
        // 允许所有来源的连接（生产环境需要配置具体域名）
        System.out.println("========================================");
        System.out.println("✅ 正在注册WebSocket处理器: /ws/login");
        System.out.println("========================================");
        
        registry.addHandler(wxLoginWebSocketHandler, "/ws/login")
                .setAllowedOrigins("*");
        
        System.out.println("✅ WebSocket处理器注册成功！");
        System.out.println("========================================");
        
        log.info("WebSocket端点已注册: /ws/login");
    }

    /**
     * 配置WebSocket容器
     * 设置消息大小限制等参数
     * 排除 test profile，避免 MOCK 环境下无 ServerContainer 导致单元测试启动报错
     */
    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @Profile("!test")
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        // 设置文本消息缓冲区大小
        container.setMaxTextMessageBufferSize(8192);
        // 设置二进制消息缓冲区大小
        container.setMaxBinaryMessageBufferSize(8192);
        // 设置会话空闲超时时间（毫秒）
        container.setMaxSessionIdleTimeout(60000L);
        return container;
    }
}
