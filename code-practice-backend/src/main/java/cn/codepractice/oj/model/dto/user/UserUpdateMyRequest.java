package cn.codepractice.oj.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/** Запрос на обновление пользователя. */
@Data
public class UserUpdateMyRequest implements Serializable {

    private String userName;

    private String userAvatar;

    private String userProfile;

    private static final long serialVersionUID = 1L;
}