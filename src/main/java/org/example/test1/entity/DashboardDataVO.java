package org.example.test1.entity;


import java.util.List;


public class DashboardDataVO {
    private Double fuzzyScore;      // 当前模糊评分
    private Double predictedScore;  // 预测总分
    private String riskLevel;       // 风险等级/诊断结论

    // --- 现有字段 ---
    private List<String> improvementPlan;
    private String stabilityIndex;
    private List<EvaluationIndicator> radarData;
    private List<String> timeLabels;
    private List<Double> excellentRates;
    private List<Double> failRates;

    // --- 新增字段：马尔科夫转移矩阵 ---
    // 这是一个 4x4 的二维数组，为了方便前端解析，用 List<List<Double>>
    private List<List<Double>> transitionMatrix;

    public DashboardDataVO() {
    }

    public DashboardDataVO(Double fuzzyScore, Double predictedScore, String riskLevel, List<String> improvementPlan, String stabilityIndex, List<EvaluationIndicator> radarData, List<String> timeLabels, List<Double> excellentRates, List<Double> failRates, List<List<Double>> transitionMatrix) {
        this.fuzzyScore = fuzzyScore;
        this.predictedScore = predictedScore;
        this.riskLevel = riskLevel;
        this.improvementPlan = improvementPlan;
        this.stabilityIndex = stabilityIndex;
        this.radarData = radarData;
        this.timeLabels = timeLabels;
        this.excellentRates = excellentRates;
        this.failRates = failRates;
        this.transitionMatrix = transitionMatrix;
    }

    public Double getFuzzyScore() {
        return fuzzyScore;
    }

    public void setFuzzyScore(Double fuzzyScore) {
        this.fuzzyScore = fuzzyScore;
    }

    public Double getPredictedScore() {
        return predictedScore;
    }

    public void setPredictedScore(Double predictedScore) {
        this.predictedScore = predictedScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public List<String> getImprovementPlan() {
        return improvementPlan;
    }

    public void setImprovementPlan(List<String> improvementPlan) {
        this.improvementPlan = improvementPlan;
    }

    public String getStabilityIndex() {
        return stabilityIndex;
    }

    public void setStabilityIndex(String stabilityIndex) {
        this.stabilityIndex = stabilityIndex;
    }

    public List<EvaluationIndicator> getRadarData() {
        return radarData;
    }

    public void setRadarData(List<EvaluationIndicator> radarData) {
        this.radarData = radarData;
    }

    public List<String> getTimeLabels() {
        return timeLabels;
    }

    public void setTimeLabels(List<String> timeLabels) {
        this.timeLabels = timeLabels;
    }

    public List<Double> getExcellentRates() {
        return excellentRates;
    }

    public void setExcellentRates(List<Double> excellentRates) {
        this.excellentRates = excellentRates;
    }

    public List<Double> getFailRates() {
        return failRates;
    }

    public void setFailRates(List<Double> failRates) {
        this.failRates = failRates;
    }

    public List<List<Double>> getTransitionMatrix() {
        return transitionMatrix;
    }

    public void setTransitionMatrix(List<List<Double>> transitionMatrix) {
        this.transitionMatrix = transitionMatrix;
    }
}