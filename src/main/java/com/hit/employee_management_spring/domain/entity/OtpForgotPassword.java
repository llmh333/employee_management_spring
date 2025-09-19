package com.hit.employee_management_spring.domain.entity;

import com.hit.employee_management_spring.audit.DateAuditing;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp_forgot_password")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpForgotPassword extends DateAuditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "varchar(6)",unique = true, nullable = false)
    private String otpCode;

    @Column(nullable = false)
    private LocalDateTime expiryDateOtpCode;

    @Column(nullable = false)
    private Boolean verifiedOtpCode;

    @Column(unique = true)
    private String tokenChangePassword;

    private LocalDateTime expiryDateToken;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_email", referencedColumnName = "email")
    private User user;
}
