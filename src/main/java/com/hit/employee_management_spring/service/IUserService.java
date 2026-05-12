package com.hit.employee_management_spring.service;

import com.hit.employee_management_spring.domain.dto.request.RegisterUserRequestDto;
import com.hit.employee_management_spring.domain.dto.request.UpdateUserRequestDto;
import com.hit.employee_management_spring.domain.dto.request.UpdateUserRoleRequestDto;
import com.hit.employee_management_spring.domain.dto.request.pagination.PaginationFullRequestDto;
import com.hit.employee_management_spring.domain.dto.request.pagination.PaginationResponseDto;
import com.hit.employee_management_spring.domain.dto.response.UserResponseDto;
import org.springframework.security.access.prepost.PreAuthorize;

public interface IUserService {

    @PreAuthorize( "hasRole('ADMIN')")
    public UserResponseDto addNewUser(RegisterUserRequestDto requestDto);

    @PreAuthorize( "hasRole('ADMIN')")
    public UserResponseDto updateUserRole(UpdateUserRoleRequestDto requestDto);

    public UserResponseDto changePassword(String email, String oldPassword, String newPassword, String confirmNewPassword);

    public UserResponseDto resetPassword(String email, String newPassword);

    @PreAuthorize( "hasRole('ADMIN')")
    public boolean deleteByUserId(String userId);

    @PreAuthorize("hasRole('ADMIN') || #userId == authentication.principal.id")
    public UserResponseDto getUserById(String userId);

    @PreAuthorize("hasRole('ADMIN')")
    public PaginationResponseDto getAllUser(PaginationFullRequestDto requestDto);

    public UserResponseDto updateUser(UpdateUserRequestDto requestDto);
}
