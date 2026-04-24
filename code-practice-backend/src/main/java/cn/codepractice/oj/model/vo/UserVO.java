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

/**
 * （）
 *
 */
@Data
public class UserVO implements Serializable {

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

    /**
     * 
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;


    /**
     * 
     *
     * @param userVO
     * @return
     */
    public static User voToObj(UserVO userVO) {
        if (userVO == null) {
            return null;
        }
        User user = new User();
        BeanUtils.copyProperties(userVO, user);
        return user;
    }

    /**
     * 
     *
     * @param user
     * @return
     */
    public static UserVO objToVo(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

}