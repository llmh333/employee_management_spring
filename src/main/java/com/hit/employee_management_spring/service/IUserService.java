package com.hit.employee_management_spring.service;

import com.hit.employee_management_spring.domain.dto.response.UserResponseDto;

public interface IUserService {

    public UserResponseDto changePassword(String email, String newPassword, String confirmNewPassword);
}
