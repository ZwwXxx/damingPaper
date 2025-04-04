package com.ruoyi.web.controller.quiz.student;

import com.alibaba.fastjson2.JSON;
import com.dm.quiz.mapper.DamingUserMapper;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.LoginUser;
import com.ruoyi.common.core.domain.model.DamingUser;
import com.ruoyi.common.core.domain.model.StudentLoginBody;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.framework.web.service.StudentLoginService;
import com.ruoyi.framework.web.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/quiz/student/user")
public class StudentUserController extends BaseController {
    @Autowired
    private StudentLoginService studentLoginService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private DamingUserMapper damingUserMapper;

    @PostMapping("/resetPwd")
    public AjaxResult resetPwd(@RequestBody StudentLoginBody studentLoginBody) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(studentLoginBody.getPassword());
        DamingUser damingUser = damingUserMapper.selectDamingUserByUserName(studentLoginBody.getUserName());
        damingUser.setPassword(encode);
        return success(damingUserMapper.updateDamingUser(damingUser));
    }

    @PostMapping("/login")
    public AjaxResult login(@RequestBody StudentLoginBody studentLoginBody) {
        AjaxResult ajax = AjaxResult.success();
        String token = studentLoginService.login(studentLoginBody.getUserName(), studentLoginBody.getPassword());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    @GetMapping("/getInfo")
    public AjaxResult getInfo() {
        DamingUser damingUser = SecurityUtils.getLoginUser().getDamingUser();
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", damingUser);
        return ajax;
    }

    // @PostMapping
    // public
    //

    @PutMapping("/updateInfo")
    public AjaxResult updateInfo(@RequestParam("avatarfile") MultipartFile avatarFile,
                                 @RequestParam("userForm") String newUserStr) throws Exception {
        // 上传文件
        String avatar="";
        if (!avatarFile.isEmpty()) {
             avatar = FileUploadUtils.upload(RuoYiConfig.getAvatarPath(), avatarFile, MimeTypeUtils.IMAGE_EXTENSION);
        }
        DamingUser newUser = JSON.parseObject(newUserStr, DamingUser.class);
        // 拿出来loginUser，然后对着DamingUser改
        LoginUser loginUser = getLoginUser();
        DamingUser damingUser = loginUser.getDamingUser();
        damingUser.setNickName(newUser.getNickName());
        damingUser.setAvatar(avatar);
        // 更新数据库
        if (damingUserMapper.updateDamingUser(damingUser) > 0) {
            AjaxResult ajax = AjaxResult.success();
            // 设置新的loginUser回去，redis刷新
            tokenService.setLoginUser(loginUser);
            ajax.put("imgUrl", avatar);
            return ajax;
        }

        return error("修改个人信息异常，请联系管理员");
    }

}
