package org.example.test1.controller;

import org.example.test1.entity.DashboardDataVO;
import org.example.test1.entity.LessonFeedbackRecordEntity;
import org.example.test1.entity.SimulationParams;
import org.example.test1.service.MarkovAnalysisServiceImpl;
import org.example.test1.mapper.LessonFeedbackRecordMapper; // 引入Mapper
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/evaluation")
@CrossOrigin
public class EvaluationController {

    @Autowired
    private MarkovAnalysisServiceImpl markovService;

    @Autowired
    private LessonFeedbackRecordMapper lessonMapper; // 直接注入Mapper简单处理入库

    // GET /api/evaluation/dashboard
    @GetMapping("/dashboard")
    public DashboardDataVO getDashboardData(@RequestParam String courseId) {
        return markovService.getAnalysisData(courseId);
    }

    // POST /api/evaluation/simulate
    @PostMapping("/simulate")
    public DashboardDataVO runSimulation(@RequestBody SimulationParams params) {
        return markovService.simulateData(params);
    }

    @PostMapping("/submit-lesson")
    public String submitLessonFeedback(@RequestBody LessonFeedbackRecordEntity record) {
        // 1. 补全基础信息
        if(record.getTeacherId() == null) record.setTeacherId("T001");
        if(record.getStudentId() == null) record.setStudentId("S_ANON_" + System.currentTimeMillis() % 1000); // 匿名ID
        if(record.getLessonDate() == null) record.setLessonDate(LocalDate.now());
        if(record.getLessonIndex() == null) record.setLessonIndex(1);
        if(record.getCourseName() == null) record.setCourseName("default-course");
        record.setCreateTime(LocalDateTime.now());

        // 2. 保存到数据库
        lessonMapper.insert(record);

        // 3. (可选) 触发Service重新计算均分，这里为了简单直接返回
        return "提交成功！感谢您的反馈。";
    }

    @PostMapping("/weights")
    public String updateWeights(@RequestBody Map<String, Double> weights) {
        // 校验：权重之和建议为 1.0 (这里不做强制校验，由前端控制)
        markovService.updateSystemWeights(weights);
        return "权重配置已更新，系统评价标准已重置。";
    }
}