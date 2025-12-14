package org.example.test1.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;

@Data
@TableName("student_grade")
public class StudentGrade {
    private Long id;
    private String studentId;
    private String courseId;

    // 成绩数值
    private Double score;

    // 时间步：第1次作业, 期中, 第3次作业... (用于定义马尔科夫链的 t, t+1)
    private Integer timeStep;

    // 记录日期
    private LocalDate recordDate;
}