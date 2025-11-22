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
     * 通用上传请求（单个）
     */
    @PostMapping("/upload")
    public AjaxResult uploadFile(MultipartFile file) throws Exception
    {
        try
        {
            if (useOss())
            {
                return buildOssAjaxResult(aliOssClient.upload(file), file.getOriginalFilename());
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
            return ajax;
        }
        catch (Exception e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 通用上传请求（多个）
     */
    @PostMapping("/uploads")
    public AjaxResult uploadFiles(List<MultipartFile> files) throws Exception
    {
        try
        {
            if (useOss())
            {
                List<String> urls = new ArrayList<>();
                List<String> fileNames = new ArrayList<>();
                List<String> newFileNames = new ArrayList<>();
                List<String> originalFilenames = new ArrayList<>();
                for (MultipartFile file : files)
                {
                    OssUploadResult result = aliOssClient.upload(file);
                    urls.add(result.getUrl());
                    fileNames.add(result.getObjectName());
                    newFileNames.add(FileUtils.getName(result.getObjectName()));
                    originalFilenames.add(file.getOriginalFilename());
                }
                AjaxResult ajax = AjaxResult.success();
                ajax.put("urls", StringUtils.join(urls, FILE_DELIMETER));
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

    private AjaxResult buildOssAjaxResult(OssUploadResult result, String originalFilename)
    {
        AjaxResult ajax = AjaxResult.success();
        ajax.put("url", result.getUrl());
        ajax.put("fileName", result.getObjectName());
        ajax.put("newFileName", FileUtils.getName(result.getObjectName()));
        ajax.put("originalFilename", originalFilename);
        return ajax;
    }
}
