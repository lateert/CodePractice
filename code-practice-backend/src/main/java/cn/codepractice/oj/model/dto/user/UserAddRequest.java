package cn.codepractice.oj.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/** Запрос на создание пользователя администратором. */
@Data
public class UserAddRequest implements Serializable {

    /** Имя пользователя. */
    private String userName;

    /** Логин пользователя. */
    private String userAccount;

    /** URL аватара. */
    private String userAvatar;

    /** Роль пользователя: user/admin/teacher/ban. */
    private String userRole;

    /** Пароль (если не задан, подставляется значение по умолчанию). */
    private String userPassword;

    private static final long serialVersionUID = 1L;
}