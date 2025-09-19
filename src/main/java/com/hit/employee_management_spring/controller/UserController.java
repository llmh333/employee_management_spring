package com.hit.employee_management_spring.controller;

import com.hit.employee_management_spring.base.ApiResponseUtil;
import com.hit.employee_management_spring.base.RestApiV1;
import com.hit.employee_management_spring.constant.UrlConstant;
import com.hit.employee_management_spring.domain.dto.request.RegisterUserRequestDto;
import com.hit.employee_management_spring.domain.dto.response.UserResponseDto;
import com.hit.employee_management_spring.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestApiV1
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PostMapping(UrlConstant.User.ADD_NEW_USER)
    public ResponseEntity<?> addNewUser(@RequestBody @Valid RegisterUserRequestDto requestDto) {
        UserResponseDto responseDto = userService.addNewUser(requestDto);
        return ApiResponseUtil.success(responseDto, HttpStatus.CREATED);
    }
}
