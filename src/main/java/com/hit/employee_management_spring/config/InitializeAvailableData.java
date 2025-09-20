package com.hit.employee_management_spring.config;

import com.hit.employee_management_spring.constant.Gender;
import com.hit.employee_management_spring.constant.RoleConstant;
import com.hit.employee_management_spring.domain.entity.Role;
import com.hit.employee_management_spring.domain.entity.User;
import com.hit.employee_management_spring.repository.RoleRepository;
import com.hit.employee_management_spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class InitializeAvailableData implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        log.info("Checking available data...");

        List<Role> roles = new ArrayList<>();
        Role roleAdmin = new Role();
        roleAdmin.setName(RoleConstant.ADMIN.name());

        Role roleUser = new Role();
        roleUser.setName(RoleConstant.USER.name());

        if (roleRepository.existsByName(RoleConstant.ADMIN.name()) ||
            roleRepository.existsByName(RoleConstant.USER.name())) {

            if (userRepository.existsByEmail("admin123@example.com")) {
                log.info("Available data found. Skipping initialization...");
                return;
            }

            User user = new User();
            user.setUsername("admin123");
            user.setPassword(passwordEncoder.encode("admin123"));
            user.setEmail("admin123@example.com");
            user.setGender(Gender.OTHER);
            user.setFirstName("Admin");
            user.setLastName("Admin");
            user.setRoles(roles);

            userRepository.save(user);
            return;
        }

        log.info("Initializing available data...");

        roles.add(roleAdmin);
        roles.add(roleUser);

        User user = new User();
        user.setUsername("admin123");
        user.setPassword("admin123");
        user.setEmail("admin123@example.com");
        user.setGender(Gender.OTHER);
        user.setFirstName("Admin");
        user.setLastName("Admin");
        user.setRoles(roles);

        userRepository.save(user);
        roleRepository.saveAll(roles);
        log.info("Available data initialized successfully");
    }
}
