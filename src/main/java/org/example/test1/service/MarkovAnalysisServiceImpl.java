package org.example.test1.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.example.test1.entity.DashboardDataVO;
import org.example.test1.entity.EvaluationIndicator;
import org.example.test1.entity.SimulationParams;
import org.example.test1.entity.TeachingQualityLog;
import org.example.test1.mapper.EvaluationIndicatorMapper;
import org.example.test1.mapper.TeachingQualityLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MarkovAnalysisServiceImpl {

    @Autowired
    private TeachingQualityLogMapper qualityLogMapper;

    @Autowired
    private EvaluationIndicatorMapper indicatorMapper;

    // 状态定义：[0:需改进, 1:一般, 2:良好, 3:优秀]
    private static final int STATE_IMPROVE = 0;
    private static final int STATE_AVERAGE = 1;
    private static final int STATE_GOOD = 2;
    private static final int STATE_EXCELLENT = 3;
    private static final int STATE_COUNT = 4;

    /**
     * 核心方法：获取教师教学质量智能评价数据
     */
    public DashboardDataVO getAnalysisData(String teacherId) {
        DashboardDataVO vo = new DashboardDataVO();

        // ---------------------------------------------------------
        // 1. 获取指标体系与权重 (AHP的核心输入)
        // ---------------------------------------------------------
        List<EvaluationIndicator> indicators = indicatorMapper.selectList(null);
        vo.setRadarData(indicators); // 设置给前端雷达图

        // 【关键步骤】将列表转为 Map<Code, Weight>，方便后续计算历史总分
        Map<String, Double> weightMap = indicators.stream()
                .collect(Collectors.toMap(EvaluationIndicator::getCode, EvaluationIndicator::getWeight));

        // 计算当前学期的模糊综合评分 (Fuzzy Score)
        double currentFuzzy = 0.0;
        for (EvaluationIndicator ind : indicators) {
            // 注意：使用 currentAvgScore (问卷平均分)
            Double score = ind.getCurrentAvgScore() != null ? ind.getCurrentAvgScore() : 0.0;
            currentFuzzy += score * ind.getWeight();
        }
        vo.setFuzzyScore(Math.round(currentFuzzy * 10.0) / 10.0);

        // ---------------------------------------------------------
        // 2. 获取历史分项数据并动态计算总分
        // ---------------------------------------------------------
        QueryWrapper<TeachingQualityLog> query = new QueryWrapper<>();
        query.eq("teacher_id", teacherId).orderByAsc("time_step");
        List<TeachingQualityLog> historyLogs = qualityLogMapper.selectList(query);

        // 动态计算每一期的加权总分
        List<Double> calculatedTotalScores = new ArrayList<>();
        if (!historyLogs.isEmpty()) {
            for (TeachingQualityLog log : historyLogs) {
                // 调用辅助方法：根据当年的指标得分 * 权重 = 当年总分
                calculatedTotalScores.add(calculateWeightedScore(log, weightMap));
            }
        } else {
            // 如果没数据，直接返回基础VO防止报错
            return vo;
        }

        // ---------------------------------------------------------
        // 3. 马尔科夫链 (Markov Chain) 预测
        // ---------------------------------------------------------
        // 3.1 构建状态转移矩阵
        double[][] transitionMatrix = buildTransitionMatrix(calculatedTotalScores);
        List<List<Double>> matrixList = new ArrayList<>();
        for (double[] row : transitionMatrix) {
            List<Double> rowList = new ArrayList<>();
            for (double val : row) {
                // 保留4位小数，方便前端显示百分比
                rowList.add(Math.round(val * 10000.0) / 10000.0);
            }
            matrixList.add(rowList);
        }
        vo.setTransitionMatrix(matrixList);

        // 3.2 获取当前状态向量 (基于最近一次的总分)
        double lastScore = calculatedTotalScores.get(calculatedTotalScores.size() - 1);
        double[] currentVector = new double[STATE_COUNT];
        currentVector[mapScoreToState(lastScore)] = 1.0;

        // 3.3 预测下一阶段 (S_next = S_current * P)
        double[] nextVector = multiplyVectorMatrix(currentVector, transitionMatrix);

        // ---------------------------------------------------------
        // 4. 组装数据与智能分析
        // ---------------------------------------------------------

        // 4.1 组装图表趋势数据
        assembleTrendChart(vo, historyLogs, calculatedTotalScores, nextVector);

        // 4.2 计算预测的具体分值 (期望值)
        double predictedScore = nextVector[0]*70 + nextVector[1]*80 + nextVector[2]*88 + nextVector[3]*95;
        vo.setPredictedScore(Math.round(predictedScore * 10.0) / 10.0);

        // 4.3 设置风险等级文本 (简单逻辑)
        if (nextVector[STATE_IMPROVE] + nextVector[STATE_AVERAGE] > 0.3) {
            vo.setRiskLevel("预警：存在质量下滑风险");
        } else {
            vo.setRiskLevel("状态：教学质量稳步提升");
        }

        // 4.4 【新增】生成智能建议 (Smart Advice)
        vo.setImprovementPlan(generateSmartAdvice(indicators));

        // 4.5 【新增】计算稳定性指数 (Stability Index)
        vo.setStabilityIndex(calculateStabilityIndex(calculatedTotalScores));

        return vo;
    }

    /**
     * 模拟推演：如果教师改进教学，分数会怎么变？
     */
    /**
     * 模拟推演：根据五维指标的投入调整，预测分数的提升情况
     */
    public DashboardDataVO simulateData(SimulationParams params) {
        DashboardDataVO vo = getAnalysisData("T001");

        // 1. 从数据库动态获取当前权重 (不再硬编码!)
        List<EvaluationIndicator> indicators = indicatorMapper.selectList(null);
        Map<String, Double> weightMap = indicators.stream()
                .collect(Collectors.toMap(EvaluationIndicator::getCode, EvaluationIndicator::getWeight));

        // 2. 计算各维度的加分 (输入 0-100)
        // 公式：投入度 * 动态权重 * 灵敏度系数(0.6)
        double boostQuality  = (params.getQuality() == null ? 0 : params.getQuality()) * weightMap.getOrDefault("quality", 0.0) * 0.6;
        double boostAttitude = (params.getAttitude() == null ? 0 : params.getAttitude()) * weightMap.getOrDefault("attitude", 0.0) * 0.6;
        double boostContent  = (params.getContent() == null ? 0 : params.getContent()) * weightMap.getOrDefault("content", 0.0) * 0.6;
        double boostMethod   = (params.getMethod() == null ? 0 : params.getMethod()) * weightMap.getOrDefault("method", 0.0) * 0.6;
        double boostEffect   = (params.getEffect() == null ? 0 : params.getEffect()) * weightMap.getOrDefault("effect", 0.0) * 0.6;

        double totalBoost = boostQuality + boostAttitude + boostContent + boostMethod + boostEffect;

        // 3. 更新预测分
        double currentScore = vo.getPredictedScore() != null ? vo.getPredictedScore() : 0.0;
        double newScore = Math.min(100.0, currentScore + totalBoost);

        newScore = Math.round(newScore * 10.0) / 10.0;
        vo.setPredictedScore(newScore);

        // 4. 更新图表
        List<Double> trend = vo.getExcellentRates();
        if (trend != null && !trend.isEmpty()) {
            trend.set(trend.size() - 1, newScore);
        }

        // 5. 更新文案
        if (newScore >= 90) vo.setRiskLevel("模拟预测：综合改进后将达到【优秀】等级");
        else if (newScore > currentScore) vo.setRiskLevel("模拟预测：教学质量将有显著提升");

        return vo;
    }

    /**
     * 新增：更新系统指标权重
     * @param newWeights 前端传来的权重 Map (e.g., "quality" -> 0.2)
     */
    public void updateSystemWeights(Map<String, Double> newWeights) {
        for (Map.Entry<String, Double> entry : newWeights.entrySet()) {
            String code = entry.getKey();
            Double weight = entry.getValue();
            if (weight != null) {
                UpdateWrapper<EvaluationIndicator> update = new UpdateWrapper<>();
                update.eq("code", code);
                update.set("weight", weight);
                indicatorMapper.update(null, update);
            }
        }
    }
    // =================================================================
    // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 私有辅助方法 (算法核心) ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    // =================================================================

    /**
     * 辅助：计算单条记录的加权总分
     */
    private double calculateWeightedScore(TeachingQualityLog log, Map<String, Double> weights) {
        double score = 0.0;
        // 防止空指针，使用 getOrDefault
        score += log.getScoreQuality() * weights.getOrDefault("quality", 0.0);
        score += log.getScoreAttitude() * weights.getOrDefault("attitude", 0.0);
        score += log.getScoreContent()  * weights.getOrDefault("content", 0.0);
        score += log.getScoreMethod()   * weights.getOrDefault("method", 0.0);
        score += log.getScoreEffect()   * weights.getOrDefault("effect", 0.0);
        return score;
    }

    /**
     * 辅助：生成智能建议列表
     */
    private List<String> generateSmartAdvice(List<EvaluationIndicator> indicators) {
        List<String> adviceList = new ArrayList<>();

        for (EvaluationIndicator ind : indicators) {
            double score = ind.getCurrentAvgScore() != null ? ind.getCurrentAvgScore() : 0.0;

            // 阈值设定：低于 85 分的项给出建议
            if (score < 85) {
                String code = ind.getCode() == null ? "" : ind.getCode();
                switch (code) {
                    case "quality":
                        adviceList.add("【教学素质】 建议加强师德师风建设，提升职业素养评分。"); break;
                    case "attitude":
                        adviceList.add("【教学态度】 建议加强课堂考勤管理，课前准备需更加充分。"); break;
                    case "content":
                        adviceList.add("【教学内容】 课程内容略显陈旧，建议引入学科前沿案例。"); break;
                    case "method":
                        adviceList.add("【教学方法】 互动不足(当前" + score + "分)，建议采用翻转课堂或PBL教学法。"); break;
                    case "effect":
                        adviceList.add("【教学效果】 学生反馈一般，建议增加课后辅导频次。"); break;
                    default:
                        adviceList.add("【" + ind.getName() + "】 该指标有待提升。");
                }
            }
        }
        if (adviceList.isEmpty()) {
            adviceList.add("🎉 恭喜！各项指标均表现优秀，请继续保持。");
        }
        return adviceList;
    }

    /**
     * 辅助：计算稳定性指数 (标准差)
     */
    private String calculateStabilityIndex(List<Double> scores) {
        if (scores == null || scores.size() < 2) return "数据不足";

        // 1. 平均值
        double sum = 0;
        for (Double s : scores) sum += s;
        double mean = sum / scores.size();

        // 2. 方差
        double varianceSum = 0;
        for (Double s : scores) {
            varianceSum += Math.pow(s - mean, 2);
        }
        double stdDev = Math.sqrt(varianceSum / scores.size());

        // 3. 评级
        if (stdDev < 1.5) return "⭐⭐⭐⭐⭐ (极度稳定)";
        if (stdDev < 3.0) return "⭐⭐⭐⭐ (表现平稳)";
        if (stdDev < 5.0) return "⭐⭐⭐ (存在波动)";
        return "⚠️ (起伏较大)";
    }

    /**
     * 辅助：构建状态转移矩阵 (简化模拟版)
     */
    private double[][] buildTransitionMatrix(List<Double> scores) {
        double[][] matrix = new double[STATE_COUNT][STATE_COUNT];
        // 这里的逻辑是：如果历史分数呈下降趋势，则向低分状态转移的概率变大
        // 为了演示效果，我们构造一个带有“惯性”的矩阵
        for(int i=0; i<STATE_COUNT; i++) {
            matrix[i][i] = 0.6; // 60%概率保持当前等级
            if(i > 0) matrix[i][i-1] = 0.3; // 30%概率下滑 (模拟风险)
            if(i < STATE_COUNT-1) matrix[i][i+1] = 0.1; // 10%概率上升
        }
        return matrix;
    }

    private double[] multiplyVectorMatrix(double[] vector, double[][] matrix) {
        double[] result = new double[STATE_COUNT];
        for (int j = 0; j < STATE_COUNT; j++) {
            for (int i = 0; i < STATE_COUNT; i++) {
                result[j] += vector[i] * matrix[i][j];
            }
        }
        return result;
    }

    private int mapScoreToState(Double score) {
        if (score < 75) return STATE_IMPROVE;
        if (score < 85) return STATE_AVERAGE;
        if (score < 92) return STATE_GOOD;
        return STATE_EXCELLENT;
    }

    private void assembleTrendChart(DashboardDataVO vo, List<TeachingQualityLog> logs, List<Double> calculatedScores, double[] nextVector) {
        List<String> labels = new ArrayList<>();
        for (TeachingQualityLog log : logs) {
            labels.add(log.getSemester());
        }

        // 添加预测点 Label
        labels.add("下学期(预测)");

        // 计算预测值
        double predictVal = nextVector[0]*70 + nextVector[1]*80 + nextVector[2]*88 + nextVector[3]*95;

        // 构造数据列表 (需要是可变的ArrayList)
        List<Double> trendData = new ArrayList<>(calculatedScores);
        trendData.add(Math.round(predictVal * 10.0) / 10.0);

        vo.setTimeLabels(labels);
        // 复用字段：excellentRates -> 总分趋势
        vo.setExcellentRates(trendData);
        // 复用字段：failRates -> 下滑概率 (模拟数据)
        double riskProb = nextVector[STATE_IMPROVE] + nextVector[STATE_AVERAGE];
        vo.setFailRates(Arrays.asList(0.02, 0.05, 0.10, 0.15, 0.25, riskProb));
    }
}