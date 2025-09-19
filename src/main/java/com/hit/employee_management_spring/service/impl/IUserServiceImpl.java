package com.hit.employee_management_spring.service.impl;

import com.hit.employee_management_spring.constant.ErrorMessage;
import com.hit.employee_management_spring.constant.TypeToken;
import com.hit.employee_management_spring.domain.dto.response.UserResponseDto;
import com.hit.employee_management_spring.domain.entity.TokenBlacklist;
import com.hit.employee_management_spring.domain.entity.User;
import com.hit.employee_management_spring.domain.entity.UserSession;
import com.hit.employee_management_spring.exception.BadRequestException;
import com.hit.employee_management_spring.exception.NotFoundException;
import com.hit.employee_management_spring.repository.TokenBlacklistRepository;
import com.hit.employee_management_spring.repository.UserRepository;
import com.hit.employee_management_spring.repository.UserSessionRepository;
import com.hit.employee_management_spring.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IUserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserSessionRepository userSessionRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Override
    public UserResponseDto changePassword(String email, String newPassword, String confirmNewPassword) {

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException(ErrorMessage.User.NOT_FOUND_BY_EMAIL, new String[]{email});
        }

        if (!newPassword.equals(confirmNewPassword)) {
            throw new BadRequestException(ErrorMessage.Auth.BOTH_NEW_PASSWORD_NOT_MATCH);
        }

        userRepository.save(user);

        user.setPassword(passwordEncoder.encode(newPassword));
        List<UserSession> userSessionList = userSessionRepository.findAllByUser(user);
        userSessionList.forEach(userSession -> {
            TokenBlacklist accessTokenBlacklist = new TokenBlacklist();
            accessTokenBlacklist.setToken(userSession.getAccessToken());
            accessTokenBlacklist.setTypeToken(TypeToken.ACCESS_TOKEN);
            accessTokenBlacklist.setExpiryDate(LocalDateTime.now().plusDays(7));

            TokenBlacklist refreshTokenBlacklist = new TokenBlacklist();
            refreshTokenBlacklist.setToken(userSession.getAccessToken());
            refreshTokenBlacklist.setTypeToken(TypeToken.ACCESS_TOKEN);
            refreshTokenBlacklist.setExpiryDate(LocalDateTime.now().plusDays(7));

            tokenBlacklistRepository.save(accessTokenBlacklist);
            tokenBlacklistRepository.save(refreshTokenBlacklist);

            userSessionRepository.delete(userSession);
        });

        return null;
    }
}
