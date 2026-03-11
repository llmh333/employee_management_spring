package com.hit.employee_management_spring.service;

import com.hit.employee_management_spring.domain.dto.request.ConfirmNewPasswordRequestDto;
import com.hit.employee_management_spring.domain.dto.response.OtpResponseDto;
import com.hit.employee_management_spring.domain.dto.response.VerifiedOtpCodeResponseDto;
import com.hit.employee_management_spring.domain.entity.User;

public interface IOtpForgotPasswordService {
    String generateOtpCode(User user);
    OtpResponseDto sendOtpCode(String receivedEmail);
    VerifiedOtpCodeResponseDto verifyOtpCode(String email, String otpCode);
    boolean confirmNewPassword(ConfirmNewPasswordRequestDto requestDto);
}
