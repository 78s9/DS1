package com.ds1.controller;

import com.ds1.dto.ApiResponse;
import com.ds1.dto.ChangePasswordRequest;
import com.ds1.dto.UpdateRoleRequest;
import com.ds1.entity.User;
import com.ds1.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
        return ResponseEntity.ok(ApiResponse.success(buildUserInfo(user)));
    }

    /**
     * PUT /api/user/me — Update current user profile (email, etc.)
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateProfile(
            Principal principal,
            @RequestBody Map<String, String> updates) {
        User user = userService.updateProfile(principal.getName(), updates);
        return ResponseEntity.ok(ApiResponse.success(buildUserInfo(user)));
    }

    /**
     * PUT /api/user/me/password — Change current user's password
     */
    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<?>> changePassword(
            Principal principal,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(principal.getName(),
                request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success("密码修改成功", null));
    }

    /**
     * DELETE /api/user/{id} — Delete a user (admin only)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteUser(
            @PathVariable Long id,
            Principal principal) {
        userService.deleteUser(id, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }

    /**
     * PUT /api/user/{id}/role — Update a user's role (admin only)
     */
    @PutMapping("/{id}/role")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoleRequest request,
            Principal principal) {
        User user = userService.updateRole(id, request.getRole(), principal.getName());
        return ResponseEntity.ok(ApiResponse.success(buildUserInfo(user)));
    }

    private Map<String, Object> buildUserInfo(User user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("email", user.getEmail());
        userInfo.put("role", user.getRole());
        userInfo.put("createdAt", user.getCreatedAt());
        return userInfo;
    }
}
