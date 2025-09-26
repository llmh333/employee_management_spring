package com.hit.employee_management_spring.service;

import com.hit.employee_management_spring.domain.dto.request.RegisterUserRequestDto;
import com.hit.employee_management_spring.domain.dto.request.UpdateUserRequestDto;
import com.hit.employee_management_spring.domain.dto.request.pagination.PaginationFullRequestDto;
import com.hit.employee_management_spring.domain.dto.request.pagination.PaginationResponseDto;
import com.hit.employee_management_spring.domain.dto.response.UserResponseDto;

public interface IUserService {

    public UserResponseDto addNewUser(RegisterUserRequestDto requestDto);

    public UserResponseDto changePassword(String email, String newPassword, String confirmNewPassword);

    public boolean deleteByUserId(String userId);

    public UserResponseDto getUserById(String userId);

    public PaginationResponseDto getAllUser(PaginationFullRequestDto requestDto);

    public UserResponseDto updateUser(UpdateUserRequestDto requestDto);
}
