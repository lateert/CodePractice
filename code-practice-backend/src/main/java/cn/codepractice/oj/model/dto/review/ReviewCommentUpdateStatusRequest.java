package cn.codepractice.oj.model.dto.review;

import lombok.Data;

import java.io.Serializable;

/**
 * Обновление статуса комментария (OPEN / RESOLVED)
 */
@Data
public class ReviewCommentUpdateStatusRequest implements Serializable {

    /**
     * Идентификатор комментария
     */
    private Long id;

    /**
     * Новый статус: OPEN / RESOLVED
     */
    private String status;

    private static final long serialVersionUID = 1L;
}

