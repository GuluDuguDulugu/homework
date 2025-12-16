<template>
  <div class="dashboard-container">
    <div class="header-section">
      <div class="header-row">
        <h2 class="page-title">智信教学质量动态智能评价系统</h2>
        <button class="config-btn" @click="openWeightModal">
          ⚙️ 配置指标权重
        </button>
      </div>

      <div class="status-cards">
        <div class="card score-card">
          <div class="label">当前模糊综合评分</div>
          <div class="value">{{ dashboardData.fuzzyScore || '-' }}</div>
          <div class="tag">基于实时权重计算</div>
        </div>
        <div class="card prediction-card">
          <div class="label">马尔科夫质量预测</div>
          <div class="value" :class="predictionTrendClass">
            {{ dashboardData.predictedScore || '-' }}
            <span class="trend-arrow" v-if="dashboardData.predictedScore">↗</span>
          </div>
          <div class="sub-text">下阶段质量期望值</div>
        </div>
        <div class="card risk-card">
          <div class="label">智能诊断结果</div>
          <div class="value warning" style="font-size: 24px;">{{ dashboardData.riskLevel || '分析中...' }}</div>
          <div class="sub-text">基于历史数据的归因分析</div>
        </div>
      </div>
    </div>

    <div class="charts-section">
      <div class="chart-box">
        <div class="chart-header">
          <h3>教师教学能力五维画像</h3>
          <span class="desc">数据来源：Lesson Feedback Big Data</span>
        </div>
        <div ref="radarChartRef" class="chart-canvas"></div>
      </div>

      <div class="chart-box">
        <div class="chart-header">
          <h3>教学质量演变与预测 (马尔科夫链)</h3>
          <span class="desc">实线：历史趋势 | 虚线：未来预测</span>
        </div>
        <div ref="lineChartRef" class="chart-canvas"></div>
      </div>
    </div>
    <div class="charts-section">
      
      <div class="chart-box">
        <div class="chart-header">
          <h3>马尔科夫状态转移概率矩阵</h3>
          <span class="desc">颜色越深代表转移概率越大 (X轴:下期状态 | Y轴:本期状态)</span>
        </div>
        <div ref="matrixChartRef" class="chart-canvas"></div>
      </div>

      <div class="chart-box">
        <div class="chart-header">
          <h3>评价指标权重分布</h3>
          <span class="desc">当前系统的评价导向占比</span>
        </div>
        <div ref="weightChartRef" class="chart-canvas"></div>
      </div>
      
    </div>

    <div class="bottom-section">
      
      <div class="simulation-section">
        <div class="sim-header">
          <h3>🧪 教学策略改进模拟 (Sensitivity Analysis)</h3>
          <p class="section-desc">调整各指标投入力度，推演对总分的提升效果</p>
        </div>
        
        <div class="sim-controls flex-fill">
          <div class="sliders-grid">
            <div class="control-item-card" v-for="item in simFactorsList" :key="item.key">
              <div class="item-header">
                <label>{{ item.label }}</label>
                <span class="factor-val">+{{ simFactors[item.key] }}%</span>
              </div>
              <div class="slider-wrapper">
                <input type="range" v-model.number="simFactors[item.key]" min="0" max="100">
                <div class="slider-track-bg" :style="{width: simFactors[item.key] + '%'}"></div>
              </div>
            </div>
          </div>

          <div class="action-area">
            <button class="sim-btn" @click="runSimulation" :disabled="loading">
              {{ loading ? '⚡ 正在演算模型...' : '🚀 运行全维推演' }}
            </button>
          </div>
        </div>

        <div class="sim-result" v-if="simResultVisible">
          <div class="result-icon">📈</div>
          <div class="result-text">
            推演完成：预计综合评分将提升至 
            <span class="highlight">{{ dashboardData.predictedScore }}</span> 分
          </div>
        </div>
      </div>

      <div class="feedback-section">
        <h3>📝 课堂即时反馈 (Student Feedback)</h3>
        <p class="section-desc">请对本次课程进行客观评价，数据将实时录入系统</p>

        <div class="feedback-form">
          <div class="form-row basic-info">
            <div class="input-group">
              <label>学生学号</label>
              <input type="text" v-model="lessonForm.studentId" placeholder="请输入您的学号">
            </div>
            <div class="input-group">
              <label>课程名称</label>
              <input type="text" v-model="lessonForm.courseName" placeholder="如：Java程序设计">
            </div>
            <div class="input-group">
              <label>上课日期</label>
              <input type="date" v-model="lessonForm.lessonDate">
            </div>
            <div class="input-group">
              <label>节次</label>
              <select v-model="lessonForm.lessonIndex">
                <option :value="1">第 1-2 节</option>
                <option :value="3">第 3-4 节</option>
                <option :value="5">第 5-6 节</option>
                <option :value="7">第 7-8 节</option>
              </select>
            </div>
          </div>

          <div class="form-title-small">五维指标评分 (拖动滑块)</div>
          <div class="form-row ratings-grid">
            <div class="rating-item" v-for="item in ratingItems" :key="item.key">
              <div class="rating-label">
                <span>{{ item.label }}</span>
                <span class="rating-score" :class="getScoreClass(lessonForm[item.key])">
                  {{ lessonForm[item.key] }}
                </span>
              </div>
              <input type="range" v-model.number="lessonForm[item.key]" min="0" max="100" class="rating-slider">
            </div>
          </div>

          <div class="form-row">
            <label>主观建议 / 留言</label>
            <textarea v-model="lessonForm.commentText" rows="3" placeholder="老师讲得怎么样？有哪些地方可以改进？..."></textarea>
          </div>

          <button class="submit-btn" @click="submitLessonFeedback" :disabled="submitting">
            {{ submitting ? '提交中...' : '提交反馈' }}
          </button>
        </div>
      </div>
    </div>

    <div class="modal-overlay" v-if="showWeightModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>⚖️ 动态调整评价指标权重</h3>
          <span class="close-btn" @click="closeWeightModal">×</span>
        </div>
        <div class="modal-body">
          <p class="modal-desc">
            请调整各项指标的权重占比，总和必须为 <strong>100%</strong>。<br>
            <span :class="totalWeight === 100 ? 'text-success' : 'text-danger'">
              当前总和: {{ totalWeight }}%
            </span>
          </p>
          
          <div class="weight-sliders">
            <div class="weight-item" v-for="item in weightFormList" :key="item.key">
              <div class="weight-label">
                <span>{{ item.label }}</span>
                <span class="weight-val">{{ weightForm[item.key] }}%</span>
              </div>
              <input type="range" v-model.number="weightForm[item.key]" min="0" max="100" class="rating-slider">
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="closeWeightModal">取消</button>
          <button class="save-btn" @click="saveWeights" :disabled="totalWeight !== 100 || saving">
            {{ saving ? '保存中...' : '确认更新标准' }}
          </button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, computed } from 'vue';
import * as echarts from 'echarts';
import axios from 'axios';

// --- 类型定义 ---
interface DashboardVO {
  fuzzyScore: number;
  predictedScore: number;
  riskLevel: string;
  radarData: any[];
  timeLabels: string[];
  excellentRates: number[];
  failRates: number[];
  transitionMatrix?: number[][];
}

const matrixChartRef = ref<HTMLElement | null>(null);
const weightChartRef = ref<HTMLElement | null>(null);
let matrixChart: echarts.ECharts | null = null;
let weightChart: echarts.ECharts | null = null;

// --- 状态管理 ---
const radarChartRef = ref<HTMLElement | null>(null);
const lineChartRef = ref<HTMLElement | null>(null);
const loading = ref(false);
const submitting = ref(false);
const simResultVisible = ref(false);

// 核心 Dashboard 数据
const dashboardData = reactive<DashboardVO>({
  fuzzyScore: 0,
  predictedScore: 0,
  riskLevel: '加载中...',
  radarData: [],
  timeLabels: [],
  excellentRates: [],
  failRates: []
});

// --- 模拟器数据 (左侧) ---
const simFactors = reactive<any>({
  quality: 0, attitude: 0, content: 0, method: 0, effect: 0
});
const simFactorsList = [
  { key: 'quality', label: '提升教学素质' },
  { key: 'attitude', label: '端正教学态度' },
  { key: 'content', label: '优化教学内容' },
  { key: 'method', label: '创新教学方法' },
  { key: 'effect', label: '加强教学效果' }
];

// --- 评教表单数据 (右侧) ---
const lessonForm = reactive<any>({
  studentId: '', // 学号
  courseName: 'Java程序设计', 
  lessonDate: new Date().toISOString().split('T')[0],
  lessonIndex: 1,
  scoreQuality: 90,
  scoreAttitude: 90,
  scoreContent: 90,
  scoreMethod: 90,
  scoreEffect: 90,
  commentText: ''
});
const ratingItems = [
  { key: 'scoreQuality', label: '教学素质' },
  { key: 'scoreAttitude', label: '教学态度' },
  { key: 'scoreContent', label: '教学内容' },
  { key: 'scoreMethod', label: '教学方法' },
  { key: 'scoreEffect', label: '教学效果' }
];

// --- 权重配置数据 (弹窗) ---
const showWeightModal = ref(false);
const saving = ref(false);
const weightForm = reactive<any>({
  quality: 0, attitude: 0, content: 0, method: 0, effect: 0
});
const weightFormList = [
  { key: 'quality', label: '教学素质' },
  { key: 'attitude', label: '教学态度' },
  { key: 'content', label: '教学内容' },
  { key: 'method', label: '教学方法' },
  { key: 'effect', label: '教学效果' }
];

// --- 计算属性 ---
const predictionTrendClass = computed(() => dashboardData.predictedScore > 85 ? 'trend-up' : 'warning');
const getScoreClass = (score: number) => {
  if (score >= 90) return 'text-success';
  if (score >= 75) return 'text-primary';
  return 'text-danger';
};
const totalWeight = computed(() => {
  return weightForm.quality + weightForm.attitude + weightForm.content + weightForm.method + weightForm.effect;
});

// --- ECharts 实例 ---
let radarChart: echarts.ECharts | null = null;
let lineChart: echarts.ECharts | null = null;

// --- 核心业务方法 ---

// 1. 获取主页数据
const fetchInitialData = async () => {
  try {
    const res = await axios.get('/api/evaluation/dashboard?courseId=T001');
    updateDashboard(res.data);
  } catch (err) {
    console.error("Using Mock Data due to error", err);
    updateDashboard(getMockData());
  }
};

// 2. 运行模拟
const runSimulation = async () => {
  loading.value = true;
  try {
    const res = await axios.post('/api/evaluation/simulate', simFactors);
    updateDashboard(res.data);
    simResultVisible.value = true;
  } finally { loading.value = false; }
};

// 3. 提交课堂评教
const submitLessonFeedback = async () => {
  if (!lessonForm.studentId) {
    alert("请填写学号！");
    return;
  }
  submitting.value = true;
  try {
    const payload = { ...lessonForm, teacherId: 'T001' };
    await axios.post('/api/evaluation/submit-lesson', payload);
    alert(`✅ 反馈提交成功！\n感谢您对《${lessonForm.courseName}》课程的评价。`);
    fetchInitialData(); // 刷新页面数据
  } catch (err) {
    alert("提交失败，请检查网络连接");
  } finally {
    submitting.value = false;
  }
};

// 4. 打开权重弹窗
const openWeightModal = () => {
  if (dashboardData.radarData && dashboardData.radarData.length > 0) {
    // 假设后端返回 weight 是小数(0.15)，这里转整数(15)显示
    dashboardData.radarData.forEach((item: any) => {
      if (weightForm.hasOwnProperty(item.code)) {
        weightForm[item.code] = Math.round(item.weight * 100);
      }
    });
  } else {
    // 兜底默认值
    Object.assign(weightForm, { quality: 15, attitude: 15, content: 25, method: 25, effect: 20 });
  }
  showWeightModal.value = true;
};

// 5. 关闭弹窗
const closeWeightModal = () => {
  showWeightModal.value = false;
};

// 6. 保存权重
const saveWeights = async () => {
  if (totalWeight.value !== 100) {
    alert("权重总和必须等于 100%");
    return;
  }
  saving.value = true;
  try {
    // 转小数发给后端
    const payload = {
      quality: weightForm.quality / 100.0,
      attitude: weightForm.attitude / 100.0,
      content: weightForm.content / 100.0,
      method: weightForm.method / 100.0,
      effect: weightForm.effect / 100.0
    };
    await axios.post('/api/evaluation/weights', payload);
    alert("✅ 权重配置已更新，系统评分标准已重置。");
    closeWeightModal();
    fetchInitialData(); // 重新拉取数据以更新分数
  } catch (err) {
    alert("保存失败");
  } finally {
    saving.value = false;
  }
};

const updateDashboard = (data: DashboardVO) => {
  Object.assign(dashboardData, data);
  renderCharts();
};

// --- 图表渲染 ---
const renderCharts = () => {
  // 雷达图配置
  if (radarChartRef.value) {
    if (!radarChart) radarChart = echarts.init(radarChartRef.value);
    radarChart.setOption({
      tooltip: {}, // 开启提示框
      radar: { 
        indicator: dashboardData.radarData.map((i:any) => ({name:i.name, max:100})), 
        center: ['50%', '55%'], 
        radius: '65%' 
      },
      series: [{ 
        type: 'radar', 
        data: [{ 
          value: dashboardData.radarData.map((i:any) => i.currentAvgScore), 
          name: '各项得分', 
          areaStyle: {color:'rgba(64,158,255,0.2)'}, 
          itemStyle:{color:'#409EFF'} 
        }] 
      }]
    });
  }
  // 折线图配置
  if (lineChartRef.value) {
    if (!lineChart) lineChart = echarts.init(lineChartRef.value);
    lineChart.setOption({
      tooltip: {trigger:'axis'},
      legend: { bottom: 0 },
      grid: { bottom: '15%', containLabel: true },
      xAxis: { type: 'category', data: dashboardData.timeLabels },
      yAxis: [{type:'value',min:60,max:100}, {type:'value',show:false}],
      series: [
        { name:'综合分', type:'line', smooth:true, data:dashboardData.excellentRates, itemStyle:{color:'#67C23A'} },
        { name:'风险率', type:'line', smooth:true, yAxisIndex:1, data:dashboardData.failRates, itemStyle:{color:'#F56C6C'}, areaStyle:{opacity:0.2} }
      ]
    });
  }
  if (matrixChartRef.value) {
    if (!matrixChart) matrixChart = echarts.init(matrixChartRef.value);
    
    // 构造热力图数据：[x, y, value]
    // 矩阵数据索引：需改进(0), 一般(1), 良好(2), 优秀(3)
    // X轴从左到右：需改进、一般、良好、优秀
    // Y轴从上到下：优秀、良好、一般、需改进
    const xStates = ['需改进', '一般', '良好', '优秀']; // X轴顺序
    const yStates = ['优秀', '良好', '一般', '需改进']; // Y轴顺序（从上到下）
    const matrixData = [];
    if (dashboardData.transitionMatrix) {
      const matrix = dashboardData.transitionMatrix;
      for (let i = 0; i < 4; i++) {
        for (let j = 0; j < 4; j++) {
          // ECharts Heatmap: [x, y, value]
          // matrix[i][j]: i对应原始状态索引(0=需改进, 1=一般, 2=良好, 3=优秀)
          // X轴：j直接使用（对应xStates数组索引）
          // Y轴：需要将i转换为Y轴位置(3-i)，使Y轴从上到下显示为：优秀、良好、一般、需改进
          const val = matrix[i]?.[j] || 0;
          matrixData.push([j, 3 - i, val]);
        }
      }
    }
    matrixChart.setOption({
      tooltip: { position: 'top' },
      grid: { height: '70%', top: '15%' },
      xAxis: { type: 'category', data: xStates, name: '下期状态' },
      yAxis: { type: 'category', data: yStates, name: '本期状态' },
      visualMap: {
        min: 0, max: 1, calculable: true, orient: 'horizontal', left: 'center', bottom: '0%',
        inRange: { color: ['#f0f9eb', '#67C23A'] } // 绿色系：越深代表概率越大
      },
      series: [{
        name: '转移概率',
        type: 'heatmap',
        data: matrixData,
        label: { show: true, formatter: (p:any) => p.value.toFixed(2) }, // 显示具体概率值
        itemStyle: {
          borderWidth: 1, borderColor: '#fff'
        }
      }]
    });
  }
  if (weightChartRef.value) {
    if (!weightChart) weightChart = echarts.init(weightChartRef.value);
    
    // 从 radarData 中提取权重数据
    const pieData = dashboardData.radarData && dashboardData.radarData.length > 0
      ? dashboardData.radarData.map((item: any) => ({
          name: item.name,
          value: (item.weight || 0) * 100 // 转换为百分比显示
        }))
      : [];

    weightChart.setOption({
      tooltip: { 
        trigger: 'item', 
        formatter: (params: any) => {
          return `${params.name}: ${params.value.toFixed(1)}%`;
        }
      },
      legend: { top: '5%', left: 'center' },
      series: [
        {
          name: '权重占比',
          type: 'pie',
          radius: ['40%', '70%'], // 环形图
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: { show: false, position: 'center' },
          emphasis: {
            label: { show: true, fontSize: 20, fontWeight: 'bold' }
          },
          labelLine: { show: false },
          data: pieData
        }
      ]
    });
  }
};

onMounted(() => {
  fetchInitialData();
  window.addEventListener('resize', () => {
    radarChart?.resize();
    lineChart?.resize();
    matrixChart?.resize(); // 新增
    weightChart?.resize(); // 新增
  });
});

// Mock Data
const getMockData = (): DashboardVO => ({
  fuzzyScore: 84.5, predictedScore: 86.2, riskLevel: '数据演示模式',
  radarData: [
    { name: '教学素质', code:'quality', weight:0.15, currentAvgScore: 92 },
    { name: '教学态度', code:'attitude', weight:0.15, currentAvgScore: 95 },
    { name: '教学内容', code:'content', weight:0.25, currentAvgScore: 88 },
    { name: '教学方法', code:'method', weight:0.25, currentAvgScore: 72 },
    { name: '教学效果', code:'effect', weight:0.20, currentAvgScore: 85 }
  ],
  timeLabels: ['2021-秋', '2022-春', '2022-秋', '2023-春', '预测'],
  excellentRates: [92.0, 90.5, 88.0, 85.0, 86.2],
  failRates: [0.02, 0.05, 0.08, 0.15, 0.12],
  // 马尔科夫状态转移概率矩阵 (4x4)
  // 状态顺序：需改进(0), 一般(1), 良好(2), 优秀(3)
  transitionMatrix: [
    [0.5, 0.3, 0.15, 0.05],  // 从"需改进"转移到各状态的概率
    [0.2, 0.4, 0.3, 0.1],    // 从"一般"转移到各状态的概率
    [0.1, 0.2, 0.5, 0.2],    // 从"良好"转移到各状态的概率
    [0.05, 0.1, 0.25, 0.6]   // 从"优秀"转移到各状态的概率
  ]
});
</script>

<style scoped>
/* =========================================
   1. 全局容器与基础字体
   ========================================= */
.dashboard-container {
  max-width: 1600px;
  margin: 0 auto;
  padding: 24px;
  background-color: #f0f2f5;
  min-height: 100vh;
  box-sizing: border-box;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
}

/* =========================================
   2. 顶部 Header 区域
   ========================================= */
.header-section {
  margin-bottom: 24px;
}

/* 标题行 & 配置按钮 */
.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  margin: 0;
  color: #1f2f3d;
  font-weight: 700;
  font-size: 24px;
  letter-spacing: 0.5px;
}

.config-btn {
  background: white;
  border: 1px solid #dcdfe6;
  color: #606266;
  padding: 10px 20px;
  border-radius: 20px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}
.config-btn:hover {
  border-color: #409EFF;
  color: #409EFF;
  transform: translateY(-2px);
}

.status-cards {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.card {
  flex: 1;
  min-width: 280px;
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  display: flex;
  flex-direction: column;
  justify-content: center;
  transition: all 0.3s ease;
  border: 1px solid #ebeef5;
}

.card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

.card .value {
  font-size: 36px;
  font-weight: 800;
  margin: 8px 0;
  color: #303133;
}

.card .label {
  color: #909399;
  font-size: 14px;
  font-weight: 500;
}

.card .value.trend-up { color: #67C23A; }
.card .value.warning { color: #E6A23C; }
.trend-arrow { font-size: 24px; margin-left: 5px; }

/* =========================================
   3. 中部图表区域
   ========================================= */
.charts-section {
  display: flex;
  gap: 20px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.chart-box {
  flex: 1;
  min-width: 500px;
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  display: flex;
  flex-direction: column;
}

.chart-header {
  margin-bottom: 20px;
}

.chart-header h3 {
  margin: 0 0 5px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.desc {
  font-size: 12px;
  color: #909399;
}

.chart-canvas {
  height: 350px;
  width: 100%;
}

/* =========================================
   4. 底部功能区 (模拟器 + 反馈表单)
   ========================================= */
.bottom-section {
  display: flex;
  gap: 24px;
  align-items: stretch; /* 关键：让左右两栏等高 */
  flex-wrap: wrap;
}

/* 通用 Section 容器 */
.simulation-section,
.feedback-section {
  flex: 1;
  min-width: 500px;
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  display: flex;
  flex-direction: column;
}

.simulation-section h3,
.feedback-section h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-desc {
  font-size: 13px;
  color: #909399;
  margin: 0 0 20px 0;
}

.sim-header { flex-shrink: 0; }

/* 控制区容器：自动撑满剩余高度 */
.sim-controls.flex-fill {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  background: linear-gradient(to bottom, #f8fbfd, #f0f7ff);
  padding: 24px;
  border-radius: 12px;
  border: 1px solid #e1eaf5;
  box-shadow: inset 0 2px 6px rgba(0, 0, 0, 0.01);
}

/* 滑块网格：单列全宽 */
.sliders-grid {
  display: grid;
  grid-template-columns: 1fr; /* 单列 */
  gap: 24px; /* 间距拉大 */
  margin-bottom: 20px;
  width: 100%;
}

.control-item-card {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #606266;
  font-weight: 600;
}

.factor-val {
  color: #409EFF;
  background: rgba(64, 158, 255, 0.1);
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: bold;
}

/* 左侧滑块样式 (自定义) */
.slider-wrapper {
  position: relative;
  height: 8px;
  display: flex;
  align-items: center;
}
.slider-wrapper::before {
  content: ''; position: absolute; width: 100%; height: 6px; background: #e4e7ed; border-radius: 3px;
}
.slider-track-bg {
  position: absolute; height: 6px; background: #409EFF; border-radius: 3px; pointer-events: none; z-index: 1; transition: width 0.1s linear;
}
.slider-wrapper input[type=range] {
  width: 100%; position: absolute; z-index: 2; opacity: 0; cursor: pointer; height: 100%; margin: 0;
}

/* 按钮区域 */
.action-area {
  margin-top: auto;
  padding-top: 20px;
  border-top: 1px dashed #dae3f0;
}

.sim-btn {
  width: 100%;
  height: 48px;
  background: linear-gradient(135deg, #409EFF, #3a8ee6);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  font-size: 15px;
  letter-spacing: 1px;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}
.sim-btn:hover { transform: translateY(-2px); box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4); }
.sim-btn:disabled { background: #a0cfff; cursor: not-allowed; transform: none; box-shadow: none; }

/* 模拟结果 */
.sim-result {
  margin-top: 15px; padding: 15px; background: #f0f9eb; color: #67C23A; border-radius: 6px; display: flex; gap: 10px; align-items: center; border: 1px solid #e1f3d8; animation: fadeIn 0.5s ease;
}
.result-icon { font-size: 20px; }
.highlight { font-weight: bold; font-size: 1.1em; margin: 0 4px; color: #E6A23C; }

/* -----------------------------------------
   右侧：课堂即时反馈 (Student Feedback)
   ----------------------------------------- */
.feedback-section { border-top: 4px solid #E6A23C; }

.feedback-form { display: flex; flex-direction: column; gap: 15px; }
.form-row { display: flex; gap: 15px; flex-wrap: wrap; }
.basic-info .input-group {
  flex: 1; min-width: 140px; display: flex; flex-direction: column; gap: 6px;
}
.input-group label { font-size: 12px; color: #606266; font-weight: 600; }
.input-group input, .input-group select {
  padding: 8px 12px; border: 1px solid #dcdfe6; border-radius: 6px; outline: none; transition: 0.3s; font-size: 14px; background: #fff;
}
.input-group input:focus, .input-group select:focus { border-color: #E6A23C; }

.form-title-small {
  font-size: 13px; font-weight: bold; color: #303133; margin-top: 10px; border-left: 3px solid #E6A23C; padding-left: 8px;
}

.ratings-grid {
  display: grid; grid-template-columns: 1fr 1fr; gap: 10px 20px; background: #fffbf0; padding: 15px; border-radius: 8px;
}
.rating-item { display: flex; flex-direction: column; gap: 5px; }
.rating-label { display: flex; justify-content: space-between; font-size: 12px; color: #606266; }
.rating-score { font-weight: bold; }
.text-success { color: #67C23A; } .text-primary { color: #409EFF; } .text-danger { color: #F56C6C; }

/* 右侧表单的原生滑块 (橙色) */
.rating-slider {
  accent-color: #E6A23C; height: 6px; width: 100%; cursor: pointer; margin-top: 5px;
}

textarea {
  width: 100%; padding: 10px; border: 1px solid #dcdfe6; border-radius: 6px; resize: vertical; outline: none; font-family: inherit; font-size: 14px; box-sizing: border-box;
}
textarea:focus { border-color: #E6A23C; }

.submit-btn {
  align-self: flex-end; padding: 10px 30px; background: #E6A23C; color: white; border: none; border-radius: 6px; font-weight: 600; cursor: pointer; transition: 0.3s; font-size: 14px;
}
.submit-btn:hover { background: #cf9236; box-shadow: 0 4px 12px rgba(230, 162, 60, 0.3); }
.submit-btn:disabled { background: #f3d19e; cursor: not-allowed; }

/* -----------------------------------------
   权重配置弹窗 (Modal)
   ----------------------------------------- */
.modal-overlay {
  position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.5); display: flex; justify-content: center; align-items: center; z-index: 999; backdrop-filter: blur(4px);
}
.modal-content {
  background: white; width: 500px; border-radius: 12px; padding: 24px; box-shadow: 0 10px 30px rgba(0,0,0,0.2); animation: fadeIn 0.3s ease;
}
.modal-header {
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;
}
.modal-header h3 { margin: 0; font-size: 20px; }
.close-btn { font-size: 24px; cursor: pointer; color: #909399; }
.modal-desc { margin-bottom: 20px; color: #606266; font-size: 14px; line-height: 1.6; }

.weight-sliders { display: flex; flex-direction: column; gap: 15px; }
.weight-item { display: flex; flex-direction: column; gap: 5px; }
.weight-label { display: flex; justify-content: space-between; font-size: 14px; font-weight: bold; color: #303133; }
.weight-val { color: #409EFF; }

.modal-footer {
  margin-top: 30px; display: flex; justify-content: flex-end; gap: 15px;
}
.cancel-btn { background: transparent; border: none; cursor: pointer; color: #909399; }
.save-btn { background: #409EFF; color: white; border: none; padding: 10px 24px; border-radius: 6px; cursor: pointer; font-weight: bold; }
.save-btn:disabled { background: #a0cfff; cursor: not-allowed; }

/* 动画与响应式 */
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
@media (max-width: 1000px) {
  .bottom-section { flex-direction: column; }
  .chart-box, .simulation-section, .feedback-section { min-width: 100%; }
}
</style>