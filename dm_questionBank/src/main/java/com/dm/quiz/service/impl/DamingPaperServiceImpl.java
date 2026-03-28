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
import java.util.stream.Collectors;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.dm.quiz.config.ModelMapperSingle;
import com.dm.quiz.domain.*;
import com.dm.quiz.dto.AutoAssemblePaperRequest;
import com.dm.quiz.dto.AutoAssembleRuleDto;
import com.dm.quiz.dto.PaperDto;
import com.dm.quiz.dto.PaperSyncImportResultDto;
import com.dm.quiz.dto.PaperSyncPackageDto;
import com.dm.quiz.dto.PaperQuestionTypeDto;
import com.dm.quiz.dto.QuestionDto;
import com.dm.quiz.mapper.*;
import com.dm.quiz.service.IDamingQuestionService;
import com.dm.quiz.enums.QuestionTypeEnum;
import com.dm.quiz.viewmodel.PaperQuestionTypeVM;
import com.dm.quiz.viewmodel.PaperQuestionVM;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
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
    protected final static ModelMapper modelMapper = ModelMapperSingle.Instance();
    @Autowired
    private DamingPaperMapper damingPaperMapper;
    @Autowired
    private DamingContentInfoMapper damingContentInfoMapper;
    @Autowired
    private DamingQuestionMapper damingQuestionMapper;
    @Autowired
    private IDamingQuestionService damingQuestionService;

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
        damingPaper.setEnableAntiCheat(Boolean.TRUE.equals(paperDto.getEnableAntiCheat()));
        // 题号规则：默认按加入顺序编号
        damingPaper.setNumberMode(paperDto.getNumberMode() == null ? 2 : paperDto.getNumberMode());
        if (StringUtils.isNotEmpty(paperDto.getStartTime())) {
            damingPaper.setStartTime(DateUtils.parseDate(paperDto.getStartTime()));
        }
        if (StringUtils.isNotEmpty(paperDto.getEndTime())) {
            damingPaper.setEndTime(DateUtils.parseDate(paperDto.getEndTime()));
        }
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
        damingPaper.setEnableAntiCheat(Boolean.TRUE.equals(paperDto.getEnableAntiCheat()));
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
        // 1.先把试卷从数据库里找出来，变为dto试卷
        DamingPaper damingPaper = damingPaperMapper.selectDamingPaperByPaperId(paperId);
        // 2.此时需要根据试卷内容id找到其框架内容
        DamingContentInfo damingContentInfo = damingContentInfoMapper.selectDamingContentInfoById(damingPaper.getPaperInfoId());
        String content = damingContentInfo.getContent();
        // 3.然后根据该内容转为list，然后由该list<VM> 转为list<Dto>给前端 这个dto包含了题目排序，
        List<PaperQuestionTypeVM> questionTypeVMS = JSONArray.parseArray(content, PaperQuestionTypeVM.class);
        // 4.拿到每个题型里的所有question的questionIDS，数据库查question集合,由于是二层结构，外层使用flatMap摊平
        List<Long> questionIds = questionTypeVMS.stream()
                .flatMap(i -> i.getPaperQuestionVMS().stream().map(PaperQuestionVM::getId))
                .collect(Collectors.toList());
        // 用于兜底：避免“试卷内容只存了完形父题ID，漏了子题ID”导致学生端看不到子题
        Set<Long> includedIds = new HashSet<>(questionIds);
        // 4.1根据id获取到每一个question
        List<DamingQuestion> damingQuestions = damingQuestionMapper.selectQuestionListByIds(questionIds);
        // 5.根据question，变为questionDTO然后设置order返回，map原题型数组，映射为DTO类型
        // 统一重新计算 itemOrder：完形父题不占号，其他题目（包括完形子题）全局连续编号
        AtomicInteger orderCounter = new AtomicInteger(0);

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
                QuestionDto questionDto = damingQuestionService.getQuestionDto(question);
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
                    List<DamingQuestion> children = damingQuestionMapper.selectDamingQuestionList(query);
                    if (!CollectionUtils.isEmpty(children)) {
                        List<QuestionDto> childDtos = children.stream()
                                .filter(Objects::nonNull)
                                .map(damingQuestionService::getQuestionDto)
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
        PaperDto paperDto = modelMapper.map(damingPaper, PaperDto.class);
        paperDto.setPaperQuestionTypeDto(paperQuestionTypeDtos);
        paperDto.setEnableAntiCheat(damingPaper.getEnableAntiCheat());
        paperDto.setQuestionCount(damingPaper.getQuestionCount());
        paperDto.setNumberMode(damingPaper.getNumberMode());
        // 手动处理Date到String的转换
        if (damingPaper.getStartTime() != null) {
            paperDto.setStartTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, damingPaper.getStartTime()));
        }
        if (damingPaper.getEndTime() != null) {
            paperDto.setEndTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, damingPaper.getEndTime()));
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
        preview.setEnableAntiCheat(Boolean.TRUE.equals(request.getEnableAntiCheat()));
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
        preview.setEnableAntiCheat(Boolean.TRUE.equals(request.getEnableAntiCheat()));
        ComputeCountAndScoreResult stats = getComputeCountAndScoreResult(preview);
        preview.setScore(stats.totalScore);
        preview.setQuestionCount(stats.totalCount);
        return preview;
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

    @Override
    public PaperSyncPackageDto exportPaperSyncPackage(Long paperId) {
        if (paperId == null) { // 参数校验：没有试卷ID无法导出
            throw new ServiceException("试卷ID不能为空"); // 抛出业务异常给前端
        } // 结束“paperId为空”分支
        PaperDto paperDto = paperIdtoDto(paperId); // 查询并组装试卷DTO（包含题型+题目）
        if (paperDto == null) { // 兜底：试卷不存在时阻断流程
            throw new ServiceException("试卷不存在"); // 返回明确错误信息
        } // 结束“试卷不存在”分支
        // 导出兜底：若存在“子题在包中但父题不在包中”，自动补齐父题，避免导入时父映射缺失
        enrichMissingParentQuestionsForExport(paperDto);
        PaperSyncPackageDto syncPackageDto = new PaperSyncPackageDto(); // 创建同步包对象
        syncPackageDto.setVersion("v1"); // 写入协议版本，便于未来兼容
        syncPackageDto.setSourcePaperId(paperId); // 记录源环境中的试卷ID
        syncPackageDto.setPaper(paperDto); // 写入完整试卷数据
        return syncPackageDto; // 返回给控制层输出JSON
    }

    @Override
    @Transactional
    public PaperSyncImportResultDto importPaperSyncPackage(PaperSyncPackageDto syncPackageDto) {
        if (syncPackageDto == null || syncPackageDto.getPaper() == null) { // 校验导入包和试卷主体
            throw new ServiceException("导入数据不能为空"); // 导入包不合法直接失败
        } // 结束空数据校验
        PaperDto sourcePaper = syncPackageDto.getPaper(); // 取出源试卷对象
        if (sourcePaper.getSubjectId() == null) { // 校验科目
            throw new ServiceException("试卷科目不能为空"); // 科目是题目去重和建卷关键字段
        } // 结束科目校验
        if (CollectionUtils.isEmpty(sourcePaper.getPaperQuestionTypeDto())) { // 校验题型列表
            throw new ServiceException("试卷题目不能为空"); // 没题目不允许建卷
        } // 结束题型校验

        PaperDto importPaper = modelMapper.map(sourcePaper, PaperDto.class); // 深拷贝源试卷，避免污染入参
        importPaper.setPaperId(null); // 清空ID，确保在目标环境新建试卷

        int insertedCount = 0; // 统计：新增题目数量
        int skippedCount = 0; // 统计：兼容口径（未新增到题库）
        int filteredCount = 0; // 统计：因空题干等规则被过滤的数量
        int parentRemapMissCount = 0; // 统计：子题父ID无法重映射的数量
        int reusedCount = 0; // 统计：命中去重复用的数量（会进试卷）
        int parentMissReusedCount = 0; // 统计：父映射缺失但忽略父ID后复用成功
        int parentMissDroppedCount = 0; // 统计：父映射缺失且无法复用，最终未进试卷
        Map<Long, Long> sourceToTargetQuestionId = new HashMap<>(); // 源题ID -> 目标题ID映射（用于父子题）
        List<PaperQuestionTypeDto> mappedTypeList = new ArrayList<>(); // 最终用于建卷的题型列表
        List<TypeImportContext> contexts = new ArrayList<>(); // 全部题型上下文（用于全局两阶段）

        for (PaperQuestionTypeDto typeDto : sourcePaper.getPaperQuestionTypeDto()) { // 遍历每个题型
            PaperQuestionTypeDto mappedType = modelMapper.map(typeDto, PaperQuestionTypeDto.class); // 复制题型基本信息
            List<QuestionDto> sourceQuestions = typeDto.getQuestionDtos(); // 源题型的题目列表
            if (CollectionUtils.isEmpty(sourceQuestions)) { // 当前题型没有题目
                mappedType.setQuestionDtos(new ArrayList<>()); // 设置空题目列表
                mappedTypeList.add(mappedType); // 保留题型结构
                TypeImportContext context = new TypeImportContext();
                context.mappedType = mappedType;
                context.processingOrder = new ArrayList<>();
                contexts.add(context);
                continue; // 进入下一个题型
            } // 结束空题型分支
            // 保持包内题目顺序（与导出/组卷一致）；勿按 itemOrder 排序——完形父题 itemOrder 为空会被排到子题之后，导致子题“丢失”或题序错乱
            List<QuestionDto> processingOrder = sourceQuestions.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            TypeImportContext context = new TypeImportContext();
            context.mappedType = mappedType;
            context.processingOrder = processingOrder;
            contexts.add(context);
            mappedTypeList.add(mappedType); // 加入最终建卷题型列表
        } // 全部题型处理完成

        // 第一阶段（全局）：先导入普通题/父题（只要不是子题都先处理）
        for (TypeImportContext context : contexts) {
            for (QuestionDto sourceQuestion : context.processingOrder) {
                if (isChildQuestion(sourceQuestion)) {
                    continue;
                }
                ImportQuestionOutcome outcome = importOneQuestion(sourceQuestion, sourcePaper.getSubjectId(), null, sourceToTargetQuestionId);
                insertedCount += outcome.insertedInc;
                skippedCount += outcome.skippedInc;
                filteredCount += outcome.filteredInc;
                reusedCount += outcome.reusedInc;
                if (outcome.questionForPaper != null) {
                    context.registerImported(sourceQuestion, outcome.questionForPaper);
                }
            }
        }

        // 第二阶段（全局）：再导入子题（支持父题在其他题型先被处理）
        for (TypeImportContext context : contexts) {
            for (QuestionDto sourceQuestion : context.processingOrder) {
                if (!isChildQuestion(sourceQuestion)) {
                    continue;
                }
                Long mappedParentId = sourceToTargetQuestionId.get(normalizeParentId(sourceQuestion.getParentId()));
                if (mappedParentId == null) {
                    parentRemapMissCount++; // 记录父映射缺失
                    // 父映射缺失时，不允许新增题目；仅尝试“忽略parentId”的复用匹配
                    Long existIdIgnoreParent = findExistingQuestionIdIgnoreParent(sourceQuestion, sourcePaper.getSubjectId());
                    if (existIdIgnoreParent != null) {
                        skippedCount++;
                        reusedCount++;
                        parentMissReusedCount++;
                        QuestionDto reusedQuestion = cloneQuestionForImport(sourceQuestion, sourcePaper.getSubjectId());
                        reusedQuestion.setParentId(null);
                        reusedQuestion.setId(existIdIgnoreParent);
                        context.registerImported(sourceQuestion, reusedQuestion);
                    } else {
                        // 匹配不到则跳过，避免把“无父映射子题”误新增成新题
                        skippedCount++;
                        parentMissDroppedCount++;
                    }
                    continue;
                }
                ImportQuestionOutcome outcome = importOneQuestion(sourceQuestion, sourcePaper.getSubjectId(), mappedParentId, sourceToTargetQuestionId);
                insertedCount += outcome.insertedInc;
                skippedCount += outcome.skippedInc;
                filteredCount += outcome.filteredInc;
                reusedCount += outcome.reusedInc;
                if (outcome.questionForPaper != null) {
                    context.registerImported(sourceQuestion, outcome.questionForPaper);
                }
            }
        }

        // 每个题型按包内原始顺序回填，再规范为「父题 + 子题」块（与 ruoyi-ui 组卷页一致）
        for (TypeImportContext context : contexts) {
            rebuildMappedQuestionsAfterImport(context);
        }

        importPaper.setPaperQuestionTypeDto(mappedTypeList); // 将映射后的题型题目写入试卷
        DamingPaper insertedPaper = insertDamingPaper(importPaper); // 调用现有建卷逻辑持久化
        if (insertedPaper == null || insertedPaper.getPaperId() == null) { // 建卷失败兜底
            throw new ServiceException("试卷导入失败"); // 抛出业务异常
        } // 结束建卷失败兜底
        PaperSyncImportResultDto resultDto = new PaperSyncImportResultDto(); // 创建返回结果对象
        resultDto.setNewPaperId(insertedPaper.getPaperId()); // 返回新试卷ID
        resultDto.setInsertedQuestionCount(insertedCount); // 返回新增题目数量
        resultDto.setSkippedQuestionCount(skippedCount); // 返回跳过题目数量
        resultDto.setFilteredQuestionCount(filteredCount); // 返回过滤题目数量（空题干等）
        resultDto.setParentRemapMissCount(parentRemapMissCount); // 返回父ID重映射缺失数量
        resultDto.setReusedQuestionCount(reusedCount); // 返回复用题目数量（进试卷）
        resultDto.setParentMissReusedCount(parentMissReusedCount); // 返回父映射缺失但复用成功数量
        resultDto.setParentMissDroppedCount(parentMissDroppedCount); // 返回父映射缺失且未入卷数量
        resultDto.setNotInPaperQuestionCount(filteredCount + parentMissDroppedCount); // 返回最终未入卷数量
        return resultDto; // 返回导入结果
    }

    private ImportQuestionOutcome importOneQuestion(QuestionDto sourceQuestion,
                                                    Integer subjectId,
                                                    Long mappedParentId,
                                                    Map<Long, Long> sourceToTargetQuestionId) {
        ImportQuestionOutcome outcome = new ImportQuestionOutcome();
        if (!isValidQuestionForSyncImport(sourceQuestion)) {
            outcome.filteredInc = 1;
            return outcome;
        }
        QuestionDto importQuestion = cloneQuestionForImport(sourceQuestion, subjectId);
        importQuestion.setParentId(mappedParentId);
        Long existId = findExistingQuestionId(importQuestion);
        Long targetId;
        if (existId != null) {
            targetId = existId;
            outcome.skippedInc = 1;
            outcome.reusedInc = 1;
        } else {
            targetId = damingQuestionService.insertDamingQuestion(importQuestion);
            outcome.insertedInc = 1;
        }
        if (sourceQuestion.getId() != null) {
            sourceToTargetQuestionId.put(sourceQuestion.getId(), targetId);
        }
        importQuestion.setId(targetId);
        outcome.questionForPaper = importQuestion;
        return outcome;
    }

    private static class ImportQuestionOutcome {
        private int insertedInc;
        private int skippedInc;
        private int filteredInc;
        private int reusedInc;
        private QuestionDto questionForPaper;
    }

    private void rebuildMappedQuestionsAfterImport(TypeImportContext context) {
        List<QuestionDto> mappedQuestions = new ArrayList<>();
        for (QuestionDto sourceQuestion : context.processingOrder) {
            if (sourceQuestion == null || sourceQuestion.getId() == null) {
                continue;
            }
            QuestionDto imported = context.sourceIdToImportedQuestion.get(sourceQuestion.getId());
            if (imported != null) {
                mappedQuestions.add(imported);
            }
        }
        mappedQuestions.addAll(context.importedWithoutSourceId);
        normalizeParentChildQuestionOrder(mappedQuestions);
        context.mappedType.setQuestionDtos(mappedQuestions);
    }

    /**
     * 将题型下的题目调整为：顶层题按其在列表中首次出现顺序，每道顶层题后紧跟其所有子题（按 clozeIndex）。
     * 与 ruoyi-ui 试卷编辑 {@code applyDisplayOrder} 一致，避免完形子题排在父题前导致学生端/题量统计异常。
     */
    private void normalizeParentChildQuestionOrder(List<QuestionDto> flat) {
        if (CollectionUtils.isEmpty(flat)) {
            return;
        }
        Map<Long, QuestionDto> byId = new HashMap<>();
        for (QuestionDto q : flat) {
            if (q != null && q.getId() != null) {
                byId.put(q.getId(), q);
            }
        }
        Map<Long, List<QuestionDto>> parentToChildren = new HashMap<>();
        for (QuestionDto q : flat) {
            if (q == null || q.getId() == null) {
                continue;
            }
            if (isChildQuestion(q)) {
                Long pid = normalizeParentId(q.getParentId());
                if (pid != null) {
                    parentToChildren.computeIfAbsent(pid, k -> new ArrayList<>()).add(q);
                }
            }
        }
        for (List<QuestionDto> ch : parentToChildren.values()) {
            ch.sort(Comparator.comparing(c -> c.getClozeIndex() == null ? 0 : c.getClozeIndex()));
        }
        List<Long> topsOrdered = new ArrayList<>();
        Set<Long> seenTop = new HashSet<>();
        for (QuestionDto q : flat) {
            if (q == null || q.getId() == null) {
                continue;
            }
            if (!isChildQuestion(q) && seenTop.add(q.getId())) {
                topsOrdered.add(q.getId());
            }
        }
        List<QuestionDto> out = new ArrayList<>();
        Set<Long> placed = new HashSet<>();
        for (Long topId : topsOrdered) {
            QuestionDto top = byId.get(topId);
            if (top == null) {
                continue;
            }
            out.add(top);
            placed.add(top.getId());
            List<QuestionDto> ch = parentToChildren.get(topId);
            if (ch != null) {
                for (QuestionDto c : ch) {
                    out.add(c);
                    placed.add(c.getId());
                }
            }
        }
        for (QuestionDto q : flat) {
            if (q == null || q.getId() == null || placed.contains(q.getId())) {
                continue;
            }
            out.add(q);
            placed.add(q.getId());
        }
        flat.clear();
        flat.addAll(out);
    }

    private static class TypeImportContext {
        private PaperQuestionTypeDto mappedType;
        private List<QuestionDto> processingOrder = new ArrayList<>();
        private Map<Long, QuestionDto> sourceIdToImportedQuestion = new HashMap<>();
        private List<QuestionDto> importedWithoutSourceId = new ArrayList<>();

        private void registerImported(QuestionDto sourceQuestion, QuestionDto importedQuestion) {
            if (sourceQuestion != null && sourceQuestion.getId() != null) {
                sourceIdToImportedQuestion.put(sourceQuestion.getId(), importedQuestion);
            } else {
                importedWithoutSourceId.add(importedQuestion);
            }
        }
    }

    private QuestionDto cloneQuestionForImport(QuestionDto sourceQuestion, Integer subjectId) {
        QuestionDto importQuestion = modelMapper.map(sourceQuestion, QuestionDto.class); // 复制题目属性
        importQuestion.setId(null); // 清空ID，避免误更新
        importQuestion.setSubjectId(subjectId); // 统一以试卷科目为准
        importQuestion.setItemOrder(null); // 导入题目不保留源itemOrder
        return importQuestion; // 返回标准化后的导入对象
    }

    private Long findExistingQuestionId(QuestionDto importQuestion) {
        if (importQuestion == null
                || importQuestion.getSubjectId() == null
                || importQuestion.getQuestionType() == null) {
            return null; // 去重关键字段缺失，不做去重直接视为不存在
        }
        // 顶层题要求题干非空；子题允许空题干（例如完形子题）
        if (!isChildQuestion(importQuestion) && StringUtils.isEmpty(importQuestion.getQuestionTitle())) {
            return null;
        }
        List<DamingQuestion> candidates = damingQuestionMapper.selectQuestionsBySubjectTypeAndTitle(
                importQuestion.getSubjectId(),
                importQuestion.getQuestionType(),
                importQuestion.getQuestionTitle()
        ); // 先通过科目+题型+题干做候选缩小范围
        if (CollectionUtils.isEmpty(candidates)) {
            return null; // 没候选，直接返回未命中
        }
        for (DamingQuestion candidate : candidates) {
            QuestionDto existingDto = damingQuestionService.getQuestionDto(candidate); // 读取候选题完整DTO
            if (isSameQuestion(existingDto, importQuestion)) {
                return candidate.getId(); // 命中等价题，返回已有题ID
            }
        }
        return null; // 候选全部不匹配，判定为新题
    }

    private Long findExistingQuestionIdIgnoreParent(QuestionDto sourceQuestion, Integer subjectId) {
        if (sourceQuestion == null || subjectId == null || sourceQuestion.getQuestionType() == null) {
            return null;
        }
        QuestionDto probe = cloneQuestionForImport(sourceQuestion, subjectId);
        probe.setParentId(null);
        List<DamingQuestion> candidates = damingQuestionMapper.selectQuestionsBySubjectTypeAndTitle(
                probe.getSubjectId(),
                probe.getQuestionType(),
                probe.getQuestionTitle()
        );
        if (CollectionUtils.isEmpty(candidates)) {
            return null;
        }
        for (DamingQuestion candidate : candidates) {
            QuestionDto existingDto = damingQuestionService.getQuestionDto(candidate);
            if (isSameQuestionIgnoreParent(existingDto, probe)) {
                return candidate.getId();
            }
        }
        return null;
    }

    private boolean isSameQuestion(QuestionDto a, QuestionDto b) {
        if (a == null || b == null) {
            return false; // 任一为空，不可判等
        }
        if (!Objects.equals(a.getSubjectId(), b.getSubjectId())
                || !Objects.equals(a.getQuestionType(), b.getQuestionType())
                || !Objects.equals(trimToEmpty(a.getQuestionTitle()), trimToEmpty(b.getQuestionTitle()))
                || !Objects.equals(trimToEmpty(a.getAnalysis()), trimToEmpty(b.getAnalysis()))
                || !Objects.equals(a.getScore(), b.getScore())
                || !Objects.equals(a.getDifficulty(), b.getDifficulty())
                || !Objects.equals(a.getExamYear(), b.getExamYear())
                || !Objects.equals(a.getExamHalf(), b.getExamHalf())
                || !Objects.equals(normalizeParentId(a.getParentId()), normalizeParentId(b.getParentId()))
                || !Objects.equals(a.getClozeIndex(), b.getClozeIndex())) {
            return false; // 任一关键属性不同即判定不是同题
        }
        if (Objects.equals(a.getQuestionType(), QuestionTypeEnum.Multiple.getCode())) {
            String[] left = a.getCorrectArray() == null ? new String[0] : a.getCorrectArray();
            String[] right = b.getCorrectArray() == null ? new String[0] : b.getCorrectArray();
            Set<String> lSet = new HashSet<>();
            Set<String> rSet = new HashSet<>();
            for (String s : left) {
                lSet.add(trimToEmpty(s));
            }
            for (String s : right) {
                rSet.add(trimToEmpty(s));
            }
            if (!lSet.equals(rSet)) {
                return false; // 多选答案集合不一致
            }
        } else if (!Objects.equals(trimToEmpty(a.getCorrect()), trimToEmpty(b.getCorrect()))) {
            return false; // 非多选答案不一致
        }
        return normalizeOptions(a).equals(normalizeOptions(b)); // 最后比较标准化后的选项内容
    }

    private boolean isSameQuestionIgnoreParent(QuestionDto a, QuestionDto b) {
        if (a == null || b == null) {
            return false;
        }
        if (!Objects.equals(a.getSubjectId(), b.getSubjectId())
                || !Objects.equals(a.getQuestionType(), b.getQuestionType())
                || !Objects.equals(trimToEmpty(a.getQuestionTitle()), trimToEmpty(b.getQuestionTitle()))
                || !Objects.equals(trimToEmpty(a.getAnalysis()), trimToEmpty(b.getAnalysis()))
                || !Objects.equals(a.getScore(), b.getScore())
                || !Objects.equals(a.getDifficulty(), b.getDifficulty())
                || !Objects.equals(a.getExamYear(), b.getExamYear())
                || !Objects.equals(a.getExamHalf(), b.getExamHalf())
                || !Objects.equals(a.getClozeIndex(), b.getClozeIndex())) {
            return false;
        }
        if (Objects.equals(a.getQuestionType(), QuestionTypeEnum.Multiple.getCode())) {
            String[] left = a.getCorrectArray() == null ? new String[0] : a.getCorrectArray();
            String[] right = b.getCorrectArray() == null ? new String[0] : b.getCorrectArray();
            Set<String> lSet = new HashSet<>();
            Set<String> rSet = new HashSet<>();
            for (String s : left) {
                lSet.add(trimToEmpty(s));
            }
            for (String s : right) {
                rSet.add(trimToEmpty(s));
            }
            if (!lSet.equals(rSet)) {
                return false;
            }
        } else if (!Objects.equals(trimToEmpty(a.getCorrect()), trimToEmpty(b.getCorrect()))) {
            return false;
        }
        return normalizeOptions(a).equals(normalizeOptions(b));
    }

    private List<String> normalizeOptions(QuestionDto dto) {
        if (dto == null || CollectionUtils.isEmpty(dto.getItems())) {
            return new ArrayList<>(); // 无选项题型返回空列表
        }
        return dto.getItems().stream()
                .filter(Objects::nonNull)
                .map(i -> trimToEmpty(i.getPrefix()) + "|" + trimToEmpty(i.getContent()) + "|" + i.getScore())
                .collect(Collectors.toList()); // 统一转为“前缀|内容|分值”格式便于比较
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim(); // 空值转空串，非空去首尾空白
    }

    private boolean isValidQuestionForSyncImport(QuestionDto questionDto) {
        if (questionDto == null) { // 空对象无效
            return false;
        }
        // 顶层题（普通题/完形父题）：题干必须非空
        if (!isChildQuestion(questionDto)) {
            return StringUtils.isNotEmpty(trimToEmpty(questionDto.getQuestionTitle()));
        }
        // 子题：允许题干为空，改为校验“是否有可作答内容”
        return hasAnswerPayload(questionDto);
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

    private boolean hasAnswerPayload(QuestionDto questionDto) {
        // 子题优先判定答案载荷：correct / correctArray
        if (StringUtils.isNotEmpty(trimToEmpty(questionDto.getCorrect()))) {
            return true;
        }
        if (questionDto.getCorrectArray() != null && questionDto.getCorrectArray().length > 0) {
            for (String answer : questionDto.getCorrectArray()) {
                if (StringUtils.isNotEmpty(trimToEmpty(answer))) {
                    return true;
                }
            }
        }
        // 兜底：部分题型可能只有选项，尚未回填 correct，也视为有效
        return !CollectionUtils.isEmpty(questionDto.getItems());
    }

    private void enrichMissingParentQuestionsForExport(PaperDto paperDto) {
        if (paperDto == null || CollectionUtils.isEmpty(paperDto.getPaperQuestionTypeDto())) {
            return;
        }
        Set<Long> existedIds = new HashSet<>();
        Set<Long> requiredParentIds = new HashSet<>();
        for (PaperQuestionTypeDto typeDto : paperDto.getPaperQuestionTypeDto()) {
            if (typeDto == null || CollectionUtils.isEmpty(typeDto.getQuestionDtos())) {
                continue;
            }
            for (QuestionDto questionDto : typeDto.getQuestionDtos()) {
                if (questionDto == null) {
                    continue;
                }
                if (questionDto.getId() != null) {
                    existedIds.add(questionDto.getId());
                }
                Long normalizedParentId = normalizeParentId(questionDto.getParentId());
                if (normalizedParentId != null) {
                    requiredParentIds.add(normalizedParentId);
                }
            }
        }
        requiredParentIds.removeAll(existedIds);
        if (requiredParentIds.isEmpty()) {
            return;
        }
        // 缺父题时，统一补充到完形题型（若存在）；不存在则补到第一个题型，确保导入时能建立映射
        PaperQuestionTypeDto targetType = findOrCreateClozeTypeForExport(paperDto);
        List<QuestionDto> targetQuestions = targetType.getQuestionDtos();
        if (targetQuestions == null) {
            targetQuestions = new ArrayList<>();
            targetType.setQuestionDtos(targetQuestions);
        }
        for (Long parentId : requiredParentIds) {
            DamingQuestion parentEntity = damingQuestionMapper.selectDamingQuestionById(parentId);
            if (parentEntity == null || parentEntity.getDelFlag() != null && parentEntity.getDelFlag() == 2) {
                continue;
            }
            QuestionDto parentDto = damingQuestionService.getQuestionDto(parentEntity);
            if (parentDto == null || parentDto.getId() == null) {
                continue;
            }
            if (existedIds.contains(parentDto.getId())) {
                continue;
            }
            // 置空题号，避免干扰已有编号；导入时只需要保证父题存在即可
            parentDto.setItemOrder(null);
            targetQuestions.add(parentDto);
            existedIds.add(parentDto.getId());
        }
    }

    private PaperQuestionTypeDto findOrCreateClozeTypeForExport(PaperDto paperDto) {
        for (PaperQuestionTypeDto typeDto : paperDto.getPaperQuestionTypeDto()) {
            if (typeDto == null || CollectionUtils.isEmpty(typeDto.getQuestionDtos())) {
                continue;
            }
            for (QuestionDto questionDto : typeDto.getQuestionDtos()) {
                if (questionDto != null && Objects.equals(questionDto.getQuestionType(), QuestionTypeEnum.Cloze.getCode())) {
                    return typeDto;
                }
            }
        }
        if (!CollectionUtils.isEmpty(paperDto.getPaperQuestionTypeDto())) {
            return paperDto.getPaperQuestionTypeDto().get(0);
        }
        PaperQuestionTypeDto newType = new PaperQuestionTypeDto();
        newType.setName(QuestionTypeEnum.Cloze.getName());
        newType.setQuestionDtos(new ArrayList<>());
        paperDto.getPaperQuestionTypeDto().add(newType);
        return newType;
    }
}
