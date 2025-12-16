package org.example.test1.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

public class EvaluationIndicator {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String code;
    private Double weight;

    // --- 关键修改 ---
    // 数据库列名是 current_avg_score
    // 这里的变量名改为 currentAvgScore，MyBatis-Plus 会自动完成驼峰转下划线映射
    private Double currentAvgScore;

    // (可选) 也可以加上注解显式指定，但这通常不需要，只要变量名对即可
    // @TableField("current_avg_score")
    @TableField(exist = false)
    private String description;

    public EvaluationIndicator(Long id, String name, String code, Double weight, Double currentAvgScore, String description) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.weight = weight;
        this.currentAvgScore = currentAvgScore;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getCurrentAvgScore() {
        return currentAvgScore;
    }

    public void setCurrentAvgScore(Double currentAvgScore) {
        this.currentAvgScore = currentAvgScore;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EvaluationIndicator() {
    }
}