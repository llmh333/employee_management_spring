package com.hit.employee_management_spring.config;

import com.hit.employee_management_spring.constant.Gender;
import com.hit.employee_management_spring.constant.RoleConstant;
import com.hit.employee_management_spring.domain.entity.Role;
import com.hit.employee_management_spring.domain.entity.User;
import com.hit.employee_management_spring.repository.RoleRepository;
import com.hit.employee_management_spring.repository.UserRepository;
import jakarta.transaction.Transactional;
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
    @Transactional
    public void run(String... args) throws Exception {

        log.info("Checking available data...");

        List<Role> roles = new ArrayList<>();
        Role roleAdmin = new Role();
        roleAdmin.setName(RoleConstant.ROLE_ADMIN.name());

        Role roleUser = new Role();
        roleUser.setName(RoleConstant.ROLE_USER.name());

        if (roleRepository.existsByName(RoleConstant.ROLE_ADMIN.name()) ||
            roleRepository.existsByName(RoleConstant.ROLE_USER.name())) {

            if (userRepository.existsByEmail("admin123@example.com")) {
                log.info("Available data found. Skipping initialization...");
                return;
            }

            List<Role> allRoles = roleRepository.findAll();

            User user = new User();
            user.setUsername("admin123");
            user.setPassword(passwordEncoder.encode("admin123"));
            user.setEmail("admin123@example.com");
            user.setGender(Gender.OTHER);
            user.setFirstName("Admin");
            user.setLastName("Admin");
            user.setRoles(allRoles);

            userRepository.save(user);
            return;
        }

        log.info("Initializing available data...");

        roles.add(roleAdmin);
        roles.add(roleUser);
        List<Role> defaultRoles = roleRepository.saveAll(roles);


        User user = new User();
        user.setUsername("admin123");
        user.setPassword(passwordEncoder.encode("admin123"));
        user.setEmail("admin123@example.com");
        user.setGender(Gender.OTHER);
        user.setFirstName("Admin");
        user.setLastName("Admin");
        user.setRoles(defaultRoles);

        userRepository.save(user);
        log.info("Available data initialized successfully");
    }
}
