package cn.codepractice.oj.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/** Запрос на обновление пользователя администратором. */
@Data
public class UserUpdateRequest implements Serializable {
    /** Идентификатор пользователя. */
    private Long id;

    /** Имя пользователя. */
    private String userName;

    /** URL аватара. */
    private String userAvatar;

    /** Текст профиля пользователя. */
    private String userProfile;

    /** Роль пользователя: user/admin/teacher/ban. */
    private String userRole;

    private static final long serialVersionUID = 1L;
}