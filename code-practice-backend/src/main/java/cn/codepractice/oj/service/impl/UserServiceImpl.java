package cn.codepractice.oj.service.impl;

import cn.codepractice.oj.common.ErrorCode;
import cn.codepractice.oj.constant.CommonConstant;
import cn.codepractice.oj.exception.BusinessException;
import cn.codepractice.oj.mapper.UserMapper;
import cn.codepractice.oj.model.dto.user.UserQueryRequest;
import cn.codepractice.oj.model.entity.User;
import cn.codepractice.oj.model.enums.UserRoleEnum;
import cn.codepractice.oj.model.vo.LoginUserVO;
import cn.codepractice.oj.model.vo.UserVO;
import cn.codepractice.oj.security.JwtUserPrincipal;
import cn.codepractice.oj.utils.SqlUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.codepractice.oj.service.UserService;
import jakarta.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/** Реализация сервиса пользователей (регистрация, вход, JWT-контекст). */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * Legacy-соль для обратной совместимости с ранее сохраненными MD5-хэшами.
     * Используется только для одноразовой миграции на BCrypt при первом входе.
     */
    private static final String LEGACY_PASSWORD_SALT = "codePractice";

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Не заполнены обязательные поля");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Логин слишком короткий (не менее 4 символов)");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Пароль слишком короткий (не менее 8 символов)");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Пароли не совпадают");
        }
        synchronized (userAccount.intern()) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_account", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "Логин уже занят");
            }
            String encryptPassword = passwordEncoder.encode(userPassword);
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setUserName(userAccount);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Ошибка регистрации (база данных)");
            }
            return user.getId();
        }
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Не заполнены обязательные поля");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Некорректный логин");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Некорректный пароль");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        User user = this.baseMapper.selectOne(queryWrapper);
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Неверный логин или пароль");
        }
        if (!matchesPasswordOrLegacyAndUpgrade(user, userPassword)) {
            log.info("user login failed, password mismatch");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Неверный логин или пароль");
        }
        return this.getLoginUserVO(user);
    }

    /**
     * Поддержка поэтапной миграции: сначала BCrypt, затем legacy MD5+salt.
     * Если legacy-хэш совпал, сразу обновляем пароль пользователя на BCrypt.
     */
    private boolean matchesPasswordOrLegacyAndUpgrade(User user, String rawPassword) {
        String storedPassword = user.getUserPassword();
        if (StringUtils.isBlank(storedPassword)) {
            return false;
        }
        if (passwordEncoder.matches(rawPassword, storedPassword)) {
            return true;
        }
        String legacyMd5 = DigestUtils.md5DigestAsHex((LEGACY_PASSWORD_SALT + rawPassword).getBytes());
        if (!legacyMd5.equalsIgnoreCase(storedPassword)) {
            return false;
        }
        user.setUserPassword(passwordEncoder.encode(rawPassword));
        boolean updated = this.updateById(user);
        if (!updated) {
            log.warn("legacy password matched but bcrypt upgrade failed, userId={}", user.getId());
        }
        return true;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof JwtUserPrincipal)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        User currentUser = this.getById(principal.getUserId());
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    @Override
    public User getLoginUserPermitNull(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof JwtUserPrincipal)) {
            return null;
        }
        JwtUserPrincipal principal = (JwtUserPrincipal) authentication.getPrincipal();
        return this.getById(principal.getUserId());
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        return isAdmin(getLoginUserPermitNull(request));
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    @Override
    public boolean isTeacher(User user) {
        return user != null && UserRoleEnum.TEACHER.getValue().equals(user.getUserRole());
    }

    @Override
    public boolean isAdminOrTeacher(User user) {
        return isAdmin(user) || isTeacher(user);
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        return true;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Параметры запроса пусты");
        }
        Long id = userQueryRequest.getId();
        String unionId = userQueryRequest.getUnionId();
        String mpOpenId = userQueryRequest.getMpOpenId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(unionId), "union_id", unionId);
        queryWrapper.eq(StringUtils.isNotBlank(mpOpenId), "mp_open_id", mpOpenId);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "user_role", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userAccount), "user_account", userAccount);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "user_profile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(userName), "user_name", userName);
        boolean asc = CommonConstant.SORT_ORDER_ASC.equals(StringUtils.trimToEmpty(sortOrder));
        String sortColumn = toUserOrderColumn(sortField);
        queryWrapper.orderBy(sortColumn != null, asc, sortColumn);
        return queryWrapper;
    }

    /** Maps API camelCase sortField to MySQL column name for ORDER BY. */
    private static String toUserOrderColumn(String sortField) {
        if (!SqlUtils.validSortField(sortField)) {
            return null;
        }
        switch (sortField) {
            case "id":
                return "id";
            case "userAccount":
                return "user_account";
            case "userName":
                return "user_name";
            case "userRole":
                return "user_role";
            case "createTime":
                return "create_time";
            case "updateTime":
                return "update_time";
            default:
                return null;
        }
    }

    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        if (userId == null || StringUtils.isAnyBlank(oldPassword, newPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Параметры пусты");
        }
        if (newPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Новый пароль не менее 8 символов");
        }
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Пользователь не найден");
        }
        if (!passwordEncoder.matches(oldPassword, user.getUserPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Текущий пароль неверен");
        }
        user.setUserPassword(passwordEncoder.encode(newPassword));
        boolean ok = updateById(user);
        if (!ok) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Не удалось обновить пароль");
        }
    }
}
