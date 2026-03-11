package com.hit.employee_management_spring.service;

import com.hit.employee_management_spring.domain.dto.request.CreateEmployeeRequestDto;
import com.hit.employee_management_spring.domain.dto.request.UpdateEmployeeRequestDto;
import com.hit.employee_management_spring.domain.dto.request.pagination.PaginationFullRequestDto;
import com.hit.employee_management_spring.domain.dto.request.pagination.PaginationResponseDto;
import com.hit.employee_management_spring.domain.dto.response.EmployeeResponseDto;
import org.springframework.security.access.prepost.PreAuthorize;

public interface IEmployeeService {
    @PreAuthorize("hasRole('ADMIN')")
    EmployeeResponseDto create(CreateEmployeeRequestDto requestDto);

    @PreAuthorize("hasRole('ADMIN')")
    EmployeeResponseDto update(UpdateEmployeeRequestDto requestDto);

    @PreAuthorize("hasRole('ADMIN')")
    boolean delete(Long id);

    EmployeeResponseDto getById(Long id);

    @PreAuthorize("hasRole('ADMIN')")
    PaginationResponseDto getAll(PaginationFullRequestDto requestDto);
}
