package com.hit.employee_management_spring.service;

import com.hit.employee_management_spring.domain.dto.request.ConfirmNewPasswordRequestDto;
import com.hit.employee_management_spring.domain.dto.response.OtpResponseDto;
import com.hit.employee_management_spring.domain.dto.response.VerifiedOtpCodeResponseDto;
import com.hit.employee_management_spring.domain.entity.User;

public interface IOtpForgotPasswordService {

    public String generateOtpCode(User user);

    public OtpResponseDto sendOtpCode(String receivedEmail);

    public VerifiedOtpCodeResponseDto verifyOtpCode(String otpCode);

    public boolean confirmNewPassword(ConfirmNewPasswordRequestDto  confirmNewPasswordRequestDto);


}
