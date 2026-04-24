package cn.codepractice.oj.aop;

import cn.codepractice.oj.annotation.AuthCheck;
import cn.codepractice.oj.common.ErrorCode;
import cn.codepractice.oj.exception.BusinessException;
import cn.codepractice.oj.model.entity.User;
import cn.codepractice.oj.model.enums.UserRoleEnum;
import cn.codepractice.oj.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 *  AOP
 *
 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 
     *
     * @param joinPoint
     * @param authCheck
     * @return
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 
        User loginUser = userService.getLoginUser(request);
        String userRole = loginUser.getUserRole();
        // ，
        if (UserRoleEnum.BAN.getValue().equals(userRole)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // mustRoleAny: разрешить, если роль пользователя в списке
        String[] roleAny = authCheck.mustRoleAny();
        if (roleAny != null && roleAny.length > 0) {
            boolean allowed = false;
            for (String r : roleAny) {
                if (r != null && r.equals(userRole)) {
                    allowed = true;
                    break;
                }
            }
            if (!allowed) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        } else if (StringUtils.isNotBlank(authCheck.mustRole())) {
            String mustRole = authCheck.mustRole();
            if (!mustRole.equals(userRole)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
        // ，
        return joinPoint.proceed();
    }
}

