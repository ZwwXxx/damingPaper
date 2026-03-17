package com.ruoyi.web.controller.quiz.student;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.oss.AliOssClient;
import com.ruoyi.common.utils.oss.OssUploadResult;
import com.ruoyi.system.domain.KnowledgeAttachment;
import com.ruoyi.system.domain.KnowledgeComment;
import com.ruoyi.system.domain.KnowledgePoint;
import com.ruoyi.system.domain.KnowledgeSubject;
import com.ruoyi.system.domain.KnowledgeFolder;
import com.ruoyi.system.domain.dto.KnowledgePointBaseDTO;
import com.ruoyi.system.domain.dto.KnowledgePointContentDTO;
import com.ruoyi.common.core.domain.model.DamingUser;
import com.ruoyi.system.service.IKnowledgeAttachmentService;
import com.ruoyi.system.service.IKnowledgeCommentService;
import com.ruoyi.system.service.IKnowledgeInteractionService;
import com.ruoyi.system.service.IKnowledgePointService;
import com.ruoyi.system.service.impl.KnowledgePointServiceImpl;
import com.ruoyi.system.service.IKnowledgeSubjectService;
import com.ruoyi.system.service.IKnowledgeFolderService;
import com.dm.quiz.service.IDamingUserService;
import com.dm.quiz.service.IQuestionKnowledgeService;
import com.dm.quiz.service.IDamingQuestionService;
import com.dm.quiz.domain.DamingQuestion;

/**
 * 前台知识点Controller
 * 
 * @author ruoyi
 * @date 2025-11-28
 */
@RestController
@RequestMapping("/student/knowledge")
public class KnowledgeController extends BaseController
{
    private static final long MAX_HTML_FILE_SIZE = 5 * 1024 * 1024L;
    private static final long MAX_VIDEO_FILE_SIZE = 200 * 1024 * 1024L;

    @Autowired
    private IKnowledgePointService knowledgePointService;

    @Autowired
    private IKnowledgeSubjectService knowledgeSubjectService;

    @Autowired
    private IKnowledgeInteractionService knowledgeInteractionService;

    @Autowired
    private IKnowledgeCommentService knowledgeCommentService;
    
    @Autowired
    private IQuestionKnowledgeService questionKnowledgeService;
    
    @Autowired
    private IDamingQuestionService damingQuestionService;

    @Autowired
    private IDamingUserService userService;

    @Autowired
    private IKnowledgeFolderService knowledgeFolderService;

    @Autowired
    private IKnowledgeAttachmentService knowledgeAttachmentService;

    @Autowired(required = false)
    private AliOssClient aliOssClient;

    @GetMapping("/subjects")
    public AjaxResult listSubjects()
    {
        KnowledgeSubject query = new KnowledgeSubject();
        query.setStatus(1); // 只查询启用的科目
        List<KnowledgeSubject> list = knowledgeSubjectService.selectKnowledgeSubjectList(query);
        return success(list);
    }

    /**
     * 创建新科目
     */
    @PostMapping("/subject")
    public AjaxResult createSubject(@RequestBody KnowledgeSubject subject)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            if (userId == null) {
                return error("请先登录");
            }

            // 获取用户信息
            DamingUser user = userService.selectDamingUserByUserId(userId);
            if (user == null) {
                return error("用户信息不存在");
            }

            // 设置创建者信息
            subject.setCreateBy(user.getUserName());
            subject.setUpdateBy(user.getUserName());
            subject.setStatus(1); // 设置为启用状态
            
            // 检查科目名称是否已存在
            KnowledgeSubject existingSubject = new KnowledgeSubject();
            existingSubject.setSubjectName(subject.getSubjectName());
            List<KnowledgeSubject> existingList = knowledgeSubjectService.selectKnowledgeSubjectList(existingSubject);
            if (existingList != null && existingList.size() > 0) {
                return error("科目名称已存在");
            }

            // 保存科目
            int result = knowledgeSubjectService.insertKnowledgeSubject(subject);
            if (result > 0) {
                return AjaxResult.success("创建科目成功", subject);
            } else {
                return error("创建科目失败");
            }
            
        } catch (Exception e) {
            logger.error("创建科目失败", e);
            return error("创建科目失败：" + e.getMessage());
        }
    }

    /**
     * 查询知识点列表（分页）
     */
    @GetMapping("/points")
    public TableDataInfo listPoints(KnowledgePoint knowledgePoint)
    {
        startPage();
        // 知识点广场（未传 authorId）：只显示已发布且已审核通过的
        if (knowledgePoint.getAuthorId() == null) {
            knowledgePoint.setStatus(1);
            knowledgePoint.setAuditStatus(1);
        }
        // 我的文章（传了 authorId）：按前端筛选条件查，可包含待审核等
        List<KnowledgePointBaseDTO> list = knowledgePointService.selectKnowledgePointList(knowledgePoint);
        fillUserStatusBaseDTO(list);
        fillAuthorAvatarBaseDTO(list);
        return getDataTable(list);
    }


    /**
     * 获取推荐知识点（保留复杂推荐算法）
     * 注意：此接口必须放在 /point/{pointId} 之前，避免路径冲突
     */
    @GetMapping("/point/recommend")
    public AjaxResult getRecommendPoints(@RequestParam(defaultValue = "10") Integer limit)
    {
        List<KnowledgePointBaseDTO> list = knowledgePointService.selectRecommendKnowledgePoints(limit);
        fillUserStatusBaseDTO(list);
        fillAuthorAvatarBaseDTO(list);
        return success(list);
    }

    /**
     * 获取知识点内容详情（详情页第二步）- 仅content等大字段
     */
    @GetMapping("/point/{pointId}/content")
    public AjaxResult getPointContent(@PathVariable Long pointId)
    {
        // 获取内容详情
        KnowledgePointContentDTO content = knowledgePointService.selectKnowledgePointContentByPointId(pointId);
        if (content == null) {
            return error("知识点内容不存在");
        }
        // 进入详情即增加浏览量
        knowledgePointService.increaseViewCount(pointId);
        return success(content);
    }

    /**
     * 获取知识点编辑数据
     */
    @GetMapping("/point/{pointId}/edit")
    public AjaxResult getPointForEdit(@PathVariable Long pointId)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            KnowledgePoint point = ((KnowledgePointServiceImpl)knowledgePointService).getCompleteKnowledgePoint(pointId);
            
            if (point == null) {
                return AjaxResult.error("知识点不存在");
            }
            
            // 检查权限 - 只有作者可以编辑
            if (!point.getAuthorId().equals(userId)) {
                return AjaxResult.error("无权编辑此知识点");
            }
            
            return success(point);
        } catch (RuntimeException e) {
            logger.error("用户认证失败: {}", e.getMessage());
            return AjaxResult.error("请先登录");
        } catch (Exception e) {
            logger.error("获取知识点编辑数据失败: {}", e.getMessage(), e);
            return AjaxResult.error("系统异常，请稍后重试");
        }
    }

    /**
     * 更新知识点
     */
    @PutMapping("/point/{pointId}")
    public AjaxResult updatePoint(@PathVariable Long pointId, @RequestBody KnowledgePoint knowledgePoint)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            String username = SecurityUtils.getUsername();
            
            KnowledgePoint existingPoint = ((KnowledgePointServiceImpl)knowledgePointService).getCompleteKnowledgePoint(pointId);
            if (existingPoint == null) {
                return AjaxResult.error("知识点不存在");
            }
            
            // 检查权限 - 只有作者可以编辑
            if (!existingPoint.getAuthorId().equals(userId)) {
                return AjaxResult.error("无权编辑此知识点");
            }
            
            // 设置更新信息
            knowledgePoint.setPointId(pointId);
            knowledgePoint.setUpdateBy(username);
            knowledgePoint.setUpdateTime(new Date());
            
            // 无需审核，更新后仍为已发布状态
            knowledgePoint.setAuditStatus(1);
            
            int result = knowledgePointService.updateKnowledgePoint(knowledgePoint);
            if (result > 0) {
                return AjaxResult.success("更新成功");
            } else {
                return AjaxResult.error("更新失败");
            }
        } catch (RuntimeException e) {
            logger.error("用户认证失败: {}", e.getMessage());
            return AjaxResult.error("请先登录");
        } catch (Exception e) {
            logger.error("更新知识点失败: {}", e.getMessage(), e);
            return AjaxResult.error("系统异常，请稍后重试");
        }
    }


    /**
     * 获取知识点附件列表（作者编辑用）
     */
    @GetMapping("/point/{pointId}/attachments")
    public AjaxResult listPointAttachments(@PathVariable Long pointId)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            KnowledgePoint existingPoint = ((KnowledgePointServiceImpl)knowledgePointService).getCompleteKnowledgePoint(pointId);
            if (existingPoint == null) {
                return AjaxResult.error("知识点不存在");
            }
            if (!existingPoint.getAuthorId().equals(userId)) {
                return AjaxResult.error("无权查看此知识点附件");
            }
            List<KnowledgeAttachment> list = knowledgeAttachmentService.selectKnowledgeAttachmentByPointId(pointId);
            fillAttachmentPreviewUrl(list);
            return success(list);
        } catch (RuntimeException e) {
            logger.error("用户认证失败: {}", e.getMessage());
            return AjaxResult.error("请先登录");
        } catch (Exception e) {
            logger.error("获取知识点附件失败: {}", e.getMessage(), e);
            return AjaxResult.error("系统异常，请稍后重试");
        }
    }

    /**
     * 获取单个附件预览URL（签名URL，覆盖 Content-Disposition=inline 以支持 iframe 预览）。
     */
    @GetMapping("/attachments/{attachmentId}/previewUrl")
    public AjaxResult getAttachmentPreviewUrl(@PathVariable Long attachmentId,
                                              @RequestParam(value = "expireSeconds", required = false, defaultValue = "600") Long expireSeconds)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            KnowledgeAttachment attachment = knowledgeAttachmentService.selectKnowledgeAttachmentById(attachmentId);
            if (attachment == null) {
                return AjaxResult.error("附件不存在");
            }
            KnowledgePoint point = ((KnowledgePointServiceImpl)knowledgePointService).getCompleteKnowledgePoint(attachment.getPointId());
            if (point == null) {
                return AjaxResult.error("知识点不存在");
            }
            if (!point.getAuthorId().equals(userId)) {
                return AjaxResult.error("无权查看此附件");
            }
            if (!useOss()) {
                return AjaxResult.error("当前未启用OSS");
            }
            String previewUrl = buildPreviewUrlForAttachment(attachment, expireSeconds);
            AjaxResult ajax = AjaxResult.success();
            ajax.put("previewUrl", previewUrl);
            return ajax;
        } catch (RuntimeException e) {
            logger.error("用户认证失败: {}", e.getMessage());
            return AjaxResult.error("请先登录");
        } catch (Exception e) {
            logger.error("获取附件预览URL失败: {}", e.getMessage(), e);
            return AjaxResult.error("系统异常，请稍后重试");
        }
    }

    /**
     * 后端代理预览 HTML：不直连 OSS，彻底规避 x-oss-force-download 强制下载。
     * 仅作者可访问（与附件列表权限一致）。
     */
    @GetMapping(value = "/attachments/{attachmentId}/preview", produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> previewAttachmentHtml(@PathVariable Long attachmentId)
    {
        Long userId = SecurityUtils.getUserId();
        KnowledgeAttachment attachment = knowledgeAttachmentService.selectKnowledgeAttachmentById(attachmentId);
        if (attachment == null) {
            return ResponseEntity.notFound().build();
        }
        KnowledgePoint point = ((KnowledgePointServiceImpl)knowledgePointService).getCompleteKnowledgePoint(attachment.getPointId());
        if (point == null) {
            return ResponseEntity.notFound().build();
        }
        if (!point.getAuthorId().equals(userId)) {
            return ResponseEntity.status(403).build();
        }
        if (!useOss()) {
            return ResponseEntity.status(500).build();
        }
        if (!"html".equalsIgnoreCase(attachment.getFileType())) {
            return ResponseEntity.status(415).build();
        }

        String objectKey = extractObjectKeyFromUrl(attachment.getFileUrl());
        if (StringUtils.isEmpty(objectKey)) {
            return ResponseEntity.status(500).build();
        }
        byte[] bytes = aliOssClient.getObjectBytes(objectKey);
        String html = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/html;charset=UTF-8"))
                .body(html);
    }

    /**
     * 上传并绑定知识点附件（HTML/视频）
     * 仅作者可操作；要求启用OSS
     */
    @PostMapping("/point/{pointId}/attachments/upload")
    public AjaxResult uploadPointAttachment(@PathVariable Long pointId,
                                            @RequestParam("file") MultipartFile file,
                                            @RequestParam(value = "sortOrder", required = false) Integer sortOrder)
    {
        try {
            if (file == null || file.isEmpty()) {
                return AjaxResult.error("请上传文件");
            }

            Long userId = SecurityUtils.getUserId();
            KnowledgePoint existingPoint = ((KnowledgePointServiceImpl)knowledgePointService).getCompleteKnowledgePoint(pointId);
            if (existingPoint == null) {
                return AjaxResult.error("知识点不存在");
            }
            if (!existingPoint.getAuthorId().equals(userId)) {
                return AjaxResult.error("无权为此知识点上传附件");
            }
            if (!useOss()) {
                return AjaxResult.error("当前未启用OSS，知识点附件要求全部上云，请先开启并配置 aliyun.oss");
            }

            String ext = FileUploadUtils.getExtension(file);
            String contentType = file.getContentType();
            boolean isHtml = !StringUtils.isEmpty(ext) && ("html".equalsIgnoreCase(ext) || "htm".equalsIgnoreCase(ext));
            boolean isVideo = contentType != null && contentType.startsWith("video/");

            if (!isHtml && !isVideo) {
                return AjaxResult.error("只允许上传HTML或视频文件（html/htm 或 video/*）");
            }
            if (isHtml && file.getSize() > MAX_HTML_FILE_SIZE) {
                return AjaxResult.error("HTML文件大小不能超过5MB");
            }
            if (isVideo && file.getSize() > MAX_VIDEO_FILE_SIZE) {
                return AjaxResult.error("视频文件大小不能超过200MB");
            }

            // 同一知识点下同名附件校验
            KnowledgeAttachment sameName = knowledgeAttachmentService
                    .selectOneByPointIdAndFileName(pointId, file.getOriginalFilename());
            if (sameName != null) {
                return AjaxResult.error("该知识点已存在同名附件，请勿重复上传。如果需要替换，请先删除原附件。");
            }

            // 计算文件内容哈希（MD5）
            String fileHash = calcFileMd5(file);

            // 全局哈希去重：如果已有相同hash的附件，直接复用其URL和大小
            java.util.List<KnowledgeAttachment> sameHashList =
                    knowledgeAttachmentService.selectKnowledgeAttachmentByHash(fileHash);
            if (sameHashList != null && !sameHashList.isEmpty()) {
                KnowledgeAttachment base = sameHashList.get(0);
                KnowledgeAttachment attachment = new KnowledgeAttachment();
                attachment.setPointId(pointId);
                attachment.setFileName(file.getOriginalFilename());
                attachment.setFileUrl(base.getFileUrl());
                attachment.setFileType(base.getFileType());
                attachment.setFileSize(base.getFileSize());
                attachment.setFileHash(fileHash);
                attachment.setSortOrder(sortOrder == null ? 0 : sortOrder);
                knowledgeAttachmentService.insertKnowledgeAttachment(attachment);

                AjaxResult ajax = AjaxResult.success("已复用历史附件，本次未重新上传文件");
                ajax.put("attachmentId", attachment.getAttachmentId());
                ajax.put("fileName", attachment.getFileName());
                ajax.put("fileUrl", attachment.getFileUrl());
                ajax.put("fileType", attachment.getFileType());
                ajax.put("fileSize", attachment.getFileSize());
                ajax.put("sortOrder", attachment.getSortOrder());
                return ajax;
            }

            OssUploadResult result = aliOssClient.upload(file);
            String url = result.getUrl();

            KnowledgeAttachment attachment = new KnowledgeAttachment();
            attachment.setPointId(pointId);
            attachment.setFileName(file.getOriginalFilename());
            attachment.setFileUrl(url);
            attachment.setFileType(isHtml ? "html" : "video");
            attachment.setFileSize(file.getSize());
            attachment.setFileHash(fileHash);
            attachment.setSortOrder(sortOrder == null ? 0 : sortOrder);
            knowledgeAttachmentService.insertKnowledgeAttachment(attachment);

            AjaxResult ajax = AjaxResult.success();
            ajax.put("attachmentId", attachment.getAttachmentId());
            ajax.put("fileName", attachment.getFileName());
            ajax.put("fileUrl", attachment.getFileUrl());
            ajax.put("fileType", attachment.getFileType());
            ajax.put("fileSize", attachment.getFileSize());
            ajax.put("sortOrder", attachment.getSortOrder());
            return ajax;
        } catch (RuntimeException e) {
            logger.error("用户认证失败: {}", e.getMessage());
            return AjaxResult.error("请先登录");
        } catch (Exception e) {
            logger.error("上传知识点附件失败: {}", e.getMessage(), e);
            return AjaxResult.error("系统异常，请稍后重试");
        }
    }

    /**
     * 更新附件元信息（文件名/排序）
     */
    @PutMapping("/attachments/{attachmentId}")
    public AjaxResult updatePointAttachment(@PathVariable Long attachmentId, @RequestBody KnowledgeAttachment body)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            KnowledgeAttachment existing = knowledgeAttachmentService.selectKnowledgeAttachmentById(attachmentId);
            if (existing == null) {
                return AjaxResult.error("附件不存在");
            }
            KnowledgePoint point = ((KnowledgePointServiceImpl)knowledgePointService).getCompleteKnowledgePoint(existing.getPointId());
            if (point == null) {
                return AjaxResult.error("知识点不存在");
            }
            if (!point.getAuthorId().equals(userId)) {
                return AjaxResult.error("无权修改此附件");
            }

            KnowledgeAttachment update = new KnowledgeAttachment();
            update.setAttachmentId(attachmentId);
            if (!StringUtils.isEmpty(body.getFileName())) {
                update.setFileName(body.getFileName());
            }
            if (body.getSortOrder() != null) {
                update.setSortOrder(body.getSortOrder());
            }
            int result = knowledgeAttachmentService.updateKnowledgeAttachment(update);
            return result > 0 ? AjaxResult.success("更新成功") : AjaxResult.error("更新失败");
        } catch (RuntimeException e) {
            logger.error("用户认证失败: {}", e.getMessage());
            return AjaxResult.error("请先登录");
        } catch (Exception e) {
            logger.error("更新附件失败: {}", e.getMessage(), e);
            return AjaxResult.error("系统异常，请稍后重试");
        }
    }

    /**
     * 删除附件（仅删除绑定记录；OSS对象如需删除可另做异步清理）
     */
    @DeleteMapping("/attachments/{attachmentId}")
    public AjaxResult deletePointAttachment(@PathVariable Long attachmentId)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            KnowledgeAttachment existing = knowledgeAttachmentService.selectKnowledgeAttachmentById(attachmentId);
            if (existing == null) {
                return AjaxResult.error("附件不存在");
            }
            KnowledgePoint point = ((KnowledgePointServiceImpl)knowledgePointService).getCompleteKnowledgePoint(existing.getPointId());
            if (point == null) {
                return AjaxResult.error("知识点不存在");
            }
            if (!point.getAuthorId().equals(userId)) {
                return AjaxResult.error("无权删除此附件");
            }
            int result = knowledgeAttachmentService.deleteKnowledgeAttachmentById(attachmentId);
            return result > 0 ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
        } catch (RuntimeException e) {
            logger.error("用户认证失败: {}", e.getMessage());
            return AjaxResult.error("请先登录");
        } catch (Exception e) {
            logger.error("删除附件失败: {}", e.getMessage(), e);
            return AjaxResult.error("系统异常，请稍后重试");
        }
    }

    /**
     * 点赞/取消点赞
     */
    @PostMapping("/like/{pointId}")
    public AjaxResult toggleLike(@PathVariable Long pointId)
    {
        Long userId = SecurityUtils.getUserId();
        boolean isLiked = knowledgeInteractionService.toggleLike(userId, pointId);
        return AjaxResult.success(isLiked ? "点赞成功" : "取消点赞", isLiked);
    }

    /**
     * 收藏/取消收藏
     */
    @PostMapping("/collect/{pointId}")
    public AjaxResult toggleCollect(@PathVariable Long pointId)
    {
        Long userId = SecurityUtils.getUserId();
        boolean isCollected = knowledgeInteractionService.toggleCollect(userId, pointId);
        return AjaxResult.success(isCollected ? "收藏成功" : "取消收藏", isCollected);
    }

    private boolean useOss()
    {
        return aliOssClient != null && aliOssClient.isEnabled();
    }

    private void fillAttachmentPreviewUrl(List<KnowledgeAttachment> list) {
        if (!useOss() || list == null || list.isEmpty()) {
            return;
        }
        for (KnowledgeAttachment att : list) {
            try {
                att.setPreviewUrl(buildPreviewUrlForAttachment(att, 600L));
            } catch (Exception ignore) {
                // 生成失败不影响列表展示，前端可退化使用 fileUrl
            }
        }
    }

    private String buildPreviewUrlForAttachment(KnowledgeAttachment attachment, Long expireSeconds) {
        if (attachment == null || StringUtils.isEmpty(attachment.getFileUrl())) {
            return null;
        }
        String objectKey = extractObjectKeyFromUrl(attachment.getFileUrl());
        if (StringUtils.isEmpty(objectKey)) {
            return null;
        }
        String type = attachment.getFileType();
        // 强制 inline 覆盖 OSS 的下载策略（签名URL可带 response-content-disposition）
        if ("html".equalsIgnoreCase(type)) {
            return aliOssClient.generatePresignedUrl(objectKey, expireSeconds == null ? 600L : expireSeconds,
                    com.aliyun.oss.HttpMethod.GET, null, "inline");
        }
        if ("video".equalsIgnoreCase(type)) {
            // 视频保持可播放，避免被强制下载
            return aliOssClient.generatePresignedUrl(objectKey, expireSeconds == null ? 600L : expireSeconds,
                    com.aliyun.oss.HttpMethod.GET, null, "inline");
        }
        return aliOssClient.generatePresignedUrl(objectKey, expireSeconds == null ? 600L : expireSeconds,
                com.aliyun.oss.HttpMethod.GET, null, "inline");
    }

    /**
     * 从完整URL中提取OSS对象key（path部分去掉开头'/'）。
     * 兼容：传入本身就是 key 的情况。
     */
    private String extractObjectKeyFromUrl(String fileUrl) {
        if (StringUtils.isEmpty(fileUrl)) {
            return null;
        }
        String url = fileUrl.trim();
        try {
            if (url.startsWith("http://") || url.startsWith("https://")) {
                java.net.URI uri = java.net.URI.create(url);
                String path = uri.getPath();
                if (StringUtils.isEmpty(path)) {
                    return null;
                }
                return path.startsWith("/") ? path.substring(1) : path;
            }
        } catch (Exception ignore) {
        }
        return url.startsWith("/") ? url.substring(1) : url;
    }

    private String calcFileMd5(MultipartFile file) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            try (java.io.InputStream is = file.getInputStream()) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    md.update(buffer, 0, read);
                }
            }
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                String hex = Integer.toHexString(b & 0xff);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (Exception e) {
            logger.warn("计算附件MD5失败，将不进行哈希去重: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取我的收藏列表（精简版 - 性能优化）
     */
    @GetMapping("/my/collects")
    public AjaxResult getMyCollects(@RequestParam(required = false) Long folderId)
    {
        Long userId = SecurityUtils.getUserId();
        List<KnowledgePointBaseDTO> list;
        
        if (folderId != null) {
            // 获取指定收藏夹的收藏（精简版）
            list = knowledgeInteractionService.getCollectedPointsByFolderLite(userId, folderId);
            logger.info("用户 {} 查询收藏夹 {} 的收藏，返回 {} 条精简数据", userId, folderId, list.size());
        } else {
            // 获取所有收藏（精简版）
            list = knowledgeInteractionService.getCollectedPointsLite(userId);
            logger.info("用户 {} 查询所有收藏，返回 {} 条精简数据", userId, list.size());
        }
        fillAuthorAvatarBaseDTO(list);
        return success(list);
    }

    /**
     * 获取知识点评论列表
     */
    @GetMapping("/comments/{pointId}")
    public AjaxResult getComments(@PathVariable Long pointId)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            List<KnowledgeComment> comments = knowledgeCommentService.selectCommentTreeByPointId(pointId, userId);
            return AjaxResult.success("查询成功", comments);
        } catch (Exception e) {
            // 未登录用户
            List<KnowledgeComment> comments = knowledgeCommentService.selectCommentTreeByPointId(pointId, null);
            return AjaxResult.success("查询成功", comments);
        }
    }

    /**
     * 添加知识点评论
     */
    @PostMapping("/comment")
    public AjaxResult addComment(@RequestBody KnowledgeComment comment)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            if (userId == null) {
                return AjaxResult.error("请先登录");
            }
            
            DamingUser user = userService.selectDamingUserByUserId(userId);
            if (user == null) {
                return AjaxResult.error("用户信息不存在，请重新登录");
            }
            
            // 设置评论信息
            comment.setUserId(userId);
            comment.setUserName(user.getUserName());
            comment.setNickName(user.getNickName());
            comment.setAvatar(user.getAvatar());
            comment.setCreateBy(user.getUserName());
            
            int result = knowledgeCommentService.insertKnowledgeComment(comment);
            if (result > 0) {
                return AjaxResult.success("评论成功");
            } else {
                return AjaxResult.error("评论失败");
            }
        } catch (RuntimeException e) {
            // 认证相关异常
            logger.error("用户认证失败: {}", e.getMessage());
            return AjaxResult.error("请先登录");
        } catch (Exception e) {
            // 其他异常
            logger.error("添加评论失败: {}", e.getMessage(), e);
            return AjaxResult.error("系统异常，请稍后重试");
        }
    }

    /**
     * 删除知识点评论
     */
    @DeleteMapping("/comment/{commentId}")
    public AjaxResult deleteComment(@PathVariable Long commentId)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            if (userId == null) {
                return AjaxResult.error("请先登录");
            }
            
            KnowledgeComment comment = knowledgeCommentService.selectKnowledgeCommentByCommentId(commentId);
            if (comment == null) {
                return AjaxResult.error("评论不存在");
            }
            
            // 只能删除自己的评论
            if (!comment.getUserId().equals(userId)) {
                return AjaxResult.error("无权删除此评论");
            }
            
            int result = knowledgeCommentService.deleteKnowledgeCommentByCommentId(commentId);
            if (result > 0) {
                return AjaxResult.success("删除成功");
            } else {
                return AjaxResult.error("删除失败");
            }
        } catch (RuntimeException e) {
            logger.error("用户认证失败: {}", e.getMessage());
            return AjaxResult.error("请先登录");
        } catch (Exception e) {
            logger.error("删除评论失败: {}", e.getMessage(), e);
            return AjaxResult.error("系统异常，请稍后重试");
        }
    }

    /**
     * 点赞/取消点赞评论
     */
    @PostMapping("/comment/like/{commentId}")
    public AjaxResult toggleCommentLike(@PathVariable Long commentId)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            if (userId == null) {
                return AjaxResult.error("请先登录");
            }
            
            boolean isLiked = knowledgeCommentService.toggleCommentLike(userId, commentId);
            return AjaxResult.success(isLiked ? "点赞成功" : "取消点赞", isLiked);
        } catch (RuntimeException e) {
            logger.error("用户认证失败: {}", e.getMessage());
            return AjaxResult.error("请先登录");
        } catch (Exception e) {
            logger.error("点赞评论失败: {}", e.getMessage(), e);
            return AjaxResult.error("系统异常，请稍后重试");
        }
    }

    /**
     * 填充用户点赞和收藏状态（私有方法）
     */
    private void fillUserStatus(List<KnowledgePoint> list)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            for (KnowledgePoint point : list) {
                point.setIsLiked(knowledgeInteractionService.isLiked(userId, point.getPointId()));
                point.setIsCollected(knowledgeInteractionService.isCollected(userId, point.getPointId()));
            }
        } catch (Exception e) {
            // 未登录用户，默认为false
            for (KnowledgePoint point : list) {
                point.setIsLiked(false);
                point.setIsCollected(false);
            }
        }
    }

    /**
     * 填充用户点赞和收藏状态（BaseDTO版本）
     */
    private void fillUserStatusBaseDTO(List<KnowledgePointBaseDTO> list)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            for (KnowledgePointBaseDTO point : list) {
                point.setIsLiked(knowledgeInteractionService.isLiked(userId, point.getPointId()));
                point.setIsCollected(knowledgeInteractionService.isCollected(userId, point.getPointId()));
            }
        } catch (Exception e) {
            // 未登录用户，默认为false
            for (KnowledgePointBaseDTO point : list) {
                point.setIsLiked(false);
                point.setIsCollected(false);
            }
        }
    }

    private void fillAuthorAvatarBaseDTO(List<KnowledgePointBaseDTO> list)
    {
        if (list == null || list.isEmpty()) {
            return;
        }
        Map<Long, String> avatarCache = new HashMap<>();
        for (KnowledgePointBaseDTO point : list) {
            Long authorId = point.getAuthorId();
            if (authorId == null) {
                continue;
            }
            if (avatarCache.containsKey(authorId)) {
                point.setAuthorAvatar(avatarCache.get(authorId));
                continue;
            }
            DamingUser user = userService.selectDamingUserByUserId(authorId);
            String avatar = user != null ? user.getAvatar() : null;
            avatarCache.put(authorId, avatar);
            point.setAuthorAvatar(avatar);
        }
    }

    /**
     * 用户发布知识点
     */
    @PostMapping("/publish")
    public AjaxResult publishKnowledge(@RequestBody KnowledgePoint knowledgePoint)
    {
        try {
            // 检查用户是否登录
            Long userId = SecurityUtils.getUserId();
            if (userId == null) {
                return error("请先登录");
            }

            // 获取用户信息
            DamingUser user = userService.selectDamingUserByUserId(userId);
            if (user == null) {
                return error("用户信息不存在");
            }

            // 设置发布者信息
            knowledgePoint.setCreateBy(user.getUserName());
            knowledgePoint.setUpdateBy(user.getUserName());
            knowledgePoint.setAuthorId(userId);  // 设置作者ID
            knowledgePoint.setAuthorName(user.getNickName() != null ? user.getNickName() : user.getUserName());  // 设置作者名称
            
            // 无需审核，直接发布成功（审核状态设为通过）
            knowledgePoint.setAuditStatus(1); // 1-通过
            knowledgePoint.setStatus(1); // 1-已发布
            
            // 初始化统计数据
            knowledgePoint.setViewCount(0);
            knowledgePoint.setLikeCount(0);
            knowledgePoint.setCollectCount(0);
            knowledgePoint.setCommentCount(0);  // 初始化评论数

            // 保存知识点
            int result = knowledgePointService.insertKnowledgePoint(knowledgePoint);
            
            if (result > 0) {
                return success("知识点发布成功");
            } else {
                return error("发布失败");
            }
            
        } catch (Exception e) {
            logger.error("发布知识点失败", e);
            return error("发布失败：" + e.getMessage());
        }
    }

    /**
     * 导出当前用户发布的知识点（用于备份或迁移到正式环境导入）
     * @param pointIds 可选，指定要导出的知识点ID，不传或为空则导出全部
     */
    @GetMapping("/export")
    public AjaxResult exportMyKnowledge(@RequestParam(value = "pointIds", required = false) List<Long> pointIds)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            if (userId == null) {
                return error("请先登录");
            }
            List<KnowledgePointBaseDTO> myList;
            if (pointIds != null && !pointIds.isEmpty()) {
                myList = new ArrayList<>();
                for (Long pointId : pointIds) {
                    KnowledgePoint q = new KnowledgePoint();
                    q.setPointId(pointId);
                    q.setAuthorId(userId);
                    q.setStatus(1);
                    List<KnowledgePointBaseDTO> one = knowledgePointService.selectKnowledgePointList(q);
                    if (one != null && !one.isEmpty()) {
                        myList.add(one.get(0));
                    }
                }
            } else {
                KnowledgePoint query = new KnowledgePoint();
                query.setAuthorId(userId);
                query.setStatus(1);
                myList = knowledgePointService.selectKnowledgePointList(query);
            }
            List<Map<String, Object>> exportList = new ArrayList<>();
            for (KnowledgePointBaseDTO base : myList) {
                KnowledgePoint full = ((KnowledgePointServiceImpl) knowledgePointService).getCompleteKnowledgePoint(base.getPointId());
                if (full == null) continue;
                Map<String, Object> item = new HashMap<>();
                item.put("subjectId", full.getSubjectId());
                item.put("subjectName", full.getSubjectName());
                item.put("title", full.getTitle());
                item.put("summary", full.getSummary());
                item.put("difficulty", full.getDifficulty());
                item.put("content", full.getContent());
                exportList.add(item);
            }
            return success(exportList);
        } catch (Exception e) {
            logger.error("导出知识点失败", e);
            return error("导出失败：" + e.getMessage());
        }
    }

    /**
     * 导入知识点（从导出文件恢复，便于正式环境一键导入）
     * 重复判定：同作者下同科目且标题完全一致视为重复，重复则跳过并在结果中返回
     */
    @PostMapping("/import")
    public AjaxResult importKnowledge(@RequestBody List<KnowledgePoint> list)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            if (userId == null) {
                return error("请先登录");
            }
            DamingUser user = userService.selectDamingUserByUserId(userId);
            if (user == null) {
                return error("用户信息不存在");
            }
            if (list == null || list.isEmpty()) {
                return error("导入数据为空");
            }
            int successCount = 0;
            List<String> skippedTitles = new ArrayList<>();
            for (KnowledgePoint item : list) {
                if (item.getTitle() == null || item.getTitle().trim().isEmpty()) continue;
                String title = item.getTitle().trim();
                // 重复判断：当前用户下同科目且同标题视为重复
                KnowledgePoint query = new KnowledgePoint();
                query.setAuthorId(userId);
                query.setSubjectId(item.getSubjectId());
                List<KnowledgePointBaseDTO> existing = knowledgePointService.selectKnowledgePointList(query);
                boolean duplicate = existing != null && existing.stream()
                    .anyMatch(p -> title.equals(p.getTitle()));
                if (duplicate) {
                    skippedTitles.add(title);
                    continue;
                }
                item.setPointId(null);
                item.setAuthorId(userId);
                item.setAuthorName(user.getNickName() != null ? user.getNickName() : user.getUserName());
                item.setCreateBy(user.getUserName());
                item.setUpdateBy(user.getUserName());
                item.setAuditStatus(1);
                item.setStatus(1);
                item.setViewCount(0);
                item.setLikeCount(0);
                item.setCollectCount(0);
                item.setCommentCount(0);
                int ret = knowledgePointService.insertKnowledgePoint(item);
                if (ret > 0) successCount++;
            }
            Map<String, Object> data = new HashMap<>();
            data.put("successCount", successCount);
            data.put("skipCount", skippedTitles.size());
            data.put("skippedTitles", skippedTitles);
            return success(data);
        } catch (Exception e) {
            logger.error("导入知识点失败", e);
            return error("导入失败：" + e.getMessage());
        }
    }

    // ==================== 收藏夹管理接口 ====================

    /**
     * 获取用户的收藏夹列表
     */
    @GetMapping("/folders")
    public AjaxResult getUserFolders()
    {
        try {
            Long userId = SecurityUtils.getUserId();
            List<KnowledgeFolder> folders = knowledgeFolderService.selectFoldersByUserId(userId);
            return success(folders);
        } catch (Exception e) {
            logger.error("获取收藏夹列表失败", e);
            return error("获取收藏夹列表失败");
        }
    }

    /**
     * 创建新收藏夹
     */
    @PostMapping("/folder")
    public AjaxResult createFolder(@RequestBody KnowledgeFolder folder)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            
            folder.setUserId(userId);
            folder.setIsDefault(0); // 非默认收藏夹
            folder.setCollectCount(0);
            
            int result = knowledgeFolderService.insertKnowledgeFolder(folder);
            if (result > 0) {
                return success("创建收藏夹成功");
            } else {
                return error("创建收藏夹失败");
            }
        } catch (Exception e) {
            logger.error("创建收藏夹失败", e);
            return error("创建收藏夹失败：" + e.getMessage());
        }
    }

    /**
     * 修改收藏夹
     */
    @PostMapping("/folder/update")
    public AjaxResult updateFolder(@RequestBody KnowledgeFolder folder)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            
            // 验证收藏夹是否属于当前用户
            KnowledgeFolder existFolder = knowledgeFolderService.selectKnowledgeFolderByFolderId(folder.getFolderId());
            if (existFolder == null || !existFolder.getUserId().equals(userId)) {
                return error("无权修改此收藏夹");
            }
            
            int result = knowledgeFolderService.updateKnowledgeFolder(folder);
            if (result > 0) {
                return success("修改收藏夹成功");
            } else {
                return error("修改收藏夹失败");
            }
        } catch (Exception e) {
            logger.error("修改收藏夹失败", e);
            return error("修改收藏夹失败：" + e.getMessage());
        }
    }

    /**
     * 删除收藏夹
     */
    @DeleteMapping("/folder/{folderId}")
    public AjaxResult deleteFolder(@PathVariable Long folderId)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            
            // 验证收藏夹是否属于当前用户
            KnowledgeFolder folder = knowledgeFolderService.selectKnowledgeFolderByFolderId(folderId);
            if (folder == null || !folder.getUserId().equals(userId)) {
                return error("无权删除此收藏夹");
            }
            
            // 不能删除默认收藏夹
            if (folder.getIsDefault() == 1) {
                return error("不能删除默认收藏夹");
            }
            
            int result = knowledgeFolderService.deleteKnowledgeFolderByFolderId(folderId);
            if (result > 0) {
                return success("删除收藏夹成功");
            } else {
                return error("删除收藏夹失败");
            }
        } catch (Exception e) {
            logger.error("删除收藏夹失败", e);
            return error("删除收藏夹失败：" + e.getMessage());
        }
    }

    /**
     * 收藏知识点到指定收藏夹
     */
    @PostMapping("/collect/{pointId}/folder/{folderId}")
    public AjaxResult collectToFolder(@PathVariable Long pointId, @PathVariable Long folderId)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            
            // 验证收藏夹是否属于当前用户
            KnowledgeFolder folder = knowledgeFolderService.selectKnowledgeFolderByFolderId(folderId);
            if (folder == null || !folder.getUserId().equals(userId)) {
                return error("无权访问此收藏夹");
            }
            
            boolean isCollected = knowledgeInteractionService.toggleCollectToFolder(userId, pointId, folderId);
            return AjaxResult.success(isCollected ? "收藏成功" : "取消收藏", isCollected);
        } catch (Exception e) {
            logger.error("收藏到指定收藏夹失败", e);
            return error("操作失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除知识点（仅能删除本人发布的）
     */
    @DeleteMapping("/points")
    public AjaxResult batchDeleteKnowledgePoints(@RequestBody List<Long> pointIds)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            if (userId == null) {
                return error("请先登录");
            }
            if (pointIds == null || pointIds.isEmpty()) {
                return error("请选择要删除的知识点");
            }
            List<Long> toDelete = new ArrayList<>();
            for (Long pointId : pointIds) {
                KnowledgePoint point = ((KnowledgePointServiceImpl) knowledgePointService).getCompleteKnowledgePoint(pointId);
                if (point != null && userId.equals(point.getAuthorId())) {
                    toDelete.add(pointId);
                }
            }
            if (toDelete.isEmpty()) {
                return error("没有可删除的知识点（仅能删除自己发布的）");
            }
            Long[] ids = toDelete.toArray(new Long[0]);
            int ret = knowledgePointService.deleteKnowledgePointByPointIds(ids);
            return success("成功删除 " + ret + " 条知识点");
        } catch (Exception e) {
            logger.error("批量删除知识点失败", e);
            return error("删除失败：" + e.getMessage());
        }
    }

    /**
     * 删除知识点
     */
    @DeleteMapping("/point/{pointId}")
    public AjaxResult deleteKnowledgePoint(@PathVariable Long pointId)
    {
        try {
            Long userId = SecurityUtils.getUserId();
            
            // 验证知识点是否存在
            KnowledgePoint point = ((KnowledgePointServiceImpl)knowledgePointService).getCompleteKnowledgePoint(pointId);
            if (point == null) {
                return error("知识点不存在");
            }
            
            // 验证是否为作者本人
            if (!point.getAuthorId().equals(userId)) {
                return error("只能删除自己发布的知识点");
            }
            
            int result = knowledgePointService.deleteKnowledgePointByPointId(pointId);
            if (result > 0) {
                return success("删除知识点成功");
            } else {
                return error("删除知识点失败");
            }
        } catch (Exception e) {
            logger.error("删除知识点失败", e);
            return error("删除知识点失败：" + e.getMessage());
        }
    }

    @GetMapping("/{pointId}/related-questions")
    public TableDataInfo getRelatedQuestions(@PathVariable Long pointId, 
                                            @RequestParam(required = false) Integer questionType,
                                            @RequestParam(required = false) Integer difficulty) {
        List<Long> questionIds = questionKnowledgeService.getQuestionIdsByKnowledgePointId(pointId);
        
        if (questionIds == null || questionIds.isEmpty()) {
            return getDataTable(new java.util.ArrayList<>());
        }
        
        DamingQuestion queryParam = new DamingQuestion();
        startPage();
        List<DamingQuestion> questions = damingQuestionService.selectDamingQuestionList(queryParam);
        questions = questions.stream()
            .filter(q -> questionIds.contains(q.getId()))
            .filter(q -> questionType == null || q.getQuestionType().equals(questionType))
            .collect(java.util.stream.Collectors.toList());
        
        return getDataTable(questions);
    }

 
}
