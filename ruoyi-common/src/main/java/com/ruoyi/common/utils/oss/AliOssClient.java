package com.ruoyi.common.utils.oss;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.PutObjectRequest;
import com.ruoyi.common.config.AliOssProperties;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.Objects;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * 阿里云OSS上传客户端.
 */
@Component
@ConditionalOnProperty(prefix = "aliyun.oss", name = "enabled", havingValue = "true")
public class AliOssClient {

    private final AliOssProperties properties;

    public AliOssClient(AliOssProperties properties) {
        this.properties = properties;
    }

    public boolean isEnabled() {
        return properties != null
                && properties.isEnabled()
                && StringUtils.isNotEmpty(properties.getEndpoint())
                && StringUtils.isNotEmpty(properties.getBucketName())
                && StringUtils.isNotEmpty(properties.getAccessKeyId())
                && StringUtils.isNotEmpty(properties.getAccessKeySecret());
    }

    /**
     * 上传文件到OSS.
     */
    public OssUploadResult upload(MultipartFile file) {
        if (!isEnabled()) {
            throw new ServiceException("OSS未开启或配置不完整");
        }
        OSS ossClient = null;
        try (InputStream inputStream = file.getInputStream()) {
            ossClient = buildClient();
            String objectName = buildObjectName(file.getOriginalFilename());
            PutObjectRequest request = new PutObjectRequest(properties.getBucketName(), objectName, inputStream);
            ossClient.putObject(request);
            return new OssUploadResult(objectName, buildFileUrl(objectName));
        } catch (OSSException | ClientException | IOException ex) {
            throw wrapException(ex);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 生成临时签名URL.
     *
     * @param objectName oss对象名称
     * @param expireSeconds 有效期秒数
     * @param method 访问方法
     * @return 有效期内可访问的临时URL
     */
    public String generatePresignedUrl(String objectName, long expireSeconds, HttpMethod method) {
        if (!isEnabled()) {
            throw new ServiceException("OSS未开启或配置不完整");
        }
        if (StringUtils.isBlank(objectName)) {
            throw new ServiceException("对象名称不能为空");
        }
        long safeExpire = expireSeconds <= 0 ? 300 : Math.min(expireSeconds, 3600);
        OSS ossClient = null;
        try {
            ossClient = buildClient();
            Date expiration = new Date(System.currentTimeMillis() + safeExpire * 1000);
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(properties.getBucketName(),
                    trimObjectKey(objectName), method == null ? HttpMethod.GET : method);
            request.setExpiration(expiration);
            URL url = ossClient.generatePresignedUrl(request);
            if (Objects.isNull(url)) {
                throw new ServiceException("生成签名URL失败");
            }
            return url.toString();
        } catch (OSSException | ClientException ex) {
            throw wrapException(ex);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    private OSS buildClient() {
        return new OSSClientBuilder()
                .build(formatEndpoint(properties.getEndpoint()),
                        properties.getAccessKeyId(), properties.getAccessKeySecret());
    }

    private String buildObjectName(String originalFilename) {
        String suffix = "";
        if (StringUtils.isNotEmpty(originalFilename) && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String dir = normalizeDir(properties.getDir());
        return dir + DateUtils.datePath() + "/" + IdUtils.fastSimpleUUID() + suffix;
    }

    private String normalizeDir(String dir) {
        if (StringUtils.isBlank(dir)) {
            return "";
        }
        String normalized = dir.replace("\\", "/");
        if (!normalized.endsWith("/")) {
            normalized += "/";
        }
        return normalized.startsWith("/") ? normalized.substring(1) : normalized;
    }

    private String buildFileUrl(String objectName) {
        String domain = StringUtils.isNotEmpty(properties.getBucketDomain())
                ? properties.getBucketDomain()
                : buildDefaultDomain();
        if (StringUtils.isEmpty(domain)) {
            return objectName;
        }
        String base = domain.endsWith("/") ? domain.substring(0, domain.length() - 1) : domain;
        String key = objectName.startsWith("/") ? objectName.substring(1) : objectName;
        return base + "/" + key;
    }

    private String trimObjectKey(String objectName) {
        return objectName.startsWith("/") ? objectName.substring(1) : objectName;
    }

    private String buildDefaultDomain() {
        if (StringUtils.isEmpty(properties.getBucketName()) || StringUtils.isEmpty(properties.getEndpoint())) {
            return null;
        }
        String endpointHost = stripScheme(properties.getEndpoint());
        return "https://" + properties.getBucketName() + "." + endpointHost;
    }

    private String formatEndpoint(String endpoint) {
        if (StringUtils.isEmpty(endpoint)) {
            return endpoint;
        }
        if (endpoint.startsWith("http://") || endpoint.startsWith("https://")) {
            return endpoint;
        }
        return "https://" + endpoint;
    }

    private String stripScheme(String endpoint) {
        if (StringUtils.isEmpty(endpoint)) {
            return endpoint;
        }
        try {
            URI uri = URI.create(endpoint);
            if (StringUtils.isNotEmpty(uri.getHost())) {
                return uri.getHost();
            }
            return endpoint.replaceFirst("https?://", "");
        } catch (Exception ex) {
            return endpoint.replaceFirst("https?://", "");
        }
    }

    private ServiceException wrapException(Exception ex) {
        return (ServiceException) new ServiceException("上传OSS失败：" + ex.getMessage())
                .setDetailMessage(StringUtils.defaultIfEmpty(ex.getMessage(), ex.toString()));
    }
}

