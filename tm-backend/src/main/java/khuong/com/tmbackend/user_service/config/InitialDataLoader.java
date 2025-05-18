package khuong.com.tmbackend.user_service.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import khuong.com.tmbackend.user_service.entity.ERole;
import khuong.com.tmbackend.user_service.entity.Role;
import khuong.com.tmbackend.user_service.repository.RoleRepository;

@Component
public class InitialDataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(InitialDataLoader.class);

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(String... args) {
        // Initialize roles if they don't exist
        initRoles();
    }

    private void initRoles() {
        // Create roles if they don't exist
        for (ERole role : ERole.values()) {
            if (!roleRepository.existsByName(role)) {
                Role newRole = new Role();
                newRole.setName(role);
                roleRepository.save(newRole);
                logger.info("Created role: {}", role);
            }
        }
        logger.info("Roles initialization completed");
    }
} 