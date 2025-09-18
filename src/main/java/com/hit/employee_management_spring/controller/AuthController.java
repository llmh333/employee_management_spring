package com.hit.employee_management_spring.controller;

import com.hit.employee_management_spring.base.ApiResponseUtil;
import com.hit.employee_management_spring.base.RestApiV1;
import com.hit.employee_management_spring.base.RestData;
import com.hit.employee_management_spring.constant.UrlConstant;
import com.hit.employee_management_spring.domain.dto.request.LoginRequestDto;
import com.hit.employee_management_spring.domain.dto.request.RegisterUserRequestDto;
import com.hit.employee_management_spring.domain.dto.response.LoginResponseDto;
import com.hit.employee_management_spring.domain.dto.response.UserResponseDto;
import com.hit.employee_management_spring.service.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestApiV1
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping(UrlConstant.Auth.REGISTER)
    public ResponseEntity<RestData<?>> register(@RequestBody @Valid RegisterUserRequestDto requestDto) {

        UserResponseDto responseDto = authService.register(requestDto);
        return ApiResponseUtil.success(responseDto);
    }

    @PostMapping(UrlConstant.Auth.LOGIN)
    public ResponseEntity<RestData<?>> login(@RequestBody @Valid LoginRequestDto requestDto) {

        LoginResponseDto responseDto = authService.login(requestDto);
        return ApiResponseUtil.success(responseDto);
    }
}
