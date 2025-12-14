package org.example.test1.entity;

import lombok.Data;

import java.util.List;

@Data
public class DashboardDataVO {
    // 1. 仪表盘头部数据
    private Double fuzzyScore;        // 模糊综合评分
    private Double predictedScore;    // 马尔科夫预测分
    private String riskLevel;         // 风险等级 (High/Medium/Low)

    // 2. 雷达图数据 (五维指标)
    private List<EvaluationIndicator> radarData;

    // 3. 马尔科夫趋势图数据
    private List<String> timeLabels;        // x轴: ["第4周", "第8周", "期末预测"]
    private List<Double> excellentRates;    // y轴: 优秀率曲线
    private List<Double> failRates;         // y轴: 不及格率曲线
}