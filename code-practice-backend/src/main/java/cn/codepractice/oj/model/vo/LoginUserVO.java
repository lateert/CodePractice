package cn.codepractice.oj.model.vo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * （）
 *
 **/
@Data
public class LoginUserVO implements Serializable {

    /**
     *  id
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

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}