package com.hit.employee_management_spring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hit.employee_management_spring.config.AuditingConfig;
import com.hit.employee_management_spring.config.WebSecurityConfig;
import com.hit.employee_management_spring.constant.UrlConstant;
import com.hit.employee_management_spring.domain.dto.request.CreatePositionRequestDto;
import com.hit.employee_management_spring.domain.dto.request.UpdatePositionRequestDto;
import com.hit.employee_management_spring.domain.dto.response.PositionResponseDto;
import com.hit.employee_management_spring.service.IPositionService;
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

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PositionController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        WebSecurityConfig.class, AuditingConfig.class }))
class PositionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IPositionService positionService;

    private PositionResponseDto positionResponseDto;

    @BeforeEach
    void setUp() {
        positionResponseDto = new PositionResponseDto();
        positionResponseDto.setId(1L);
        positionResponseDto.setTitle("Developer");
        positionResponseDto.setDepartmentId(1L);
        positionResponseDto.setDepartmentName("IT");
    }

    @Test
    @DisplayName("POST /api/v1/positions - Should create position")
    void create_Success() throws Exception {
        // Arrange
        CreatePositionRequestDto request = new CreatePositionRequestDto();
        request.setTitle("Developer");
        request.setDepartmentId(1L);
        request.setBaseSalary(BigDecimal.valueOf(1000.0));

        when(positionService.create(any(CreatePositionRequestDto.class))).thenReturn(positionResponseDto);

        // Act & Assert
        mockMvc.perform(post(UrlConstant.BASE_URL + UrlConstant.Position.CREATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    @DisplayName("PUT /api/v1/positions - Should update position")
    void update_Success() throws Exception {
        // Arrange
        UpdatePositionRequestDto request = new UpdatePositionRequestDto();
        request.setId(1L);
        request.setTitle("Senior Developer");
        request.setDepartmentId(1L);

        when(positionService.update(any(UpdatePositionRequestDto.class))).thenReturn(positionResponseDto);

        // Act & Assert
        mockMvc.perform(put(UrlConstant.BASE_URL + UrlConstant.Position.UPDATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    @DisplayName("GET /api/v1/positions/{id} - Should return position")
    void getById_Success() throws Exception {
        // Arrange
        when(positionService.getById(1L)).thenReturn(positionResponseDto);

        // Act & Assert
        mockMvc.perform(get(UrlConstant.BASE_URL + UrlConstant.Position.GET_BY_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    @DisplayName("GET /api/v1/positions - Should return all positions")
    void getAll_Success() throws Exception {
        // Arrange
        when(positionService.getAll()).thenReturn(List.of(positionResponseDto));

        // Act & Assert
        mockMvc.perform(get(UrlConstant.BASE_URL + UrlConstant.Position.GET_ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("DELETE /api/v1/positions/{id} - Should delete position")
    void delete_Success() throws Exception {
        // Arrange
        when(positionService.delete(1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete(UrlConstant.BASE_URL + UrlConstant.Position.DELETE, 1L))
                .andExpect(status().isNoContent());
    }
}
