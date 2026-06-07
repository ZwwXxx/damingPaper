package com.dm.quiz.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.dm.quiz.config.ModelMapperSingle;
import com.dm.quiz.domain.*;
import com.dm.quiz.dto.AutoAssemblePaperRequest;
import com.dm.quiz.dto.AutoAssembleRuleDto;
import com.dm.quiz.dto.PaperDto;
import com.dm.quiz.dto.PaperQuestionTypeDto;
import com.dm.quiz.dto.QuestionDto;
import com.dm.quiz.mapper.*;
import com.dm.quiz.service.IDamingQuestionService;
import com.dm.quiz.support.SnowflakeIdGenerator;
import com.dm.quiz.enums.QuestionTypeEnum;
import com.dm.quiz.viewmodel.PaperQuestionTypeVM;
import com.dm.quiz.viewmodel.PaperQuestionVM;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dm.quiz.service.IDamingPaperService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * 试卷Service业务层处理
 *
 * @author zww
 * @date 2024-10-10
 */
@Service
public class DamingPaperServiceImpl implements IDamingPaperService {
    /** 耗时分析专用 logger，application.yml 中单独设为 INFO */
    private static final Logger timingLog = LoggerFactory.getLogger("com.dm.quiz.timing.PaperRender");
    private static final long PAPER_RENDER_SLOW_MS = 1000L;

    protected final static ModelMapper modelMapper = ModelMapperSingle.Instance();
    @Autowired
    private DamingPaperMapper damingPaperMapper;
    @Autowired
    private DamingContentInfoMapper damingContentInfoMapper;
    @Autowired
    private DamingQuestionMapper damingQuestionMapper;
    @Autowired
    private IDamingQuestionService damingQuestionService;
    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

    /**
     * 查询试卷
     *
     * @param paperId 试卷主键
     * @return 试卷
     */
    @Override
    public DamingPaper selectDamingPaperByPaperId(Long paperId) {
        return damingPaperMapper.selectDamingPaperByPaperId(paperId);
    }

    /**
     * 查询试卷列表
     *
     * @param damingPaper 试卷
     * @return 试卷
     */
    @Override
    public List<DamingPaper> selectDamingPaperList(DamingPaper damingPaper) {

        return damingPaperMapper.selectDamingPaperList(damingPaper);
    }

    /**
     * 新增试卷
     *
     * @param paperDto 试卷
     * @return 结果
     */
    @Override
    @Transactional
    public DamingPaper insertDamingPaper(PaperDto paperDto) {
        // 参数空值校验
        if (StringUtils.isEmpty(paperDto.getPaperName()) || paperDto.getSubjectId() == null || paperDto.getPaperQuestionTypeDto() == null) {
            return null;
        }
        // 1.加工题型的题目只保留id和顺序,跟questionInfo的content差不多，不过这里不一样的是，装题目的题型对象也得变，内部的题目列表属性得变为只有id和order的
        // 一个变列表，一个重组列表里的题目值，可以使用modelmapper，直接转换类型
        // ，然后存该content的id，后续可以根据该id找到所有题型，
        // 且每个题型下有id和排序号，可以根据序号排列或者打乱
        // 获得题型数组，为多个，且还需深入获得题型的题目因此这里用map遍历
        List<PaperQuestionTypeDto> paperQuestionTypeDto = paperDto.getPaperQuestionTypeDto();
        DamingContentInfo damingContentInfo = new DamingContentInfo();
        String paperQuestionTypeDtoToVMString = paperQuestionTypeDtoToVMString(paperQuestionTypeDto);
        damingContentInfo.setContent(paperQuestionTypeDtoToVMString);
        damingContentInfo.setId(snowflakeIdGenerator.nextId());
        // 以下方法将该试卷题目内容打包为json，存到内容表里
        damingContentInfoMapper.insertContentInfo(damingContentInfo);

        // 2.每个题型下的题目数量size做一个sum，得到总数量，
        // 映射基本属性，科目，等乱七八糟的属性， 加工前端没有传来的值，题目数量和总分
        if (StringUtils.isEmpty(paperDto.getStartTime())) {
            paperDto.setStartTime(null);
        }
        if (StringUtils.isEmpty(paperDto.getEndTime())) {
            paperDto.setEndTime(null);
        }
        DamingPaper damingPaper = modelMapper.map(paperDto, DamingPaper.class);
        ComputeCountAndScoreResult result = getComputeCountAndScoreResult(paperDto);
        // 同时别忘了还得设置一个内容id，后续根据这个id数据库里找内容，转题型的列表list（VM类型的）
        damingPaper.setQuestionCount(result.totalCount);
        damingPaper.setScore(result.totalScore);
        damingPaper.setPaperName(paperDto.getPaperName());
        damingPaper.setSubjectId(paperDto.getSubjectId());
        damingPaper.setPaperInfoId(damingContentInfo.getId());
        damingPaper.setDelFlag(0);
        damingPaper.setPaperType(paperDto.getPaperType());
        // 防作弊开关：兼容旧版只传 enableAntiCheat 的请求；新版优先使用子功能开关组合。
        AntiCheatFlags antiCheatFlags = resolveAntiCheatFlags(
                paperDto.getEnableAntiCheat(),
                paperDto.getEnableAntiCheatCutScreen(),
                paperDto.getEnableAntiCheatSingleQuestion(),
                paperDto.getEnableAntiCheatDisableActions(),
                paperDto.getEnableAntiCheatDevToolsDetection(),
                paperDto.getEnableAntiCheatBrowserEnvironmentDetection(),
                paperDto.getEnableAntiCheatShuffle()
        );
        damingPaper.setEnableAntiCheat(antiCheatFlags.enableAntiCheat);
        damingPaper.setEnableAntiCheatCutScreen(antiCheatFlags.enableAntiCheatCutScreen);
        damingPaper.setEnableAntiCheatSingleQuestion(antiCheatFlags.enableAntiCheatSingleQuestion);
        damingPaper.setEnableAntiCheatDisableActions(antiCheatFlags.enableAntiCheatDisableActions);
        damingPaper.setEnableAntiCheatDevToolsDetection(antiCheatFlags.enableAntiCheatDevToolsDetection);
        damingPaper.setEnableAntiCheatBrowserEnvironmentDetection(antiCheatFlags.enableAntiCheatBrowserEnvironmentDetection);
        damingPaper.setEnableAntiCheatShuffle(antiCheatFlags.enableAntiCheatShuffle);
        // 题号规则：默认按加入顺序编号
        damingPaper.setNumberMode(paperDto.getNumberMode() == null ? 2 : paperDto.getNumberMode());
        if (StringUtils.isNotEmpty(paperDto.getStartTime())) {
            damingPaper.setStartTime(DateUtils.parseDate(paperDto.getStartTime()));
        }
        if (StringUtils.isNotEmpty(paperDto.getEndTime())) {
            damingPaper.setEndTime(DateUtils.parseDate(paperDto.getEndTime()));
        }
        damingPaper.setPaperId(snowflakeIdGenerator.nextId());
        damingPaperMapper.insertDamingPaper(damingPaper);
        return damingPaper;
    }

    private static ComputeCountAndScoreResult getComputeCountAndScoreResult(PaperDto paperDto) {
        // 计算所有题目数量,流式操作，将多个题型map循环，映射每一个item为integer类型的size，得到每个题型的数量，存入数组
        // 将数组里的每一个题型的题目数量进行sum求和 ,
        int totalCount = paperDto.getPaperQuestionTypeDto().stream()
                .flatMap(i -> i.getQuestionDtos().stream())
                // 完形父题不计入题目数量（子题才是真正作答题）
                .filter(q -> q != null && !Objects.equals(q.getQuestionType(), QuestionTypeEnum.Cloze.getCode()))
                .mapToInt(q -> 1)
                .sum();
        // 计算所有题目的总分
        // 由于分数流属于第二层，会有很多个intstream，因此第一层我们要提前把多个intstream压缩为一个，然后进行sum处理
        int totalScore = paperDto.getPaperQuestionTypeDto().stream()
                .flatMap(i -> i.getQuestionDtos().stream())
                // 完形父题不计入总分（子题分数之和才是有效总分）
                .filter(q -> q != null && !Objects.equals(q.getQuestionType(), QuestionTypeEnum.Cloze.getCode()))
                .mapToInt(q -> q.getScore() == null ? 0 : q.getScore())
                .sum();
        ComputeCountAndScoreResult result = new ComputeCountAndScoreResult(totalCount, totalScore);
        return result;
    }

    private static class ComputeCountAndScoreResult {
        public final int totalCount;
        public final int totalScore;

        public ComputeCountAndScoreResult(int totalCount, int totalScore) {
            this.totalCount = totalCount;
            this.totalScore = totalScore;
        }
    }


    private static String paperQuestionTypeDtoToVMString(List<PaperQuestionTypeDto> paperQuestionTypeDto) {
        AtomicInteger index = new AtomicInteger(0);
        // 这一步将前端的题型类转为后端的题型类，也就是其里面的题目类改造为order和id的
        // 方法2，使用modelMapper映射对象数据不使用set 和new直接比对相同属性名，拷贝属性和值
        List<PaperQuestionTypeVM> paperQuestionTypeVMList = paperQuestionTypeDto.stream().map(i -> {
            // 这一步将内部列表映射到另一个list，只映射属性名一致的值和属性，同步了name，省去new,setName
            PaperQuestionTypeVM paperQuestionTypeVM = modelMapper.map(i, PaperQuestionTypeVM.class);
            // PaperQuestionTypeVM paperQuestionTypeVM = new PaperQuestionTypeVM();
            // BeanUtils.copyProperties(i,paperQuestionTypeVM);
            List<PaperQuestionVM> questionVMS = i.getQuestionDtos().stream().map(q -> {
                // 将题目列表转为别的类，仅包含order和id，这里id属性一致，映射，order自己set
                PaperQuestionVM questionVM = modelMapper.map(q, PaperQuestionVM.class);
                // PaperQuestionVM questionVM = new PaperQuestionVM();
                // BeanUtils.copyProperties(q,questionVM);
                // 完形父题不占题号（itemOrder置空且不递增）
                if (q != null && Objects.equals(q.getQuestionType(), QuestionTypeEnum.Cloze.getCode()) && q.getParentId() == null) {
                    questionVM.setItemOrder(null);
                } else {
                    questionVM.setItemOrder(index.getAndIncrement());
                }
                return questionVM;
            }).collect(Collectors.toList());
            // 将遍历的出来新的的题目列表赋值给题型.name不用给了，映射好了
            paperQuestionTypeVM.setPaperQuestionVMS(questionVMS);
            return paperQuestionTypeVM;
        }).collect(Collectors.toList());
        return JSON.toJSONString(paperQuestionTypeVMList);
    }

    /**
     * 修改试卷
     *
     * @param paperDto 试卷
     * @return 结果
     */
    @Override
    public int updateDamingPaper(PaperDto paperDto) {
        // 主要是修改info内容，然后根据新的info计算出新的count和score，其他model映射即可
        // 1.根据paperDto的paperId查询paper
        DamingPaper damingPaper = damingPaperMapper.selectDamingPaperByPaperId(paperDto.getPaperId());
        // 2.映射paperDto给查询到的paper
        if (StringUtils.isEmpty(paperDto.getStartTime())) {
            paperDto.setStartTime(null);
        }
        if (StringUtils.isEmpty(paperDto.getEndTime())) {
            paperDto.setEndTime(null);
        }
        modelMapper.map(paperDto, damingPaper);
        // 防作弊开关：兼容旧版只传 enableAntiCheat 的请求；新版优先使用子功能开关组合。
        AntiCheatFlags antiCheatFlags = resolveAntiCheatFlags(
                paperDto.getEnableAntiCheat(),
                paperDto.getEnableAntiCheatCutScreen(),
                paperDto.getEnableAntiCheatSingleQuestion(),
                paperDto.getEnableAntiCheatDisableActions(),
                paperDto.getEnableAntiCheatDevToolsDetection(),
                paperDto.getEnableAntiCheatBrowserEnvironmentDetection(),
                paperDto.getEnableAntiCheatShuffle()
        );
        damingPaper.setEnableAntiCheat(antiCheatFlags.enableAntiCheat);
        damingPaper.setEnableAntiCheatCutScreen(antiCheatFlags.enableAntiCheatCutScreen);
        damingPaper.setEnableAntiCheatSingleQuestion(antiCheatFlags.enableAntiCheatSingleQuestion);
        damingPaper.setEnableAntiCheatDisableActions(antiCheatFlags.enableAntiCheatDisableActions);
        damingPaper.setEnableAntiCheatDevToolsDetection(antiCheatFlags.enableAntiCheatDevToolsDetection);
        damingPaper.setEnableAntiCheatBrowserEnvironmentDetection(antiCheatFlags.enableAntiCheatBrowserEnvironmentDetection);
        damingPaper.setEnableAntiCheatShuffle(antiCheatFlags.enableAntiCheatShuffle);
        if (StringUtils.isNotEmpty(paperDto.getStartTime())) {
            damingPaper.setStartTime(DateUtils.parseDate(paperDto.getStartTime()));
        }
        if (StringUtils.isNotEmpty(paperDto.getEndTime())) {
            damingPaper.setEndTime(DateUtils.parseDate(paperDto.getEndTime()));
        }
        // 3.根据paperInfoId查出来，然后根据最新的paperDto里的题型，设置（先转为vm）给contentInfo然后update
        DamingContentInfo damingContentInfo = damingContentInfoMapper.selectDamingContentInfoById(damingPaper.getPaperInfoId());
        List<PaperQuestionTypeDto> paperQuestionTypeDto = paperDto.getPaperQuestionTypeDto();
        String paperQuestionTypeDtoToVMString = paperQuestionTypeDtoToVMString(paperQuestionTypeDto);
        damingContentInfo.setContent(paperQuestionTypeDtoToVMString);
        damingContentInfoMapper.updateDamingQuestionInfo(damingContentInfo);
        // 4.将该题型遍历重新计算score和count题目数量，然后设置给paper
        ComputeCountAndScoreResult computeCountAndScoreResult = getComputeCountAndScoreResult(paperDto);
        damingPaper.setQuestionCount(computeCountAndScoreResult.totalCount);
        damingPaper.setScore(computeCountAndScoreResult.totalScore);
        damingPaper.setNumberMode(paperDto.getNumberMode() == null ? damingPaper.getNumberMode() : paperDto.getNumberMode());
        // 5.更新paper
        return damingPaperMapper.updateDamingPaper(damingPaper);

    }

    /**
     * 批量删除试卷
     *
     * @param paperIds 需要删除的试卷主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteDamingPaperByPaperIds(Long[] paperIds) {
        return damingPaperMapper.deleteDamingPaperByPaperIds(paperIds);
    }

    /**
     * 删除试卷信息
     *
     * @param paperId 试卷主键
     * @return 结果
     */
    @Override
    public int deleteDamingPaperByPaperId(Long paperId) {
        return damingPaperMapper.deleteDamingPaperByPaperId(paperId);
    }

    /**
     * 试卷VM转为DTO给前端  ,总体就是将VM数据的题目的itemOrder
     *
     * @param paperId
     */
    @Override
    public PaperDto paperIdtoDto(Long paperId) {
        long totalStart = System.currentTimeMillis();
        long stageStart = totalStart;

        // 1.先把试卷从数据库里找出来，变为dto试卷
        DamingPaper damingPaper = damingPaperMapper.selectDamingPaperByPaperId(paperId);
        long queryPaperMs = System.currentTimeMillis() - stageStart;
        stageStart = System.currentTimeMillis();
        if (damingPaper == null) {
            throw new ServiceException("试卷不存在或已删除");
        }
        // 2.此时需要根据试卷内容id找到其框架内容
        if (damingPaper.getPaperInfoId() == null) {
            throw new ServiceException("试卷内容不存在，请联系管理员");
        }
        DamingContentInfo damingContentInfo = damingContentInfoMapper.selectDamingContentInfoById(damingPaper.getPaperInfoId());
        long queryContentMs = System.currentTimeMillis() - stageStart;
        stageStart = System.currentTimeMillis();
        if (damingContentInfo == null || StringUtils.isEmpty(damingContentInfo.getContent())) {
            throw new ServiceException("试卷内容为空，请联系管理员");
        }
        String content = damingContentInfo.getContent();
        // 3.然后根据该内容转为list，然后由该list<VM> 转为list<Dto>给前端 这个dto包含了题目排序，
        List<PaperQuestionTypeVM> questionTypeVMS = JSONArray.parseArray(content, PaperQuestionTypeVM.class);
        long parseJsonMs = System.currentTimeMillis() - stageStart;
        stageStart = System.currentTimeMillis();
        // 4.拿到每个题型里的所有question的questionIDS，数据库查question集合,由于是二层结构，外层使用flatMap摊平
        List<Long> questionIds = questionTypeVMS.stream()
                .flatMap(i -> i.getPaperQuestionVMS().stream().map(PaperQuestionVM::getId))
                .collect(Collectors.toList());
        // 用于兜底：避免“试卷内容只存了完形父题ID，漏了子题ID”导致学生端看不到子题
        Set<Long> includedIds = new HashSet<>(questionIds);
        // 4.1根据id获取到每一个question
        List<DamingQuestion> damingQuestions = damingQuestionMapper.selectQuestionListByIds(questionIds);
        long batchQueryMs = System.currentTimeMillis() - stageStart;
        stageStart = System.currentTimeMillis();
        // 5.根据question，变为questionDTO然后设置order返回，map原题型数组，映射为DTO类型
        // 统一重新计算 itemOrder：完形父题不占号，其他题目（包括完形子题）全局连续编号
        AtomicInteger orderCounter = new AtomicInteger(0);
        AtomicLong getQuestionDtoCost = new AtomicLong(0L);
        AtomicLong clozeQueryCost = new AtomicLong(0L);

        List<PaperQuestionTypeDto> paperQuestionTypeDtos = questionTypeVMS.stream().map(i -> {
            PaperQuestionTypeDto paperQuestionTypeDto = modelMapper.map(i, PaperQuestionTypeDto.class);
            List<QuestionDto> questionDtoStream = new ArrayList<>();
            List<PaperQuestionVM> paperQuestions = i.getPaperQuestionVMS();
            if (paperQuestions == null) {
                paperQuestions = new ArrayList<>();
            }
            for (PaperQuestionVM x : paperQuestions) {
                if (x == null || x.getId() == null) {
                    continue;
                }
                DamingQuestion question = damingQuestions.stream()
                        .filter(p -> p != null && p.getId() != null && p.getId().equals(x.getId()))
                        .findFirst()
                        .orElse(null);
                if (question == null) {
                    // 题目已被删除或不存在，跳过
                    continue;
                }
                long dtoStart = System.currentTimeMillis();
                QuestionDto questionDto = damingQuestionService.getQuestionDto(question);
                getQuestionDtoCost.addAndGet(System.currentTimeMillis() - dtoStart);
                if (questionDto == null) {
                    continue;
                }

                boolean isClozeParent = questionDto.getQuestionType() != null
                        && questionDto.getQuestionType().equals(QuestionTypeEnum.Cloze.getCode())
                        && questionDto.getParentId() == null;

                if (isClozeParent) {
                    // 完形父题：不参与题号
                    questionDto.setItemOrder(null);
                    questionDtoStream.add(questionDto);

                    // 兜底补齐子题：若试卷内容里没存子题ID，则按 parentId 查询子题并追加
                    DamingQuestion query = new DamingQuestion();
                    query.setParentId(questionDto.getId());
                    long clozeStart = System.currentTimeMillis();
                    List<DamingQuestion> children = damingQuestionMapper.selectDamingQuestionList(query);
                    clozeQueryCost.addAndGet(System.currentTimeMillis() - clozeStart);
                    if (!CollectionUtils.isEmpty(children)) {
                        List<QuestionDto> childDtos = children.stream()
                                .filter(Objects::nonNull)
                                .map(child -> {
                                    long childDtoStart = System.currentTimeMillis();
                                    QuestionDto childDto = damingQuestionService.getQuestionDto(child);
                                    getQuestionDtoCost.addAndGet(System.currentTimeMillis() - childDtoStart);
                                    return childDto;
                                })
                                .filter(Objects::nonNull)
                                // 按 clozeIndex 排序，保证“第几空”顺序稳定
                                .sorted((a, b) -> {
                                    Integer ai = a.getClozeIndex() == null ? 0 : a.getClozeIndex();
                                    Integer bi = b.getClozeIndex() == null ? 0 : b.getClozeIndex();
                                    return ai.compareTo(bi);
                                })
                                .collect(Collectors.toList());

                        for (QuestionDto childDto : childDtos) {
                            if (childDto.getId() != null && includedIds.contains(childDto.getId())) {
                                // 已经在试卷内容里显式包含过的子题，避免重复追加
                                continue;
                            }
                            childDto.setItemOrder(orderCounter.getAndIncrement());
                            questionDtoStream.add(childDto);
                        }
                    }
                } else {
                    // 普通题 / 完形子题：全局自增题号
                    questionDto.setItemOrder(orderCounter.getAndIncrement());
                    questionDtoStream.add(questionDto);
                }
            }
            // 5.3 将加工后的questionDto List设置给题型TypeDto（与组卷页 applyDisplayOrder 一致：父题后紧跟子题）
            normalizeParentChildQuestionOrder(questionDtoStream);
            paperQuestionTypeDto.setQuestionDtos(questionDtoStream);
            return paperQuestionTypeDto;
        }).collect(Collectors.toList());
        long buildDtoMs = System.currentTimeMillis() - stageStart;
        stageStart = System.currentTimeMillis();
        PaperDto paperDto = modelMapper.map(damingPaper, PaperDto.class);
        paperDto.setPaperQuestionTypeDto(paperQuestionTypeDtos);
        paperDto.setEnableAntiCheat(damingPaper.getEnableAntiCheat());
        paperDto.setEnableAntiCheatCutScreen(Boolean.TRUE.equals(damingPaper.getEnableAntiCheatCutScreen()));
        paperDto.setEnableAntiCheatSingleQuestion(Boolean.TRUE.equals(damingPaper.getEnableAntiCheatSingleQuestion()));
        paperDto.setEnableAntiCheatDisableActions(Boolean.TRUE.equals(damingPaper.getEnableAntiCheatDisableActions()));
        paperDto.setEnableAntiCheatDevToolsDetection(Boolean.TRUE.equals(damingPaper.getEnableAntiCheatDevToolsDetection()));
        paperDto.setEnableAntiCheatBrowserEnvironmentDetection(Boolean.TRUE.equals(damingPaper.getEnableAntiCheatBrowserEnvironmentDetection()));
        paperDto.setEnableAntiCheatShuffle(Boolean.TRUE.equals(damingPaper.getEnableAntiCheatShuffle()));
        paperDto.setQuestionCount(damingPaper.getQuestionCount());
        paperDto.setNumberMode(damingPaper.getNumberMode());
        // 手动处理Date到String的转换
        if (damingPaper.getStartTime() != null) {
            paperDto.setStartTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, damingPaper.getStartTime()));
        }
        if (damingPaper.getEndTime() != null) {
            paperDto.setEndTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, damingPaper.getEndTime()));
        }
        long assembleMs = System.currentTimeMillis() - stageStart;
        long totalMs = System.currentTimeMillis() - totalStart;
        int outputQuestionCount = paperQuestionTypeDtos.stream()
                .mapToInt(type -> type.getQuestionDtos() == null ? 0 : type.getQuestionDtos().size())
                .sum();
        String timingMsg = "[试卷渲染耗时] paperId={} | 总={}ms | 查试卷={}ms | 查内容={}ms | 解析JSON={}ms | 批量查题={}ms | 构建题目DTO={}ms(含getQuestionDto={}ms, 完形子题查询={}ms) | 组装结果={}ms | 题目数={}/{}";
        Object[] timingArgs = new Object[]{
                paperId, totalMs, queryPaperMs, queryContentMs, parseJsonMs, batchQueryMs,
                buildDtoMs, getQuestionDtoCost.get(), clozeQueryCost.get(), assembleMs,
                outputQuestionCount, questionIds.size()
        };
        if (totalMs > PAPER_RENDER_SLOW_MS) {
            timingLog.warn(timingMsg, timingArgs);
        } else {
            timingLog.info(timingMsg, timingArgs);
        }
        return paperDto;
        // 搞到底就是为了给题目设置一个顺序，不容易啊，如果序号一开始就设置在DTO上不转存到VM上然后单独加Order就不用引起列表不一致了。(不行，这样VM会有很多空属性，不必要的属性在前后端流通)
        // 噢噢那样的话就写死了好像，这样存到数据库即使没有入卷也会有一个顺序，只有在入卷的时候才会对题型里的题目做一个序号处理，使其后续能递增或是乱序,
        // 不过即使写在questionDto上前端不指定后端自增一个顺序存到info里好像也可以啊
    }

    @Override
    public PaperDto autoAssemblePaper(AutoAssemblePaperRequest request) {
        if (request == null || request.getSubjectId() == null) {
            throw new ServiceException("请先选择科目");
        }
        if (CollectionUtils.isEmpty(request.getRules())) {
            throw new ServiceException("请至少配置一条组卷规则");
        }
        AtomicInteger orderCounter = new AtomicInteger(0);
        Set<Long> usedQuestionIds = new HashSet<>();
        List<PaperQuestionTypeDto> paperQuestionTypeDtos = new ArrayList<>();
        for (AutoAssembleRuleDto rule : request.getRules()) {
            if (rule.getQuestionType() == null) {
                throw new ServiceException("题型不能为空");
            }
            if (rule.getQuestionCount() == null || rule.getQuestionCount() <= 0) {
                throw new ServiceException("题目数量必须大于0");
            }
            List<DamingQuestion> candidates = damingQuestionMapper.selectRandomQuestions(
                    request.getSubjectId(),
                    rule.getQuestionType(),
                    rule.getQuestionCount()
            );
            if (CollectionUtils.isEmpty(candidates)) {
                throw new ServiceException("题库中没有匹配的题目可供自动组卷");
            }
            List<QuestionDto> questionDtos = new ArrayList<>();
            for (DamingQuestion candidate : candidates) {
                if (usedQuestionIds.contains(candidate.getId())) {
                    continue;
                }
                questionDtos.add(toQuestionDto(candidate, orderCounter));
                usedQuestionIds.add(candidate.getId());
                if (questionDtos.size() >= rule.getQuestionCount()) {
                    break;
                }
            }
            if (questionDtos.size() < rule.getQuestionCount()) {
                throw new ServiceException("题型【" + resolveSectionName(rule) + "】剩余可用题目数量不足");
            }
            PaperQuestionTypeDto typeDto = new PaperQuestionTypeDto();
            typeDto.setName(resolveSectionName(rule));
            typeDto.setQuestionDtos(questionDtos);
            paperQuestionTypeDtos.add(typeDto);
        }
        PaperDto preview = new PaperDto();
        preview.setPaperQuestionTypeDto(paperQuestionTypeDtos);
        preview.setSubjectId(request.getSubjectId());
        preview.setPaperName(request.getPaperName());
        preview.setPaperType(request.getPaperType());
        preview.setSuggestTime(request.getSuggestTime());
        AntiCheatFlags antiCheatFlags = resolveAntiCheatFlags(
                request.getEnableAntiCheat(),
                request.getEnableAntiCheatCutScreen(),
                request.getEnableAntiCheatSingleQuestion(),
                request.getEnableAntiCheatDisableActions(),
                request.getEnableAntiCheatDevToolsDetection(),
                request.getEnableAntiCheatBrowserEnvironmentDetection(),
                request.getEnableAntiCheatShuffle()
        );
        preview.setEnableAntiCheat(antiCheatFlags.enableAntiCheat);
        preview.setEnableAntiCheatCutScreen(antiCheatFlags.enableAntiCheatCutScreen);
        preview.setEnableAntiCheatSingleQuestion(antiCheatFlags.enableAntiCheatSingleQuestion);
        preview.setEnableAntiCheatDisableActions(antiCheatFlags.enableAntiCheatDisableActions);
        preview.setEnableAntiCheatDevToolsDetection(antiCheatFlags.enableAntiCheatDevToolsDetection);
        preview.setEnableAntiCheatBrowserEnvironmentDetection(antiCheatFlags.enableAntiCheatBrowserEnvironmentDetection);
        preview.setEnableAntiCheatShuffle(antiCheatFlags.enableAntiCheatShuffle);
        if (!CollectionUtils.isEmpty(paperQuestionTypeDtos)) {
            ComputeCountAndScoreResult stats = getComputeCountAndScoreResult(preview);
            preview.setScore(stats.totalScore);
            preview.setQuestionCount(stats.totalCount);
        } else {
            preview.setScore(0);
            preview.setQuestionCount(0);
        }
        return preview;
    }

    /**
     * 按日期范围自动组卷：
     * 将指定科目、指定日期范围内“所有题目”，按题型（单选、多选、判断等）自动分组，生成试卷预览。
     * 不直接落库，前端拿到结果后可继续调整题型/题目，再点击提交生成正式试卷。
     */
    @Override
    public PaperDto autoAssemblePaperByDate(AutoAssemblePaperRequest request) {
        if (request == null || request.getSubjectId() == null) {
            throw new ServiceException("请先选择科目");
        }
        if (StringUtils.isEmpty(request.getBeginTime()) || StringUtils.isEmpty(request.getEndTime())) {
            throw new ServiceException("请选择题目创建日期范围");
        }
        List<DamingQuestion> candidates = damingQuestionMapper.selectQuestionsBySubjectAndCreateTime(
                request.getSubjectId(),
                request.getBeginTime(),
                request.getEndTime()
        );
        if (CollectionUtils.isEmpty(candidates)) {
            throw new ServiceException("选定日期范围内没有题目");
        }
        AtomicInteger orderCounter = new AtomicInteger(0);
        List<PaperQuestionTypeDto> paperQuestionTypeDtos = new ArrayList<>();
        // 按题型顺序（单选、多选、主观、判断、填空）分组
        for (QuestionTypeEnum typeEnum : QuestionTypeEnum.values()) {
            List<DamingQuestion> typeQuestions = candidates.stream()
                    .filter(q -> q.getQuestionType() != null && q.getQuestionType().equals(typeEnum.getCode()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(typeQuestions)) {
                continue;
            }
            List<QuestionDto> questionDtos = new ArrayList<>();
            for (DamingQuestion question : typeQuestions) {
                questionDtos.add(toQuestionDto(question, orderCounter));
            }
            PaperQuestionTypeDto typeDto = new PaperQuestionTypeDto();
            typeDto.setName(typeEnum.getName());
            typeDto.setQuestionDtos(questionDtos);
            paperQuestionTypeDtos.add(typeDto);
        }
        if (CollectionUtils.isEmpty(paperQuestionTypeDtos)) {
            throw new ServiceException("选定日期范围内没有可用题目");
        }
        PaperDto preview = new PaperDto();
        preview.setPaperQuestionTypeDto(paperQuestionTypeDtos);
        preview.setSubjectId(request.getSubjectId());
        preview.setPaperName(request.getPaperName());
        preview.setPaperType(request.getPaperType());
        preview.setSuggestTime(request.getSuggestTime());
        AntiCheatFlags antiCheatFlags = resolveAntiCheatFlags(
                request.getEnableAntiCheat(),
                request.getEnableAntiCheatCutScreen(),
                request.getEnableAntiCheatSingleQuestion(),
                request.getEnableAntiCheatDisableActions(),
                request.getEnableAntiCheatDevToolsDetection(),
                request.getEnableAntiCheatBrowserEnvironmentDetection(),
                request.getEnableAntiCheatShuffle()
        );
        preview.setEnableAntiCheat(antiCheatFlags.enableAntiCheat);
        preview.setEnableAntiCheatCutScreen(antiCheatFlags.enableAntiCheatCutScreen);
        preview.setEnableAntiCheatSingleQuestion(antiCheatFlags.enableAntiCheatSingleQuestion);
        preview.setEnableAntiCheatDisableActions(antiCheatFlags.enableAntiCheatDisableActions);
        preview.setEnableAntiCheatDevToolsDetection(antiCheatFlags.enableAntiCheatDevToolsDetection);
        preview.setEnableAntiCheatBrowserEnvironmentDetection(antiCheatFlags.enableAntiCheatBrowserEnvironmentDetection);
        preview.setEnableAntiCheatShuffle(antiCheatFlags.enableAntiCheatShuffle);
        ComputeCountAndScoreResult stats = getComputeCountAndScoreResult(preview);
        preview.setScore(stats.totalScore);
        preview.setQuestionCount(stats.totalCount);
        return preview;
    }

    private static boolean isTrue(Boolean value) {
        return Boolean.TRUE.equals(value);
    }

    /**
     * 防作弊子功能开关组合规则：
     * - 如果新版请求提供了任意一个子开关（不为 null），则以子开关为准；
     * - 如果旧版请求只有 enableAntiCheat（子开关都为 null），则把 enableAntiCheat 视为“全部子开关开”。
     */
    private static AntiCheatFlags resolveAntiCheatFlags(
            Boolean enableAntiCheat,
            Boolean enableAntiCheatCutScreen,
            Boolean enableAntiCheatSingleQuestion,
            Boolean enableAntiCheatDisableActions,
            Boolean enableAntiCheatDevToolsDetection,
            Boolean enableAntiCheatBrowserEnvironmentDetection,
            Boolean enableAntiCheatShuffle
    ) {
        boolean master = isTrue(enableAntiCheat);
        boolean subAnySpecified =
                enableAntiCheatCutScreen != null ||
                        enableAntiCheatSingleQuestion != null ||
                        enableAntiCheatDisableActions != null ||
                        enableAntiCheatDevToolsDetection != null ||
                        enableAntiCheatBrowserEnvironmentDetection != null ||
                        enableAntiCheatShuffle != null;

        boolean effectiveCutScreen = subAnySpecified ? isTrue(enableAntiCheatCutScreen) : master;
        boolean effectiveSingleQuestion = subAnySpecified ? isTrue(enableAntiCheatSingleQuestion) : master;
        boolean effectiveDisableActions = subAnySpecified ? isTrue(enableAntiCheatDisableActions) : master;
        boolean effectiveDevTools = subAnySpecified ? isTrue(enableAntiCheatDevToolsDetection) : master;
        boolean effectiveBrowserEnv = subAnySpecified ? isTrue(enableAntiCheatBrowserEnvironmentDetection) : master;
        boolean effectiveShuffle = subAnySpecified ? isTrue(enableAntiCheatShuffle) : master;

        boolean enableAntiCheatFinal = effectiveCutScreen
                || effectiveSingleQuestion
                || effectiveDisableActions
                || effectiveDevTools
                || effectiveBrowserEnv
                || effectiveShuffle;

        return new AntiCheatFlags(
                enableAntiCheatFinal,
                effectiveCutScreen,
                effectiveSingleQuestion,
                effectiveDisableActions,
                effectiveDevTools,
                effectiveBrowserEnv,
                effectiveShuffle
        );
    }

    private static class AntiCheatFlags {
        public final boolean enableAntiCheat;
        public final boolean enableAntiCheatCutScreen;
        public final boolean enableAntiCheatSingleQuestion;
        public final boolean enableAntiCheatDisableActions;
        public final boolean enableAntiCheatDevToolsDetection;
        public final boolean enableAntiCheatBrowserEnvironmentDetection;
        public final boolean enableAntiCheatShuffle;

        private AntiCheatFlags(
                boolean enableAntiCheat,
                boolean enableAntiCheatCutScreen,
                boolean enableAntiCheatSingleQuestion,
                boolean enableAntiCheatDisableActions,
                boolean enableAntiCheatDevToolsDetection,
                boolean enableAntiCheatBrowserEnvironmentDetection,
                boolean enableAntiCheatShuffle
        ) {
            this.enableAntiCheat = enableAntiCheat;
            this.enableAntiCheatCutScreen = enableAntiCheatCutScreen;
            this.enableAntiCheatSingleQuestion = enableAntiCheatSingleQuestion;
            this.enableAntiCheatDisableActions = enableAntiCheatDisableActions;
            this.enableAntiCheatDevToolsDetection = enableAntiCheatDevToolsDetection;
            this.enableAntiCheatBrowserEnvironmentDetection = enableAntiCheatBrowserEnvironmentDetection;
            this.enableAntiCheatShuffle = enableAntiCheatShuffle;
        }
    }

    private QuestionDto toQuestionDto(DamingQuestion question, AtomicInteger orderCounter) {
        QuestionDto questionDto = damingQuestionService.getQuestionDto(question);
        questionDto.setItemOrder(orderCounter.getAndIncrement());
        return questionDto;
    }

    private String resolveSectionName(AutoAssembleRuleDto rule) {
        if (StringUtils.isNotEmpty(rule.getSectionName())) {
            return rule.getSectionName();
        }
        for (QuestionTypeEnum value : QuestionTypeEnum.values()) {
            if (rule.getQuestionType() != null && rule.getQuestionType().equals(value.getCode())) {
                return value.getName();
            }
        }
        return "题型" + rule.getQuestionType();
    }

    /**
     * 将题型下的题目调整为：顶层题按其在列表中首次出现顺序，每道顶层题后紧跟其所有子题（按 clozeIndex）。
     * 与 ruoyi-ui 试卷编辑 {@code applyDisplayOrder} 一致，避免完形子题排在父题前导致学生端/题量统计异常。
     *
     * <p><b>数据流转示例</b>（下列 id 仅便于阅读，线上为 Snowflake 长整型；obj 表示 {@link QuestionDto} 引用）：
     * <pre>
     * 设定：父题 id=100（完形 parentId=null）；子题 id=102（parentId=100, clozeIndex=1）；
     *      子题 id=101（parentId=100, clozeIndex=2）；另有一道普通顶层题 id=50。
     *
     * 【入参 flat】顺序错乱，例如：[102, 100, 101] —— 子题夹在前后，且未保证「父在前」。
     *
     * ① 构建 byId 后：{ 100→obj100, 101→obj101, 102→obj102 } —— 仅 id 快速查找，不改变顺序。
     *
     * ② 扫描子题入桶 parentToChildren：仅 102、101 有 parentId=100 → { 100 → [102, 101] }
     *    （桶内顺序 = flat 中子题出现先后：先 102 后 101）
     *
     * ③ 每桶按 clozeIndex 升序：102(index=1) 在 101(index=2) 前 → { 100 → [102, 101] }（若入桶已是 1、2 顺序，排序后不变）
     *
     * ④ 提取顶层顺序 topsOrdered：按 flat 从前到后只看「非子题」且去重 → 仅 100 → [100]
     *    若 flat=[50, 102, 100, 101]（普通题在完形子题前）→ topsOrdered=[50, 100]。
     *
     * ⑤ 组装 out：依次取 topId；先 out.add(父题)，再 out.add 该父桶内子题列表
     *    上例 [102,100,101] → out=[100, 102, 101]；placed={100,102,101}
     *
     * ⑥ 兜底：若某题既非已处理父/子（如异常孤立题），按原 flat 顺序追加到 out，并记入 placed。
     *
     * ⑦ 最后 flat.clear(); flat.addAll(out) —— 调用方持有的列表引用不变，内容被替换为稳定顺序。
     * </pre>
     */
    private void normalizeParentChildQuestionOrder(List<QuestionDto> flat) {
        if (CollectionUtils.isEmpty(flat)) { // 入参列表是否为空
            return; // 例：flat=[] → 直接返回，以下结构均无
        }
        Map<Long, QuestionDto> byId = new HashMap<>(); // 题目 id -> 题目 DTO，便于 O(1) 按 id 取
        for (QuestionDto q : flat) { // 遍历原列表，构建 id 映射
            if (q != null && q.getId() != null) { // 跳过空对象或缺 id 的项
                byId.put(q.getId(), q); // 例：见 JavaDoc ①，放入 100/101/102 对应对象引用
            }
        }
        Map<Long, List<QuestionDto>> parentToChildren = new HashMap<>(); // 父题 id -> 其子题列表（多桶）
        for (QuestionDto q : flat) { // 再次遍历，收集每道子题
            if (q == null || q.getId() == null) { // 无效项跳过
                continue; // 进入下一轮
            }
            if (isChildQuestion(q)) { // 判断是否为带父题 id 的子题
                Long pid = normalizeParentId(q.getParentId()); // 例：q=102 时 pid=100；parentId=0 时 pid=null 不入桶
                if (pid != null) { // 确有合法父题 id
                    parentToChildren.computeIfAbsent(pid, k -> new ArrayList<>()).add(q); // 例：见 JavaDoc ②，100→[102,101]
                }
            }
        }
        for (List<QuestionDto> ch : parentToChildren.values()) { // 遍历每个父题下的子题列表
            ch.sort(Comparator.comparing(c -> c.getClozeIndex() == null ? 0 : c.getClozeIndex())); // 例：见 JavaDoc ③，按第1空、第2空…排序
        }
        List<Long> topsOrdered = new ArrayList<>(); // 顶层题 id 的先后顺序（与 flat 首次出现顺序一致）
        Set<Long> seenTop = new HashSet<>(); // 去重：同一顶层 id 只记录一次
        for (QuestionDto q : flat) { // 遍历原列表，提取顶层题 id 序列
            if (q == null || q.getId() == null) { // 无效项跳过
                continue; // 进入下一轮
            }
            if (!isChildQuestion(q) && seenTop.add(q.getId())) { // 非子题且该 id 首次出现
                topsOrdered.add(q.getId()); // 例：见 JavaDoc ④，[102,100,101]→topsOrdered=[100]；含 50 时→[50,100]
            }
        }
        List<QuestionDto> out = new ArrayList<>(); // 重排后的结果列表
        Set<Long> placed = new HashSet<>(); // 已写入 out 的题目 id，防重复
        for (Long topId : topsOrdered) { // 按顶层题出现顺序依次处理
            QuestionDto top = byId.get(topId); // 例：topId=100 → obj100
            if (top == null) { // 映射中缺失则跳过（数据异常）
                continue; // 进入下一轮
            }
            out.add(top); // 例：先追加父题 100
            placed.add(top.getId()); // placed 记入 100
            List<QuestionDto> ch = parentToChildren.get(topId); // 例：topId=100 → [102,101] 或 null（无子题）
            if (ch != null) { // 若有子题
                for (QuestionDto c : ch) { // 遍历已按 clozeIndex 排好序的子题
                    out.add(c); // 例：见 JavaDoc ⑤，依次追加 102、101
                    placed.add(c.getId()); // placed 追加 102、101
                }
            }
        }
        for (QuestionDto q : flat) { // 兜底：处理仍未放入的题
            if (q == null || q.getId() == null || placed.contains(q.getId())) { // 空、缺 id 或已放置则跳过
                continue; // 例：正常完形父子已齐 → 本循环不追加；见 JavaDoc ⑥
            }
            out.add(q); // 例：孤立题按 flat 顺序补入 out
            placed.add(q.getId()); // 标记为已放置
        }
        flat.clear(); // 例：原 [102,100,101] 被清空
        flat.addAll(out); // 例：见 JavaDoc ⑦，flat 变为 [100,102,101]（与 out 同序）
    }

    private boolean isChildQuestion(QuestionDto questionDto) {
        if (questionDto == null) {
            return false;
        }
        // 注意：历史数据里 parentId=0 也表示顶层题，不应视为子题
        Long parentId = questionDto.getParentId();
        return parentId != null && parentId > 0;
    }

    private Long normalizeParentId(Long parentId) {
        // 统一将 null/0/负数视为“无父题”，避免比较和映射时出现误差
        if (parentId == null || parentId <= 0) {
            return null;
        }
        return parentId;
    }
}
