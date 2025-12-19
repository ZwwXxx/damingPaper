package com.dm.quiz.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
        int totalCount = paperDto.getPaperQuestionTypeDto().stream().mapToInt(i -> i.getQuestionDtos().size()).sum();
        // 计算所有题目的总分
        // 由于分数流属于第二层，会有很多个intstream，因此第一层我们要提前把多个intstream压缩为一个，然后进行sum处理
        int totalScore = paperDto.getPaperQuestionTypeDto().stream().flatMapToInt(i -> i.getQuestionDtos().stream().mapToInt(q -> q.getScore())).sum();
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
                questionVM.setItemOrder(index.getAndIncrement());
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
        List<Long> questionIds = questionTypeVMS.stream().flatMap(i -> i.getPaperQuestionVMS().stream().map(x -> x.getId())).collect(Collectors.toList());
        // 4.1根据id获取到每一个question
        List<DamingQuestion> damingQuestions = damingQuestionMapper.selectQuestionListByIds(questionIds);
        // 5.根据question，变为questionDTO然后设置order返回，map原题型数组，映射为DTO类型
        List<PaperQuestionTypeDto> paperQuestionTypeDtos = questionTypeVMS.stream().map(i -> {
            PaperQuestionTypeDto paperQuestionTypeDto = modelMapper.map(i, PaperQuestionTypeDto.class);
            List<QuestionDto> questionDtoStream = i.getPaperQuestionVMS().stream().map(x -> {
                DamingQuestion question = damingQuestions.stream().filter(p -> p.getId().equals(x.getId())).findFirst().get();
                // 5.1获取questionDto。
                QuestionDto questionDto = damingQuestionService.getQuestionDto(question);
                // 5.2此时的顺序只有VM的question才有也就是x
                questionDto.setItemOrder(x.getItemOrder());
                return questionDto;
            }).collect(Collectors.toList());
            // 5.3 将加工后的questionDto List设置给题型TypeDto
            paperQuestionTypeDto.setQuestionDtos(questionDtoStream);
            return paperQuestionTypeDto;
        }).collect(Collectors.toList());
        PaperDto paperDto = modelMapper.map(damingPaper, PaperDto.class);
        paperDto.setPaperQuestionTypeDto(paperQuestionTypeDtos);
        paperDto.setEnableAntiCheat(damingPaper.getEnableAntiCheat());
        paperDto.setQuestionCount(damingPaper.getQuestionCount());
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
}
