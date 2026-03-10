package com.ruoyi.web.controller.quiz.admin;

import com.dm.quiz.domain.DamingAnimation;
import com.dm.quiz.service.IDamingAnimationService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.oss.AliOssClient;
import com.ruoyi.common.utils.oss.OssUploadResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 动画库Controller
 *
 * @author zww
 * @date 2026-03-04
 */
@RestController
@RequestMapping("/quiz/animation")
public class DamingAnimationController extends BaseController {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024L;

    @Autowired
    private IDamingAnimationService damingAnimationService;

    @Autowired(required = false)
    private AliOssClient aliOssClient;

    /**
     * 查询动画库列表
     */
    @PreAuthorize("@ss.hasPermi('quiz:animation:list')")
    @GetMapping("/list")
    public TableDataInfo list(DamingAnimation damingAnimation) {
        startPage();
        List<DamingAnimation> list = damingAnimationService.selectDamingAnimationList(damingAnimation);
        return getDataTable(list);
    }

    /**
     * 获取动画库详情
     */
    @PreAuthorize("@ss.hasPermi('quiz:animation:query')")
    @GetMapping("/{animationId}")
    public AjaxResult getInfo(@PathVariable Long animationId) {
        return success(damingAnimationService.selectDamingAnimationByAnimationId(animationId));
    }

    /**
     * 动画HTML上传（上传成功即入库并返回 animationId）
     */
    @PreAuthorize("@ss.hasPermi('quiz:animation:upload')")
    @Log(title = "动画库", businessType = BusinessType.INSERT)
    @PostMapping("/upload")
    public AjaxResult upload(@RequestParam("file") MultipartFile file,
                             @RequestParam(value = "subjectId", required = false) Integer subjectId) throws Exception {
        if (file == null || file.isEmpty()) {
            return AjaxResult.error("请上传HTML文件");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            return AjaxResult.error("文件大小不能超过5MB");
        }

        String ext = FileUploadUtils.getExtension(file);
        if (StringUtils.isEmpty(ext) || (!"html".equalsIgnoreCase(ext) && !"htm".equalsIgnoreCase(ext))) {
            return AjaxResult.error("只允许上传 .html/.htm 文件");
        }

        if (!useOss()) {
            return AjaxResult.error("当前未启用OSS，动画HTML要求全部上云，请先开启并配置 aliyun.oss");
        }

        OssUploadResult result = aliOssClient.upload(file);
        String url = result.getUrl();
        String objectName = result.getObjectName();

        DamingAnimation animation = new DamingAnimation();
        animation.setSubjectId(subjectId);
        animation.setAnimationName(file.getOriginalFilename());
        animation.setAnimationUrl(url);
        animation.setObjectName(objectName);
        animation.setDelFlag("0");
        animation.setCreateUser(SecurityUtils.getUsername());
        damingAnimationService.insertDamingAnimation(animation);

        Map<String, Object> data = new HashMap<>(4);
        data.put("animationId", animation.getAnimationId());
        data.put("url", url);
        data.put("animationName", animation.getAnimationName());
        data.put("objectName", objectName);
        return AjaxResult.success(data);
    }

    /**
     * 删除动画（软删除）
     */
    @PreAuthorize("@ss.hasPermi('quiz:animation:remove')")
    @Log(title = "动画库", businessType = BusinessType.DELETE)
    @DeleteMapping("/{animationIds}")
    public AjaxResult remove(@PathVariable Long[] animationIds) {
        return toAjax(damingAnimationService.deleteDamingAnimationByAnimationIds(animationIds));
    }

    /**
     * 导出动画库列表
     */
    @PreAuthorize("@ss.hasPermi('quiz:animation:export')")
    @Log(title = "动画库", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DamingAnimation damingAnimation) {
        List<DamingAnimation> list = damingAnimationService.selectDamingAnimationList(damingAnimation);
        ExcelUtil<DamingAnimation> util = new ExcelUtil<>(DamingAnimation.class);
        util.exportExcel(response, list, "动画库数据");
    }

    private boolean useOss() {
        return aliOssClient != null && aliOssClient.isEnabled();
    }
}

