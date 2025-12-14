package org.example.test1.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("evaluation_indicator")
public class EvaluationIndicator {
    private Long id;

    // 指标名称：如 "教学方法", "教学态度"
    private String name;

    // 指标代码：quality, attitude, content, method, effect
    private String code;

    // AHP计算出的权重 (0.0 - 1.0)
    // 对应文档中“利用层次分析法计算各级指标权重”
    private Double weight;

    // 当前模糊评分 (0-100)，用于雷达图展示
    private Double currentScore;
}