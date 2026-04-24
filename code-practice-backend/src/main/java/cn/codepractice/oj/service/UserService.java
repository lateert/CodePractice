package cn.codepractice.oj.service;

import cn.codepractice.oj.model.dto.user.UserQueryRequest;
import cn.codepractice.oj.model.entity.User;
import cn.codepractice.oj.model.vo.LoginUserVO;
import cn.codepractice.oj.model.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 
 *
 */
public interface UserService extends IService<User> {

    /**
     * 
     *
     * @param userAccount   
     * @param userPassword  
     * @param checkPassword 
     * @return  id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 
     *
     * @param userAccount  
     * @param userPassword 
     * @param request
     * @return 
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * （）
     *
     * @param request
     * @return
     */
    User getLoginUserPermitNull(HttpServletRequest request);

    /**
     * 
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 
     *
     * @param user
     * @return
     */
    boolean isAdmin(User user);

    /**
     *  преподаватель
     */
    boolean isTeacher(User user);

    /**
     *  администратор или преподаватель (доступ к курсам, задачам, отправкам, статистике)
     */
    boolean isAdminOrTeacher(User user);

    /**
     * 
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 
     *
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 
     *
     * @param userList
     * @return
     */
    List<UserVO> getUserVO(List<User> userList);

    /**
     * 
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * Смена пароля текущего пользователя (преподаватель/студент).
     *
     * @param userId       id пользователя
     * @param oldPassword  текущий пароль
     * @param newPassword  новый пароль
     */
    void updatePassword(Long userId, String oldPassword, String newPassword);

}
