package com.hit.employee_management_spring.controller;

import com.hit.employee_management_spring.base.ApiResponseUtil;
import com.hit.employee_management_spring.base.RestApiV1;
import com.hit.employee_management_spring.base.RestData;
import com.hit.employee_management_spring.constant.UrlConstant;
import com.hit.employee_management_spring.domain.dto.request.ConfirmNewPasswordRequestDto;
import com.hit.employee_management_spring.domain.dto.request.ForgotPasswordRequestDto;
import com.hit.employee_management_spring.domain.dto.request.VerifyOtpCodeRequestDto;
import com.hit.employee_management_spring.domain.dto.response.OtpResponseDto;
import com.hit.employee_management_spring.domain.dto.response.VerifiedOtpCodeResponseDto;
import com.hit.employee_management_spring.service.IOtpForgotPasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestApiV1
@RequiredArgsConstructor
@Tag(name = "Forgot Password", description = "3-step password reset flow: send OTP → verify OTP → set new password")
public class ForgotPasswordController {

    private final IOtpForgotPasswordService otpForgotPasswordService;

    @Operation(
        summary = "Step 1 — Send OTP",
        description = "Sends a 6-digit OTP to the provided email address. OTP expires in 5 minutes. A new OTP cannot be requested while a valid one is still active."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "OTP sent successfully",
            content = @Content(schema = @Schema(implementation = OtpResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Email not found or OTP already active",
            content = @Content(schema = @Schema(implementation = RestData.class)))
    })
    @PostMapping(UrlConstant.ForgotPassword.GET_OTP_CODE)
    public ResponseEntity<?> sendOtp(@RequestBody @Valid ForgotPasswordRequestDto requestDto) {
        OtpResponseDto otpResponseDto = otpForgotPasswordService.sendOtpCode(requestDto.getEmail());
        return ApiResponseUtil.success(otpResponseDto, HttpStatus.CREATED);
    }

    @Operation(
        summary = "Step 2 — Verify OTP",
        description = "Verifies the OTP code for the given email. On success, returns a one-time `tokenChangePassword` (valid 5 min) to use in step 3."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OTP verified — returns tokenChangePassword",
            content = @Content(schema = @Schema(implementation = VerifiedOtpCodeResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid or expired OTP",
            content = @Content(schema = @Schema(implementation = RestData.class))),
        @ApiResponse(responseCode = "404", description = "OTP not found for this email",
            content = @Content(schema = @Schema(implementation = RestData.class)))
    })
    @PostMapping(UrlConstant.ForgotPassword.VERIFY_OTP_CODE)
    public ResponseEntity<?> verifyOtpCode(@RequestBody @Valid VerifyOtpCodeRequestDto requestDto) {
        VerifiedOtpCodeResponseDto responseDto = otpForgotPasswordService.verifyOtpCode(
                requestDto.getEmail(), requestDto.getOtpCode());
        return ApiResponseUtil.success(responseDto, HttpStatus.OK);
    }

    @Operation(
        summary = "Step 3 — Confirm new password",
        description = "Sets a new password using the `tokenChangePassword` received from step 2. Token is invalidated after use."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Password changed successfully"),
        @ApiResponse(responseCode = "400", description = "Passwords do not match or token expired",
            content = @Content(schema = @Schema(implementation = RestData.class))),
        @ApiResponse(responseCode = "404", description = "Change password token not found",
            content = @Content(schema = @Schema(implementation = RestData.class)))
    })
    @PostMapping(UrlConstant.ForgotPassword.CONFIRM_NEW_PASSWORD)
    public ResponseEntity<?> confirmNewPassword(@RequestBody @Valid ConfirmNewPasswordRequestDto requestDto) {
        otpForgotPasswordService.confirmNewPassword(requestDto);
        return ApiResponseUtil.success(null, HttpStatus.NO_CONTENT);
    }
}
