package com.dm.quiz.domain.ai;

import lombok.Data;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@Data
public class RequestMessage {

    public void clear() {
        // 在这里清除 RequestMessage 对象内部的状态
        messages.clear();
        // 释放其他相关资源
    }

    /**
     * 聊天上下文
     */
    List<Message> messages = new ArrayList<>();

    public void pringMessages() {
        for (Message message : messages) {
            System.out.print(message);
        }
    }

    /**
     * 范围(0~1.0]
     * 较高的数值会使输出更加随机
     */
    float temperature = Float.parseFloat("0.95");

    /**
     * 影响文本的多样性，取值越大生成的文本多样性越强
     * 建议该参数与temperature只设置一个。建议top_p和temperature不要同时更改
     */
    float top_p = Float.parseFloat("0.8");

    /**
     * 通过对已生成的token增加惩罚，减少重复生成的现象
     * 值越大，惩罚越大
     * 取值范围[1,2]
     */
    float penalty_score = Float.parseFloat("1.0");

    /**
     * 是否以流式接口形式返回数据
     */
    boolean stream = true;

    /**
     * 模型人设
     */
    String system = null;

    /**
     * 表示用户唯一标识符，用于监测和检测滥用行为。防止接口恶意调用。
     */
    String user_id = "";

    public void addMessage(Message message) {
        this.messages.add(message);
    }
}
