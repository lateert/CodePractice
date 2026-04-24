package cn.codepractice.oj.model.dto.user;

import cn.codepractice.oj.common.PageRequest;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * id
     */
    private String unionId;

    /**
     * openId
     */
    private String mpOpenId;

    /**
     * （，）
     */
    private String userAccount;

    /**
     * 
     */
    private String userName;

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