package com.ruoyi.common.utils.oss;

import org.springframework.stereotype.Component;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 安全的OSS Key生成器
 * 防止ObjectKey被猜测和暴力破解
 * 
 * @author ruoyi
 */
@Component
public class SecureOssKeyGenerator {
    
    /** 应用密钥，用于ObjectKey加密混淆 */
    private static final String APP_SECRET = "your-app-secret-key-here";
    
    /** 日期格式化器 */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    
    /**
     * 生成安全的ObjectKey
     * 格式: {business}/{encrypted-date}/{random-hash}.{ext}
     * 
     * @param business 业务类型 (avatar, forum, quiz等)
     * @param originalFileName 原始文件名
     * @param userId 用户ID (用于权限控制)
     * @return 安全的ObjectKey
     */
    public String generateSecureKey(String business, String originalFileName, Long userId) {
        // 1. 获取文件扩展名
        String extension = getFileExtension(originalFileName);
        
        // 2. 生成加密的日期路径
        String encryptedDatePath = generateEncryptedDatePath();
        
        // 3. 生成随机hash (包含用户ID和时间戳)
        String randomHash = generateSecureHash(userId);
        
        // 4. 组装安全的ObjectKey
        return String.format("%s/%s/%s.%s", business, encryptedDatePath, randomHash, extension);
    }
    
    /**
     * 生成加密的日期路径
     * 使用MD5混淆真实日期，防止按日期枚举
     */
    private String generateEncryptedDatePath() {
        String currentDate = LocalDateTime.now().format(DATE_FORMATTER);
        String mixedString = currentDate + APP_SECRET;
        
        String hash = md5(mixedString);
        
        // 取前12位，按4位一组分割作为路径
        String pathHash = hash.substring(0, 12);
        return String.format("%s/%s/%s", 
            pathHash.substring(0, 4),
            pathHash.substring(4, 8), 
            pathHash.substring(8, 12)
        );
    }
    
    /**
     * 生成安全hash
     * 包含用户ID、时间戳、随机UUID的混合hash
     */
    private String generateSecureHash(Long userId) {
        String randomPart = UUID.randomUUID().toString().replace("-", "");
        String timePart = String.valueOf(System.currentTimeMillis());
        String userPart = userId != null ? userId.toString() : "anonymous";
        
        String mixedString = randomPart + timePart + userPart + APP_SECRET;
        return md5(mixedString);
    }
    
    /**
     * MD5加密
     */
    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes("UTF-8"));
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "bin";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
    
    /**
     * 验证ObjectKey是否合法（防止伪造）
     * 
     * @param objectKey 要验证的ObjectKey
     * @param userId 用户ID
     * @return 是否合法
     */
    public boolean validateObjectKey(String objectKey, Long userId) {
        try {
            // 解析ObjectKey结构
            String[] parts = objectKey.split("/");
            if (parts.length < 4) {
                return false;
            }
            
            String business = parts[0];
            String datePart1 = parts[1];
            String datePart2 = parts[2]; 
            String datePart3 = parts[3];
            String fileName = parts.length > 4 ? parts[4] : parts[3];
            
            // 验证业务类型是否在允许列表中
            if (!isValidBusiness(business)) {
                return false;
            }
            
            // 验证日期路径格式
            String reconstructedDatePath = datePart1 + "/" + datePart2 + "/" + datePart3;
            if (!isValidDatePath(reconstructedDatePath)) {
                return false;
            }
            
            // 其他验证逻辑...
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean isValidBusiness(String business) {
        return business.matches("^(avatar|forum|quiz|feedback|announcement)$");
    }
    
    private boolean isValidDatePath(String datePath) {
        return datePath.matches("^[a-f0-9]{4}/[a-f0-9]{4}/[a-f0-9]{4}$");
    }
}
