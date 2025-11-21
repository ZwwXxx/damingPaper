package com.dm.quiz.service.impl;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.alibaba.fastjson2.JSONArray;
import com.dm.quiz.domain.*;
import com.dm.quiz.dto.PaperAnswerDto;
import com.dm.quiz.dto.PaperDto;
import com.dm.quiz.dto.PaperReviewDto;
import com.dm.quiz.dto.QuestionAnswerDto;
import com.dm.quiz.dto.PaperReviewSubmitRequest;
import com.dm.quiz.dto.ReviewQuestionScoreDto;
import com.dm.quiz.dto.SubmitAnswerResult;
import com.dm.quiz.event.PaperAnswerEvent;
import com.dm.quiz.mapper.*;
import com.dm.quiz.service.IDamingPaperService;
import com.dm.quiz.viewmodel.PaperQuestionTypeVM;
import com.dm.quiz.domain.DamingPaper;
import com.dm.quiz.enums.QuestionTypeEnum;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import com.dm.quiz.service.IDamingPaperAnswerService;

/**
 * 试卷作答情况Service业务层处理
 *
 * @author zww
 * @date 2024-10-13
 */
@Service
public class DamingPaperAnswerServiceImpl implements IDamingPaperAnswerService {
    private static final int REVIEW_STATUS_PENDING = 1;
    private static final int REVIEW_STATUS_DONE = 2;
    @Autowired
    private DamingPaperAnswerMapper damingPaperAnswerMapper;
    @Autowired
    private IDamingPaperService damingPaperService;
    @Autowired
    private DamingContentInfoMapper damingContentInfoMapper;
    @Autowired
    private DamingQuestionMapper damingQuestionMapper;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private DamingQuestionAnswerMapper damingQuestionAnswerMapper;
    
    /**
     * 查询试卷作答情况
     *
     * @param paperAnswerId 试卷作答情况主键
     * @return 试卷作答情况
     */
    @Override
    public DamingPaperAnswer selectDamingPaperAnswerByPaperAnswerId(Long paperAnswerId) {
        return damingPaperAnswerMapper.selectDamingPaperAnswerByPaperAnswerId(paperAnswerId);
    }

    /**
     * 查询试卷作答情况列表
     *
     * @param damingPaperAnswer 试卷作答情况
     * @return 试卷作答情况
     */
    @Override
    public List<DamingPaperAnswer> selectDamingPaperAnswerList(DamingPaperAnswer damingPaperAnswer) {
        return damingPaperAnswerMapper.selectDamingPaperAnswerList(damingPaperAnswer);
    }

    /**
     * 新增试卷作答情况
     *
     * @param damingPaperAnswer 试卷作答情况
     * @return 结果
     */
    @Override
    public int insertDamingPaperAnswer(DamingPaperAnswer damingPaperAnswer) {
        damingPaperAnswer.setCreateTime(DateUtils.getNowDate());
        return damingPaperAnswerMapper.insertDamingPaperAnswer(damingPaperAnswer);
    }

    /**
     * 修改试卷作答情况
     *
     * @param damingPaperAnswer 试卷作答情况
     * @return 结果
     */
    @Override
    public int updateDamingPaperAnswer(DamingPaperAnswer damingPaperAnswer) {
        return damingPaperAnswerMapper.updateDamingPaperAnswer(damingPaperAnswer);
    }

    /**
     * 批量删除试卷作答情况
     *
     * @param paperAnswerIds 需要删除的试卷作答情况主键
     * @return 结果
     */
    @Override
    public int deleteDamingPaperAnswerByPaperAnswerIds(Long[] paperAnswerIds) {
        return damingPaperAnswerMapper.deleteDamingPaperAnswerByPaperAnswerIds(paperAnswerIds);
    }

    /**
     * 删除试卷作答情况信息
     *
     * @param paperAnswerId 试卷作答情况主键
     * @return 结果
     */
    @Override
    public int deleteDamingPaperAnswerByPaperAnswerId(Long paperAnswerId) {
        return damingPaperAnswerMapper.deleteDamingPaperAnswerByPaperAnswerId(paperAnswerId);
    }

    @Override
    public SubmitAnswerResult submitAnswer(PaperAnswerDto paperAnswerDto) {
        // 一，比对用户提交的答案和原卷答案，设置做题记录的得分和是否正确,生成题目回答记录表
        // 1.获取原卷,拿到原卷里的所有原题，
        DamingPaper damingPaper = damingPaperService.selectDamingPaperByPaperId(paperAnswerDto.getPaperId());
        String content = damingContentInfoMapper.selectDamingContentInfoById(damingPaper.getPaperInfoId()).getContent();
        List<PaperQuestionTypeVM> questionTypeVMS = JSONArray.parseArray(content, PaperQuestionTypeVM.class);
        List<Long> questionIds = questionTypeVMS.stream().flatMap(paperQuestionTypeVM -> paperQuestionTypeVM
                .getPaperQuestionVMS().stream()
                .map(paperQuestionVM -> paperQuestionVM.getId())).collect(Collectors.toList());
        List<DamingQuestion> damingQuestions = damingQuestionMapper.selectQuestionListByIds(questionIds);

        // 将题目结构的转化为题目答案，外层遍历题型，类似于变量的作用域，提升order顺序作用域 _______外层不加{}方法体隐式返回对象，类似js的res=>()
        AtomicInteger objectiveScoreCounter = new AtomicInteger(0);
        AtomicInteger objectiveCorrectCounter = new AtomicInteger(0);
        AtomicBoolean hasSubjective = new AtomicBoolean(false);
        List<DamingQuestionAnswer> questionAnswerList = questionTypeVMS.stream().flatMap(questionTypeVM ->
                // 遍历试卷题目，与用户填写试卷的 题目列表与答案进行id，比较，然后单独对比答案
                questionTypeVM.getPaperQuestionVMS().stream().map(paperQuestionVM -> {
                    // 过滤对应的题目实体
                    // 过滤对应的用户回答实体
                    DamingQuestion matchQuestion = damingQuestions.stream()
                            .filter(damingQuestion -> damingQuestion.getId().equals(paperQuestionVM.getId())).findFirst().get();
                    // 2.遍历题目回答列表DTO，用户的回答比对答案，计算分数和做对情况和记录 题目记录是属于哪张试卷的，属于哪个科目的,最后生成题目记录表
                    // 将计算后的值设置到题目记录类里
                    QuestionAnswerDto questionAnswerDto = paperAnswerDto.getQuestionAnswerDtos().stream()
                            .filter(x -> x.getQuestionId().equals(matchQuestion.getId())).findFirst().get();
                    // 新建题目记录实体
                    DamingQuestionAnswer damingQuestionAnswer = new DamingQuestionAnswer();
                    damingQuestionAnswer.setItemOrder(paperQuestionVM.getItemOrder());
                    boolean isSubjective = Objects.equals(matchQuestion.getQuestionType(), QuestionTypeEnum.Subjective.getCode());
                    // 如果是多选题，遍历用户答案数组与数据库里的correct转数组进行比对
                    String userAnswer = questionAnswerDto.getContent();
                    boolean isCorrect = matchQuestion.getCorrect().equals(userAnswer);
                    if (matchQuestion.getQuestionType() == QuestionTypeEnum.Multiple.getCode()) {
                        userAnswer = String.join(",", questionAnswerDto.getContentArray());
                        String[] matchCorrect = matchQuestion.getCorrect().split(",");
                        String[] questionAnswerDtoContentArray = questionAnswerDto.getContentArray();
                        // 检查长度
                        if (matchCorrect.length != questionAnswerDtoContentArray.length) {
                            isCorrect = false;
                        } else {
                            // 数组转为set集合，去重加无视顺序
                            Set<String> set1 = new HashSet<>(Arrays.asList(matchCorrect));
                            Set<String> set2 = new HashSet<>(Arrays.asList(questionAnswerDtoContentArray));
                            isCorrect = set1.equals(set2);
                        }
                    }
                    if (userAnswer == null || "".equals(userAnswer.trim())) {
                        userAnswer = "未填";
                    }
                    damingQuestionAnswer.setUserAnswer(userAnswer);
                    if (isSubjective) {
                        hasSubjective.set(true);
                        damingQuestionAnswer.setIsCorrect(Boolean.FALSE);
                        damingQuestionAnswer.setFinalScore(0);
                        damingQuestionAnswer.setReviewStatus(REVIEW_STATUS_PENDING);
                    } else {
                        damingQuestionAnswer.setIsCorrect(isCorrect);
                        damingQuestionAnswer.setFinalScore(damingQuestionAnswer.getIsCorrect() ? matchQuestion.getScore() : 0);
                        damingQuestionAnswer.setReviewStatus(REVIEW_STATUS_DONE);
                        objectiveScoreCounter.addAndGet(damingQuestionAnswer.getFinalScore());
                        if (Boolean.TRUE.equals(damingQuestionAnswer.getIsCorrect())) {
                            objectiveCorrectCounter.incrementAndGet();
                        }
                    }
                    damingQuestionAnswer.setQuestionScore(matchQuestion.getScore());
                    damingQuestionAnswer.setQuestionType(matchQuestion.getQuestionType());
                    damingQuestionAnswer.setQuestionId(matchQuestion.getId());
                    damingQuestionAnswer.setPaperId(paperAnswerDto.getPaperId());
                    damingQuestionAnswer.setSubjectId(matchQuestion.getSubjectId());
                    damingQuestionAnswer.setCreateUser(SecurityUtils.getUsername());
                    return damingQuestionAnswer;
                })
        ).collect(Collectors.toList());
        // 二，将题目回答表的分数，做对数量设置给试卷记录表
        DamingPaperAnswer damingPaperAnswer = new DamingPaperAnswer();
        damingPaperAnswer.setCreateUser(SecurityUtils.getUsername());
        damingPaperAnswer.setPaperId(paperAnswerDto.getPaperId());
        damingPaperAnswer.setPaperName(damingPaper.getPaperName());
        int objectiveScore = objectiveScoreCounter.get();
        damingPaperAnswer.setFinalScore(objectiveScore);
        damingPaperAnswer.setObjectiveScore(objectiveScore);
        damingPaperAnswer.setSubjectiveScore(0);
        damingPaperAnswer.setReviewStatus(hasSubjective.get() ? REVIEW_STATUS_PENDING : REVIEW_STATUS_DONE);
        damingPaperAnswer.setCorrectCount(objectiveCorrectCounter.get());
        damingPaperAnswer.setQuestionCount(damingPaper.getQuestionCount());
        damingPaperAnswer.setPaperScore(damingPaper.getScore());
        damingPaperAnswer.setSubjectId(damingPaper.getSubjectId());
        damingPaperAnswer.setDoTime(paperAnswerDto.getDoTime());

        // 这里还得给题目回答设置回答试卷的id，方便后续找答卷->题目回答,然后将以上两条表记录插入数据库
        // 同时需要记录日志，xx用户完成了xx试卷，耗时xx秒
        // 为了不阻塞进程，我们使用事件发布，异步处理,这里就先将试卷得分进行返回，
        // 三，发布事件根据分数和用户，试卷名生成日志和 插入上述两个表的内容
        PaperAnswerInfo paperAnswerInfo = new PaperAnswerInfo();
        paperAnswerInfo.setDamingPaperAnswer(damingPaperAnswer);
        paperAnswerInfo.setDamingQuestionAnswerList(questionAnswerList);
        applicationContext.publishEvent(new PaperAnswerEvent(paperAnswerInfo));
        SubmitAnswerResult result = new SubmitAnswerResult();
        result.setFinalScore(damingPaperAnswer.getFinalScore());
        result.setObjectiveScore(damingPaperAnswer.getObjectiveScore());
        result.setReviewStatus(damingPaperAnswer.getReviewStatus());
        return result;
    }


    /**
     * 查看某次考试记录,思路为，原始试卷dto（包含原题那些）+
     * 答卷dto，（包含id，耗时，分数，题目回答记录，且也要实体转dto，获得/题目原始分数题序，题序用来给哪个题型遍历选项和答案，是否做对。答题内容）
     */
    @Override
    public PaperReviewDto getPaperReviewDto(Long paperAnswerId) {
        // 准备好两个实体，后续分别转为DTO,塞入
        PaperReviewDto paperReviewDto = new PaperReviewDto();
        PaperAnswerDto paperAnswerDto = new PaperAnswerDto();
        // 1.查出来数据库的答卷，然后转为DTO，该dto下的题目回答记录也得是dto,符合前端格式
        DamingPaperAnswer damingPaperAnswer = damingPaperAnswerMapper.selectDamingPaperAnswerByPaperAnswerId(paperAnswerId);
        paperAnswerDto.setPaperId(damingPaperAnswer.getPaperId());
        paperAnswerDto.setPaperAnswerId(damingPaperAnswer.getPaperAnswerId());
        paperAnswerDto.setFinalScore(damingPaperAnswer.getFinalScore());
        paperAnswerDto.setObjectiveScore(damingPaperAnswer.getObjectiveScore());
        paperAnswerDto.setSubjectiveScore(damingPaperAnswer.getSubjectiveScore());
        paperAnswerDto.setReviewStatus(damingPaperAnswer.getReviewStatus());
        paperAnswerDto.setReviewRemark(damingPaperAnswer.getReviewRemark());
        paperAnswerDto.setCreateUser(damingPaperAnswer.getCreateUser());
        // 根据答卷id找到所有在这张试卷上回答的答题记录
        List<DamingQuestionAnswer> damingQuestionAnswers = damingQuestionAnswerMapper.selectByPaperAnswerId(damingPaperAnswer.getPaperAnswerId());
        // 将数据库的答题记录列表转为dto
        List<QuestionAnswerDto> questionAnswerDtos = damingQuestionAnswers.stream().map(x -> {
            QuestionAnswerDto questionAnswerDto = new QuestionAnswerDto();
            if (x.getQuestionType() == 2) {
                String[] split = x.getUserAnswer().split(",");
                Arrays.sort(split);
                questionAnswerDto.setContentArray(split);
            } else {
                questionAnswerDto.setContent(x.getUserAnswer());
            }
            questionAnswerDto.setQuestionId(x.getQuestionId());
            questionAnswerDto.setItemOrder(x.getItemOrder());
            questionAnswerDto.setCorrect(x.getIsCorrect());
            questionAnswerDto.setAnswerId(x.getAnswerId());
            questionAnswerDto.setFinalScore(x.getFinalScore());
            questionAnswerDto.setQuestionScore(x.getQuestionScore());
            questionAnswerDto.setReviewStatus(x.getReviewStatus());
            questionAnswerDto.setReviewComment(x.getReviewComment());
            return questionAnswerDto;
        }).collect(Collectors.toList());
        paperAnswerDto.setQuestionAnswerDtos(questionAnswerDtos);
        paperAnswerDto.setDoTime(damingPaperAnswer.getDoTime());
        // 2.根据答题记录的试卷id查出来试卷然后转为试卷dto
        PaperDto paperDto = damingPaperService.paperIdtoDto(damingPaperAnswer.getPaperId());
        paperReviewDto.setPaperAnswerDto(paperAnswerDto);
        paperReviewDto.setPaperDto(paperDto);
        return paperReviewDto;

    }

    @Override
    public void reviewPaper(PaperReviewSubmitRequest request) {
        if (request == null || request.getPaperAnswerId() == null) {
            throw new RuntimeException("参数缺失");
        }
        DamingPaperAnswer paperAnswer = damingPaperAnswerMapper.selectDamingPaperAnswerByPaperAnswerId(request.getPaperAnswerId());
        if (paperAnswer == null) {
            throw new RuntimeException("试卷不存在");
        }
        if (!Objects.equals(paperAnswer.getReviewStatus(), REVIEW_STATUS_PENDING)) {
            throw new RuntimeException("该试卷无需批改");
        }
        List<DamingQuestionAnswer> answers = damingQuestionAnswerMapper.selectByPaperAnswerId(paperAnswer.getPaperAnswerId());
        Map<Long, DamingQuestionAnswer> subjectiveMap = answers.stream()
                .filter(ans -> Objects.equals(ans.getQuestionType(), QuestionTypeEnum.Subjective.getCode()))
                .collect(Collectors.toMap(DamingQuestionAnswer::getAnswerId, ans -> ans));
        if (subjectiveMap.isEmpty()) {
            throw new RuntimeException("试卷不包含主观题");
        }
        int subjectiveScore = 0;
        for (ReviewQuestionScoreDto item : request.getQuestionScores()) {
            DamingQuestionAnswer target = subjectiveMap.get(item.getAnswerId());
            if (target == null) {
                throw new RuntimeException("题目不存在或无需批改");
            }
            int score = item.getScore() == null ? 0 : item.getScore();
            if (score < 0 || (target.getQuestionScore() != null && score > target.getQuestionScore())) {
                throw new RuntimeException("题目评分超出范围");
            }
            target.setFinalScore(score);
            target.setReviewStatus(REVIEW_STATUS_DONE);
            target.setReviewComment(item.getComment());
            target.setIsCorrect(null);
            subjectiveScore += score;
            damingQuestionAnswerMapper.updateDamingQuestionAnswer(target);
        }
        paperAnswer.setSubjectiveScore(subjectiveScore);
        int objectiveScore = paperAnswer.getObjectiveScore() == null ? 0 : paperAnswer.getObjectiveScore();
        paperAnswer.setFinalScore(objectiveScore + subjectiveScore);
        paperAnswer.setReviewStatus(REVIEW_STATUS_DONE);
        paperAnswer.setReviewUser(SecurityUtils.getUsername());
        paperAnswer.setReviewTime(new Date());
        paperAnswer.setReviewRemark(request.getReviewRemark());
        damingPaperAnswerMapper.updateDamingPaperAnswer(paperAnswer);
    }
}
