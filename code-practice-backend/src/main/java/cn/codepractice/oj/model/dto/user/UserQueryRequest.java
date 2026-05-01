package cn.codepractice.oj.model.dto.user;

import cn.codepractice.oj.common.PageRequest;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** Параметры фильтрации и пагинации списка пользователей. */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
    /** Идентификатор пользователя. */
    private Long id;

    /** UnionID внешнего провайдера. */
    private String unionId;

    /** OpenID внешнего провайдера. */
    private String mpOpenId;

    /** Подстрока в логине пользователя. */
    private String userAccount;

    /** Подстрока в имени пользователя. */
    private String userName;

    /** Подстрока в профиле пользователя. */
    private String userProfile;

    /** Роль пользователя: user/admin/teacher/ban. */
    private String userRole;

    private static final long serialVersionUID = 1L;
}