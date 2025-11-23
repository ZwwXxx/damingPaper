package com.ruoyi.common.service;

import com.ruoyi.common.domain.WxScanLog;
import com.ruoyi.common.mapper.WxScanLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 微信扫码记录服务
 * 
 * @author ruoyi
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "wx.mp", name = "enabled", havingValue = "true")
public class WxScanLogService {
    
    @Autowired
    private WxScanLogMapper scanLogMapper;
    
    /** 二维码默认过期时间（分钟） */
    private static final int DEFAULT_EXPIRE_MINUTES = 5;
    
    /**
     * 创建扫码记录（二维码生成时调用）
     * 
     * @param qrcodeUrl 二维码ticket
     * @param sessionId WebSocket会话ID
     * @param ipAddress 客户端IP
     * @param userAgent 客户端UA
     * @param sceneId 微信二维码场景值
     * @param redirectUrl 登录成功后重定向URL
     * @return 扫码记录
     */
    public WxScanLog createScanLog(String qrcodeUrl, String sessionId, 
                                    String ipAddress, String userAgent, 
                                    Integer sceneId, String redirectUrl) {
        WxScanLog scanLog = new WxScanLog();
        
        // 生成唯一场景值字符串
        String sceneStr = generateSceneStr();
        scanLog.setSceneStr(sceneStr);
        scanLog.setSceneId(sceneId);
        scanLog.setQrcodeUrl(qrcodeUrl);
        scanLog.setSessionId(sessionId);
        scanLog.setIpAddress(ipAddress);
        scanLog.setUserAgent(userAgent);
        scanLog.setRedirectUrl(redirectUrl);
        scanLog.setScanStatus(WxScanLog.ScanStatus.WAITING);
        
        // 设置时间
        Date now = new Date();
        scanLog.setCreateTime(now);
        // 默认5分钟过期
        scanLog.setExpireTime(new Date(now.getTime() + DEFAULT_EXPIRE_MINUTES * 60 * 1000));
        
        scanLogMapper.insert(scanLog);
        log.info("创建扫码记录成功 - sceneStr: {}, sceneId: {}, sessionId: {}", 
                sceneStr, sceneId, sessionId);
        
        return scanLog;
    }
    
    /**
     * 更新为已扫码状态（用户扫码时调用）
     * 
     * @param sceneStr 场景值
     * @param wxOpenId 微信OpenID
     * @param wxUnionId 微信UnionID
     * @param wxNickname 微信昵称
     * @return 是否更新成功
     */
    public boolean updateToScanned(String sceneStr, String wxOpenId, 
                                   String wxUnionId, String wxNickname) {
        // 先查询记录是否存在且可以扫码
        WxScanLog scanLog = scanLogMapper.selectBySceneStr(sceneStr);
        if (scanLog == null) {
            log.warn("扫码记录不存在 - sceneStr: {}", sceneStr);
            return false;
        }
        
        if (!scanLog.canScan()) {
            log.warn("二维码不可扫码 - sceneStr: {}, status: {}, expired: {}", 
                    sceneStr, scanLog.getScanStatus(), scanLog.isExpired());
            return false;
        }
        
        int rows = scanLogMapper.updateToScanned(sceneStr, new Date(), 
                wxOpenId, wxUnionId, wxNickname);
        
        if (rows > 0) {
            log.info("更新为已扫码状态成功 - sceneStr: {}, wxOpenId: {}", sceneStr, wxOpenId);
            return true;
        }
        
        return false;
    }
    
    /**
     * 更新为已授权状态（用户点击授权时调用）
     * 
     * @param sceneStr 场景值
     * @return 是否更新成功
     */
    public boolean updateToAuthorized(String sceneStr) {
        WxScanLog scanLog = scanLogMapper.selectBySceneStr(sceneStr);
        if (scanLog == null || !scanLog.canAuth()) {
            log.warn("二维码不可授权 - sceneStr: {}", sceneStr);
            return false;
        }
        
        int rows = scanLogMapper.updateToAuthorized(sceneStr, new Date());
        if (rows > 0) {
            log.info("更新为已授权状态成功 - sceneStr: {}", sceneStr);
            return true;
        }
        
        return false;
    }
    
    /**
     * 更新为登录成功状态（登录成功时调用）
     * 
     * @param sceneStr 场景值
     * @param damingUserId 前台用户ID
     * @param wxUnionId 微信UnionID（可选，授权时更新）
     * @param wxNickname 微信昵称（可选，授权时更新）
     * @return 是否更新成功
     */
    public boolean updateToSuccess(String sceneStr, Integer damingUserId, 
                                   String wxUnionId, String wxNickname) {
        Date now = new Date();
        int rows = scanLogMapper.updateToSuccess(sceneStr, now, now, damingUserId);
        if (rows > 0) {
            log.info("更新为登录成功状态 - sceneStr: {}, userId: {}, unionId: {}, nickname: {}", 
                    sceneStr, damingUserId, wxUnionId, wxNickname);
            return true;
        }
        return false;
    }
    
    /**
     * 更新为已过期状态
     * 
     * @param sceneStr 场景值
     * @return 是否更新成功
     */
    public boolean updateToExpired(String sceneStr) {
        int rows = scanLogMapper.updateToExpired(sceneStr);
        if (rows > 0) {
            log.info("更新为已过期状态 - sceneStr: {}", sceneStr);
            return true;
        }
        return false;
    }
    
    /**
     * 更新为已取消状态（用户关闭页面时调用）
     * 
     * @param sceneStr 场景值
     * @return 是否更新成功
     */
    public boolean updateToCancelled(String sceneStr) {
        int rows = scanLogMapper.updateToCancelled(sceneStr);
        if (rows > 0) {
            log.info("更新为已取消状态 - sceneStr: {}", sceneStr);
            return true;
        }
        return false;
    }
    
    /**
     * 根据场景值字符串查询
     * 
     * @param sceneStr 场景值字符串
     * @return 扫码记录
     */
    public WxScanLog getBySceneStr(String sceneStr) {
        return scanLogMapper.selectBySceneStr(sceneStr);
    }
    
    /**
     * 根据场景值数字查询
     * 
     * @param sceneId 场景值数字
     * @return 扫码记录
     */
    public WxScanLog getBySceneId(Integer sceneId) {
        return scanLogMapper.selectBySceneId(sceneId);
    }
    
    /**
     * 根据会话ID查询最新记录
     * 
     * @param sessionId 会话ID
     * @return 扫码记录
     */
    public WxScanLog getLatestBySessionId(String sessionId) {
        return scanLogMapper.selectLatestBySessionId(sessionId);
    }
    
    /**
     * 根据OpenID查询最新的扫码记录
     * 用于授权回调时查找对应的扫码记录
     * 
     * @param wxOpenId 微信OpenID
     * @return 最新的扫码记录
     */
    public WxScanLog getByOpenId(String wxOpenId) {
        List<WxScanLog> list = scanLogMapper.selectByOpenId(wxOpenId, 1);
        return (list != null && !list.isEmpty()) ? list.get(0) : null;
    }
    
    /**
     * 根据OpenID查询扫码历史
     * 
     * @param wxOpenId 微信OpenID
     * @param limit 查询数量
     * @return 扫码记录列表
     */
    public List<WxScanLog> getHistoryByOpenId(String wxOpenId, int limit) {
        return scanLogMapper.selectByOpenId(wxOpenId, limit);
    }
    
    /**
     * 批量更新过期的二维码
     * 
     * @return 更新数量
     */
    public int batchUpdateExpired() {
        int count = scanLogMapper.batchUpdateExpired();
        if (count > 0) {
            log.info("批量更新过期二维码 - 数量: {}", count);
        }
        return count;
    }
    
    /**
     * 清理指定天数前的记录
     * 
     * @param days 保留天数
     * @return 删除数量
     */
    public int cleanOldRecords(int days) {
        Date beforeDate = new Date(System.currentTimeMillis() - days * 24 * 60 * 60 * 1000L);
        int count = scanLogMapper.deleteByCreateTimeBefore(beforeDate);
        if (count > 0) {
            log.info("清理{}天前的扫码记录 - 数量: {}", days, count);
        }
        return count;
    }
    
    /**
     * 生成唯一场景值
     * 格式: timestamp_uuid后8位
     * 
     * @return 场景值
     */
    private String generateSceneStr() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String timestamp = String.valueOf(System.currentTimeMillis());
        return timestamp + "_" + uuid.substring(0, 8);
    }
}
