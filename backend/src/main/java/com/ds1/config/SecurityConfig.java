package com.ds1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.MediaType;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .cors().and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
                // Public endpoints
                .antMatchers("/api/auth/**").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                // Admin-only endpoints
                .antMatchers(HttpMethod.DELETE, "/api/user/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/user/*/role").hasRole("ADMIN")
                .antMatchers("/api/dashboard/users").hasRole("ADMIN")
                .antMatchers("/api/logs", "/api/logs/**").hasRole("ADMIN")
                // All other requests need authentication
                .anyRequest().authenticated()
            .and()
            .exceptionHandling()
                // 未认证（token 缺失/过期，或随账号被删、被降权而失效）一律返回 401，
                // 而不是 Spring 在没有 AuthenticationEntryPoint 时的默认 403。
                // 前端 request.js 只在 401 分支清 token 并跳登录页；拿到 403 会被当成
                // 「没有访问权限」这种终态，用户就拿着一个已作废的 token 卡在页面里出不去。
                // 已认证但权限不足仍走 AccessDeniedHandler，保持 403。
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(
                            "{\"code\":401,\"message\":\"登录状态已失效，请重新登录\",\"data\":null}");
                })
            .and()
            // H2 console uses frames
            .headers().frameOptions().sameOrigin()
            .and()
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
