package com.ds1.controller;

import com.ds1.dto.ApiResponse;
import com.ds1.dto.LoginRequest;
import com.ds1.dto.RegisterRequest;
import com.ds1.service.OperationLogService;
import com.ds1.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

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
        try {
            userService.register(request);
            logService.log(request.getUsername(), "REGISTER", "认证",
                    "用户注册: " + request.getUsername(), getClientIp(httpRequest), "SUCCESS");
            return ResponseEntity.ok(ApiResponse.success("注册成功", null));
        } catch (RuntimeException e) {
            logService.log(request.getUsername(), "REGISTER", "认证",
                    "注册失败: " + e.getMessage(), getClientIp(httpRequest), "FAIL");
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequest request,
                                                HttpServletRequest httpRequest) {
        try {
            Map<String, Object> result = userService.login(request);
            logService.log(request.getUsername(), "LOGIN", "认证",
                    "用户登录: " + request.getUsername(), getClientIp(httpRequest), "SUCCESS");
            return ResponseEntity.ok(ApiResponse.success("登录成功", result));
        } catch (RuntimeException e) {
            logService.log(request.getUsername(), "LOGIN", "认证",
                    "登录失败: " + e.getMessage(), getClientIp(httpRequest), "FAIL");
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, e.getMessage()));
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
