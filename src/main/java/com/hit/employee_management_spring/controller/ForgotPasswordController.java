package com.hit.employee_management_spring.controller;

import com.hit.employee_management_spring.base.ApiResponseUtil;
import com.hit.employee_management_spring.base.RestApiV1;
import com.hit.employee_management_spring.constant.UrlConstant;
import com.hit.employee_management_spring.domain.dto.request.ConfirmNewPasswordRequestDto;
import com.hit.employee_management_spring.domain.dto.request.ForgotPasswordRequestDto;
import com.hit.employee_management_spring.domain.dto.request.VerifyOtpCodeRequestDto;
import com.hit.employee_management_spring.domain.dto.response.OtpResponseDto;
import com.hit.employee_management_spring.domain.dto.response.VerifiedOtpCodeResponseDto;
import com.hit.employee_management_spring.service.IOtpForgotPasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestApiV1
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final IOtpForgotPasswordService otpForgotPasswordService;

    @PostMapping(UrlConstant.ForgotPassword.GET_OTP_CODE)
    public ResponseEntity<?> sendOtp(@RequestBody @Valid ForgotPasswordRequestDto requestDto) {
        OtpResponseDto otpResponseDto = otpForgotPasswordService.sendOtpCode(requestDto.getEmail());
        return ApiResponseUtil.success(otpResponseDto, HttpStatus.CREATED);
    }

    @PostMapping(UrlConstant.ForgotPassword.VERIFY_OTP_CODE)
    public ResponseEntity<?> verifyOtpCode(@RequestBody @Valid VerifyOtpCodeRequestDto requestDto) {
        VerifiedOtpCodeResponseDto responseDto = otpForgotPasswordService.verifyOtpCode(requestDto.getOtpCode());
        return ApiResponseUtil.success(responseDto, HttpStatus.OK);
    }

    @PostMapping(UrlConstant.ForgotPassword.CONFIRM_NEW_PASSWORD)
    public ResponseEntity<?> confirmNewPassword(@RequestBody @Valid ConfirmNewPasswordRequestDto requestDto) {
        otpForgotPasswordService.confirmNewPassword(requestDto);
        return ApiResponseUtil.success(null, HttpStatus.NO_CONTENT);
    }
}
