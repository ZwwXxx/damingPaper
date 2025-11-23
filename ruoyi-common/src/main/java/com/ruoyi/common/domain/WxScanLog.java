package com.ruoyi.common.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 微信扫码登录状态追踪实体
 * 
 * @author ruoyi
 */
@Data
public class WxScanLog implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** 主键ID */
    private Long id;
    
    /** 场景值字符串（业务唯一标识） */
    private String sceneStr;
    
    /** 场景值数字（微信二维码场景值） */
    private Integer sceneId;
    
    /** 二维码ticket */
    private String qrcodeUrl;
    
    /** 二维码图片URL */
    private String qrcodeImageUrl;
    
    /** WebSocket会话ID */
    private String sessionId;
    
    /** 客户端IP地址 */
    private String ipAddress;
    
    /** 客户端User-Agent */
    private String userAgent;
    
    /** 扫码状态: 0-待扫码 1-已扫码 2-已授权 3-登录成功 4-已过期 5-已取消 */
    private Integer scanStatus;
    
    /** 微信OpenID */
    private String wxOpenId;
    
    /** 微信UnionID */
    private String wxUnionId;
    
    /** 微信昵称 */
    private String wxNickname;
    
    /** 关联的前台用户ID */
    private Integer damingUserId;
    
    /** 二维码生成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    
    /** 用户扫码时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date scanTime;
    
    /** 用户授权时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date authTime;
    
    /** 登录成功时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;
    
    /** 二维码过期时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;
    
    /** 登录成功后重定向URL */
    private String redirectUrl;
    
    /** 错误信息 */
    private String errorMsg;
    
    /** 备注 */
    private String remark;
    
    /**
     * 扫码状态枚举
     */
    public static class ScanStatus {
        /** 待扫码 */
        public static final int WAITING = 0;
        /** 已扫码 */
        public static final int SCANNED = 1;
        /** 已授权 */
        public static final int AUTHORIZED = 2;
        /** 登录成功 */
        public static final int SUCCESS = 3;
        /** 已过期 */
        public static final int EXPIRED = 4;
        /** 已取消 */
        public static final int CANCELLED = 5;
        
        /**
         * 获取状态描述
         */
        public static String getDesc(Integer status) {
            if (status == null) return "未知";
            switch (status) {
                case WAITING: return "待扫码";
                case SCANNED: return "已扫码";
                case AUTHORIZED: return "已授权";
                case SUCCESS: return "登录成功";
                case EXPIRED: return "已过期";
                case CANCELLED: return "已取消";
                default: return "未知";
            }
        }
    }
    
    /**
     * 判断是否已过期
     */
    public boolean isExpired() {
        return expireTime != null && expireTime.before(new Date());
    }
    
    /**
     * 判断是否可以扫码（状态为待扫码且未过期）
     */
    public boolean canScan() {
        return scanStatus != null 
            && scanStatus.equals(ScanStatus.WAITING) 
            && !isExpired();
    }
    
    /**
     * 判断是否可以授权（状态为已扫码且未过期）
     */
    public boolean canAuth() {
        return scanStatus != null 
            && scanStatus.equals(ScanStatus.SCANNED) 
            && !isExpired();
    }
}
