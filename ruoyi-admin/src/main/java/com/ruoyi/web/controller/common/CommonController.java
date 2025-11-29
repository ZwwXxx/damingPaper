package com.ruoyi.web.controller.common;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.oss.AliOssClient;
import com.ruoyi.common.utils.oss.OssUploadResult;
import com.ruoyi.framework.config.ServerConfig;

/**
 * 通用请求处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/common")
public class CommonController
{
    private static final Logger log = LoggerFactory.getLogger(CommonController.class);

    @Autowired
    private ServerConfig serverConfig;

    @Autowired(required = false)
    private AliOssClient aliOssClient;

    private static final String FILE_DELIMETER = ",";
    private static final long DEFAULT_SIGN_EXPIRE = TimeUnit.MINUTES.toSeconds(5);

    /**
     * 通用下载请求
     * 
     * @param fileName 文件名称
     * @param delete 是否删除
     */
    @GetMapping("/download")
    public void fileDownload(String fileName, Boolean delete, HttpServletResponse response, HttpServletRequest request)
    {
        try
        {
            if (!FileUtils.checkAllowDownload(fileName))
            {
                throw new Exception(StringUtils.format("文件名称({})非法，不允许下载。 ", fileName));
            }
            String realFileName = System.currentTimeMillis() + fileName.substring(fileName.indexOf("_") + 1);
            String filePath = RuoYiConfig.getDownloadPath() + fileName;

            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, realFileName);
            FileUtils.writeBytes(filePath, response.getOutputStream());
            if (delete)
            {
                FileUtils.deleteFile(filePath);
            }
        }
        catch (Exception e)
        {
            log.error("下载文件失败", e);
        }
    }

    /**
     * 获取OSS对象临时访问链接.
     */
    @GetMapping("/oss/sign")
    public AjaxResult generateOssSignedUrl(@RequestParam("objectName") String objectName,
            @RequestParam(value = "expireSeconds", required = false) Long expireSeconds) {
        if (!useOss()) {
            return AjaxResult.error("当前未启用OSS");
        }
        String presignedUrl = aliOssClient.generatePresignedUrl(objectName,
                expireSeconds == null ? DEFAULT_SIGN_EXPIRE : expireSeconds, null);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("url", presignedUrl);
        ajax.put("expireSeconds", expireSeconds == null ? DEFAULT_SIGN_EXPIRE : expireSeconds);
        return ajax;
    }

    /**
     * 通用上传请求（单个）- 简化版本
     * 直接返回完整的CDN地址，简单粗暴
     */
    @PostMapping("/upload")
    public AjaxResult uploadFile(MultipartFile file) throws Exception
    {
        try
        {
            // ⭐ 文件大小限制：2MB
            long maxFileSize = 2 * 1024 * 1024; // 2MB = 2 * 1024 * 1024 bytes
            if (file.getSize() > maxFileSize) 
            {
                return AjaxResult.error("文件大小不能超过2MB，当前文件大小：" + formatFileSize(file.getSize()));
            }
            
            // ⭐ 文件类型限制：仅允许图片
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) 
            {
                return AjaxResult.error("只允许上传图片文件，当前文件类型：" + contentType);
            }
            
            if (useOss())
            {
                // ⭐ 直接返回完整的CDN地址
                OssUploadResult result = aliOssClient.upload(file);
                // 使用自定义域名（推荐）或OSS域名 + 防盗链
                String cdnDomain = "https://daming-paper.oss-cn-guangzhou.aliyuncs.com";
                String fullUrl = cdnDomain + "/" + result.getObjectName();
                
                AjaxResult ajax = AjaxResult.success();
                ajax.put("url", fullUrl);  // 完整CDN地址
                ajax.put("fileName", result.getObjectName());  // 纯文件路径（不带域名）
                ajax.put("newFileName", FileUtils.getName(result.getObjectName()));
                ajax.put("originalFilename", file.getOriginalFilename());
                ajax.put("fileSize", formatFileSize(file.getSize())); // 返回文件大小
                return ajax;
            }
            // 上传文件路径
            String filePath = RuoYiConfig.getUploadPath();
            // 上传并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath, file);
            String url = serverConfig.getUrl() + fileName;
            AjaxResult ajax = AjaxResult.success();
            ajax.put("url", url);
            ajax.put("fileName", fileName);
            ajax.put("newFileName", FileUtils.getName(fileName));
            ajax.put("originalFilename", file.getOriginalFilename());
            ajax.put("fileSize", formatFileSize(file.getSize())); // 返回文件大小
            return ajax;
        }
        catch (Exception e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 通用上传请求（多个）
     * 
     * OSS模式：只返回ObjectName列表，不返回完整URL
     */
    @PostMapping("/uploads")
    public AjaxResult uploadFiles(List<MultipartFile> files) throws Exception
    {
        try
        {
            // ⭐ 批量上传：每个文件都需要检查大小和类型
            long maxFileSize = 2 * 1024 * 1024; // 2MB
            for (MultipartFile file : files) {
                if (file.getSize() > maxFileSize) {
                    return AjaxResult.error("文件'" + file.getOriginalFilename() + 
                        "'大小超过2MB，当前大小：" + formatFileSize(file.getSize()));
                }
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return AjaxResult.error("文件'" + file.getOriginalFilename() + 
                        "'不是图片文件，类型：" + contentType);
                }
            }
            
            if (useOss())
            {
                // ⭐ OSS模式：只返回ObjectName，不返回URL
                List<String> fileNames = new ArrayList<>();
                List<String> newFileNames = new ArrayList<>();
                List<String> originalFilenames = new ArrayList<>();
                for (MultipartFile file : files)
                {
                    OssUploadResult result = aliOssClient.upload(file);
                    fileNames.add(result.getObjectName());
                    newFileNames.add(FileUtils.getName(result.getObjectName()));
                    originalFilenames.add(file.getOriginalFilename());
                }
                AjaxResult ajax = AjaxResult.success();
                ajax.put("fileNames", StringUtils.join(fileNames, FILE_DELIMETER));
                ajax.put("newFileNames", StringUtils.join(newFileNames, FILE_DELIMETER));
                ajax.put("originalFilenames", StringUtils.join(originalFilenames, FILE_DELIMETER));
                return ajax;
            }
            // 上传文件路径
            String filePath = RuoYiConfig.getUploadPath();
            List<String> urls = new ArrayList<String>();
            List<String> fileNames = new ArrayList<String>();
            List<String> newFileNames = new ArrayList<String>();
            List<String> originalFilenames = new ArrayList<String>();
            for (MultipartFile file : files)
            {
                // 上传并返回新文件名称
                String fileName = FileUploadUtils.upload(filePath, file);
                String url = serverConfig.getUrl() + fileName;
                urls.add(url);
                fileNames.add(fileName);
                newFileNames.add(FileUtils.getName(fileName));
                originalFilenames.add(file.getOriginalFilename());
            }
            AjaxResult ajax = AjaxResult.success();
            ajax.put("urls", StringUtils.join(urls, FILE_DELIMETER));
            ajax.put("fileNames", StringUtils.join(fileNames, FILE_DELIMETER));
            ajax.put("newFileNames", StringUtils.join(newFileNames, FILE_DELIMETER));
            ajax.put("originalFilenames", StringUtils.join(originalFilenames, FILE_DELIMETER));
            return ajax;
        }
        catch (Exception e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 本地资源通用下载
     */
    @GetMapping("/download/resource")
    public void resourceDownload(String resource, HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        try
        {
            if (!FileUtils.checkAllowDownload(resource))
            {
                throw new Exception(StringUtils.format("资源文件({})非法，不允许下载。 ", resource));
            }
            // 本地资源路径
            String localPath = RuoYiConfig.getProfile();
            // 数据库资源地址
            String downloadPath = localPath + StringUtils.substringAfter(resource, Constants.RESOURCE_PREFIX);
            // 下载名称
            String downloadName = StringUtils.substringAfterLast(downloadPath, "/");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, downloadName);
            FileUtils.writeBytes(downloadPath, response.getOutputStream());
        }
        catch (Exception e)
        {
            log.error("下载文件失败", e);
        }
    }

    private boolean useOss()
    {
        return aliOssClient != null && aliOssClient.isEnabled();
    }

    /**
     * 构建OSS上传结果（旧方法，兼容性保留）
     * @deprecated 建议使用 buildOssAjaxResultWithObjectName，只返回ObjectName
     */
    @Deprecated
    private AjaxResult buildOssAjaxResult(OssUploadResult result, String originalFilename)
    {
        AjaxResult ajax = AjaxResult.success();
        ajax.put("url", result.getUrl());
        ajax.put("fileName", result.getObjectName());
        ajax.put("newFileName", FileUtils.getName(result.getObjectName()));
        ajax.put("originalFilename", originalFilename);
        return ajax;
    }
    
    /**
     * 构建OSS上传结果 - 只返回ObjectName，不返回完整URL
     * 前端需要通过签名接口获取临时访问URL，防止URL泄露和盗链
     */
    private AjaxResult buildOssAjaxResultWithObjectName(OssUploadResult result, String originalFilename)
    {
        AjaxResult ajax = AjaxResult.success();
        // ⭐ 只返回ObjectName（相对路径），不返回完整URL
        ajax.put("fileName", result.getObjectName());
        ajax.put("newFileName", FileUtils.getName(result.getObjectName()));
        ajax.put("originalFilename", originalFilename);
        return ajax;
    }
    
    /**
     * 格式化文件大小
     * @param size 文件大小（字节）
     * @return 格式化后的大小字符串
     */
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + "B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1fKB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.1fMB", size / (1024.0 * 1024.0));
        } else {
            return String.format("%.1fGB", size / (1024.0 * 1024.0 * 1024.0));
        }
    }
}
