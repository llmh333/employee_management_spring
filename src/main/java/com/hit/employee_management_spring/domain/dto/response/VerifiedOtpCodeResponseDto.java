package com.hit.employee_management_spring.domain.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerifiedOtpCodeResponseDto {

    private String tokenChangePassword;
    private LocalDateTime expiryDateToken;
}
