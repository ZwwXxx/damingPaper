package com.dm.quiz.dto;

public class PaperReviewDto {
    private PaperAnswerDto paperAnswerDto;
    private PaperDto paperDto;

    public PaperAnswerDto getPaperAnswerDto() {
        return paperAnswerDto;
    }

    public void setPaperAnswerDto(PaperAnswerDto paperAnswerDto) {
        this.paperAnswerDto = paperAnswerDto;
    }

    public PaperDto getPaperDto() {
        return paperDto;
    }

    public void setPaperDto(PaperDto paperDto) {
        this.paperDto = paperDto;
    }
}
