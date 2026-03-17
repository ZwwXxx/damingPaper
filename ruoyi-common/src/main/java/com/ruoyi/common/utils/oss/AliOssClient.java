package com.ruoyi.common.utils.oss;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.ResponseHeaderOverrides;
import com.aliyun.oss.model.OSSObject;
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
import org.apache.commons.io.IOUtils;
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

            // 显式设置 Content-Type，避免浏览器将 HTML/视频当作下载附件处理
            ObjectMetadata metadata = new ObjectMetadata();
            String originalFilename = file.getOriginalFilename();
            String suffix = "";
            if (StringUtils.isNotEmpty(originalFilename) && originalFilename.contains(".")) {
                suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            }

            // 1. 对于 html / 视频，始终按后缀强制使用正确的预览 Content-Type
            if ("html".equals(suffix) || "htm".equals(suffix)) {
                metadata.setContentType("text/html;charset=UTF-8");
            } else if ("mp4".equals(suffix)) {
                metadata.setContentType("video/mp4");
            } else if ("webm".equals(suffix)) {
                metadata.setContentType("video/webm");
            } else if ("ogg".equals(suffix)) {
                metadata.setContentType("video/ogg");
            } else {
                // 2. 其他类型：优先使用浏览器上传的 contentType，兜底为 octet-stream
                String contentType = file.getContentType();
                if (StringUtils.isNotEmpty(contentType)) {
                    metadata.setContentType(contentType);
                } else {
                    metadata.setContentType("application/octet-stream");
                }
            }

            PutObjectRequest request = new PutObjectRequest(properties.getBucketName(), objectName, inputStream, metadata);
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

    /**
     * 生成临时签名URL（可覆盖响应头，用于预览 inline）。
     */
    public String generatePresignedUrl(String objectName,
                                       long expireSeconds,
                                       HttpMethod method,
                                       String responseContentType,
                                       String responseContentDisposition) {
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

            ResponseHeaderOverrides overrides = new ResponseHeaderOverrides();
            if (StringUtils.isNotEmpty(responseContentType)) {
                overrides.setContentType(responseContentType);
            }
            if (StringUtils.isNotEmpty(responseContentDisposition)) {
                overrides.setContentDisposition(responseContentDisposition);
            }
            request.setResponseHeaders(overrides);

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

    /**
     * 获取对象内容字节（用于后端代理预览，避免 OSS 强制下载策略影响）。
     */
    public byte[] getObjectBytes(String objectName) {
        if (!isEnabled()) {
            throw new ServiceException("OSS未开启或配置不完整");
        }
        if (StringUtils.isBlank(objectName)) {
            throw new ServiceException("对象名称不能为空");
        }
        OSS ossClient = null;
        OSSObject ossObject = null;
        try {
            ossClient = buildClient();
            ossObject = ossClient.getObject(properties.getBucketName(), trimObjectKey(objectName));
            if (ossObject == null || ossObject.getObjectContent() == null) {
                throw new ServiceException("读取OSS对象失败");
            }
            try (InputStream is = ossObject.getObjectContent()) {
                return IOUtils.toByteArray(is);
            }
        } catch (OSSException | ClientException | IOException ex) {
            throw wrapException(ex);
        } finally {
            if (ossObject != null) {
                try {
                    ossObject.close();
                } catch (Exception ignore) {
                }
            }
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

