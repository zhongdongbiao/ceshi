package utry.data.modular.technicalQuality.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class TrainingInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 培训方式
     */
    private String trainingMethod;
    /**
     * 培训性质
     */
    private String trainingNature;
    /**
     * 培训时间
     */
    private String trainingTime;
    /**
     * 培训地点
     */
    private String trainingLocation;
    /**
     * 学习系统课件结果
     */
    private String studyCoursewareResults;
    /**
     * 学习系统考试成绩
     */
    private String studyTestGrades;
    /**
     * 学习系统考试结果
     */
    private String studyTestResults;
    /**
     * 现地培训书面成绩
     */
    private String inSituWrittenGrades;
    /**
     * 现地培训结果
     */
    private String inSituResults;
    /**
     * 工程师资质类别
     */
    private String engineerType;
    /**
     * 总评价
     */
    private String overallRating;
    /**
     * 导入时间
     */
    private String importTime;
    /**
     * 工程师新导入标志
     */
    private String importFlag;
}

