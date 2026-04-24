package cn.codepractice.oj.unit;

import cn.codepractice.oj.common.ErrorCode;
import cn.codepractice.oj.exception.BusinessException;
import cn.codepractice.oj.mapper.UserMapper;
import cn.codepractice.oj.model.entity.User;
import cn.codepractice.oj.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Модульные проверки регистрации пользователя (JUnit 5 + Mockito, подмена {@link UserMapper}).
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplRegisterTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void bindBaseMapper() {
        ReflectionTestUtils.setField(userService, "baseMapper", userMapper);
    }

    @Test
    void userRegister_blankAccount_throws() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> userService.userRegister("", "password1", "password1"));
        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), ex.getCode());
    }

    @Test
    void userRegister_shortAccount_throws() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> userService.userRegister("abc", "password1", "password1"));
        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), ex.getCode());
    }

    @Test
    void userRegister_shortPassword_throws() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> userService.userRegister("user1", "short", "short"));
        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), ex.getCode());
    }

    @Test
    void userRegister_passwordMismatch_throws() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> userService.userRegister("user1", "password1", "password2"));
        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), ex.getCode());
    }

    @Test
    void userRegister_duplicate_throws() {
        when(userMapper.selectCount(any())).thenReturn(1L);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> userService.userRegister("user1", "password12", "password12"));
        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), ex.getCode());
    }

    @Test
    void userRegister_success_returnsId() {
        when(userMapper.selectCount(any())).thenReturn(0L);
        when(passwordEncoder.encode("password12")).thenReturn("hashed-password");
        when(userMapper.insert(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(42L);
            return 1;
        });
        long id = userService.userRegister("user1", "password12", "password12");
        assertEquals(42L, id);
    }
}
