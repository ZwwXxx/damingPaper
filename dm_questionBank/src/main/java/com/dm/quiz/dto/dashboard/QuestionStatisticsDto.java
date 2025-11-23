package com.dm.quiz.dto.dashboard;

import java.util.List;

/**
 * 题目统计DTO
 * 
 * @author Cascade
 * @date 2025-11-22
 */
public class QuestionStatisticsDto {
    
    /** 科目分布 */
    private List<SubjectDistributionDto> subjectDistribution;
    
    /** 题型分布 */
    private List<TypeDistributionDto> typeDistribution;
    
    /** 热门收藏题目TOP10 */
    private List<TopFavoriteDto> topFavorites;

    public List<SubjectDistributionDto> getSubjectDistribution() {
        return subjectDistribution;
    }

    public void setSubjectDistribution(List<SubjectDistributionDto> subjectDistribution) {
        this.subjectDistribution = subjectDistribution;
    }

    public List<TypeDistributionDto> getTypeDistribution() {
        return typeDistribution;
    }

    public void setTypeDistribution(List<TypeDistributionDto> typeDistribution) {
        this.typeDistribution = typeDistribution;
    }

    public List<TopFavoriteDto> getTopFavorites() {
        return topFavorites;
    }

    public void setTopFavorites(List<TopFavoriteDto> topFavorites) {
        this.topFavorites = topFavorites;
    }
    
    /**
     * 科目分布数据
     */
    public static class SubjectDistributionDto {
        /** 科目名称 */
        private String subjectName;
        
        /** 题目数量 */
        private Long count;

        public String getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }
    
    /**
     * 题型分布数据
     */
    public static class TypeDistributionDto {
        /** 题型 */
        private Integer type;
        
        /** 题型名称 */
        private String typeName;
        
        /** 题目数量 */
        private Long count;

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }
    
    /**
     * 热门收藏题目数据
     */
    public static class TopFavoriteDto {
        /** 题目ID */
        private Long questionId;
        
        /** 题目内容预览 */
        private String preview;
        
        /** 收藏次数 */
        private Long favCount;

        public Long getQuestionId() {
            return questionId;
        }

        public void setQuestionId(Long questionId) {
            this.questionId = questionId;
        }

        public String getPreview() {
            return preview;
        }

        public void setPreview(String preview) {
            this.preview = preview;
        }

        public Long getFavCount() {
            return favCount;
        }

        public void setFavCount(Long favCount) {
            this.favCount = favCount;
        }
    }
}
