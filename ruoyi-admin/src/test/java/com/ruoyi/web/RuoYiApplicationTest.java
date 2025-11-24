package com.ruoyi.web;

import com.dm.quiz.service.impl.DamingQuestionServiceImpl;
import com.ruoyi.RuoYiApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

// package com.ruoyi.web;
//
//
// import com.dm.quiz.domain.DamingQuestion;
// import com.dm.quiz.mapper.DamingQuestionMapper;
// import com.dm.quiz.service.impl.DamingQuestionServiceImpl;
// import com.ruoyi.RuoYiApplication;
// import com.ruoyi.common.core.domain.AjaxResult;
// import com.ruoyi.framework.web.service.SysPermissionService;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.test.context.junit4.SpringRunner;
//
// import java.util.List;
//
// // import org.springframework.boot.test.context.SpringBootTest;
@SpringBootTest(classes = RuoYiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class RuoYiApplicationTest {
    // @Autowired
    // DamingQuestionServiceImpl damingQuestionService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 生成bc密码
     */
    @Test
   public void bcPwd(){
        String encode = bCryptPasswordEncoder.encode("1");
        System.out.println(encode);
        System.out.println("1");
    }}

//
//     @Test
//     public void testQuestionDto(){
//         List<DamingQuestion> damingQuestion = damingQuestionService.selectDamingQuestionList(null);
//         System.out.println(damingQuestion);
//     }
//
//     @Autowired
//     private DamingQuestionMapper damingQuestionMapper;
//
//     @Test
//     public void list() {
//         List<DamingQuestion> damingQuestions = damingQuestionService.selectDamingQuestionList(null);
//     }
// }
