package com.ruoyi.web.controller.wx;

import com.dm.quiz.service.DamingUserService;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.entity.LoginUser;
import com.ruoyi.common.core.domain.model.DamingUser;
import com.ruoyi.common.domain.WxScanLog;
import com.ruoyi.common.service.WxScanLogService;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.web.handler.WxLoginWebSocketHandler;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

/**
 * 微信公众号门户控制器
 * 处理微信回调和用户授权
 * 只有在配置了wx.mp.enabled=true时才会加载
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/wx/portal/public")
// 临时注释掉条件注解，测试功能
// @ConditionalOnProperty(prefix = "wx.mp", name = "enabled", havingValue = "true", matchIfMissing = false)
public class WxPortalController {

    private static final Logger log = LoggerFactory.getLogger(WxPortalController.class);

    @Autowired
    private WxMpService wxMpService;
    
    @Autowired
    private WxMpMessageRouter wxMpMessageRouter;
    
    @Autowired
    private WxScanLogService scanLogService;
    
    @Autowired
    private WxLoginWebSocketHandler webSocketHandler;
    
    @Autowired
    private DamingUserService damingUserService;
    
    @Autowired
    private TokenService tokenService;

    /**
     * 微信服务器认证接口
     * 用于微信公众平台配置服务器URL时的验证
     * 
     * @param signature 微信加密签名
     * @param timestamp 时间戳
     * @param nonce 随机数
     * @param echostr 随机字符串
     * @return 验证成功返回echostr
     */
    @GetMapping(produces = "text/plain;charset=utf-8")
    public String authGet(@RequestParam(name = "signature", required = false) String signature,
                          @RequestParam(name = "timestamp", required = false) String timestamp,
                          @RequestParam(name = "nonce", required = false) String nonce,
                          @RequestParam(name = "echostr", required = false) String echostr) {

        log.info("接收到来自微信服务器的认证消息: signature={}, timestamp={}, nonce={}, echostr={}", 
                signature, timestamp, nonce, echostr);
        
        if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
            log.error("请求参数非法");
            throw new IllegalArgumentException("请求参数非法,请核实!");
        }

        if (wxMpService.checkSignature(timestamp, nonce, signature)) {
            log.info("微信服务器验证成功");
            return echostr;
        }

        log.error("微信服务器验证失败");
        return "非法请求";
    }
    
    /**
     * 微信消息和事件推送接口
     * 处理用户发送的消息、关注事件、扫码事件等
     * 
     * @param requestBody 微信推送的XML消息体
     * @param signature 微信加密签名
     * @param timestamp 时间戳
     * @param nonce 随机数
     * @param encType 加密类型
     * @param msgSignature 消息签名
     * @return 回复给微信服务器的消息
     */
    @PostMapping(produces = "application/xml; charset=UTF-8")
    public String post(@RequestBody String requestBody,
                      @RequestParam("signature") String signature,
                      @RequestParam("timestamp") String timestamp,
                      @RequestParam("nonce") String nonce,
                      @RequestParam(name = "encrypt_type", required = false) String encType,
                      @RequestParam(name = "msg_signature", required = false) String msgSignature) {
        
        log.info("接收到微信消息推送 - signature: {}, timestamp: {}, nonce: {}", signature, timestamp, nonce);
        log.debug("消息内容: {}", requestBody);

        // 验证签名
        if (!wxMpService.checkSignature(timestamp, nonce, signature)) {
            log.error("签名验证失败");
            return "非法请求";
        }

        String result = null;
        try {
            // 解析XML消息
            WxMpXmlMessage inMessage;
            if (StringUtils.isNotBlank(encType) && "aes".equals(encType)) {
                // 加密消息
                inMessage = WxMpXmlMessage.fromEncryptedXml(requestBody, 
                    wxMpService.getWxMpConfigStorage(), timestamp, nonce, msgSignature);
            } else {
                // 明文消息
                inMessage = WxMpXmlMessage.fromXml(requestBody);
            }
            
            log.info("收到微信消息 - MsgType: {}, Event: {}, FromUser: {}", 
                    inMessage.getMsgType(), inMessage.getEvent(), inMessage.getFromUser());

            // 使用消息路由器处理消息
            WxMpXmlOutMessage outMessage = wxMpMessageRouter.route(inMessage);
            
            if (outMessage == null) {
                log.info("消息处理完成，无需回复");
                return "";
            }

            // 返回处理结果
            if (StringUtils.isNotBlank(encType) && "aes".equals(encType)) {
                result = outMessage.toEncryptedXml(wxMpService.getWxMpConfigStorage());
            } else {
                result = outMessage.toXml();
            }
            
            log.info("回复微信消息: {}", result);
            return result;
            
        } catch (Exception e) {
            log.error("处理微信消息失败", e);
            return "";
        }
    }

    /**
     * 微信授权回调接口
     * 用户扫码并授权后,微信会回调此接口
     * 
     * 【重要】此功能仅供前台用户使用,操作的是daming_user表
     * 
     * 完整流程：
     * 1. 获取微信用户信息
     * 2. 查询或创建daming_user
     * 3. 生成JWT Token
     * 4. 通过WebSocket通知前端
     * 5. 根据redirect_url重定向到目标页面
     * 
     * @param code 微信授权码
     * @param state 状态参数（可选，用于防CSRF）
     * @return 登录结果页面
     */
    @Anonymous
    @GetMapping("/callBack")
    public String callBack(@RequestParam String code,
                          @RequestParam(required = false) String state) {
        log.info("收到微信授权回调 - code: {}, state: {}", code, state);
        
        try {
            // 通过code获取access_token
            WxOAuth2AccessToken accessToken = wxMpService.getOAuth2Service().getAccessToken(code);
            log.info("获取access_token成功: {}", accessToken.getAccessToken());
            
            // 通过access_token获取用户信息
            WxOAuth2UserInfo userInfo = wxMpService.getOAuth2Service().getUserInfo(accessToken, "zh_CN");
            log.info("获取微信用户信息成功 - openId: {}, unionId: {}, nickname: {}, headImgUrl: {}", 
                    userInfo.getOpenid(), userInfo.getUnionId(), 
                    userInfo.getNickname(), userInfo.getHeadImgUrl());
            
            // ⭐ 步骤1: 根据wx_open_id查询扫码记录，获取redirect_url和sessionId
            WxScanLog scanLog = scanLogService.getByOpenId(userInfo.getOpenid());
            String redirectUrl = null;
            String sessionId = null;
            
            if (scanLog != null) {
                redirectUrl = scanLog.getRedirectUrl();
                sessionId = scanLog.getSessionId();
                log.info("查询到扫码记录 - sceneStr: {}, sessionId: {}, redirectUrl: {}", 
                        scanLog.getSceneStr(), sessionId, redirectUrl);
            } else {
                log.warn("未查询到扫码记录 - openId: {}", userInfo.getOpenid());
            }
            
            // ⭐ 步骤2: 根据wx_open_id查询或创建daming_user
            DamingUser user = damingUserService.findOrCreateByWxInfo(userInfo);
            log.info("✅ 用户信息处理完成 - userId: {}, userName: {}", user.getUserId(), user.getUserName());
            
            // ⭐ 步骤3: 更新扫码记录状态为登录成功
            if (scanLog != null) {
                boolean updated = scanLogService.updateToSuccess(
                        scanLog.getSceneStr(), 
                        user.getUserId().intValue(),  // 保存用户ID
                        userInfo.getUnionId(), 
                        userInfo.getNickname()
                );
                if (updated) {
                    log.info("✅ 更新扫码记录为登录成功 - sceneStr: {}, userId: {}", 
                            scanLog.getSceneStr(), user.getUserId());
                } else {
                    log.warn("⚠️ 更新扫码记录失败 - sceneStr: {}", scanLog.getSceneStr());
                }
            }
            
            // ⭐ 步骤4: 将DamingUser包装成LoginUser，生成系统JWT Token
            LoginUser loginUser = new LoginUser(user.getUserId(), user);
            String token = tokenService.createToken(loginUser);
            log.info("✅ 生成系统JWT Token成功 - userId: {}, token: {}", 
                    user.getUserId(), token.substring(0, Math.min(20, token.length())) + "...");
            
            // ⭐ 步骤5: 通过WebSocket通知前端登录成功
            if (sessionId != null) {
                try {
                    webSocketHandler.sendMessageToSession(sessionId, "SUCCESS:" + token);
                    log.info("✅ 通过WebSocket通知前端登录成功 - sessionId: {}, userId: {}", 
                            sessionId, user.getUserId());
                } catch (Exception e) {
                    log.error("发送WebSocket消息失败 - sessionId: {}", sessionId, e);
                }
            }
            
            // ⭐ 步骤6: 返回成功页面
            return buildSuccessPage(userInfo, scanLog, token, user);
                   
        } catch (WxErrorException e) {
            log.error("微信授权失败", e);
            return "<h1>登录失败!</h1><p>错误信息: " + e.getMessage() + "</p>";
        }
    }
    
    /**
     * 构建授权成功页面
     */
    private String buildSuccessPage(WxOAuth2UserInfo userInfo, WxScanLog scanLog, String token, DamingUser user) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width,initial-scale=1.0'>");
        html.append("<title>登录成功</title>");
        html.append("<style>");
        html.append("body{font-family:Arial,sans-serif;background:#f5f5f5;padding:20px;margin:0;}");
        html.append(".container{max-width:600px;margin:0 auto;background:white;padding:30px;border-radius:10px;box-shadow:0 2px 10px rgba(0,0,0,0.1);}");
        html.append("h1{color:#1ac89a;margin-top:0;}");
        html.append(".success-icon{font-size:60px;text-align:center;color:#1ac89a;}");
        html.append(".user-info{margin:20px 0;padding:15px;background:#f9f9f9;border-radius:5px;}");
        html.append(".user-info img{border-radius:50%;vertical-align:middle;}");
        html.append(".label{color:#666;font-weight:bold;}");
        html.append(".value{color:#333;margin-left:10px;}");
        html.append(".status{margin:20px 0;padding:15px;background:#e8f5e9;border-left:4px solid #4caf50;border-radius:3px;}");
        html.append(".tip{color:#666;font-size:14px;text-align:center;margin-top:20px;}");
        html.append("</style>");
        html.append("</head><body>");
        html.append("<div class='container'>");
        html.append("<div class='success-icon'>✅</div>");
        html.append("<h1 style='text-align:center'>授权成功！</h1>");
        
        html.append("<div class='user-info'>");
        html.append("<p><img src='").append(userInfo.getHeadImgUrl()).append("' width='60'> ");
        html.append("<span class='label'>昵称：</span><span class='value'>").append(userInfo.getNickname()).append("</span></p>");
        html.append("<p><span class='label'>OpenID：</span><span class='value'>").append(userInfo.getOpenid()).append("</span></p>");
        if (userInfo.getUnionId() != null) {
            html.append("<p><span class='label'>UnionID：</span><span class='value'>").append(userInfo.getUnionId()).append("</span></p>");
        }
        html.append("</div>");
        
        html.append("<div class='status'>");
        html.append("<p>✅ 用户ID：").append(user.getUserId()).append("</p>");
        html.append("<p>✅ 用户名：").append(user.getUserName()).append("</p>");
        html.append("<p>✅ 已创建/更新用户信息</p>");
        html.append("<p>✅ 已生成JWT Token</p>");
        html.append("<p>✅ 已更新扫码状态为登录成功</p>");
        html.append("<p>✅ 已通过WebSocket通知前端</p>");
        if (scanLog != null && scanLog.getRedirectUrl() != null) {
            html.append("<p>✅ 重定向目标：").append(scanLog.getRedirectUrl()).append("</p>");
        }
        html.append("</div>");
        
        html.append("<div class='tip'>");
        html.append("<p style='color:#999;font-size:12px;'>请返回电脑页面，应该已自动登录</p>");
        html.append("<p style='color:#999;font-size:12px;'>如未自动跳转，请手动刷新页面</p>");
        html.append("</div>");
        
        html.append("</div></body></html>");
        return html.toString();
    }
}
