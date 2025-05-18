package khuong.com.tmbackend.user_service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import khuong.com.tmbackend.user_service.entity.ERole;
import khuong.com.tmbackend.user_service.entity.Role;
import khuong.com.tmbackend.user_service.entity.User;
import khuong.com.tmbackend.user_service.entity.UserRole;
import khuong.com.tmbackend.user_service.repository.RoleRepository;
import khuong.com.tmbackend.user_service.repository.UserRepository;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Create admin role if not exists
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(ERole.ROLE_ADMIN);
                    return roleRepository.save(role);
                });

        // Create admin user if not exists
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@techmart.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("Admin");
            admin.setEnabled(true);

            // Add admin role
            UserRole userRole = new UserRole();
            userRole.setUser(admin);
            userRole.setRole(adminRole);
            admin.getRoles().add(userRole);

            userRepository.save(admin);
            System.out.println("Admin user created successfully");
        }
    }
} 