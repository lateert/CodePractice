package cn.codepractice.oj.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {

    /**
     * 
     */
    String mustRole() default "";

    /**
     *  из перечисленных ролей (например admin или teacher)
     */
    String[] mustRoleAny() default {};
}

