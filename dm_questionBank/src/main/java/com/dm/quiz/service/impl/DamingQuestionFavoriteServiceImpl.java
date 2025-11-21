package com.dm.quiz.service.impl;

import com.dm.quiz.domain.DamingPaper;
import com.dm.quiz.domain.DamingQuestion;
import com.dm.quiz.domain.DamingQuestionFavorite;
import com.dm.quiz.dto.QuestionFavoriteRequest;
import com.dm.quiz.mapper.DamingPaperMapper;
import com.dm.quiz.mapper.DamingQuestionFavoriteMapper;
import com.dm.quiz.mapper.DamingQuestionMapper;
import com.dm.quiz.service.IDamingQuestionFavoriteService;
import com.dm.quiz.service.IDamingQuestionService;
import com.dm.quiz.viewmodel.FavoriteQuestionVO;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 题目收藏服务实现
 */
@Service
public class DamingQuestionFavoriteServiceImpl implements IDamingQuestionFavoriteService {

    @Autowired
    private DamingQuestionFavoriteMapper damingQuestionFavoriteMapper;
    @Autowired
    private DamingQuestionMapper damingQuestionMapper;
    @Autowired
    private IDamingQuestionService damingQuestionService;
    @Autowired
    private DamingPaperMapper damingPaperMapper;

    @Override
    public void addFavorite(QuestionFavoriteRequest request) {
        if (request == null || request.getQuestionId() == null || request.getPaperAnswerId() == null) {
            throw new ServiceException("收藏参数缺失");
        }
        String username = SecurityUtils.getUsername();
        if (StringUtils.isBlank(username)) {
            throw new ServiceException("未登录或登录已失效");
        }
        DamingQuestionFavorite exist = damingQuestionFavoriteMapper.selectOne(request.getQuestionId(), username);
        if (exist != null) {
            // 已收藏则无需重复插入
            return;
        }
        DamingQuestionFavorite favorite = new DamingQuestionFavorite();
        favorite.setQuestionId(request.getQuestionId());
        favorite.setPaperAnswerId(request.getPaperAnswerId());
        favorite.setPaperId(request.getPaperId());
        favorite.setSubjectId(request.getSubjectId());
        favorite.setCreateUser(username);
        favorite.setRemark(request.getRemark());
        favorite.setCreateTime(DateUtils.getNowDate());
        damingQuestionFavoriteMapper.insertDamingQuestionFavorite(favorite);
    }

    @Override
    public void removeFavorite(Long questionId, String username) {
        if (questionId == null) {
            return;
        }
        if (StringUtils.isBlank(username)) {
            username = SecurityUtils.getUsername();
        }
        if (StringUtils.isBlank(username)) {
            throw new ServiceException("未登录或登录已失效");
        }
        damingQuestionFavoriteMapper.deleteByUserAndQuestion(questionId, username);
    }

    @Override
    public List<Long> queryFavoriteQuestionIds(String username, Long paperAnswerId) {
        if (StringUtils.isBlank(username)) {
            username = SecurityUtils.getUsername();
        }
        if (StringUtils.isBlank(username)) {
            throw new ServiceException("未登录或登录已失效");
        }
        List<Long> ids = damingQuestionFavoriteMapper.selectFavoriteQuestionIds(username, paperAnswerId);
        return ids == null ? Collections.emptyList() : ids;
    }

    @Override
    public List<FavoriteQuestionVO> selectFavoriteQuestionList(DamingQuestionFavorite filter) {
        if (filter == null) {
            filter = new DamingQuestionFavorite();
        }
        if (!SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            filter.setCreateUser(SecurityUtils.getUsername());
        }
        List<DamingQuestionFavorite> favoriteList = damingQuestionFavoriteMapper.selectDamingQuestionFavoriteList(filter);
        if (favoriteList == null || favoriteList.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, DamingPaper> paperMap = buildPaperMap(favoriteList);
        Map<Long, com.dm.quiz.dto.QuestionDto> questionMap = buildQuestionDtoMap(favoriteList);
        return favoriteList.stream()
                .map(fav -> buildFavoriteVO(fav, paperMap.get(fav.getPaperId()), questionMap.get(fav.getQuestionId())))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Map<Long, DamingPaper> buildPaperMap(List<DamingQuestionFavorite> favorites) {
        Set<Long> paperIds = favorites.stream()
                .map(DamingQuestionFavorite::getPaperId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (paperIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return damingPaperMapper.selectDamingPaperListByIds(paperIds.stream().collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(DamingPaper::getPaperId, paper -> paper));
    }

    private Map<Long, com.dm.quiz.dto.QuestionDto> buildQuestionDtoMap(List<DamingQuestionFavorite> favorites) {
        Set<Long> questionIds = favorites.stream()
                .map(DamingQuestionFavorite::getQuestionId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (questionIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return damingQuestionMapper.selectQuestionListByIds(questionIds.stream().collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(DamingQuestion::getId, question -> damingQuestionService.getQuestionDto(question)));
    }

    private FavoriteQuestionVO buildFavoriteVO(DamingQuestionFavorite favorite,
                                               DamingPaper paper,
                                               com.dm.quiz.dto.QuestionDto questionDto) {
        if (favorite == null) {
            return null;
        }
        FavoriteQuestionVO vo = new FavoriteQuestionVO();
        vo.setFavoriteId(favorite.getFavoriteId());
        vo.setPaperId(favorite.getPaperId());
        vo.setPaperAnswerId(favorite.getPaperAnswerId());
        vo.setQuestionId(favorite.getQuestionId());
        vo.setSubjectId(favorite.getSubjectId());
        if (favorite.getCreateTime() != null) {
            vo.setCreateTime(DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", favorite.getCreateTime()));
        }
        if (paper != null) {
            vo.setPaperName(paper.getPaperName());
        }
        if (questionDto != null) {
            vo.setQuestionTitle(questionDto.getQuestionTitle());
            vo.setQuestionType(questionDto.getQuestionType());
            vo.setOptions(questionDto.getItems());
            vo.setCorrect(questionDto.getCorrect());
            vo.setCorrectArray(questionDto.getCorrectArray());
        }
        return vo;
    }
}
