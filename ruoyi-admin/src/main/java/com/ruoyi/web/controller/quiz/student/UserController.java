package com.ruoyi.web.controller.quiz.student;

import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
public class UserController {

    // 存储用户信息
    @PostMapping("/save")
    public String saveUser(@RequestParam String name, @RequestParam int age, HttpSession session) {
        User user = new User(name, age);
        session.setAttribute("user", user); // 存储到 session
        return "User information saved!";
    }

    // 读取用户信息
    @GetMapping("/retrieve")
    public User retrieveUser(HttpSession session) {
        User user = (User) session.getAttribute("user"); // 从 session 读取
        if (user == null) {
            return null; // 如果没有存储的用户信息
        }
        return user; // 返回存储的用户信息
    }
}
