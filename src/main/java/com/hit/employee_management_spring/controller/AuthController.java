package com.hit.employee_management_spring.controller;

import com.hit.employee_management_spring.base.ApiResponseUtil;
import com.hit.employee_management_spring.base.RestApiV1;
import com.hit.employee_management_spring.base.RestData;
import com.hit.employee_management_spring.constant.ErrorMessage;
import com.hit.employee_management_spring.constant.UrlConstant;
import com.hit.employee_management_spring.domain.dto.request.LoginRequestDto;
import com.hit.employee_management_spring.domain.dto.request.RegisterUserRequestDto;
import com.hit.employee_management_spring.domain.dto.response.LoginResponseDto;
import com.hit.employee_management_spring.domain.dto.response.UserResponseDto;
import com.hit.employee_management_spring.service.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestApiV1
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;
    private final MessageSource messageSource;

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

    @PostMapping(UrlConstant.Auth.LOGOUT)
    public ResponseEntity<RestData<?>> logout(HttpServletRequest request) {
        boolean resultLogout = authService.logout(request);
        if (resultLogout) {
            return ApiResponseUtil.success(null, HttpStatus.NO_CONTENT);
        }
        String message = messageSource.getMessage(ErrorMessage.Auth.UNAUTHENTICATED, null, request.getLocale());
        return ApiResponseUtil.error(message, HttpStatus.UNAUTHORIZED);
    }

}
