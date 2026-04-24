package cn.codepractice.oj.model.vo;

import lombok.Data;

import java.util.List;

/**
 * Отчёт по успеваемости по курсу: студенты и их результаты по задачам.
 */
@Data
public class CourseProgressVO {
    private Long courseId;
    private String courseTitle;
    private CourseSummaryVO courseSummary;
    /** Список задач курса (для колонок таблицы, даже если нет студентов) */
    private List<QuestionProgressItemVO> courseQuestions;
    private List<QuestionSummaryVO> questionSummaries;
    private List<StudentProgressVO> students;
}
