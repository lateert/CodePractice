package cn.codepractice.oj.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 
 *
 */
@Data
public class UserAddRequest implements Serializable {

    /**
     * 
     */
    private String userName;

    /**
     * 
     */
    private String userAccount;

    /**
     * 
     */
    private String userAvatar;

    /**
     * : user, admin
     */
    private String userRole;

    /**
     *  пароль (если не задан — по умолчанию 12345678)
     */
    private String userPassword;

    private static final long serialVersionUID = 1L;
}