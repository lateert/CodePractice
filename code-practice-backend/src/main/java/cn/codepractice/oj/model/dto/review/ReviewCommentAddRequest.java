package cn.codepractice.oj.model.dto.review;

import lombok.Data;

import java.io.Serializable;

/**
 * Добавление комментария к отправке
 */
@Data
public class ReviewCommentAddRequest implements Serializable {

    /**
     * Идентификатор отправки (question_submit.id)
     */
    private Long submissionId;

    /**
     * Номер строки кода (опционально)
     */
    private Integer lineNumber;

    /**
     * Текст комментария
     */
    private String commentText;

    private static final long serialVersionUID = 1L;
}

