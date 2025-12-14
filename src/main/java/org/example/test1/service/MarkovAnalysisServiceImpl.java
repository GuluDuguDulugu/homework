package org.example.test1.service;

import org.example.test1.entity.DashboardDataVO;
import org.example.test1.entity.EvaluationIndicator;
import org.example.test1.entity.SimulationParams;
import org.example.test1.entity.StudentGrade;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MarkovAnalysisServiceImpl {

    // 定义状态空间：[0:不及格, 1:及格, 2:良好, 3:优秀]
    // 对应文档中“状态转移矩阵准确反映成绩变化趋势”的要求
    private static final int STATE_FAIL = 0; // < 60
    private static final int STATE_PASS = 1; // 60-75
    private static final int STATE_GOOD = 2; // 75-85
    private static final int STATE_EXCELLENT = 3; // > 85
    private static final int STATE_COUNT = 4;

    /**
     * 核心方法：获取仪表盘所需的所有智能分析数据
     */
    public DashboardDataVO getAnalysisData(String courseId) {
        DashboardDataVO vo = new DashboardDataVO();

        // Step 1: 获取并计算静态模糊评价 (模拟数据，实际应从数据库读取)
        vo.setRadarData(getMockRadarData());
        vo.setFuzzyScore(calculateFuzzyScore(vo.getRadarData()));

        // Step 2: 马尔科夫链核心运算
        // 2.1 获取历史成绩序列 (模拟从数据库查出的 StudentGrade 列表)
        List<StudentGrade> historyData = getMockHistoryData();

        // 2.2 计算状态转移矩阵 P (4x4)
        double[][] transitionMatrix = buildTransitionMatrix(historyData);

        // 2.3 获取当前状态向量 S0 (例如第12周的成绩分布)
        double[] currentVector = calculateStateDistribution(
                historyData.stream().filter(g -> g.getTimeStep() == 3).collect(Collectors.toList())
        );

        // 2.4 预测下一阶段 (S_next = S0 * P)
        double[] nextVector = multiplyVectorMatrix(currentVector, transitionMatrix);

        // Step 3: 组装趋势图数据
        vo.setTimeLabels(Arrays.asList("第4周", "第8周", "第12周", "期末(预测)"));

        // 提取"优秀率"(索引3) 和 "不及格率"(索引0) 的变化轨迹
        // 这里简化处理，实际应循环计算每一步的向量
        List<Double> exRates = new ArrayList<>();
        List<Double> failRates = new ArrayList<>();

        // 假设前3个时间步的真实数据
        exRates.add(0.15); exRates.add(0.18); exRates.add(currentVector[STATE_EXCELLENT]);
        // 添加预测数据
        exRates.add(nextVector[STATE_EXCELLENT]);

        failRates.add(0.10); failRates.add(0.08); failRates.add(currentVector[STATE_FAIL]);
        failRates.add(nextVector[STATE_FAIL]);

        vo.setExcellentRates(exRates.stream().map(d -> d * 100).collect(Collectors.toList()));
        vo.setFailRates(failRates.stream().map(d -> d * 100).collect(Collectors.toList()));

        // 计算预测加权分
        double predictedScore = nextVector[0]*50 + nextVector[1]*65 + nextVector[2]*80 + nextVector[3]*95;
        vo.setPredictedScore(Math.round(predictedScore * 10.0) / 10.0);

        // 简单的风险判定逻辑
        vo.setRiskLevel(nextVector[STATE_FAIL] > 0.15 ? "高风险" : "低风险");

        return vo;
    }

    /**
     * 算法实现：构建马尔科夫转移矩阵 P
     * 逻辑：统计所有学生从 t 到 t+1 的状态变化次数，然后按行归一化
     */
    private double[][] buildTransitionMatrix(List<StudentGrade> grades) {
        double[][] matrix = new double[STATE_COUNT][STATE_COUNT];
        int[][] counts = new int[STATE_COUNT][STATE_COUNT];

        // 按学生分组，排序时间步
        Map<String, List<StudentGrade>> studentMap = grades.stream()
                .collect(Collectors.groupingBy(StudentGrade::getStudentId));

        for (List<StudentGrade> list : studentMap.values()) {
            list.sort(Comparator.comparingInt(StudentGrade::getTimeStep));
            // 遍历该学生的时间序列
            for (int i = 0; i < list.size() - 1; i++) {
                int currentState = mapScoreToState(list.get(i).getScore());
                int nextState = mapScoreToState(list.get(i + 1).getScore());
                counts[currentState][nextState]++;
            }
        }

        // 归一化处理：Count -> Probability
        for (int i = 0; i < STATE_COUNT; i++) {
            int rowSum = Arrays.stream(counts[i]).sum();
            for (int j = 0; j < STATE_COUNT; j++) {
                if (rowSum == 0) {
                    // 如果某状态从未出现，防止除零，设为惯性保持（概率1.0）
                    matrix[i][j] = (i == j) ? 1.0 : 0.0;
                } else {
                    matrix[i][j] = (double) counts[i][j] / rowSum;
                }
            }
        }
        return matrix;
    }

    /**
     * 辅助算法：向量与矩阵乘法 (S_next = S_curr * P)
     */
    private double[] multiplyVectorMatrix(double[] vector, double[][] matrix) {
        double[] result = new double[STATE_COUNT];
        for (int j = 0; j < STATE_COUNT; j++) {
            for (int i = 0; i < STATE_COUNT; i++) {
                result[j] += vector[i] * matrix[i][j];
            }
        }
        return result;
    }

    // 将分数映射为状态索引
    private int mapScoreToState(Double score) {
        if (score < 60) return STATE_FAIL;
        if (score < 75) return STATE_PASS;
        if (score < 85) return STATE_GOOD;
        return STATE_EXCELLENT;
    }

    // 计算当前成绩分布向量
    private double[] calculateStateDistribution(List<StudentGrade> grades) {
        double[] dist = new double[STATE_COUNT];
        if (grades.isEmpty()) return dist;

        for (StudentGrade g : grades) {
            dist[mapScoreToState(g.getScore())]++;
        }
        // 归一化
        for (int i = 0; i < STATE_COUNT; i++) dist[i] /= grades.size();
        return dist;
    }

    // 简单加权计算模糊分
    private Double calculateFuzzyScore(List<EvaluationIndicator> indicators) {
        double sum = 0;
        double weightSum = 0;
        for (EvaluationIndicator ind : indicators) {
            sum += ind.getCurrentScore() * ind.getWeight();
            weightSum += ind.getWeight();
        }
        return Math.round((sum / weightSum) * 10.0) / 10.0;
    }

    // Mock Data Helpers... (省略具体的Mock数据生成代码，以免太长)
    private List<EvaluationIndicator> getMockRadarData() {
        List<EvaluationIndicator> list = new ArrayList<>();
        // 对应文档中的五大指标
        list.add(createInd("教学素质", "quality", 0.15, 90.0));
        list.add(createInd("教学态度", "attitude", 0.15, 95.0));
        list.add(createInd("教学内容", "content", 0.25, 88.0));
        list.add(createInd("教学方法", "method", 0.25, 72.0)); // 模拟短板
        list.add(createInd("教学效果", "effect", 0.20, 85.0));
        return list;
    }

    private EvaluationIndicator createInd(String name, String code, Double w, Double s) {
        EvaluationIndicator e = new EvaluationIndicator();
        e.setName(name); e.setCode(code); e.setWeight(w); e.setCurrentScore(s);
        return e;
    }

    private List<StudentGrade> getMockHistoryData() {
        // 这里应返回 List<StudentGrade>，包含多个学生在 timeStep 1, 2, 3 的成绩
        // 这里的逻辑对于马尔科夫矩阵的生成至关重要
        return new ArrayList<>();
    }

    /**
     * 模拟推演：根据改进参数，调整预测结果
     * 逻辑：教学干预（互动、辅导）会提高“状态转移矩阵”中向好状态转移的概率
     */
    public DashboardDataVO simulateData(SimulationParams params) {
        // 1. 先获取原本的基础数据
        // 注意：这里为了演示，传入 null 或默认 ID 均可，复用之前的逻辑
        DashboardDataVO vo = getAnalysisData("CS101");

        // 2. 计算“干预系数” (简单模拟算法)
        // 假设：互动每增加10%，总分提升0.5分；辅导每增加10%，总分提升0.8分
        double interactionEffect = (params.getInteraction() == null ? 0 : params.getInteraction()) * 0.05;
        double tutoringEffect = (params.getTutoring() == null ? 0 : params.getTutoring()) * 0.08;

        double totalBoost = interactionEffect + tutoringEffect;

        // 3. 修正预测分数 (马尔科夫稳态预测值的偏移)
        double newScore = vo.getPredictedScore() + totalBoost;
        // 封顶 100 分
        vo.setPredictedScore(Math.min(100.0, Math.round(newScore * 10.0) / 10.0));

        // 4. 修正趋势图 (让“优秀率”曲线的预测点上扬)
        List<Double> exRates = vo.getExcellentRates();
        if (exRates != null && !exRates.isEmpty()) {
            // 获取最后一个点（预测点）
            int lastIdx = exRates.size() - 1;
            double originalPredict = exRates.get(lastIdx);

            // 加上增益 (转换回百分比)
            double newPredict = originalPredict + totalBoost;
            exRates.set(lastIdx, Math.min(100.0, newPredict));
        }

        // 5. 修正“不及格率” (风险降低)
        List<Double> failRates = vo.getFailRates();
        if (failRates != null && !failRates.isEmpty()) {
            int lastIdx = failRates.size() - 1;
            double originalFail = failRates.get(lastIdx);
            // 减去增益的一半作为风险降低值
            double newFail = Math.max(0.0, originalFail - (totalBoost * 0.5));
            failRates.set(lastIdx, newFail);
        }

        // 6. 更新风险提示
        if (vo.getPredictedScore() > 88) {
            vo.setRiskLevel("低风险 (改进显著)");
        }

        return vo;
    }
}