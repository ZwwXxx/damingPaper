package com.dm.quiz.enums;

public enum QuestionTypeEnum {
    Single(1,"单选题"),
    Multiple(2,"多选题"),
    Subjective(3,"主观题");

    QuestionTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }
    int code;
    String name;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
