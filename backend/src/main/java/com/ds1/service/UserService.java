package com.ds1.service;

import com.ds1.dto.LoginRequest;
import com.ds1.dto.RegisterRequest;
import com.ds1.entity.User;
import com.ds1.exception.BusinessException;
import com.ds1.repository.UserRepository;
import com.ds1.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Register a new user
     */
    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "用户名已存在");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "邮箱已被注册");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole("USER");

        return userRepository.save(user);
    }

    /**
     * Login and return JWT token
     */
    public Map<String, Object> login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED.value(), "用户名或密码错误"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED.value(), "用户名或密码错误");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("username", user.getUsername());
        result.put("email", user.getEmail());
        result.put("role", user.getRole());
        result.put("createdAt", user.getCreatedAt());
        return result;
    }

    /**
     * Get current user info
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND.value(), "用户不存在"));
    }

    /**
     * Get dashboard statistics
     */
    public Map<String, Object> getDashboardStats() {
        long totalUsers = userRepository.count();
        long todayNew = userRepository.countByCreatedAtAfter(LocalDate.now().atStartOfDay());

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("todayNew", todayNew);
        return stats;
    }

    /**
     * Get all users (for admin table)
     */
    public List<Map<String, Object>> getAllUsers() {
        return userRepository.findAll().stream().map(user -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", user.getId());
            map.put("username", user.getUsername());
            map.put("email", user.getEmail());
            map.put("role", user.getRole());
            map.put("createdAt", user.getCreatedAt());
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * Update current user's profile
     */
    public User updateProfile(String username, Map<String, String> updates) {
        User user = findByUsername(username);

        if (updates.containsKey("email")) {
            String newEmail = updates.get("email");
            if (newEmail == null || newEmail.trim().isEmpty()) {
                throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "邮箱不能为空");
            }
            newEmail = newEmail.trim();
            if (!newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
                throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "邮箱已被占用");
            }
            user.setEmail(newEmail);
        }

        return userRepository.save(user);
    }

    /**
     * Change the current user's password (verify old password first)
     */
    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = findByUsername(username);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "原密码错误");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Delete a user by id (admin only, cannot delete self)
     */
    public void deleteUser(Long id, String currentUsername) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND.value(), "用户不存在"));

        if (user.getUsername().equals(currentUsername)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "不能删除自己");
        }

        userRepository.delete(user);
    }

    /**
     * Update a user's role (admin only)
     */
    public User updateRole(Long id, String role, String currentUsername) {
        if (!"ADMIN".equals(role) && !"USER".equals(role)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "无效的角色，只能为 ADMIN 或 USER");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND.value(), "用户不存在"));

        if (user.getUsername().equals(currentUsername)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "不能修改自己的角色");
        }

        user.setRole(role);
        return userRepository.save(user);
    }
}
