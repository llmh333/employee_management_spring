package com.hit.employee_management_spring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hit.employee_management_spring.config.AuditingConfig;
import com.hit.employee_management_spring.config.WebSecurityConfig;
import com.hit.employee_management_spring.constant.UrlConstant;
import com.hit.employee_management_spring.domain.dto.request.RegisterUserRequestDto;
import com.hit.employee_management_spring.domain.dto.request.UpdateUserRequestDto;
import com.hit.employee_management_spring.domain.dto.request.pagination.PaginationResponseDto;
import com.hit.employee_management_spring.domain.dto.response.UserResponseDto;
import com.hit.employee_management_spring.service.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        WebSecurityConfig.class, AuditingConfig.class }))
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IUserService userService;

    @MockitoBean
    private MessageSource messageSource;

    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        userResponseDto = new UserResponseDto();
        userResponseDto.setId("user-uuid");
        userResponseDto.setUsername("testuser");
        userResponseDto.setEmail("test@example.com");
    }

    @Test
    @DisplayName("GET /api/v1/users/{userId} - Should return user")
    void getUserById_Success() throws Exception {
        // Arrange
        when(userService.getUserById("user-uuid")).thenReturn(userResponseDto);

        // Act & Assert
        mockMvc.perform(get(UrlConstant.BASE_URL + UrlConstant.User.GET_USER_BY_ID, "user-uuid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value("user-uuid"));
    }

    @Test
    @DisplayName("GET /api/v1/users - Should return paginated users")
    void getAllUser_Success() throws Exception {
        // Arrange
        PaginationResponseDto<UserResponseDto> responseDto = new PaginationResponseDto<>();
        responseDto.setItems(List.of(userResponseDto));

        when(userService.getAllUser(any())).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(get(UrlConstant.BASE_URL + UrlConstant.User.GET_ALL_USER)
                .param("pageNum", "0")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.data").isArray());
    }

    @Test
    @DisplayName("POST /api/v1/users - Should add new user")
    void addNewUser_Success() throws Exception {
        // Arrange
        RegisterUserRequestDto request = new RegisterUserRequestDto();
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        request.setPassword("Password123!");
        request.setConfirmPassword("Password123!");

        when(userService.addNewUser(any(RegisterUserRequestDto.class))).thenReturn(userResponseDto);

        // Act & Assert
        mockMvc.perform(post(UrlConstant.BASE_URL + UrlConstant.User.ADD_NEW_USER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value("user-uuid"));
    }

    @Test
    @DisplayName("PUT /api/v1/users - Should update user")
    void updateUser_Success() throws Exception {
        // Arrange
        UpdateUserRequestDto request = new UpdateUserRequestDto();
        request.setId("user-uuid");
        request.setFirstName("Updated");

        when(userService.updateUser(any(UpdateUserRequestDto.class))).thenReturn(userResponseDto);

        // Act & Assert
        mockMvc.perform(put(UrlConstant.BASE_URL + UrlConstant.User.UPDATE_USER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value("user-uuid"));
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{userId} - Should delete user")
    void deleteUserById_Success() throws Exception {
        // Arrange
        when(userService.deleteByUserId("user-uuid")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete(UrlConstant.BASE_URL + UrlConstant.User.DELETE_USER_BY_ID, "user-uuid"))
                .andExpect(status().isNoContent());
    }
}
