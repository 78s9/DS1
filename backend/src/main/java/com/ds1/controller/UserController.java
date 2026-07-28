package com.ds1.controller;

import com.ds1.dto.ApiResponse;
import com.ds1.entity.User;
import com.ds1.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET /api/user/me — Get current user info (requires JWT)
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser(Principal principal) {
        User user = userService.findByUsername(principal.getName());

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("email", user.getEmail());
        userInfo.put("role", user.getRole());
        userInfo.put("createdAt", user.getCreatedAt());

        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }
}
