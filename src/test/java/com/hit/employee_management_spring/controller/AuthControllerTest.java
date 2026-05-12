package com.hit.employee_management_spring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hit.employee_management_spring.constant.UrlConstant;
import com.hit.employee_management_spring.domain.dto.request.LoginRequestDto;
import com.hit.employee_management_spring.domain.dto.request.RegisterUserRequestDto;
import com.hit.employee_management_spring.domain.dto.response.LoginResponseDto;
import com.hit.employee_management_spring.domain.dto.response.UserResponseDto;
import com.hit.employee_management_spring.repository.TokenBlacklistCacheRepository;
import com.hit.employee_management_spring.repository.UserCacheRepository;
import com.hit.employee_management_spring.security.AccessDeniedExceptionHandler;
import com.hit.employee_management_spring.security.AuthenticationEntryPointHandler;
import com.hit.employee_management_spring.security.JwtTokenProvider;
import com.hit.employee_management_spring.service.IAuthService;
import com.hit.employee_management_spring.service.ICustomUserDetailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private IAuthService authService;
    @MockBean private MessageSource messageSource;

    // Security Mocks
    @MockBean private JwtTokenProvider jwtTokenProvider;
    @MockBean private ICustomUserDetailService customUserDetailService;
    @MockBean private UserCacheRepository userCacheRepository;
    @MockBean private TokenBlacklistCacheRepository tokenBlacklistCacheRepository;
    @MockBean private AccessDeniedExceptionHandler accessDeniedExceptionHandler;
    @MockBean private AuthenticationEntryPointHandler authenticationEntryPointHandler;

    @Test
    void register_ShouldReturn200_WhenValid() throws Exception {
        RegisterUserRequestDto request = new RegisterUserRequestDto();
        request.setUsername("testuser");
        request.setEmail("test@gmail.com");
        request.setPassword("password123");
        request.setConfirmPassword("password123");

        when(authService.register(any())).thenReturn(new UserResponseDto());

        mockMvc.perform(post(UrlConstant.BASE_URL + UrlConstant.Auth.REGISTER)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true));
    }

    @Test
    void login_ShouldReturn200_WhenValid() throws Exception {
        LoginRequestDto request = new LoginRequestDto("user", "pass");
        when(authService.login(any())).thenReturn(new LoginResponseDto("access", "refresh", "user"));

        mockMvc.perform(post(UrlConstant.BASE_URL + UrlConstant.Auth.LOGIN)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("access"));
    }
}
