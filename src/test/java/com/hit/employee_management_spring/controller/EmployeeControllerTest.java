package com.hit.employee_management_spring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hit.employee_management_spring.base.RestData;
import com.hit.employee_management_spring.constant.UrlConstant;
import com.hit.employee_management_spring.domain.dto.request.CreateEmployeeRequestDto;
import com.hit.employee_management_spring.domain.dto.request.UpdateEmployeeRequestDto;
import com.hit.employee_management_spring.config.AuditingConfig;
import com.hit.employee_management_spring.config.WebSecurityConfig;
import com.hit.employee_management_spring.domain.dto.response.EmployeeResponseDto;
import com.hit.employee_management_spring.enums.EmployeeStatus;
import com.hit.employee_management_spring.service.IEmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EmployeeController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {WebSecurityConfig.class, AuditingConfig.class}))
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IEmployeeService employeeService;

    private EmployeeResponseDto employeeResponseDto;

    @BeforeEach
    void setUp() {
        employeeResponseDto = new EmployeeResponseDto();
        employeeResponseDto.setId(1L);
        employeeResponseDto.setEmployeeCode("EMP-2024-TEST");
        employeeResponseDto.setUserId("user-uuid");
        employeeResponseDto.setPositionId(1L);
    }

    @Test
    @DisplayName("POST /api/v1/employees - Should create employee")
    void createEmployee_Success() throws Exception {
        // Arrange
        CreateEmployeeRequestDto request = new CreateEmployeeRequestDto();
        request.setUserId("user-uuid");
        request.setPositionId(1L);
        request.setHireDate(LocalDate.now());

        when(employeeService.create(any(CreateEmployeeRequestDto.class))).thenReturn(employeeResponseDto);

        // Act & Assert
        mockMvc.perform(post(UrlConstant.BASE_URL + UrlConstant.Employee.CREATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.employeeCode").value("EMP-2024-TEST"));
    }

    @Test
    @DisplayName("PUT /api/v1/employees - Should update employee")
    void updateEmployee_Success() throws Exception {
        // Arrange
        UpdateEmployeeRequestDto request = new UpdateEmployeeRequestDto();
        request.setId(1L);
        request.setStatus(EmployeeStatus.INACTIVE);

        when(employeeService.update(any(UpdateEmployeeRequestDto.class))).thenReturn(employeeResponseDto);

        // Act & Assert
        mockMvc.perform(put(UrlConstant.BASE_URL + UrlConstant.Employee.UPDATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    @DisplayName("GET /api/v1/employees/{id} - Should return employee")
    void getEmployeeById_Success() throws Exception {
        // Arrange
        when(employeeService.getById(1L)).thenReturn(employeeResponseDto);

        // Act & Assert
        mockMvc.perform(get(UrlConstant.BASE_URL + UrlConstant.Employee.GET_BY_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    @DisplayName("DELETE /api/v1/employees/{id} - Should delete employee")
    void deleteEmployee_Success() throws Exception {
        // Arrange
        when(employeeService.delete(1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete(UrlConstant.BASE_URL + UrlConstant.Employee.DELETE, 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /api/v1/employees - Should return 400 when validation fails")
    void createEmployee_ValidationFailure() throws Exception {
        // Arrange
        CreateEmployeeRequestDto request = new CreateEmployeeRequestDto();
        // missing fields

        // Act & Assert
        mockMvc.perform(post(UrlConstant.BASE_URL + UrlConstant.Employee.CREATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
