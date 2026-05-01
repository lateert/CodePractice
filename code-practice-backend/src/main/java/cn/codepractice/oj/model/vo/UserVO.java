package cn.codepractice.oj.model.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import cn.hutool.json.JSONUtil;
import cn.codepractice.oj.model.dto.question.JudgeConfig;
import cn.codepractice.oj.model.entity.Question;
import cn.codepractice.oj.model.entity.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/** Публичное представление пользователя для API. */
@Data
public class UserVO implements Serializable {

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

    private static final long serialVersionUID = 1L;


    /** Преобразует `UserVO` в сущность `User`. */
    public static User voToObj(UserVO userVO) {
        if (userVO == null) {
            return null;
        }
        User user = new User();
        BeanUtils.copyProperties(userVO, user);
        return user;
    }

    /** Преобразует сущность `User` в `UserVO`. */
    public static UserVO objToVo(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

}