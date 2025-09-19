package com.hit.employee_management_spring.domain.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class OtpResponseDto {

    private String otpCode;

    private LocalDateTime expiryDateOtpCode;
}
