package cn.codepractice.oj.model.vo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/** Данные текущего пользователя после авторизации. */
@Data
public class LoginUserVO implements Serializable {

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

    /** Время создания учётной записи. */
    private Date createTime;

    /** Время последнего обновления учётной записи. */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}