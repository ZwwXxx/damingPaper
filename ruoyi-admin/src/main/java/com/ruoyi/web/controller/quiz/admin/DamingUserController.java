package com.ruoyi.web.controller.quiz.admin;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.domain.entity.LoginUser;
import com.ruoyi.common.core.domain.model.DamingUser;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.dm.quiz.service.IDamingUserService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 刷题用户Controller
 *
 * @author zww
 * @date 2024-10-18
 */
@RestController
@RequestMapping("/quiz/user")
public class DamingUserController extends BaseController {
    @Autowired
    private IDamingUserService damingUserService;

    /**
     * 查询刷题用户列表
     */
    @PreAuthorize("@ss.hasPermi('quiz:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(DamingUser damingUser) {
        startPage();
        List<DamingUser> list = damingUserService.selectDamingUserList(damingUser);
        return getDataTable(list);
    }

    /**
     * 导出刷题用户列表
     */
    @PreAuthorize("@ss.hasPermi('quiz:user:export')")
    @Log(title = "刷题用户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DamingUser damingUser) {
        List<DamingUser> list = damingUserService.selectDamingUserList(damingUser);
        ExcelUtil<DamingUser> util = new ExcelUtil<DamingUser>(DamingUser.class);
        util.exportExcel(response, list, "刷题用户数据");
    }

    /**
     * 获取刷题用户详细信息
     */
    @PreAuthorize("@ss.hasPermi('quiz:user:query')")
    @GetMapping(value = "/{userId}")
    public AjaxResult getInfo(@PathVariable("userId") Long userId) {
        return success(damingUserService.selectDamingUserByUserId(userId));
    }

    /**
     * 新增刷题用户
     */
    @PreAuthorize("@ss.hasPermi('quiz:user:add')")
    @Log(title = "刷题用户", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult addUser(@RequestParam(value = "avatarfile", required = false) MultipartFile avatarFile,
                              @RequestParam("userForm") String newUserStr) throws Exception {

        String avatar = FileUploadUtils.upload(RuoYiConfig.getAvatarPath(), avatarFile, MimeTypeUtils.IMAGE_EXTENSION);
        DamingUser newUser = JSON.parseObject(newUserStr, DamingUser.class);
        newUser.setAvatar(avatar);
        System.out.println(newUser);
        newUser.setCreateTime(new Date());
        newUser.setUpdateTime(new Date());
        return toAjax(damingUserService.insertDamingUser(newUser));
    }

    /**
     * 修改刷题用户
     */
    @PreAuthorize("@ss.hasPermi('quiz:user:edit')")
    @Log(title = "刷题用户", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult updateInfo(@RequestParam(value = "avatarfile", required = false) MultipartFile avatarFile,
                                 @RequestParam("userForm") String newUserStr) throws Exception {

        DamingUser newUser = JSON.parseObject(newUserStr, DamingUser.class);
        // 上传文件
        String avatar = "";
        if (avatarFile != null) {
            avatar = FileUploadUtils.upload(RuoYiConfig.getAvatarPath(), avatarFile, MimeTypeUtils.IMAGE_EXTENSION);
            newUser.setAvatar(avatar);
        }

        if (damingUserService.updateDamingUser(newUser) > 0) {
            AjaxResult ajax = AjaxResult.success();
            ajax.put("imgUrl", newUser.getAvatar());
            return ajax;
        }
        return error("修改个人信息异常，请联系管理员");
    }

    /**
     * 删除刷题用户
     */
    @PreAuthorize("@ss.hasPermi('quiz:user:remove')")
    @Log(title = "刷题用户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds) {
        return toAjax(damingUserService.deleteDamingUserByUserIds(userIds));
    }
}
