package com.hit.employee_management_spring.controller;

import com.hit.employee_management_spring.base.ApiResponseUtil;
import com.hit.employee_management_spring.base.RestApiV1;
import com.hit.employee_management_spring.constant.ErrorMessage;
import com.hit.employee_management_spring.constant.UrlConstant;
import com.hit.employee_management_spring.domain.dto.request.RegisterUserRequestDto;
import com.hit.employee_management_spring.domain.dto.request.UpdateUserRequestDto;
import com.hit.employee_management_spring.domain.dto.request.pagination.PaginationFullRequestDto;
import com.hit.employee_management_spring.domain.dto.request.pagination.PaginationResponseDto;
import com.hit.employee_management_spring.domain.dto.response.UserResponseDto;
import com.hit.employee_management_spring.service.IUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestApiV1
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;
    private final MessageSource messageSource;

    @GetMapping(UrlConstant.User.GET_USER_BY_ID)
    public ResponseEntity<?> getUserById(@PathVariable String userId) {
        UserResponseDto responseDto = userService.getUserById(userId);
        return ApiResponseUtil.success(responseDto);
    }

    @GetMapping(UrlConstant.User.GET_ALL_USER)
    public ResponseEntity<?> getAllUser(@Valid PaginationFullRequestDto requestDto) {
        PaginationResponseDto responseDto = userService.getAllUser(requestDto);
        return ApiResponseUtil.success(responseDto);
    }


    @PostMapping(UrlConstant.User.ADD_NEW_USER)
    public ResponseEntity<?> addNewUser(@RequestBody @Valid RegisterUserRequestDto requestDto) {
        UserResponseDto responseDto = userService.addNewUser(requestDto);
        return ApiResponseUtil.success(responseDto, HttpStatus.CREATED);
    }

    @DeleteMapping(UrlConstant.User.DELETE_USER_BY_ID)
    public ResponseEntity<?> deleteUserById(@PathVariable String userId) {
        boolean resultDelete = userService.deleteByUserId(userId);
        if (!resultDelete) {
            String message = messageSource.getMessage(ErrorMessage.Auth.UNAUTHENTICATED, null, LocaleContextHolder.getLocale());
            return ApiResponseUtil.error(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ApiResponseUtil.success(null, HttpStatus.NO_CONTENT);
    }

    @PutMapping(UrlConstant.User.UPDATE_USER)
    public ResponseEntity<?> updateUser(@PathVariable String userId,
                                        @RequestBody @Valid UpdateUserRequestDto requestDto) {
        UserResponseDto userResponseDto = userService.updateUser(userId, requestDto);
        return ApiResponseUtil.success(userResponseDto, HttpStatus.OK);
    }
}
