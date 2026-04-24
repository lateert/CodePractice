package cn.codepractice.oj.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * Запрос на смену пароля (преподаватель/студент)
 */
@Data
public class UserUpdatePasswordRequest implements Serializable {

    /**
     * Текущий пароль
     */
    private String oldPassword;

    /**
     * Новый пароль
     */
    private String newPassword;

    private static final long serialVersionUID = 1L;
}
