package com.ds1.controller;

import com.ds1.dto.ApiResponse;
import com.ds1.dto.LoginRequest;
import com.ds1.dto.RegisterRequest;
import com.ds1.exception.BusinessException;
import com.ds1.service.OperationLogService;
import com.ds1.service.UserService;
import com.ds1.util.ClientIpUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

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

    public AuthController(UserService userService, OperationLogService logService) {
        this.userService = userService;
        this.logService = logService;
    }

    /**
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@Valid @RequestBody RegisterRequest request,
                                                   HttpServletRequest httpRequest) {
        String ip = ClientIpUtil.getClientIp(httpRequest);
        try {
            userService.register(request);
            logService.log(request.getUsername(), "REGISTER", "认证",
                    "用户注册: " + request.getUsername(), ip, "SUCCESS");
            return ResponseEntity.ok(ApiResponse.success("注册成功", null));
        } catch (BusinessException e) {
            logService.log(request.getUsername(), "REGISTER", "认证",
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
        try {
            Map<String, Object> result = userService.login(request);
            logService.log(request.getUsername(), "LOGIN", "认证",
                    "用户登录: " + request.getUsername(), ip, "SUCCESS");
            return ResponseEntity.ok(ApiResponse.success("登录成功", result));
        } catch (BusinessException e) {
            logService.log(request.getUsername(), "LOGIN", "认证",
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
        logService.log(username, "LOGOUT", "认证",
                "用户登出: " + username, ClientIpUtil.getClientIp(httpRequest), "SUCCESS");
        return ResponseEntity.ok(ApiResponse.success("登出成功", null));
    }
}
