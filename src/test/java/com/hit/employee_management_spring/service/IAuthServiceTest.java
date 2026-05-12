package com.hit.employee_management_spring.service;

import com.hit.employee_management_spring.constant.ErrorMessage;
import com.hit.employee_management_spring.domain.dto.request.LoginRequestDto;
import com.hit.employee_management_spring.domain.dto.request.RegisterUserRequestDto;
import com.hit.employee_management_spring.domain.dto.response.LoginResponseDto;
import com.hit.employee_management_spring.domain.dto.response.UserResponseDto;
import com.hit.employee_management_spring.domain.entity.Role;
import com.hit.employee_management_spring.domain.entity.User;
import com.hit.employee_management_spring.domain.mapper.UserMapper;
import com.hit.employee_management_spring.enums.RoleConstant;
import com.hit.employee_management_spring.exception.BadRequestException;
import com.hit.employee_management_spring.repository.RoleRepository;
import com.hit.employee_management_spring.repository.TokenBlacklistCacheRepository;
import com.hit.employee_management_spring.repository.UserCacheRepository;
import com.hit.employee_management_spring.repository.UserRepository;
import com.hit.employee_management_spring.security.JwtTokenProvider;
import com.hit.employee_management_spring.security.UserPrincipal;
import com.hit.employee_management_spring.service.impl.IAuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IAuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private UserCacheRepository userCacheRepository;
    @Mock private TokenBlacklistCacheRepository tokenBlacklistCacheRepository;

    @InjectMocks
    private IAuthServiceImpl authService;

    private RegisterUserRequestDto registerDto;
    private User user;

    @BeforeEach
    void setUp() {
        registerDto = new RegisterUserRequestDto();
        registerDto.setUsername("testuser");
        registerDto.setEmail("test@gmail.com");
        registerDto.setPassword("password");
        registerDto.setConfirmPassword("password");

        user = new User();
        user.setId("user-id");
        user.setUsername("testuser");
        user.setEmail("test@gmail.com");
        user.setPassword("password");
    }

    @Test
    void register_ShouldReturnUserResponseDto_WhenSuccess() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName(RoleConstant.ROLE_USER.name())).thenReturn(new Role());
        when(userMapper.toUser(any())).thenReturn(user);
        when(passwordEncoder.encode(any())).thenReturn("hashed-password");
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toUserResponseDto(any())).thenReturn(new UserResponseDto());

        UserResponseDto result = authService.register(registerDto);

        assertThat(result).isNotNull();
        verify(userRepository).save(any());
    }

    @Test
    void register_ShouldThrowBadRequest_WhenPasswordMismatch() {
        registerDto.setConfirmPassword("wrong");
        assertThatThrownBy(() -> authService.register(registerDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorMessage.Validation.PASSWORD_NOT_MATCH);
    }

    @Test
    void login_ShouldReturnTokens_WhenCredentialsCorrect() {
        LoginRequestDto loginDto = new LoginRequestDto("testuser", "password");
        UserPrincipal principal = new UserPrincipal("user-id", "testuser", "password", "test@gmail.com", "F", "L", null, Collections.emptyList());
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null);

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(userRepository.findById("user-id")).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateToken(any(), eq(false))).thenReturn("access-token");
        when(jwtTokenProvider.generateToken(any(), eq(true))).thenReturn("refresh-token");

        LoginResponseDto result = authService.login(loginDto);

        assertThat(result.getAccessToken()).isEqualTo("access-token");
        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    @Test
    void login_ShouldThrowBadRequest_WhenCredentialsWrong() {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException(""));
        assertThatThrownBy(() -> authService.login(new LoginRequestDto("u", "p")))
                .isInstanceOf(BadRequestException.class);
    }
}
