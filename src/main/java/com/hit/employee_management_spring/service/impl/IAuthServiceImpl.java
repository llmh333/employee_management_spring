package com.hit.employee_management_spring.service.impl;

import com.hit.employee_management_spring.constant.ErrorMessage;
import com.hit.employee_management_spring.constant.RoleConstant;
import com.hit.employee_management_spring.domain.dto.request.LoginRequestDto;
import com.hit.employee_management_spring.domain.dto.request.RegisterUserRequestDto;
import com.hit.employee_management_spring.domain.dto.response.LoginResponseDto;
import com.hit.employee_management_spring.domain.dto.response.UserResponseDto;
import com.hit.employee_management_spring.domain.entity.Role;
import com.hit.employee_management_spring.domain.entity.User;
import com.hit.employee_management_spring.domain.mapper.UserMapper;
import com.hit.employee_management_spring.exception.BadRequestException;
import com.hit.employee_management_spring.exception.DuplicateDataException;
import com.hit.employee_management_spring.exception.NotFoundException;
import com.hit.employee_management_spring.repository.RoleRepository;
import com.hit.employee_management_spring.repository.TokenBlacklistCacheRepository;
import com.hit.employee_management_spring.repository.UserCacheRepository;
import com.hit.employee_management_spring.repository.UserRepository;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class IAuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserCacheRepository userCacheRepository;
    private final TokenBlacklistCacheRepository tokenBlacklistCacheRepository;

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
        Role defaultRole = roleRepository.findByName(RoleConstant.ROLE_USER.name());
        List<Role> roles = new ArrayList<>();
        roles.add(defaultRole);
        User newUser = userMapper.toUser(requestDto);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setRoles(roles);
        return userMapper.toUserResponseDto(userRepository.save(newUser));
    }

    @Override
    public LoginResponseDto login(LoginRequestDto requestDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestDto.getUsernameOrEmail(), requestDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            User loginUser = userRepository.findById(userPrincipal.getId())
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.User.NOT_FOUND_BY_ID, new String[]{userPrincipal.getId()}));

            String accessToken = jwtTokenProvider.generateToken(userPrincipal, false);
            String refreshToken = jwtTokenProvider.generateToken(userPrincipal, true);

            userCacheRepository.save(loginUser);

            return new LoginResponseDto(accessToken, refreshToken, userPrincipal.getUsername());

        } catch (InternalAuthenticationServiceException | BadCredentialsException e) {
            throw new BadRequestException(ErrorMessage.Auth.USERNAME_OR_PASSWORD_WRONG);
        }
    }

    @Override
    public boolean logout(HttpServletRequest request) {
        String accessToken = JwtAuthenticationFilterChain.extractTokenByRequest(request);
        if (accessToken == null) {
            return false;
        }
        long accessTtl = jwtTokenProvider.getRemainingTtlSeconds(accessToken);
        tokenBlacklistCacheRepository.blacklist(accessToken, accessTtl > 0 ? accessTtl : 1);
        SecurityContextHolder.clearContext();
        return true;
    }
}
