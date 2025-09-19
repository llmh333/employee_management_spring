package com.hit.employee_management_spring.service.impl;

import com.hit.employee_management_spring.constant.ErrorMessage;
import com.hit.employee_management_spring.constant.TypeToken;
import com.hit.employee_management_spring.domain.dto.request.RegisterUserRequestDto;
import com.hit.employee_management_spring.domain.dto.response.UserResponseDto;
import com.hit.employee_management_spring.domain.entity.TokenBlacklist;
import com.hit.employee_management_spring.domain.entity.User;
import com.hit.employee_management_spring.domain.entity.UserSession;
import com.hit.employee_management_spring.domain.mapper.UserMapper;
import com.hit.employee_management_spring.exception.BadRequestException;
import com.hit.employee_management_spring.exception.DuplicateDataException;
import com.hit.employee_management_spring.exception.NotFoundException;
import com.hit.employee_management_spring.repository.TokenBlacklistRepository;
import com.hit.employee_management_spring.repository.UserRepository;
import com.hit.employee_management_spring.repository.UserSessionRepository;
import com.hit.employee_management_spring.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class IUserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserSessionRepository userSessionRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto addNewUser(RegisterUserRequestDto requestDto) {

        if (userRepository.existsByUsername(requestDto.getUsername())) {
            throw new DuplicateDataException(ErrorMessage.User.USERNAME_ALREADY_EXIST);
        }
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new DuplicateDataException(ErrorMessage.User.EMAIL_ALREADY_EXIST);
        }

        if (!requestDto.getPassword().equals(requestDto.getConfirmPassword())) {
            throw new BadRequestException(ErrorMessage.Validation.PASSWORD_NOT_MATCH);
        }

        User newUser = userMapper.toUser(requestDto);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        return userMapper.toUserResponseDto(userRepository.save(newUser));
    }

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

    @Override
    public boolean deleteByUserId(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(ErrorMessage.User.NOT_FOUND_BY_ID, new String[]{userId});
        }

        userRepository.deleteById(userId);

        return true;
    }
}
