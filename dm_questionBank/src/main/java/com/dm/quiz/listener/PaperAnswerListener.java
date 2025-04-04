package com.dm.quiz.listener;

import com.dm.quiz.domain.DamingPaperAnswer;
import com.dm.quiz.domain.DamingQuestionAnswer;
import com.dm.quiz.domain.PaperAnswerInfo;
import com.dm.quiz.event.PaperAnswerEvent;
import com.dm.quiz.mapper.DamingPaperAnswerMapper;
import com.dm.quiz.mapper.DamingQuestionAnswerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaperAnswerListener implements ApplicationListener<PaperAnswerEvent> {
    @Autowired
    private DamingQuestionAnswerMapper damingQuestionAnswerMapper;
    @Autowired
    private DamingPaperAnswerMapper damingPaperAnswerMapper;

    @Override
    // @Async
    public void onApplicationEvent(PaperAnswerEvent event) {
        PaperAnswerInfo paperAnswerInfo = (PaperAnswerInfo) event.getSource();
        DamingPaperAnswer damingPaperAnswer = paperAnswerInfo.getDamingPaperAnswer();
        List<DamingQuestionAnswer> damingQuestionAnswerList = paperAnswerInfo.getDamingQuestionAnswerList();
        // 给每个题目回答加一个试卷回答id，关联后续可以通过答卷找到所有在这张答卷的答题记录
        // 不返回记录，使用foreach，
        damingPaperAnswerMapper.insertDamingPaperAnswer(damingPaperAnswer);
        damingQuestionAnswerList.stream()
                .forEach(x->x.setPaperAnswerId(damingPaperAnswer.getPaperAnswerId()));
        damingQuestionAnswerMapper.insertDamingQuestionAnswerList(damingQuestionAnswerList);
    }
}
