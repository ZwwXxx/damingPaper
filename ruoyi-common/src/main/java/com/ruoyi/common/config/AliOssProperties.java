package com.ruoyi.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云OSS配置.
 */
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
public class AliOssProperties {

    /**
     * 是否启用OSS上传.
     */
    private boolean enabled;

    /**
     * 访问域名，例如 https://oss-cn-shanghai.aliyuncs.com.
     */
    private String endpoint;

    /**
     * 自定义访问域名（可选），优先级高于 endpoint 自动拼接.
     */
    private String bucketDomain;

    /**
     * 访问凭证.
     */
    private String accessKeyId;

    private String accessKeySecret;

    /**
     * 目标存储桶.
     */
    private String bucketName;

    /**
     * 文件根目录.
     */
    private String dir = "upload";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getBucketDomain() {
        return bucketDomain;
    }

    public void setBucketDomain(String bucketDomain) {
        this.bucketDomain = bucketDomain;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }
}

