package com.dm.quiz.domain.ai;

import lombok.Data;

@Data
public class Message {
    /**
     * 用户角色
     * 目前支持：
     * user 用户
     * assistant 对话助手
     */
    String role;

    /**
     * 对话内容。
     */
    String content;

    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }

    @Override
    public String toString() {
        String status=role.equals("user")?"你：":"千帆AI：";
        return status+content;
    }


}
