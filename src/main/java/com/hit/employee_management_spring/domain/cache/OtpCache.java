package com.hit.employee_management_spring.domain.cache;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpCache implements Serializable {
    private String email;
    private String otpCode;
    private LocalDateTime expiryAt;
}
