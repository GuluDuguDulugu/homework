package org.example.test1.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 对应数据库表: lesson_feedback_record
 * 记录每一堂课、每一个学生的详细评分
 */

@TableName("lesson_feedback_record")
public class LessonFeedbackRecordEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String studentId;
    private String teacherId;
    private String courseName;  // 新增：课程名
    private LocalDate lessonDate; // 新增：上课日期
    private Integer lessonIndex;  // 新增：第几节课

    // 五维评分
    private Integer scoreQuality;
    private Integer scoreAttitude;
    private Integer scoreContent;
    private Integer scoreMethod;
    private Integer scoreEffect;

    private String commentText; // 新增：主观评价
    private LocalDateTime createTime;

    public LessonFeedbackRecordEntity(Long id, String studentId, String teacherId, String courseName, LocalDate lessonDate, Integer lessonIndex, Integer scoreQuality, Integer scoreAttitude, Integer scoreContent, Integer scoreMethod, Integer scoreEffect, String commentText, LocalDateTime createTime) {
        this.id = id;
        this.studentId = studentId;
        this.teacherId = teacherId;
        this.courseName = courseName;
        this.lessonDate = lessonDate;
        this.lessonIndex = lessonIndex;
        this.scoreQuality = scoreQuality;
        this.scoreAttitude = scoreAttitude;
        this.scoreContent = scoreContent;
        this.scoreMethod = scoreMethod;
        this.scoreEffect = scoreEffect;
        this.commentText = commentText;
        this.createTime = createTime;
    }

    public LessonFeedbackRecordEntity() {}

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

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public LocalDate getLessonDate() {
        return lessonDate;
    }

    public void setLessonDate(LocalDate lessonDate) {
        this.lessonDate = lessonDate;
    }

    public Integer getLessonIndex() {
        return lessonIndex;
    }

    public void setLessonIndex(Integer lessonIndex) {
        this.lessonIndex = lessonIndex;
    }

    public Integer getScoreQuality() {
        return scoreQuality;
    }

    public void setScoreQuality(Integer scoreQuality) {
        this.scoreQuality = scoreQuality;
    }

    public Integer getScoreAttitude() {
        return scoreAttitude;
    }

    public void setScoreAttitude(Integer scoreAttitude) {
        this.scoreAttitude = scoreAttitude;
    }

    public Integer getScoreContent() {
        return scoreContent;
    }

    public void setScoreContent(Integer scoreContent) {
        this.scoreContent = scoreContent;
    }

    public Integer getScoreMethod() {
        return scoreMethod;
    }

    public void setScoreMethod(Integer scoreMethod) {
        this.scoreMethod = scoreMethod;
    }

    public Integer getScoreEffect() {
        return scoreEffect;
    }

    public void setScoreEffect(Integer scoreEffect) {
        this.scoreEffect = scoreEffect;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}