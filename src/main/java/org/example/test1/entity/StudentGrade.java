package org.example.test1.entity;


import java.time.LocalDate;


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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Integer getTimeStep() {
        return timeStep;
    }

    public void setTimeStep(Integer timeStep) {
        this.timeStep = timeStep;
    }

    public LocalDate getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDate recordDate) {
        this.recordDate = recordDate;
    }

    public StudentGrade(Long id, String studentId, String courseId, Double score, Integer timeStep, LocalDate recordDate) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.score = score;
        this.timeStep = timeStep;
        this.recordDate = recordDate;
    }
    public StudentGrade() {
    }
}