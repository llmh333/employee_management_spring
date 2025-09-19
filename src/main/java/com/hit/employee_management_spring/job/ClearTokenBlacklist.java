package com.hit.employee_management_spring.job;

import com.hit.employee_management_spring.repository.TokenBlacklistRepository;
import com.hit.employee_management_spring.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Log4j2
public class ClearTokenBlacklist {

    private TokenBlacklistRepository tokenBlacklistRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void clearTokenBlacklist() {
        log.info("Deleting expired token blacklist");
        tokenBlacklistRepository.deleteExpiredTokenBlacklist();
        log.info("Deleting expired token blacklist successfully");
    }
}
