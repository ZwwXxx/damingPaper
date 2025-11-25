package com.dm.quiz.service.impl;

import com.dm.quiz.dto.dashboard.front.*;
import com.dm.quiz.dto.dashboard.front.StudyTimeDto.HourDistribution;
import com.dm.quiz.dto.dashboard.front.WrongQuestionStatDto.SubjectDistribution;
import com.dm.quiz.dto.dashboard.front.WrongQuestionStatDto.TypeDistribution;
import com.dm.quiz.mapper.PersonalDashboardMapper;
import com.dm.quiz.service.IPersonalDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 个人学习报表Service实现
 * 
 * @author daming
 */
@Service
public class PersonalDashboardServiceImpl implements IPersonalDashboardService {

    @Autowired
    private PersonalDashboardMapper personalDashboardMapper;

    @Override
    public PersonalOverviewDto getOverview(String username) {
        System.out.println("=== PersonalDashboard Service: username = " + username);
        
        PersonalOverviewDto dto = new PersonalOverviewDto();
        
        // 累计数据
        Long totalQuestions = personalDashboardMapper.getTotalQuestions(username);
        System.out.println("=== 查询到的做题数: " + totalQuestions);
        dto.setTotalQuestions(totalQuestions != null ? totalQuestions : 0L);
        
        Long totalExams = personalDashboardMapper.getTotalExams(username);
        dto.setTotalExams(totalExams != null ? totalExams : 0L);
        
        Long totalWrong = personalDashboardMapper.getTotalWrongQuestions(username);
        dto.setTotalWrongQuestions(totalWrong != null ? totalWrong : 0L);
        
        Long totalFavorites = personalDashboardMapper.getTotalFavorites(username);
        dto.setTotalFavorites(totalFavorites != null ? totalFavorites : 0L);
        
        // 最近7天数据
        Long weekQuestions = personalDashboardMapper.getRecentQuestions(username, 7);
        dto.setWeekQuestions(weekQuestions != null ? weekQuestions : 0L);
        
        Long weekExams = personalDashboardMapper.getRecentExams(username, 7);
        dto.setWeekExams(weekExams != null ? weekExams : 0L);
        
        // 累计学习时长
        Long totalStudyTime = personalDashboardMapper.getTotalStudyTime(username);
        dto.setTotalStudyTime(totalStudyTime != null ? totalStudyTime : 0L);
        
        return dto;
    }

    @Override
    public List<ExamTrendDto> getExamTrend(String username, Integer limit, String paperName) {
        List<Map<String, Object>> dataList = personalDashboardMapper.getExamTrend(
            username, 
            limit != null ? limit : 10,
            paperName
        );
        List<ExamTrendDto> result = new ArrayList<>();
        
        for (Map<String, Object> data : dataList) {
            ExamTrendDto dto = new ExamTrendDto();
            dto.setPaperAnswerId(((Number) data.get("paperAnswerId")).longValue());
            dto.setPaperId(((Number) data.get("paperId")).longValue());
            dto.setPaperName((String) data.get("paperName"));
            dto.setPaperScore(((Number) data.get("paperScore")).intValue());
            
            Object finalScoreObj = data.get("finalScore");
            dto.setFinalScore(finalScoreObj != null ? ((Number) finalScoreObj).doubleValue() : 0.0);
            
            Object scorePercentObj = data.get("scorePercent");
            dto.setScorePercent(scorePercentObj != null ? ((Number) scorePercentObj).doubleValue() : 0.0);
            
            // 处理时间类型转换：LocalDateTime -> Date
            Object createTimeObj = data.get("createTime");
            if (createTimeObj instanceof java.time.LocalDateTime) {
                java.time.LocalDateTime localDateTime = (java.time.LocalDateTime) createTimeObj;
                dto.setCreateTime(java.util.Date.from(localDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant()));
            } else if (createTimeObj instanceof java.util.Date) {
                dto.setCreateTime((java.util.Date) createTimeObj);
            }
            
            Object doTimeObj = data.get("doTime");
            dto.setDoTime(doTimeObj != null ? ((Number) doTimeObj).intValue() : 0);
            
            result.add(dto);
        }
        
        return result;
    }

    @Override
    public List<String> getPaperList(String username) {
        return personalDashboardMapper.getPaperList(username);
    }

    @Override
    public List<SubjectScoreDto> getSubjectScores(String username) {
        List<Map<String, Object>> dataList = personalDashboardMapper.getSubjectScores(username);
        List<SubjectScoreDto> result = new ArrayList<>();
        
        for (Map<String, Object> data : dataList) {
            SubjectScoreDto dto = new SubjectScoreDto();
            dto.setSubjectId(((Number) data.get("subjectId")).longValue());
            dto.setSubjectName((String) data.get("subjectName"));
            
            Object avgScoreObj = data.get("avgScore");
            dto.setAvgScore(avgScoreObj != null ? ((Number) avgScoreObj).doubleValue() : 0.0);
            
            Object avgScorePercentObj = data.get("avgScorePercent");
            dto.setAvgScorePercent(avgScorePercentObj != null ? ((Number) avgScorePercentObj).doubleValue() : 0.0);
            
            Object maxScoreObj = data.get("maxScore");
            dto.setMaxScore(maxScoreObj != null ? ((Number) maxScoreObj).doubleValue() : 0.0);
            
            Object minScoreObj = data.get("minScore");
            dto.setMinScore(minScoreObj != null ? ((Number) minScoreObj).doubleValue() : 0.0);
            
            dto.setExamCount(((Number) data.get("examCount")).longValue());
            
            result.add(dto);
        }
        
        return result;
    }

    @Override
    public WrongQuestionStatDto getWrongQuestionStat(String username) {
        WrongQuestionStatDto dto = new WrongQuestionStatDto();
        
        // 错题总数
        Long totalWrong = personalDashboardMapper.getTotalWrongQuestions(username);
        dto.setTotalWrong(totalWrong != null ? totalWrong : 0L);
        
        // 按科目分布
        List<Map<String, Object>> subjectData = personalDashboardMapper.getWrongQuestionsBySubject(username);
        List<SubjectDistribution> subjectList = new ArrayList<>();
        for (Map<String, Object> data : subjectData) {
            SubjectDistribution dist = new SubjectDistribution();
            dist.setSubjectName((String) data.get("subjectName"));
            dist.setCount(((Number) data.get("count")).longValue());
            subjectList.add(dist);
        }
        dto.setSubjectDistribution(subjectList);
        
        // 按题型分布
        List<Map<String, Object>> typeData = personalDashboardMapper.getWrongQuestionsByType(username);
        List<TypeDistribution> typeList = new ArrayList<>();
        for (Map<String, Object> data : typeData) {
            TypeDistribution dist = new TypeDistribution();
            dist.setTypeName((String) data.get("typeName"));
            dist.setCount(((Number) data.get("count")).longValue());
            typeList.add(dist);
        }
        dto.setTypeDistribution(typeList);
        
        return dto;
    }

    @Override
    public StudyTimeDto getStudyTimeDistribution(String username, Integer days) {
        StudyTimeDto dto = new StudyTimeDto();
        
        Integer queryDays = days != null ? days : 30;
        
        // 学习天数
        Long studyDays = personalDashboardMapper.getStudyDays(username, queryDays);
        dto.setStudyDays(studyDays != null ? studyDays : 0L);
        
        // 连续学习天数
        Long continuousDays = personalDashboardMapper.getContinuousStudyDays(username);
        dto.setContinuousDays(continuousDays != null ? continuousDays : 0L);
        
        // 24小时分布
        List<Map<String, Object>> hourData = personalDashboardMapper.getHourDistribution(username, queryDays);
        List<HourDistribution> hourList = new ArrayList<>();
        for (Map<String, Object> data : hourData) {
            HourDistribution dist = new HourDistribution();
            dist.setHour(((Number) data.get("hour")).intValue());
            dist.setCount(((Number) data.get("count")).longValue());
            hourList.add(dist);
        }
        dto.setHourDistribution(hourList);
        
        return dto;
    }
}
