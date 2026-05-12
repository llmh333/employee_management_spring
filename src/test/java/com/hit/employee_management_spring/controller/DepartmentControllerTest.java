package com.hit.employee_management_spring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hit.employee_management_spring.constant.UrlConstant;
import com.hit.employee_management_spring.domain.dto.request.CreateDepartmentRequestDto;
import com.hit.employee_management_spring.domain.dto.request.UpdateDepartmentRequestDto;
import com.hit.employee_management_spring.domain.dto.response.DepartmentResponseDto;
import com.hit.employee_management_spring.repository.TokenBlacklistCacheRepository;
import com.hit.employee_management_spring.repository.UserCacheRepository;
import com.hit.employee_management_spring.security.AccessDeniedExceptionHandler;
import com.hit.employee_management_spring.security.AuthenticationEntryPointHandler;
import com.hit.employee_management_spring.security.JwtTokenProvider;
import com.hit.employee_management_spring.service.ICustomUserDetailService;
import com.hit.employee_management_spring.service.IDepartmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DepartmentController.class)
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IDepartmentService departmentService;

    // Security mocks
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @MockBean
    private ICustomUserDetailService customUserDetailService;
    @MockBean
    private UserCacheRepository userCacheRepository;
    @MockBean
    private TokenBlacklistCacheRepository tokenBlacklistCacheRepository;
    @MockBean
    private AccessDeniedExceptionHandler accessDeniedExceptionHandler;
    @MockBean
    private AuthenticationEntryPointHandler authenticationEntryPointHandler;

    private DepartmentResponseDto departmentResponse;

    @BeforeEach
    void setUp() {
        departmentResponse = new DepartmentResponseDto();
        departmentResponse.setId(1L);
        departmentResponse.setName("IT");
        departmentResponse.setDescription("Information Technology");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ShouldReturn201_WhenSuccess() throws Exception {
        CreateDepartmentRequestDto request = new CreateDepartmentRequestDto();
        request.setName("IT");

        when(departmentService.create(any())).thenReturn(departmentResponse);

        mockMvc.perform(post(UrlConstant.BASE_URL + UrlConstant.Department.CREATE)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.name").value("IT"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ShouldReturn200_WhenSuccess() throws Exception {
        UpdateDepartmentRequestDto request = new UpdateDepartmentRequestDto();
        request.setId(1L);
        request.setName("IT Updated");

        when(departmentService.update(any())).thenReturn(departmentResponse);

        mockMvc.perform(put(UrlConstant.BASE_URL + UrlConstant.Department.UPDATE)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ShouldReturn204_WhenSuccess() throws Exception {
        mockMvc.perform(delete(UrlConstant.BASE_URL + UrlConstant.Department.DELETE, 1L)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void getById_ShouldReturn200_WhenFound() throws Exception {
        when(departmentService.getById(1L)).thenReturn(departmentResponse);

        mockMvc.perform(get(UrlConstant.BASE_URL + UrlConstant.Department.GET_BY_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    @WithMockUser
    void getAll_ShouldReturn200() throws Exception {
        when(departmentService.getAll()).thenReturn(List.of(departmentResponse));

        mockMvc.perform(get(UrlConstant.BASE_URL + UrlConstant.Department.GET_ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }
}
