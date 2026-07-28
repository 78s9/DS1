package com.ds1.controller;

import com.ds1.dto.ApiResponse;
import com.ds1.dto.LoginRequest;
import com.ds1.dto.RegisterRequest;
import com.ds1.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            userService.register(request);
            return ResponseEntity.ok(ApiResponse.success("注册成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(ApiResponse.success("登录成功",
                    userService.login(request)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, e.getMessage()));
        }
    }
}
