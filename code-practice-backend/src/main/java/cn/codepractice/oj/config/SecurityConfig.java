package cn.codepractice.oj.config;

import cn.codepractice.oj.constant.UserConstant;
import cn.codepractice.oj.security.JwtAuthenticationFilter;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Resource
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/doc.html",
                                "/webjars/**",
                                "/knife4j/**",
                                "/favicon.ico"
                        ).permitAll()
                        .requestMatchers(
                                "/user/login",
                                "/v1/user/login",
                                "/user/register",
                                "/v1/user/register",
                                "/user/get/login",
                                "/v1/user/get/login",
                                "/user/refresh",
                                "/v1/user/refresh",
                                "/oauth/**",
                                "/v1/oauth/**",
                                "/integration/**",
                                "/v1/integration/**",
                                "/course/list/public",
                                "/v1/course/list/public",
                                "/course/list/page",
                                "/v1/course/list/page",
                                "/question/list/page/vo",
                                "/v1/question/list/page/vo",
                                "/question/get/vo",
                                "/v1/question/get/vo",
                                "/question/codeTemplate",
                                "/v1/question/codeTemplate"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/course/question/list", "/v1/course/question/list")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/file/local/**", "/v1/file/local/**")
                        .permitAll()
                        .requestMatchers(
                                "/user/add",
                                "/v1/user/add",
                                "/user/delete",
                                "/v1/user/delete",
                                "/user/update",
                                "/v1/user/update",
                                "/user/get",
                                "/v1/user/get",
                                "/user/get/vo",
                                "/v1/user/get/vo",
                                "/user/list/page",
                                "/v1/user/list/page"
                        ).hasRole(UserConstant.ADMIN_ROLE.toUpperCase())
                        .requestMatchers(
                                "/statistics/**",
                                "/v1/statistics/**",
                                "/question/add",
                                "/v1/question/add",
                                "/question/delete",
                                "/v1/question/delete",
                                "/question/update",
                                "/v1/question/update",
                                "/question/list/page",
                                "/v1/question/list/page",
                                "/course/update",
                                "/v1/course/update",
                                "/course/publish",
                                "/v1/course/publish",
                                "/course/delete",
                                "/v1/course/delete",
                                "/course/question/add",
                                "/v1/course/question/add",
                                "/course/question/courses-by-questions",
                                "/v1/course/question/courses-by-questions",
                                "/question/solution/add",
                                "/v1/question/solution/add"
                        ).hasAnyRole(
                                UserConstant.ADMIN_ROLE.toUpperCase(),
                                UserConstant.TEACHER_ROLE.toUpperCase())
                        .requestMatchers(
                                "/course/enroll",
                                "/v1/course/enroll",
                                "/course/enroll/**",
                                "/v1/course/enroll/**",
                                "/question/submit",
                                "/v1/question/submit",
                                "/question/submit/**",
                                "/v1/question/submit/**",
                                "/question_submit",
                                "/v1/question_submit",
                                "/question_submit/**",
                                "/v1/question_submit/**"
                        ).authenticated()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
