package com.ds1.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 跨域配置 —— 只放开显式配置的来源白名单。
 *
 * <p>原先是 {@code allowedOriginPatterns("*")} + {@code allowCredentials(true)}：
 * 鉴权走 {@code Authorization} 头而非 Cookie，实际危害被削弱，但这是教科书式的错误配置，
 * 一旦以后改用 Cookie 承载凭证就会直接变成任意站点可读取用户数据的漏洞。
 */
@Configuration
public class CorsConfig {

    private static final Logger log = LoggerFactory.getLogger(CorsConfig.class);

    /**
     * 允许跨域的来源，逗号分隔（对应配置项 {@code app.cors.allowed-origins}）。
     */
    @Value("${app.cors.allowed-origins:}")
    private String allowedOrigins;

    @Bean
    public CorsFilter corsFilter() {
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .collect(Collectors.toList());

        if (origins.isEmpty()) {
            log.warn("未配置 app.cors.allowed-origins，所有跨域请求都会被拒绝。"
                    + "如前端与后端不同源，请设置环境变量 CORS_ALLOWED_ORIGINS。");
        } else {
            log.info("CORS 允许的来源: {}", origins);
        }

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(origins);
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
