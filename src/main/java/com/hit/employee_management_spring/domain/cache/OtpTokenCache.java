package com.hit.employee_management_spring.domain.cache;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpTokenCache implements Serializable {
    private String email;
    private LocalDateTime expiryAt;
}
