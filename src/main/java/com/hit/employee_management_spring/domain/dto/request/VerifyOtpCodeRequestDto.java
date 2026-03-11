package com.hit.employee_management_spring.domain.dto.request;

import com.hit.employee_management_spring.constant.ErrorMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "OTP verification request")
public class VerifyOtpCodeRequestDto {

    @Schema(description = "Registered email address", example = "user@example.com")
    @NotBlank(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    @Email(message = ErrorMessage.Validation.EMAIL)
    private String email;

    @Schema(description = "6-digit OTP code received via email", example = "123456")
    @NotBlank(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    private String otpCode;
}
