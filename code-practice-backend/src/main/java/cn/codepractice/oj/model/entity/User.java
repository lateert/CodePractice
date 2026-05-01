package cn.codepractice.oj.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/** Сущность пользователя платформы. */
@TableName(value = "user")
@Data
public class User implements Serializable {

    /** Идентификатор пользователя. */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** Логин пользователя. */
    private String userAccount;

    /** Хэш пароля. */
    private String userPassword;

    /** UnionID внешнего провайдера (если используется). */
    private String unionId;

    /** OpenID внешнего провайдера (если используется). */
    private String mpOpenId;

    /** Отображаемое имя. */
    private String userName;

    /** URL аватара. */
    private String userAvatar;

    /** Текст профиля пользователя. */
    private String userProfile;

    /** Роль пользователя: user/admin/teacher/ban. */
    private String userRole;

    /** Время создания записи. */
    private Date createTime;

    /** Время последнего обновления записи. */
    private Date updateTime;

    /** Признак логического удаления. */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}