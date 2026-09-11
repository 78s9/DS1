package com.ds1.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 启动期校验 JWT 密钥。
 *
 * <p>密钥由环境变量 {@code JWT_SECRET} 注入，不再硬编码进仓库 —— 硬编码并提交的密钥
 * 意味着任何拿到这份代码的人都能签发 {@code role=ADMIN} 的 token，等于管理员权限公开。
 *
 * <p>这里在启动期直接失败而不是打条警告：一个空密钥或弱密钥的服务能正常启动、
 * 悄悄跑上生产，才是最危险的情况。
 */
@Component
public class JwtSecretValidator {

    private static final Logger log = LoggerFactory.getLogger(JwtSecretValidator.class);

    /** HS256 要求密钥不少于 256 bits，即 32 字节。 */
    private static final int MIN_SECRET_BYTES = 32;

    /** application.yml 里 dev profile 的兜底密钥前缀，用于识别「还在用默认密钥」。 */
    private static final String DEV_SECRET_PREFIX = "ds1-dev-only";

    private final String secret;
    private final Environment environment;

    public JwtSecretValidator(@Value("${jwt.secret:}") String secret, Environment environment) {
        this.secret = secret;
        this.environment = environment;
    }

    @PostConstruct
    public void validate() {
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalStateException(
                    "JWT 密钥未配置。请设置环境变量 JWT_SECRET（至少 " + MIN_SECRET_BYTES + " 字节）。");
        }

        int length = secret.getBytes(StandardCharsets.UTF_8).length;
        if (length < MIN_SECRET_BYTES) {
            throw new IllegalStateException(
                    "JWT 密钥强度不足：当前 " + length + " 字节，HS256 要求至少 " + MIN_SECRET_BYTES + " 字节。");
        }

        if (secret.startsWith(DEV_SECRET_PREFIX)) {
            if (!isDevProfileActive()) {
                throw new IllegalStateException(
                        "检测到开发用默认 JWT 密钥，但当前未启用 dev profile。"
                        + "生产环境必须通过环境变量 JWT_SECRET 配置独立密钥。");
            }
            log.warn("正在使用内置的开发用 JWT 密钥，仅限本地开发。生产环境请设置环境变量 JWT_SECRET。");
            return;
        }

        log.info("JWT 密钥校验通过（来自 {} 配置）。",
                System.getenv("JWT_SECRET") != null ? "环境变量 JWT_SECRET" : "配置项 jwt.secret");
    }

    private boolean isDevProfileActive() {
        return Arrays.asList(environment.getActiveProfiles()).contains("dev");
    }
}
