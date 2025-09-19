package com.hit.employee_management_spring.job;

import com.hit.employee_management_spring.repository.OtpForgotPasswordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Log4j2
@Component
public class ClearExpiredOtpCode {

    private final OtpForgotPasswordRepository otpForgotPasswordRepository;

    @Scheduled(cron = "*/30 * * * * *")
    public void deleteExpiredOtpCode() {
        log.info("Deleting expired OTP Code");
        otpForgotPasswordRepository.deleteExpiredOtpCode();
        log.info("Delete expired OTP Code successfully");
    }
}
