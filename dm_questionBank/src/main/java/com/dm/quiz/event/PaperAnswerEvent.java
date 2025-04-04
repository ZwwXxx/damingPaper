package com.dm.quiz.event;

import org.springframework.context.ApplicationEvent;

public class PaperAnswerEvent extends ApplicationEvent {
    public PaperAnswerEvent(Object source) {
        super(source);
    }
}
