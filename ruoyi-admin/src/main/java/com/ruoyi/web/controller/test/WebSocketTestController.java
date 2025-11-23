package com.ruoyi.web.controller.test;

import com.ruoyi.common.annotation.Anonymous;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * WebSocket测试控制器
 * 用于验证后端服务是否正常
 */
@Anonymous
@RestController
@RequestMapping("/test")
public class WebSocketTestController {

    @GetMapping("/ping")
    public String ping() {
        return "pong - 后端服务正常运行！时间: " + System.currentTimeMillis();
    }
    
    @GetMapping("/ws-status")
    public String wsStatus() {
        return "WebSocket配置已加载，路径: /ws/login";
    }
}
