package cn.codepractice.oj.unit;

import cn.codepractice.oj.common.ErrorCode;
import cn.codepractice.oj.exception.BusinessException;
import cn.codepractice.oj.mapper.UserMapper;
import cn.codepractice.oj.model.dto.user.UserQueryRequest;
import cn.codepractice.oj.model.entity.User;
import cn.codepractice.oj.model.vo.LoginUserVO;
import cn.codepractice.oj.service.impl.UserServiceImpl;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Дополнительные модульные проверки {@link UserServiceImpl}: вход, выход, запросы.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplMoreUnitTest {

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
    void userLogin_blank_throws() {
        HttpServletRequest req = org.mockito.Mockito.mock(HttpServletRequest.class);
        assertThrows(BusinessException.class, () -> userService.userLogin("", "password12", req));
    }

    @Test
    void userLogin_userNotFound_throws() {
        when(userMapper.selectOne(org.mockito.ArgumentMatchers.<Wrapper<User>>any())).thenReturn(null);
        HttpServletRequest req = org.mockito.Mockito.mock(HttpServletRequest.class);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> userService.userLogin("user1", "password12", req));
        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), ex.getCode());
    }

    @Test
    void userLogin_success_setsSession() {
        User u = new User();
        u.setId(9L);
        u.setUserAccount("user1");
        u.setUserName("n");
        u.setUserPassword("hashed-password");
        when(userMapper.selectOne(org.mockito.ArgumentMatchers.<Wrapper<User>>any())).thenReturn(u);
        when(passwordEncoder.matches("password12", "hashed-password")).thenReturn(true);
        HttpServletRequest req = org.mockito.Mockito.mock(HttpServletRequest.class);

        LoginUserVO vo = userService.userLogin("user1", "password12", req);
        assertNotNull(vo);
        assertEquals(9L, vo.getId());
    }

    @Test
    void userLogout_ok() {
        HttpServletRequest req = org.mockito.Mockito.mock(HttpServletRequest.class);
        assertEquals(true, userService.userLogout(req));
        assertEquals(null, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void getQueryWrapper_null_throws() {
        assertThrows(BusinessException.class, () -> userService.getQueryWrapper(null));
    }

    @Test
    void getQueryWrapper_withFilters() {
        UserQueryRequest r = new UserQueryRequest();
        r.setUserAccount("a");
        r.setUserRole("user");
        r.setSortField("userName");
        r.setSortOrder("ascend");
        QueryWrapper<User> w = userService.getQueryWrapper(r);
        assertNotNull(w);
    }
}
