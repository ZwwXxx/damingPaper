package com.ruoyi.common.utils.oss;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * OSS签名URL工具类
 * 统一处理ObjectName到临时签名URL的转换，防止真实URL泄露和盗链
 * 
 * 使用场景：
 * 1. 论坛帖子图片
 * 2. 用户反馈图片
 * 3. 用户头像
 * 4. 试卷答题图片
 * 5. 题目图片
 * 
 * @author ruoyi
 */
@Component
public class OssSignUrlHelper {
    
    private static final Logger log = LoggerFactory.getLogger(OssSignUrlHelper.class);
    
    /** 默认签名URL有效期（秒）- 10分钟 */
    public static final long DEFAULT_EXPIRE_SECONDS = 600;
    
    @Autowired(required = false)
    private AliOssClient aliOssClient;
    
    /**
     * 判断是否启用了OSS
     */
    public boolean isOssEnabled() {
        return aliOssClient != null && aliOssClient.isEnabled();
    }
    
    /**
     * 将ObjectName转换为签名URL（单个）
     * 
     * @param objectName OSS对象名称（相对路径）
     * @return 签名URL，如果OSS未启用或转换失败则返回原值
     */
    public String convertToSignedUrl(String objectName) {
        return convertToSignedUrl(objectName, DEFAULT_EXPIRE_SECONDS);
    }
    
    /**
     * 将ObjectName转换为签名URL（单个，自定义有效期）
     * 
     * @param objectName OSS对象名称（相对路径）
     * @param expireSeconds 有效期（秒）
     * @return 签名URL，如果OSS未启用或转换失败则返回原值
     */
    public String convertToSignedUrl(String objectName, long expireSeconds) {
        if (objectName == null || objectName.trim().isEmpty()) {
            return objectName;
        }
        
        // 如果未启用OSS，直接返回原值
        if (!isOssEnabled()) {
            log.debug("OSS未启用，直接返回ObjectName: {}", objectName);
            return objectName;
        }
        
        try {
            // 判断是否为ObjectName（不含http/https开头）
            if (!objectName.startsWith("http://") && !objectName.startsWith("https://")) {
                // ⭐ 生成临时签名URL
                String signedUrl = aliOssClient.generatePresignedUrl(objectName, expireSeconds, null);
                log.debug("生成签名URL成功 - ObjectName: {}, 有效期: {}秒", objectName, expireSeconds);
                return signedUrl;
            } else {
                // 已经是完整URL，直接返回（兼容旧数据）
                log.debug("已经是完整URL，直接返回: {}", objectName);
                return objectName;
            }
        } catch (Exception e) {
            log.error("生成签名URL失败 - ObjectName: {}, 错误: {}", objectName, e.getMessage());
            // 失败时返回原ObjectName
            return objectName;
        }
    }
    
    /**
     * 将ObjectName列表转换为签名URL列表
     * 
     * @param objectNames OSS对象名称列表（相对路径）
     * @return 签名URL列表
     */
    public List<String> convertToSignedUrls(List<String> objectNames) {
        return convertToSignedUrls(objectNames, DEFAULT_EXPIRE_SECONDS);
    }
    
    /**
     * 将ObjectName列表转换为签名URL列表（自定义有效期）
     * 
     * @param objectNames OSS对象名称列表（相对路径）
     * @param expireSeconds 有效期（秒）
     * @return 签名URL列表
     */
    public List<String> convertToSignedUrls(List<String> objectNames, long expireSeconds) {
        if (objectNames == null || objectNames.isEmpty()) {
            return objectNames;
        }
        
        // 如果未启用OSS，直接返回原列表
        if (!isOssEnabled()) {
            log.debug("OSS未启用，直接返回ObjectName列表");
            return objectNames;
        }
        
        List<String> signedUrls = new ArrayList<>(objectNames.size());
        for (String objectName : objectNames) {
            signedUrls.add(convertToSignedUrl(objectName, expireSeconds));
        }
        
        return signedUrls;
    }
    
    /**
     * 将逗号分隔的ObjectName字符串转换为签名URL字符串
     * 适用于数据库中以逗号分隔存储多个图片路径的场景
     * 
     * @param objectNamesStr 逗号分隔的ObjectName字符串
     * @return 逗号分隔的签名URL字符串
     */
    public String convertCommaSeparatedToSignedUrls(String objectNamesStr) {
        return convertCommaSeparatedToSignedUrls(objectNamesStr, DEFAULT_EXPIRE_SECONDS);
    }
    
    /**
     * 将逗号分隔的ObjectName字符串转换为签名URL字符串（自定义有效期）
     * 
     * @param objectNamesStr 逗号分隔的ObjectName字符串
     * @param expireSeconds 有效期（秒）
     * @return 逗号分隔的签名URL字符串
     */
    public String convertCommaSeparatedToSignedUrls(String objectNamesStr, long expireSeconds) {
        if (objectNamesStr == null || objectNamesStr.trim().isEmpty()) {
            return objectNamesStr;
        }
        
        String[] objectNames = objectNamesStr.split(",");
        List<String> signedUrls = new ArrayList<>(objectNames.length);
        
        for (String objectName : objectNames) {
            String trimmed = objectName.trim();
            if (!trimmed.isEmpty()) {
                signedUrls.add(convertToSignedUrl(trimmed, expireSeconds));
            }
        }
        
        return String.join(",", signedUrls);
    }
}
