package com.hit.employee_management_spring.service.impl;

import com.hit.employee_management_spring.constant.ErrorMessage;
import com.hit.employee_management_spring.domain.entity.User;
import com.hit.employee_management_spring.exception.NotFoundException;
import com.hit.employee_management_spring.repository.UserRepository;
import com.hit.employee_management_spring.security.UserPrincipal;
import com.hit.employee_management_spring.service.ICustomUserDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class ICustomUserDetailServiceImpl implements ICustomUserDetailService, UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserPrincipal loadUserByUsername(String username) {

        log.info("Loading user by username: {}", username);
        Optional<User> user = userRepository.findUserByUsername(username);
        if (user.isEmpty()) {
            throw new NotFoundException(ErrorMessage.User.NOT_FOUND_BY_USERNAME, new String[]{username});
        }
        log.info("Loading user by username: {} successfully", username);

        return UserPrincipal.create(user.get());
    }

    @Override
    public UserPrincipal loadUserByEmail(String email) {
        Optional<User> user = userRepository.findUserByUsername(email);
        if (user.isEmpty()) {
            throw new NotFoundException(ErrorMessage.User.NOT_FOUND_BY_EMAIL, new String[]{email});
        }

        return UserPrincipal.create(user.get());
    }
}
