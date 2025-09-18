package com.hit.employee_management_spring.service.impl;

import com.hit.employee_management_spring.constant.ErrorMessage;
import com.hit.employee_management_spring.constant.TypeToken;
import com.hit.employee_management_spring.domain.dto.request.LoginRequestDto;
import com.hit.employee_management_spring.domain.dto.request.RegisterUserRequestDto;
import com.hit.employee_management_spring.domain.dto.response.LoginResponseDto;
import com.hit.employee_management_spring.domain.dto.response.UserResponseDto;
import com.hit.employee_management_spring.domain.entity.TokenBlacklist;
import com.hit.employee_management_spring.domain.entity.User;
import com.hit.employee_management_spring.domain.entity.UserSession;
import com.hit.employee_management_spring.domain.mapper.UserMapper;
import com.hit.employee_management_spring.exception.BadRequestException;
import com.hit.employee_management_spring.exception.DuplicateDataException;
import com.hit.employee_management_spring.repository.TokenBlacklistRepository;
import com.hit.employee_management_spring.repository.UserRepository;
import com.hit.employee_management_spring.repository.UserSessionRepository;
import com.hit.employee_management_spring.security.JwtAuthenticationFilterChain;
import com.hit.employee_management_spring.security.JwtTokenProvider;
import com.hit.employee_management_spring.security.UserPrincipal;
import com.hit.employee_management_spring.service.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class IAuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public UserResponseDto register(RegisterUserRequestDto requestDto) {

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
        log.info("password: {}", requestDto.getPassword());
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        return userMapper.toUserResponseDto(userRepository.save(newUser));
    }

    @Override
    @Transactional
    public LoginResponseDto login(LoginRequestDto requestDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestDto.getUsernameOrEmail(), requestDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            String accessToken = jwtTokenProvider.generateToken(userPrincipal, false);
            String refreshToken = jwtTokenProvider.generateToken(userPrincipal, true);

            Optional<User> userOptional = userRepository.findById(userPrincipal.getId());
            UserSession newSession = UserSession.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .user(userOptional.get())
                    .build();
            userSessionRepository.save(newSession);

            return new LoginResponseDto(accessToken, refreshToken, userPrincipal.getUsername());

        } catch (InternalAuthenticationServiceException | BadCredentialsException e) {
            throw new BadRequestException(ErrorMessage.Auth.USERNAME_OR_PASSWORD_WRONG);
        }
    }

    @Override
    @Transactional
    public boolean logout(HttpServletRequest request) {

        String token = JwtAuthenticationFilterChain.extractTokenByRequest(request);
        if (token == null) {
            return false;
        }

        UserSession userSession = userSessionRepository.findUserSessionByAccessToken(token);
        if (userSession == null) {
            return false;
        }

        TokenBlacklist newAccessTokenBlacklist = TokenBlacklist.builder()
                .token(userSession.getAccessToken())
                .typeToken(TypeToken.ACCESS_TOKEN)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();

        TokenBlacklist newRefreshTokenBlacklist = TokenBlacklist.builder()
                .token(userSession.getRefreshToken())
                .typeToken(TypeToken.ACCESS_TOKEN)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();

        userSessionRepository.delete(userSession);

        tokenBlacklistRepository.save(newAccessTokenBlacklist);
        tokenBlacklistRepository.save(newRefreshTokenBlacklist);

        SecurityContextHolder.clearContext();

        return true;
    }
}
