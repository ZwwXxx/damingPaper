package com.ruoyi.web.controller.quiz.student;

import com.dm.quiz.dto.StudentRegisterRequest;
import com.dm.quiz.service.DamingUserService;
import com.dm.quiz.service.IDamingUserService;
import com.dm.quiz.service.StudentEmailCaptchaService;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.LoginUser;
import com.ruoyi.common.core.domain.model.DamingUser;
import com.ruoyi.common.core.domain.model.LoginBody;
import com.ruoyi.framework.web.service.StudentLoginService;
import com.ruoyi.framework.web.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 前台用户Controller
 * 
 * @author ruoyi
 */
@Slf4j
@RestController
@RequestMapping("/quiz/student/user")
public class StudentUserController {
    
    @Autowired
    private TokenService tokenService;
    
    @Autowired
    private DamingUserService userService;
    
    @Autowired
    private IDamingUserService damingUserService;
    
    @Autowired
    private StudentLoginService loginService;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private com.dm.quiz.service.IDamingFollowService followService;

    @Autowired
    private StudentEmailCaptchaService emailCaptchaService;
    
    /**
     * 前台学生登录
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody(required = false) LoginBody loginBody) {
        if (loginBody == null) {
            return AjaxResult.error("请求体不能为空");
        }
        String account = loginBody.getUsername() != null ? loginBody.getUsername().trim() : "";
        String pwd = loginBody.getPassword() != null ? loginBody.getPassword() : "";
        if (account.isEmpty()) {
            return AjaxResult.error("请输入邮箱或用户名");
        }
        if (pwd.isEmpty()) {
            return AjaxResult.error("请输入密码");
        }
        log.info("学生登录 - 账号: {}", account);

        String token = loginService.login(account, pwd);
        
        AjaxResult ajax = AjaxResult.success("登录成功");
        ajax.put(Constants.TOKEN, token);
        log.info("✅ 学生登录成功 - 账号: {}", account);
        return ajax;
    }
    
    /**
     * 前台学生注册：邮箱 + 昵称 + 密码 + 邮箱验证码；user_name 与 email 均为规范化邮箱
     */
    @PostMapping("/registry")
    public AjaxResult registry(@RequestBody StudentRegisterRequest req) {
        if (req == null) {
            return AjaxResult.error("参数无效");
        }
        String emailNorm = emailCaptchaService.normalizeEmail(req.getEmail());
        if (!StringUtils.hasText(emailNorm)) {
            return AjaxResult.error("请输入邮箱");
        }
        if (!emailCaptchaService.isValidEmailFormat(emailNorm)) {
            return AjaxResult.error("邮箱格式不正确");
        }
        if (!StringUtils.hasText(req.getNickName()) || !StringUtils.hasText(req.getNickName().trim())) {
            return AjaxResult.error("请输入昵称");
        }
        if (req.getPassword() == null || req.getPassword().trim().isEmpty()) {
            return AjaxResult.error("密码不能为空");
        }
        if (!StringUtils.hasText(req.getEmailCode())) {
            return AjaxResult.error("请输入邮箱验证码");
        }

        if (damingUserService.selectDamingUserByEmail(emailNorm) != null
                || damingUserService.selectDamingUserByUserName(emailNorm) != null) {
            return AjaxResult.error("该邮箱已被注册");
        }

        String verifyErr = emailCaptchaService.verifyAndConsumeRegisterCode(emailNorm, req.getEmailCode());
        if (verifyErr != null) {
            return AjaxResult.error(verifyErr);
        }

        DamingUser user = new DamingUser();
        user.setUserName(emailNorm);
        user.setEmail(emailNorm);
        user.setNickName(req.getNickName().trim());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setDelFlag("0");
        Date now = new Date();
        user.setCreateTime(now);
        user.setUpdateTime(now);

        log.info("学生注册 - 邮箱账号: {}, 昵称: {}", emailNorm, user.getNickName());

        int result = damingUserService.insertDamingUser(user);

        if (result > 0) {
            log.info("✅ 学生注册成功 - {}", emailNorm);
            return AjaxResult.success("注册成功");
        }
        log.error("注册失败 - {}", emailNorm);
        return AjaxResult.error("注册失败");
    }
    
    /**
     * 获取当前登录用户信息
     * 使用系统TokenService，与Spring Security无缝集成
     */
    @GetMapping("/getInfo")
    public AjaxResult getInfo(HttpServletRequest request) {
        log.info("获取用户信息 - 开始");
        
        // 使用系统TokenService从request中获取LoginUser
        LoginUser loginUser = tokenService.getLoginUser(request);
        
        if (loginUser == null) {
            log.warn("Token验证失败或未登录");
            return AjaxResult.error(401, "登录已过期，请重新登录");
        }
        
        // 获取DamingUser
        DamingUser user = loginUser.getDamingUser();
        
        if (user == null) {
            log.warn("用户信息为空");
            return AjaxResult.error(401, "用户信息异常");
        }
        
        log.info("✅ 获取用户信息成功 - userId: {}, userName: {}", user.getUserId(), user.getUserName());
        
        // 返回用户信息（只返回user对象，避免冗余）
        return AjaxResult.success().put("user", user);
    }
    
    /**
     * 更新当前登录用户信息
     * 只允许更新昵称和头像
     * 
     * 前端使用步骤：
     * 1. 如果需要更新头像，先调用 POST /common/upload 上传图片到OSS
     * 2. 获取返回的图片URL
     * 3. 调用本接口，传入昵称和图片URL
     * 
     * @param request HTTP请求
     * @param updateUser 更新的用户信息（JSON格式，包含nickName和avatar）
     * @return 更新结果
     */
    @PutMapping("/updateInfo")
    public AjaxResult updateInfo(HttpServletRequest request, @RequestBody DamingUser updateUser) {
        log.info("📝 收到更新请求 - nickName: {}, avatar: {}", 
                updateUser.getNickName(), 
                updateUser.getAvatar());
        
        // 获取当前登录用户
        LoginUser loginUser = tokenService.getLoginUser(request);
        
        if (loginUser == null) {
            log.warn("Token验证失败或未登录");
            return AjaxResult.error(401, "登录已过期，请重新登录");
        }
        
        DamingUser currentUser = loginUser.getDamingUser();
        
        if (currentUser == null) {
            log.warn("用户信息为空");
            return AjaxResult.error(401, "用户信息异常");
        }
        
        // 只允许更新昵称和头像，设置userId确保更新正确的用户
        updateUser.setUserId(currentUser.getUserId());
        log.info("🔄 准备更新 - userId: {}, nickName: {}, avatar: {}", 
                updateUser.getUserId(), 
                updateUser.getNickName(), 
                updateUser.getAvatar());
        
        // 调用service更新
        int rows = damingUserService.updateDamingUser(updateUser);
        
        if (rows > 0) {
            log.info("✅ 更新用户信息成功 - userId: {}, 影响行数: {}", currentUser.getUserId(), rows);
            
            // 只更新缓存中需要更新的字段，避免重新查询导致的签名URL转换
            if (updateUser.getNickName() != null) {
                currentUser.setNickName(updateUser.getNickName());
            }
            if (updateUser.getAvatar() != null && !updateUser.getAvatar().trim().isEmpty()) {
                // ⚠️ 注意：这里存储的是原始ObjectName，不是签名URL
                currentUser.setAvatar(updateUser.getAvatar());
            }
            
            // ⭐ 关键：更新Redis缓存中的LoginUser
            loginUser.setDamingUser(currentUser);
            tokenService.refreshToken(loginUser);
            log.info("🔄 已更新Redis缓存 - avatar存储为原始路径: {}", currentUser.getAvatar());
            
            // 返回成功，让前端重新调用getInfo获取最新数据（会动态签名）
            return AjaxResult.success("更新成功");
        } else {
            log.warn("⚠️ 更新用户信息失败 - 影响行数为0");
            return AjaxResult.error("更新失败");
        }
    }
    @PostMapping("/follow/{followingId}")
    public AjaxResult toggleFollow(HttpServletRequest request, @PathVariable Long followingId, @RequestParam(defaultValue = "true") boolean follow) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (loginUser == null || loginUser.getDamingUser() == null) {
            return AjaxResult.error(401, "登录已过期，请重新登录");
        }
        Long followerId = loginUser.getDamingUser().getUserId();
        if (followerId.equals(followingId)) {
            return AjaxResult.error("不能关注自己");
        }
        boolean result = followService.setFollow(followerId, followingId, follow);
        return result ? AjaxResult.success(follow ? "关注成功" : "取消关注") : AjaxResult.error(follow ? "关注失败" : "取消关注失败");
    }
    
    @GetMapping("/follow/status/{followingId}")
    public AjaxResult isFollowing(HttpServletRequest request, @PathVariable Long followingId) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (loginUser == null || loginUser.getDamingUser() == null) {
            return AjaxResult.error(401, "登录已过期，请重新登录");
        }
        Long followerId = loginUser.getDamingUser().getUserId();
        boolean isFollowed = followService.isFollowing(followerId, followingId);
        return AjaxResult.success().put("isFollowed", isFollowed);
    }
    
    @GetMapping("/followers/{userId}/count")
    public AjaxResult countFollowers(@PathVariable Long userId) {
        int count = followService.getFollowers(userId).size();
        return AjaxResult.success().put("count", count);
    }
}
