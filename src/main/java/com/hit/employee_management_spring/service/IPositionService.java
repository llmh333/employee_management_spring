package com.hit.employee_management_spring.service;

import com.hit.employee_management_spring.domain.dto.request.CreatePositionRequestDto;
import com.hit.employee_management_spring.domain.dto.request.UpdatePositionRequestDto;
import com.hit.employee_management_spring.domain.dto.response.PositionResponseDto;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

public interface IPositionService {
    @PreAuthorize("hasRole('ADMIN')")
    PositionResponseDto create(CreatePositionRequestDto requestDto);

    @PreAuthorize("hasRole('ADMIN')")
    PositionResponseDto update(UpdatePositionRequestDto requestDto);

    @PreAuthorize("hasRole('ADMIN')")
    boolean delete(Long id);

    PositionResponseDto getById(Long id);

    List<PositionResponseDto> getByDepartment(Long departmentId);

    List<PositionResponseDto> getAll();
}
