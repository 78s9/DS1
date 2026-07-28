package com.ds1.config;

import com.ds1.entity.User;
import com.ds1.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@ds1.com");
            admin.setRole("ADMIN");
            userRepository.save(admin);
            System.out.println("========================================");
            System.out.println("  默认管理员已创建:");
            System.out.println("  用户名: admin");
            System.out.println("  密码:   admin123");
            System.out.println("========================================");
        }
    }
}
