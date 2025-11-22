package com.ruoyi.common.utils.oss;

/**
 * OSS上传结果.
 */
public class OssUploadResult {

    private final String objectName;
    private final String url;

    public OssUploadResult(String objectName, String url) {
        this.objectName = objectName;
        this.url = url;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getUrl() {
        return url;
    }
}

