package cn.codepractice.oj.constant;

/** Имена ролей и ключ состояния входа в сессии. */
public interface UserConstant {

    /** Атрибут сессии: текущий пользователь после входа. */
    String USER_LOGIN_STATE = "user_login";

    /** Роль по умолчанию (студент). */
    String DEFAULT_ROLE = "user";

    /** Полный административный доступ. */
    String ADMIN_ROLE = "admin";

    /** Преподаватель: курсы, проверки, отчёты без прав админа. */
    String TEACHER_ROLE = "teacher";

    /** Учётная запись заблокирована. */
    String BAN_ROLE = "ban";
}
