package org.example.test1.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("teaching_quality_log")
public class TeachingQualityLog {
    private Long id;
    private String teacherId;
    private String semester;
    private Integer timeStep;

    // 新增的五大指标分项
    private Double scoreQuality;  // 素质
    private Double scoreAttitude; // 态度
    private Double scoreContent;  // 内容
    private Double scoreMethod;   // 方法
    private Double scoreEffect;   // 效果

    public TeachingQualityLog(Long id, String teacherId, String semester, Integer timeStep, Double scoreQuality, Double scoreAttitude, Double scoreContent, Double scoreMethod, Double scoreEffect) {
        this.id = id;
        this.teacherId = teacherId;
        this.semester = semester;
        this.timeStep = timeStep;
        this.scoreQuality = scoreQuality;
        this.scoreAttitude = scoreAttitude;
        this.scoreContent = scoreContent;
        this.scoreMethod = scoreMethod;
        this.scoreEffect = scoreEffect;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public Integer getTimeStep() {
        return timeStep;
    }

    public void setTimeStep(Integer timeStep) {
        this.timeStep = timeStep;
    }

    public Double getScoreQuality() {
        return scoreQuality;
    }

    public void setScoreQuality(Double scoreQuality) {
        this.scoreQuality = scoreQuality;
    }

    public Double getScoreAttitude() {
        return scoreAttitude;
    }

    public void setScoreAttitude(Double scoreAttitude) {
        this.scoreAttitude = scoreAttitude;
    }

    public Double getScoreContent() {
        return scoreContent;
    }

    public void setScoreContent(Double scoreContent) {
        this.scoreContent = scoreContent;
    }

    public Double getScoreMethod() {
        return scoreMethod;
    }

    public void setScoreMethod(Double scoreMethod) {
        this.scoreMethod = scoreMethod;
    }

    public Double getScoreEffect() {
        return scoreEffect;
    }

    public void setScoreEffect(Double scoreEffect) {
        this.scoreEffect = scoreEffect;
    }

    public TeachingQualityLog() {
    }
}