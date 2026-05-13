package cn.codepractice.oj.controller;

import cn.codepractice.oj.annotation.AuthCheck;
import cn.codepractice.oj.common.BaseResponse;
import cn.codepractice.oj.common.DeleteRequest;
import cn.codepractice.oj.common.ErrorCode;
import cn.codepractice.oj.common.ResultUtils;
import cn.codepractice.oj.constant.UserConstant;
import cn.codepractice.oj.exception.BusinessException;
import cn.codepractice.oj.exception.ThrowUtils;
import cn.codepractice.oj.model.entity.User;
import cn.codepractice.oj.model.vo.LoginUserVO;
import cn.codepractice.oj.model.vo.UserVO;
import cn.codepractice.oj.security.AuthTokenFacade;
import cn.codepractice.oj.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.codepractice.oj.model.dto.user.UserAddRequest;
import cn.codepractice.oj.model.dto.user.UserLoginRequest;
import cn.codepractice.oj.model.dto.user.UserQueryRequest;
import cn.codepractice.oj.model.dto.user.UserRegisterRequest;
import cn.codepractice.oj.model.dto.user.UserRefreshRequest;
import cn.codepractice.oj.model.dto.user.UserUpdateMyRequest;
import cn.codepractice.oj.model.dto.user.UserUpdatePasswordRequest;
import cn.codepractice.oj.model.dto.user.UserUpdateRequest;

import java.util.List;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** API пользователей: аутентификация, админские операции и обновление своего профиля. */
@RestController
@RequestMapping({"/user", "/v1/user"})
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private AuthTokenFacade authTokenFacade;

    @Resource
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest,
                                               HttpServletRequest request,
                                               HttpServletResponse response) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        User loginUser = userService.getById(loginUserVO.getId());
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Пользователь не найден");
        }
        authTokenFacade.issueTokens(loginUser, response);
        return ResultUtils.success(loginUserVO);
    }

    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request, HttpServletResponse response) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        authTokenFacade.logout(request, response);
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * Обновление access-токена по refresh-токену.
     */
    @PostMapping("/refresh")
    public BaseResponse<LoginUserVO> refresh(@RequestBody(required = false) UserRefreshRequest refreshRequest,
                                             HttpServletRequest request,
                                             HttpServletResponse response) {
        String refreshToken = refreshRequest == null ? null : refreshRequest.getRefreshToken();
        User user = authTokenFacade.refresh(request, response, refreshToken);
        return ResultUtils.success(userService.getLoginUserVO(user));
    }

    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(user));
    }

    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
        if (userAddRequest == null || StringUtils.isBlank(userAddRequest.getUserAccount())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isBlank(userAddRequest.getUserPassword())
                || userAddRequest.getUserPassword().length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Пароль обязателен и должен быть не короче 8 символов");
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        String rawPassword = userAddRequest.getUserPassword();
        String encryptPassword = passwordEncoder.encode(rawPassword);
        user.setUserPassword(encryptPassword);
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());
    }

    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest,
            HttpServletRequest request) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /** Сущность пользователя по id (только админ). */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /** DTO пользователя по id. */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id, HttpServletRequest request) {
        BaseResponse<User> response = getUserById(id, request);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }

    /** Постраничный список сущностей (админ). */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest,
            HttpServletRequest request) {
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        return ResultUtils.success(userPage);
    }

    /** Постраничный список DTO; ограничение размера страницы. */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest,
            HttpServletRequest request) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        // защита от слишком крупных выборок в публичном списке VO
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
        List<UserVO> userVO = userService.getUserVO(userPage.getRecords());
        userVOPage.setRecords(userVO);
        return ResultUtils.success(userVOPage);
    }

    /** Обновление полей текущего пользователя по сессии. */
    @PostMapping("/update/my")
    public BaseResponse<Boolean> updateMyUser(@RequestBody UserUpdateMyRequest userUpdateMyRequest,
            HttpServletRequest request) {
        if (userUpdateMyRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        User user = new User();
        BeanUtils.copyProperties(userUpdateMyRequest, user);
        user.setId(loginUser.getId());
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * Смена пароля (преподаватель/студент).
     */
    @PostMapping("/update/my/password")
    public BaseResponse<Boolean> updateMyPassword(@RequestBody UserUpdatePasswordRequest request,
            HttpServletRequest httpRequest) {
        if (request == null || StringUtils.isAnyBlank(request.getOldPassword(), request.getNewPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(httpRequest);
        userService.updatePassword(loginUser.getId(), request.getOldPassword(), request.getNewPassword());
        return ResultUtils.success(true);
    }
}
