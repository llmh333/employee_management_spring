package com.hit.employee_management_spring.service;

import com.hit.employee_management_spring.domain.dto.request.CreateDepartmentRequestDto;
import com.hit.employee_management_spring.domain.dto.request.UpdateDepartmentRequestDto;
import com.hit.employee_management_spring.domain.dto.response.DepartmentResponseDto;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

public interface IDepartmentService {
    @PreAuthorize("hasRole('ADMIN')")
    DepartmentResponseDto create(CreateDepartmentRequestDto requestDto);

    @PreAuthorize("hasRole('ADMIN')")
    DepartmentResponseDto update(UpdateDepartmentRequestDto requestDto);

    @PreAuthorize("hasRole('ADMIN')")
    boolean delete(Long id);

    DepartmentResponseDto getById(Long id);

    List<DepartmentResponseDto> getAll();
}
