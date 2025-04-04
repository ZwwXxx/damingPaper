package com.dm.quiz.domain.ai;

import lombok.Data;

@Data
public class ResponseMessage {
    // 本轮对话id
    String id;

    // 回包类型。 chat.completion:多轮对话返回
    String object;

    // 时间戳
    int created;

    // 表示当前子句的序号。只有在流式接口模式下才会返回该字段
    int sentence_id;

    // 表示当前子句是否是最后一句。只有在流式接口模式下会返回该字段。
    boolean is_end;

    // 对话返回结果。
    String result;

    /**
     * 表示用户输入是否存在安全，是否关闭当前会话，清理历史回话信息。
     * true：是，表示用户输入存在安全风险，建议关闭当前会话，清理历史会话信息。
     * false：否，表示用户输入无安全风险。
     */
    boolean need_clear_history;

    // token统计信息，token数 = 汉字数+单词数*1.3 （仅为估算逻辑）。
    Usage usage;
}
