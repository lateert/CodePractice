package cn.codepractice.oj.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 
 *
 */
@Data
public class UserUpdateRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 
     */
    private String userName;

    /**
     * 
     */
    private String userAvatar;

    /**
     * 
     */
    private String userProfile;

    /**
     * ：user/admin/ban
     */
    private String userRole;

    private static final long serialVersionUID = 1L;
}