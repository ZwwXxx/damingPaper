package com.ruoyi.web.handler;

import com.dm.quiz.service.DamingUserService;
import com.ruoyi.common.core.domain.entity.LoginUser;
import com.ruoyi.common.core.domain.model.DamingUser;
import com.ruoyi.common.service.WxScanLogService;
import com.ruoyi.framework.web.service.TokenService;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
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

    @Value("${wx.mp.authScope:base}")
    private String authScope;

    @Autowired
    private WxScanLogService scanLogService;

    @Autowired
    private DamingUserService damingUserService;

    @Autowired
    private TokenService tokenService;

    @Lazy
    @Autowired
    private WxLoginWebSocketHandler webSocketHandler;

    // 静默授权URL（只获取openId，无需用户确认）
    public static final String URL_BASE = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base&state=%s#wechat_redirect";

    // 完整授权URL（获取用户信息，需要用户确认）
    public static final String URL_USERINFO = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=%s#wechat_redirect";

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map,
                                    WxMpService wxMpService, WxSessionManager wxSessionManager) throws WxErrorException {
        String openId = wxMpXmlMessage.getFromUser();
        Integer sceneId;
        String eventType = wxMpXmlMessage.getEvent(); // subscribe 或 SCAN

        try {
            String eventKey = wxMpXmlMessage.getEventKey();
            eventKey = eventKey.replace("qrscene_", "");
            sceneId = Integer.parseInt(eventKey);
            log.info("用户扫码 - openId: {}, sceneId: {}, eventType: {}", openId, sceneId, eventType);
        } catch (Exception e) {
            log.error("解析EventKey失败 - eventKey: {}", wxMpXmlMessage.getEventKey(), e);
            return null;
        }

        // ⭐ 更新扫码记录状态为"已扫码"，并保存openId
        com.ruoyi.common.domain.WxScanLog scanLog = null;
        String sessionId = null;

        try {
            // 根据sceneId查询扫码记录
            scanLog = scanLogService.getBySceneId(sceneId);
            if (scanLog != null && scanLog.getSceneStr() != null) {
                String sceneStr = scanLog.getSceneStr();
                sessionId = scanLog.getSessionId();
                log.info("查询到扫码记录 - sceneId: {}, sceneStr: {}, sessionId: {}", sceneId, sceneStr, sessionId);

                // 扫码时只有openId，unionId和nickname要在授权后才有，所以传null
                boolean updated = scanLogService.updateToScanned(sceneStr, openId, null, null);
                if (updated) {
                    log.info("✅ 更新扫码状态成功 - sceneStr: {}, openId: {}", sceneStr, openId);
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

        // ⭐ 推送授权链接给用户
        String redirectUri = callback + "/wx/portal/public/callBack";
        log.info("构建授权URL - callback: {}, redirectUri: {}", callback, redirectUri);

        // 使用sceneId作为state参数，方便回调时关联
        String state = String.valueOf(sceneId);

        // ⭐⭐⭐ 调试日志：查看实际读取到的 authScope 值
        log.info("🔍 调试 - authScope 配置值: [{}], 长度: {}, 是否为base: {}",
                authScope,
                authScope != null ? authScope.length() : "null",
                "base".equalsIgnoreCase(authScope));

        // ⭐ 根据配置选择授权方式
        String authorizeUrl;
        String authModeDesc;

        if ("userinfo".equalsIgnoreCase(authScope)) {
            // 完整授权（snsapi_userinfo）：可获取用户详细信息，需要用户点击授权
            authorizeUrl = String.format(URL_USERINFO,
                wxMpService.getWxMpConfigStorage().getAppId(),
                URLEncoder.encode(redirectUri),
                state);
            authModeDesc = "完整授权(snsapi_userinfo)";
            log.info("使用完整授权模式 - URL: {}", authorizeUrl);
        } else {
            // 静默授权（snsapi_base）：只获取openId，已关注用户自动完成授权
            authorizeUrl = String.format(URL_BASE,
                wxMpService.getWxMpConfigStorage().getAppId(),
                URLEncoder.encode(redirectUri),
                state);
            authModeDesc = "静默授权(snsapi_base)";
            log.info("使用静默授权模式 - URL: {}", authorizeUrl);
        }

        // ⭐ 根据事件类型和授权模式返回不同的消息
        String content;
        if ("subscribe".equals(eventType)) {
            // 首次关注
            if ("userinfo".equalsIgnoreCase(authScope)) {
                content = "✅ 感谢关注「大明刷题」！\n\n" +
                         "请点击下方链接完成登录：\n" +
                         "<a href=\"" + authorizeUrl + "\">📱 点击授权登录</a>";
            } else {
                // base模式 - 首次关注：简洁提示，前端会自动跳转
                content = "✅ 感谢关注「大明刷题」！\n\n" +
                         "正在为您自动登录，请稍候...";
            }
        } else {
            // 已关注用户扫码
            if ("userinfo".equalsIgnoreCase(authScope)) {
                content = "欢迎回来！\n\n" +
                         "请点击下方链接完成登录：\n" +
                         "<a href=\"" + authorizeUrl + "\">📱 点击授权登录</a>";
            } else {
                // base模式 - 已关注：前端会自动跳转，微信只发简单提示
                content = "✅ 正在为您自动登录，请稍候...";
            }
        }

        log.info("推送授权消息 - eventType: {}, authMode: {}, hasSessionId: {}",
                eventType, authModeDesc, sessionId != null);

        // ⭐⭐⭐ 关键：根据授权模式选择不同的处理方式
        if (sessionId != null) {
            try {
                if ("userinfo".equalsIgnoreCase(authScope)) {
                    // ========== userinfo模式：需要跳转授权获取详细信息 ==========
                    webSocketHandler.sendMessageToSession(sessionId, "SCANNED");
                    log.info("✅ [userinfo模式] 发送SCANNED消息 - sessionId: {}", sessionId);
                } else {
                    // ========== base模式：直接根据openId完成登录 ==========
                    log.info("✅ [base模式] 开始直接登录流程 - openId: {}", openId);

                    // 1. 创建只包含openId的用户信息对象
                    WxOAuth2UserInfo userInfo = new WxOAuth2UserInfo();
                    userInfo.setOpenid(openId);

                    // 2. 根据openId查询或创建用户
                    DamingUser user = damingUserService.findOrCreateByWxInfo(userInfo);
                    log.info("✅ 用户信息处理完成 - userId: {}, userName: {}", user.getUserId(), user.getUserName());

                    // 3. 更新扫码记录状态为登录成功
                    if (scanLog != null) {
                        boolean updated = scanLogService.updateToSuccess(
                                scanLog.getSceneStr(),
                                user.getUserId().intValue(),
                                null,  // unionId
                                null   // nickname
                        );
                        if (updated) {
                            log.info("✅ 更新扫码记录为登录成功 - sceneStr: {}, userId: {}",
                                    scanLog.getSceneStr(), user.getUserId());
                        }
                    }

                    // 4. 生成JWT Token
                    LoginUser loginUser = new LoginUser(user.getUserId(), user);
                    String token = tokenService.createToken(loginUser);
                    log.info("✅ 生成系统JWT Token成功 - userId: {}, token: {}",
                            user.getUserId(), token.substring(0, Math.min(20, token.length())) + "...");

                    // 5. 通过WebSocket发送token给前端，前端收到后自动登录
                    webSocketHandler.sendMessageToSession(sessionId, "SUCCESS:" + token);
                    log.info("✅ [base模式] 发送token给前端自动登录 - sessionId: {}, userId: {}",
                            sessionId, user.getUserId());
                }
            } catch (Exception e) {
                log.error("处理登录逻辑失败 - sessionId: {}, openId: {}", sessionId, openId, e);
                try {
                    webSocketHandler.sendMessageToSession(sessionId, "ERROR:登录失败，请重试");
                } catch (Exception e2) {
                    log.error("发送错误消息失败", e2);
                }
            }
        }

        WxMpXmlOutTextMessage m = WxMpXmlOutMessage.TEXT().content(content)
                .fromUser(wxMpXmlMessage.getToUser()).toUser(wxMpXmlMessage.getFromUser())
                .build();
        return m;
    }
}
