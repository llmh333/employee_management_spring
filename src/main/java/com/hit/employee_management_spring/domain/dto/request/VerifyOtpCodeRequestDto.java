package com.hit.employee_management_spring.domain.dto.request;

import com.hit.employee_management_spring.constant.ErrorMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOtpCodeRequestDto {

    @NotBlank(message = ErrorMessage.Validation.FIELD_NOT_BLANK)
    private String otpCode;
}
