package com.dm.quiz.dto.dashboard.front;

import java.io.Serializable;
import java.util.List;

/**
 * 个人错题统计DTO
 * 
 * @author daming
 */
public class WrongQuestionStatDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 错题总数 */
    private Long totalWrong;
    
    /** 按科目分布 */
    private List<SubjectDistribution> subjectDistribution;
    
    /** 按题型分布 */
    private List<TypeDistribution> typeDistribution;

    public Long getTotalWrong() {
        return totalWrong;
    }

    public void setTotalWrong(Long totalWrong) {
        this.totalWrong = totalWrong;
    }

    public List<SubjectDistribution> getSubjectDistribution() {
        return subjectDistribution;
    }

    public void setSubjectDistribution(List<SubjectDistribution> subjectDistribution) {
        this.subjectDistribution = subjectDistribution;
    }

    public List<TypeDistribution> getTypeDistribution() {
        return typeDistribution;
    }

    public void setTypeDistribution(List<TypeDistribution> typeDistribution) {
        this.typeDistribution = typeDistribution;
    }

    /**
     * 科目分布
     */
    public static class SubjectDistribution implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String subjectName;
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
     * 题型分布
     */
    public static class TypeDistribution implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String typeName;
        private Long count;

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
}
