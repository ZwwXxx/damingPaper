package com.ruoyi.web.controller.quiz.student;

import com.dm.quiz.service.DamingUserService;
import com.dm.quiz.service.IDamingUserService;
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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    
    /**
     * 前台学生登录
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody) {
        log.info("学生登录 - 用户名: {}", loginBody.getUsername());
        
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword());
        
        AjaxResult ajax = AjaxResult.success("登录成功");
        ajax.put(Constants.TOKEN, token);
        log.info("✅ 学生登录成功 - 用户名: {}", loginBody.getUsername());
        return ajax;
    }
    
    /**
     * 前台学生注册
     */
    @PostMapping("/registry")
    public AjaxResult registry(@RequestBody DamingUser user) {
        log.info("学生注册 - 用户名: {}, 昵称: {}", user.getUserName(), user.getNickName());
        log.info("收到的密码: {}", user.getPassword());
        
        // 检查用户名是否已存在
        DamingUser query = new DamingUser();
        query.setUserName(user.getUserName());
        List<DamingUser> existUsers = damingUserService.selectDamingUserList(query);
        if (existUsers != null && !existUsers.isEmpty()) {
            log.warn("用户名已存在: {}", user.getUserName());
            return AjaxResult.error("用户名已存在");
        }
        
        // 验证密码不为空
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            log.error("密码为空");
            return AjaxResult.error("密码不能为空");
        }
        
        // 加密密码
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setDelFlag("0");
        
        log.info("加密后的密码: {}", encodedPassword);
        
        // 插入用户
        int result = damingUserService.insertDamingUser(user);
        
        if (result > 0) {
            log.info("✅ 学生注册成功 - 用户名: {}", user.getUserName());
            return AjaxResult.success("注册成功");
        } else {
            log.error("注册失败 - 用户名: {}", user.getUserName());
            return AjaxResult.error("注册失败");
        }
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
}
