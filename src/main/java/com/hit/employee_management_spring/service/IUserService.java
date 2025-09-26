package com.hit.employee_management_spring.service;

import com.hit.employee_management_spring.domain.dto.request.RegisterUserRequestDto;
import com.hit.employee_management_spring.domain.dto.request.UpdateUserRequestDto;
import com.hit.employee_management_spring.domain.dto.request.pagination.PaginationFullRequestDto;
import com.hit.employee_management_spring.domain.dto.request.pagination.PaginationResponseDto;
import com.hit.employee_management_spring.domain.dto.response.UserResponseDto;
import org.springframework.security.access.prepost.PreAuthorize;

public interface IUserService {

    @PreAuthorize( "hasRole('ADMIN')")
    public UserResponseDto addNewUser(RegisterUserRequestDto requestDto);

    public UserResponseDto changePassword(String email, String newPassword, String confirmNewPassword);

    @PreAuthorize( "hasRole('ADMIN')")
    public boolean deleteByUserId(String userId);

    @PreAuthorize("hasRole('ADMIN') || #userId == authentication.principal.id")
    public UserResponseDto getUserById(String userId);

    @PreAuthorize("hasRole('ADMIN')")
    public PaginationResponseDto getAllUser(PaginationFullRequestDto requestDto);

    public UserResponseDto updateUser(UpdateUserRequestDto requestDto);
}
