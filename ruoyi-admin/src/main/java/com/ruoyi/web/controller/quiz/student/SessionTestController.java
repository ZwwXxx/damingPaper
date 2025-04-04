package com.ruoyi.web.controller.quiz.student;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class SessionTestController {

    @GetMapping("/setSession")
    public String setSession(HttpSession session) {
        session.setAttribute("test", "Hello, World!");
        return "Session attribute set.";
    }

    @GetMapping(value = "/getSession", produces = "text/event-stream")
    public SseEmitter getSession(HttpSession session) throws IOException {
        SseEmitter emitter = new SseEmitter();
        Object test = session.getAttribute("test");
        if (test!=null){
            System.out.println("查询到了session");
        }
        emitter.send("1");
        emitter.complete();
        return emitter;
    }
}
