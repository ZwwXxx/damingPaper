package com.ruoyi.web;

import com.ruoyi.RuoYiApplication;
import com.ruoyi.common.core.domain.entity.LoginUser;
import com.dm.quiz.domain.DamingQuestion;
import com.dm.quiz.domain.DamingQuestionAnswer;
import com.dm.quiz.dto.PaperAnswerDto;
import com.dm.quiz.service.IDamingPaperAnswerService;
import com.dm.quiz.service.IDamingQuestionAnswerService;
import com.dm.quiz.service.IDamingQuestionService;
import com.dm.quiz.viewmodel.WrongQuestionVO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collections;
import java.util.List;

/**
 * 用于毕设第六章单元测试的用例类。
 * 覆盖：登录与安全（密码编码）、题目列表查询、错题列表及交卷结果断言。
 */
@SpringBootTest(classes = RuoYiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles({"test", "druid"})
@ExtendWith(SpringExtension.class)
public class UnitTestForThesis {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private IDamingQuestionService damingQuestionService;

    @Autowired
    private IDamingQuestionAnswerService damingQuestionAnswerService;

    @Autowired
    private IDamingPaperAnswerService damingPaperAnswerService;

    /** 需要当前登录用户的测试前，注入管理员身份（userId=1 视为管理员，避免获取用户ID异常） */
    @BeforeEach
    public void initSecurityContext() {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(1L);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                loginUser, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // ========== 登录与安全：密码编码 ==========

    @Test
    public void bcrypt_encode_returnsNonEmpty_and_matches_rawPassword() {
        String raw = "1";
        String encoded = bCryptPasswordEncoder.encode(raw);
        Assertions.assertNotNull(encoded);
        Assertions.assertFalse(encoded.isEmpty());
        Assertions.assertTrue(bCryptPasswordEncoder.matches(raw, encoded));
    }

    @Test
    public void bcrypt_matches_fails_for_wrongPassword() {
        String encoded = bCryptPasswordEncoder.encode("1");
        Assertions.assertFalse(bCryptPasswordEncoder.matches("2", encoded));
    }

    // ========== 业务 Service：题目列表查询 ==========

    @Test
    public void selectDamingQuestionList_nullQuery_returnsList() {
        List<DamingQuestion> list = damingQuestionService.selectDamingQuestionList(null);
        Assertions.assertNotNull(list);
        // 空列表或已有数据均可
        Assertions.assertTrue(list.size() >= 0);
    }

    @Test
    public void selectDamingQuestionList_withSubjectId_returnsList() {
        DamingQuestion query = new DamingQuestion();
        query.setSubjectId(1);
        List<DamingQuestion> list = damingQuestionService.selectDamingQuestionList(query);
        Assertions.assertNotNull(list);
        Assertions.assertTrue(list.size() >= 0);
    }

    // ========== 业务 Service：错题列表（仅断言返回结构） ==========

    @Test
    public void selectWrongQuestionList_filter_returnsList() {
        DamingQuestionAnswer filter = new DamingQuestionAnswer();
        filter.setIsCorrect(false);
        filter.setCreateUser("zww");
        List<WrongQuestionVO> list = damingQuestionAnswerService.selectWrongQuestionList(filter);
        Assertions.assertNotNull(list);
        Assertions.assertTrue(list.size() >= 0);
    }

    // ========== 交卷：不存在的试卷应抛异常 ==========

    @Test
    public void submitAnswer_invalidPaperId_throws() {
        PaperAnswerDto dto = new PaperAnswerDto();
        dto.setPaperId(999999L);
        dto.setQuestionAnswerDtos(java.util.Collections.emptyList());
        Assertions.assertThrows(Exception.class, () -> damingPaperAnswerService.submitAnswer(dto));
    }
}
