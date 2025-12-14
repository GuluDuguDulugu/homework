package org.example.test1.controller;

import org.example.test1.entity.DashboardDataVO;
import org.example.test1.entity.SimulationParams;
import org.example.test1.service.MarkovAnalysisServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/evaluation")
@CrossOrigin // 允许跨域，方便Vue调试
public class EvaluationController {

    @Autowired
    private MarkovAnalysisServiceImpl markovService;

    // GET /api/evaluation/dashboard?courseId=CS101
    @GetMapping("/dashboard")
    public DashboardDataVO getDashboardData(@RequestParam String courseId) {
        // 调用核心服务
        return markovService.getAnalysisData(courseId);
    }

    // POST /api/evaluation/simulate
    // 对应文档中“对优选方案进行实证模拟”
    @PostMapping("/simulate")
    public DashboardDataVO runSimulation(@RequestBody SimulationParams params) {
        // 这里可以接收前端传来的“调整后的权重”或“假设提升的分数”
        // 重新运行 markovService 的计算逻辑并返回结果
        return markovService.simulateData(params);
    }
}