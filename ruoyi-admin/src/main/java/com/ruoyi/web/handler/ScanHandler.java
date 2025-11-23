package com.ruoyi.web.handler;

import com.ruoyi.common.service.WxScanLogService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.Map;

/**
 * 微信扫码事件处理器
 * 只有在配置了wx.mp.enabled=true时才会加载
 * 
 * @author ruoyi
 */
@Component
// 临时注释掉条件注解，测试功能
// @ConditionalOnProperty(prefix = "wx.mp", name = "enabled", havingValue = "true", matchIfMissing = false)
public class ScanHandler implements WxMpMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(ScanHandler.class);

    @Value("${wx.mp.callback}")
    private String callback;
    
    @Autowired
    private WxScanLogService scanLogService;
    
    @Lazy
    @Autowired
    private WxLoginWebSocketHandler webSocketHandler;

    public static final String URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map,
                                    WxMpService wxMpService, WxSessionManager wxSessionManager) throws WxErrorException {
        String openId = wxMpXmlMessage.getFromUser();
        Integer sceneId;

        try {
            String eventKey = wxMpXmlMessage.getEventKey();
            eventKey = eventKey.replace("qrscene_", "");
            sceneId = Integer.parseInt(eventKey);
            log.info("用户扫码 - openId: {}, sceneId: {}", openId, sceneId);
        } catch (Exception e) {
            log.error("解析EventKey失败 - eventKey: {}", wxMpXmlMessage.getEventKey(), e);
            return null;
        }

        // ⭐ 更新扫码记录状态为"已扫码"，并保存openId
        try {
            // 根据sceneId查询扫码记录
            com.ruoyi.common.domain.WxScanLog scanLog = scanLogService.getBySceneId(sceneId);
            if (scanLog != null && scanLog.getSceneStr() != null) {
                String sceneStr = scanLog.getSceneStr();
                log.info("查询到扫码记录 - sceneId: {}, sceneStr: {}", sceneId, sceneStr);
                
                // 扫码时只有openId，unionId和nickname要在授权后才有，所以传null
                boolean updated = scanLogService.updateToScanned(sceneStr, openId, null, null);
                if (updated) {
                    log.info("✅ 更新扫码状态成功 - sceneStr: {}, openId: {}", sceneStr, openId);
                    
                    // ⭐ 通过WebSocket通知前端"已扫码"
                    String sessionId = scanLog.getSessionId();
                    if (sessionId != null) {
                        try {
                            webSocketHandler.sendMessageToSession(sessionId, "SCANNED");
                            log.info("✅ 通过WebSocket通知前端已扫码 - sessionId: {}", sessionId);
                        } catch (Exception e) {
                            log.error("发送WebSocket消息失败 - sessionId: {}", sessionId, e);
                        }
                    }
                } else {
                    log.warn("⚠️ 更新扫码状态失败 - sceneStr: {}", sceneStr);
                }
            } else {
                log.warn("⚠️ 未找到对应的扫码记录或sceneStr为空 - sceneId: {}, scanLog: {}", 
                        sceneId, scanLog);
            }
        } catch (Exception e) {
            log.error("更新扫码状态异常 - sceneId: {}, openId: {}", sceneId, openId, e);
        }

        // 推送授权链接给用户
        String redirectUri = callback + "/wx/portal/public/callBack";
        log.info("构建授权URL - callback: {}, redirectUri: {}", callback, redirectUri);
        
        String authorizeUrl = String.format(URL, wxMpService.getWxMpConfigStorage().getAppId(), 
            URLEncoder.encode(redirectUri));
        log.info("完整授权URL: {}", authorizeUrl);
        
        String content = "请点击登录:<a href=\"" + authorizeUrl + "\">登录</a>";
        WxMpXmlOutTextMessage m = WxMpXmlOutMessage.TEXT().content(content)
                .fromUser(wxMpXmlMessage.getToUser()).toUser(wxMpXmlMessage.getFromUser())
                .build();
        return m;
    }
}
