package com.hit.employee_management_spring.service;

import com.hit.employee_management_spring.domain.dto.request.LoginRequestDto;
import com.hit.employee_management_spring.domain.dto.request.RegisterUserRequestDto;
import com.hit.employee_management_spring.domain.dto.response.LoginResponseDto;
import com.hit.employee_management_spring.domain.dto.response.UserResponseDto;
import jakarta.servlet.http.HttpServletRequest;

public interface IAuthService {

    UserResponseDto register(RegisterUserRequestDto requestDto);

    LoginResponseDto login(LoginRequestDto requestDto);

    boolean logout(HttpServletRequest request);

}
