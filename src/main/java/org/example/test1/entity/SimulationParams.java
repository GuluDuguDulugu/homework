package org.example.test1.entity;

import lombok.Data;

@Data
public class SimulationParams {
    // 对应前端的 "interaction" 滑块 (0-100)
    private Integer interaction;

    // 对应前端的 "tutoring" 滑块 (0-100)
    private Integer tutoring;
}