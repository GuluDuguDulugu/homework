package org.example.test1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.test1.entity.LessonFeedbackRecordEntity;

import java.util.Map;

@Mapper
public interface LessonFeedbackRecordMapper extends BaseMapper<LessonFeedbackRecordEntity> {

    // 功能 1: 计算该教师的历史总平均分 (用于宏观评价)
    @Select("SELECT " +
            "AVG(score_quality) as avgQuality, " +
            "AVG(score_attitude) as avgAttitude, " +
            "AVG(score_content) as avgContent, " +
            "AVG(score_method) as avgMethod, " +
            "AVG(score_effect) as avgEffect " +
            "FROM lesson_feedback_record WHERE teacher_id = #{teacherId}")
    Map<String, Double> calculateGlobalAverages(String teacherId);

    // 功能 2: 计算该教师【最近一次课】的平均分 (用于捕捉实时状态波动)
    // 逻辑：找到该教师最近的一个 lesson_date，算那一天所有学生的均分
    @Select("SELECT " +
            "AVG(score_quality) as avgQuality, " +
            "AVG(score_attitude) as avgAttitude, " +
            "AVG(score_content) as avgContent, " +
            "AVG(score_method) as avgMethod, " +
            "AVG(score_effect) as avgEffect " +
            "FROM lesson_feedback_record " +
            "WHERE teacher_id = #{teacherId} " +
            "AND lesson_date = (SELECT MAX(lesson_date) FROM lesson_feedback_record WHERE teacher_id = #{teacherId})")
    Map<String, Double> calculateLatestLessonAverages(String teacherId);
}