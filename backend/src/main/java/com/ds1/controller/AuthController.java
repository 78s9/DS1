package com.ds1.controller;

import com.ds1.dto.ApiResponse;
import com.ds1.dto.LoginRequest;
import com.ds1.dto.RegisterRequest;
import com.ds1.exception.BusinessException;
import com.ds1.service.OperationLogService;
import com.ds1.service.UserService;
import com.ds1.util.ClientIpUtil;
import com.ds1.util.RateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 认证接口。此 Controller 从 {@link com.ds1.config.LogAspect} 中排除，
 * 自行记录操作日志 —— 这样失败时可以记录尝试登录/注册的用户名（切面里拿到的会是 anonymous），
 * 状态码则由 {@link com.ds1.exception.GlobalExceptionHandler} 统一返回。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final OperationLogService logService;
    private final RateLimiter rateLimiter;

    /** 同一 IP + 用户名在窗口内允许的登录失败次数。 */
    @Value("${app.auth-rate-limit.login-max-failures:5}")
    private int loginMaxFailures;

    @Value("${app.auth-rate-limit.login-window-minutes:5}")
    private int loginWindowMinutes;

    /** 同一 IP 在窗口内允许的注册尝试次数（成功与失败都计入）。 */
    @Value("${app.auth-rate-limit.register-max-attempts:10}")
    private int registerMaxAttempts;

    @Value("${app.auth-rate-limit.register-window-minutes:60}")
    private int registerWindowMinutes;

    public AuthController(UserService userService, OperationLogService logService,
                          RateLimiter rateLimiter) {
        this.userService = userService;
        this.logService = logService;
        this.rateLimiter = rateLimiter;
    }

    /**
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@Valid @RequestBody RegisterRequest request,
                                                   HttpServletRequest httpRequest) {
        String ip = ClientIpUtil.getClientIp(httpRequest);
        String limitKey = "register:" + ip;
        long windowMillis = TimeUnit.MINUTES.toMillis(registerWindowMinutes);

        try {
            rateLimiter.checkAllowed(limitKey, registerMaxAttempts, windowMillis,
                    "注册过于频繁，请稍后再试");
            userService.register(request);
            rateLimiter.record(limitKey, windowMillis); // 成功与失败都计入：注册接口是刷号重灾区
            logService.logQuietly(request.getUsername(), "REGISTER", "认证",
                    "用户注册: " + request.getUsername(), ip, "SUCCESS");
            return ResponseEntity.ok(ApiResponse.success("注册成功", null));
        } catch (BusinessException e) {
            if (e.getStatus() != HttpStatus.TOO_MANY_REQUESTS.value()) {
                // 限流本身不再累计，否则被拦截期间窗口会被自己的重试无限续期
                rateLimiter.record(limitKey, windowMillis);
            }
            logService.logQuietly(request.getUsername(), "REGISTER", "认证",
                    "注册失败: " + e.getMessage(), ip, "FAIL");
            throw e; // 交给全局异常处理器统一返回状态码
        }
    }

    /**
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequest request,
                                                HttpServletRequest httpRequest) {
        String ip = ClientIpUtil.getClientIp(httpRequest);
        String limitKey = "login:" + ip + ":" + request.getUsername();
        long windowMillis = TimeUnit.MINUTES.toMillis(loginWindowMinutes);

        try {
            rateLimiter.checkAllowed(limitKey, loginMaxFailures, windowMillis,
                    "登录失败次数过多，请 " + loginWindowMinutes + " 分钟后再试");
            Map<String, Object> result = userService.login(request);
            // 成功即清零，免得正常的打字错误一直占着配额
            rateLimiter.reset(limitKey);
            logService.logQuietly(request.getUsername(), "LOGIN", "认证",
                    "用户登录: " + request.getUsername(), ip, "SUCCESS");
            return ResponseEntity.ok(ApiResponse.success("登录成功", result));
        } catch (BusinessException e) {
            if (e.getStatus() != HttpStatus.TOO_MANY_REQUESTS.value()) {
                // 只累计失败次数，且被限流时不再续期
                rateLimiter.record(limitKey, windowMillis);
            }
            logService.logQuietly(request.getUsername(), "LOGIN", "认证",
                    "登录失败: " + e.getMessage(), ip, "FAIL");
            throw e; // 交给全局异常处理器统一返回状态码
        }
    }

    /**
     * POST /api/auth/logout
     * JWT 无状态认证下登出无需服务端撤销 token（前端清除即可），
     * 这里保留接口用于记录登出操作日志。
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(java.security.Principal principal,
                                                 HttpServletRequest httpRequest) {
        String username = principal != null ? principal.getName() : "unknown";
        logService.logQuietly(username, "LOGOUT", "认证",
                "用户登出: " + username, ClientIpUtil.getClientIp(httpRequest), "SUCCESS");
        return ResponseEntity.ok(ApiResponse.success("登出成功", null));
    }
}
