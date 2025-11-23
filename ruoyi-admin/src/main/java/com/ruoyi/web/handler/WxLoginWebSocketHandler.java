package com.ruoyi.web.handler;

import com.ruoyi.common.domain.WxScanLog;
import com.ruoyi.common.service.WxScanLogService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 微信扫码登录WebSocket处理器
 * 处理WebSocket连接、消息接收和二维码生成
 * 
 * 完整流程：
 * 1. 前端打开登录页，建立WebSocket连接
 * 2. 前端发送"request_qrcode"请求二维码
 * 3. 后端生成二维码并创建扫码记录（状态：待扫码）
 * 4. 用户扫码后，ScanHandler更新状态为已扫码
 * 5. 用户授权后，WxPortalController更新状态为已授权并登录成功
 * 
 * @author ruoyi
 */
@Component
// 临时注释掉条件注解，测试WebSocket功能
// @ConditionalOnProperty(prefix = "wx.mp", name = "enabled", havingValue = "true", matchIfMissing = false)
public class WxLoginWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(WxLoginWebSocketHandler.class);

    private final WxMpService wxMpService;
    private final WxScanLogService scanLogService;

    /**
     * 存储所有活动的WebSocket会话
     * key: sessionId, value: WebSocketSession
     */
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    
    /**
     * 存储会话与场景值的映射关系
     * key: sessionId, value: sceneStr
     */
    private final ConcurrentHashMap<String, String> sessionSceneMap = new ConcurrentHashMap<>();

    public WxLoginWebSocketHandler(WxMpService wxMpService, WxScanLogService scanLogService) {
        this.wxMpService = wxMpService;
        this.scanLogService = scanLogService;
        log.info("========================================");
        log.info("✅ WxLoginWebSocketHandler 已创建！");
        log.info("========================================");
    }

    /**
     * WebSocket连接建立后调用
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        sessions.put(sessionId, session);
        log.info("WebSocket连接建立成功，sessionId: {}, 当前连接数: {}", sessionId, sessions.size());
    }

    /**
     * 接收到客户端消息时调用
     * 前端发送"request_qrcode"或"request_qrcode:redirect_url"请求二维码
     * 
     * 消息格式：
     * 1. "request_qrcode" - 不带重定向
     * 2. "request_qrcode:/profile" - 带重定向到/profile页面
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        String sessionId = session.getId();
        
        log.info("收到WebSocket消息，sessionId: {}, 消息内容: {}", sessionId, payload);
        
        // 解析消息，提取重定向URL
        String redirectUrl = null;
        if (payload.startsWith("request_qrcode:")) {
            // 格式: request_qrcode:/profile
            redirectUrl = payload.substring("request_qrcode:".length());
            log.info("检测到重定向URL: {}", redirectUrl);
        } else if (!"request_qrcode".equals(payload)) {
            log.warn("未知的消息类型: {}", payload);
            return;
        }
        
        try {
            // 获取客户端信息
            String ipAddress = getClientIp(session);
            String userAgent = getUserAgent(session);
            
            // 生成微信场景值（整数）
            Integer sceneId = Math.abs(java.util.UUID.randomUUID().hashCode());
            
            // 向微信申请临时二维码，有效期5分钟
            WxMpQrCodeTicket ticket = wxMpService.getQrcodeService()
                    .qrCodeCreateTmpTicket(sceneId, (int) Duration.ofMinutes(5).getSeconds());
            
            // 创建扫码记录，保存场景值和重定向URL
            WxScanLog scanLog = scanLogService.createScanLog(
                ticket.getUrl(), 
                sessionId, 
                ipAddress, 
                userAgent, 
                sceneId,
                redirectUrl  // 保存重定向URL
            );
            
            // 保存会话与场景值的映射
            sessionSceneMap.put(sessionId, scanLog.getSceneStr());
            
            // 将二维码URL推送给前端
            String qrcodeUrl = ticket.getUrl();
            session.sendMessage(new TextMessage(qrcodeUrl));
            
            log.info("二维码生成成功 - sessionId: {}, sceneStr: {}, sceneId: {}, redirectUrl: {}, URL: {}", 
                    sessionId, scanLog.getSceneStr(), sceneId, redirectUrl, qrcodeUrl);
            
        } catch (WxErrorException e) {
            log.error("生成二维码失败，sessionId: {}", sessionId, e);
            session.sendMessage(new TextMessage("ERROR:生成二维码失败"));
        } catch (IOException e) {
            log.error("发送消息失败，sessionId: {}", sessionId, e);
        }
    }

    /**
     * WebSocket连接关闭后调用
     * 用户关闭页面时，更新扫码记录状态为已取消
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();
        sessions.remove(sessionId);
        
        // 获取该会话对应的场景值，更新状态为已取消
        String sceneStr = sessionSceneMap.remove(sessionId);
        if (sceneStr != null) {
            WxScanLog scanLog = scanLogService.getBySceneStr(sceneStr);
            // 只有待扫码或已扫码状态才更新为已取消
            if (scanLog != null && (scanLog.getScanStatus() == WxScanLog.ScanStatus.WAITING 
                    || scanLog.getScanStatus() == WxScanLog.ScanStatus.SCANNED)) {
                scanLogService.updateToCancelled(sceneStr);
                log.info("用户关闭页面，更新扫码记录为已取消 - sessionId: {}, sceneStr: {}", sessionId, sceneStr);
            }
        }
        
        log.info("WebSocket连接关闭 - sessionId: {}, 状态: {}, 剩余连接数: {}", 
                sessionId, status, sessions.size());
    }

    /**
     * 处理WebSocket传输错误
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String sessionId = session.getId();
        log.error("WebSocket传输错误，sessionId: {}", sessionId, exception);
        
        if (session.isOpen()) {
            session.close();
        }
        sessions.remove(sessionId);
    }

    /**
     * 获取当前活动会话数
     */
    public int getActiveSessionCount() {
        return sessions.size();
    }

    /**
     * 向指定会话发送消息
     */
    public void sendMessageToSession(String sessionId, String message) {
        WebSocketSession session = sessions.get(sessionId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
                log.info("发送消息成功，sessionId: {}, 消息: {}", sessionId, message);
            } catch (IOException e) {
                log.error("发送消息失败，sessionId: {}", sessionId, e);
            }
        } else {
            log.warn("会话不存在或已关闭，sessionId: {}", sessionId);
        }
    }

    /**
     * 广播消息给所有连接
     */
    public void broadcastMessage(String message) {
        sessions.forEach((sessionId, session) -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    log.error("广播消息失败，sessionId: {}", sessionId, e);
                }
            }
        });
        log.info("广播消息完成，接收数: {}", sessions.size());
    }
    
    /**
     * 根据场景值查找对应的会话ID
     * 用于扫码事件触发时，找到对应的前端会话并推送消息
     * 
     * @param sceneStr 场景值
     * @return 会话ID，如果找不到返回null
     */
    public String getSessionIdBySceneStr(String sceneStr) {
        for (ConcurrentHashMap.Entry<String, String> entry : sessionSceneMap.entrySet()) {
            if (sceneStr.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIp(WebSocketSession session) {
        try {
            Object remoteAddress = session.getAttributes().get("REMOTE_ADDRESS");
            if (remoteAddress != null) {
                return remoteAddress.toString();
            }
            // 尝试从handshake headers获取
            HttpHeaders headers = session.getHandshakeHeaders();
            String ip = headers.getFirst("X-Forwarded-For");
            if (ip != null && !ip.isEmpty()) {
                return ip.split(",")[0].trim();
            }
            ip = headers.getFirst("X-Real-IP");
            if (ip != null && !ip.isEmpty()) {
                return ip;
            }
            return "unknown";
        } catch (Exception e) {
            log.warn("获取客户端IP失败", e);
            return "unknown";
        }
    }
    
    /**
     * 获取客户端User-Agent
     */
    private String getUserAgent(WebSocketSession session) {
        try {
            HttpHeaders headers = session.getHandshakeHeaders();
            String ua = headers.getFirst("User-Agent");
            return ua != null ? ua : "unknown";
        } catch (Exception e) {
            log.warn("获取User-Agent失败", e);
            return "unknown";
        }
    }
}
