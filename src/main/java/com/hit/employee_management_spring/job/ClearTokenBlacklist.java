package com.hit.employee_management_spring.job;

import com.hit.employee_management_spring.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
public class ClearTokenBlacklist {

    private UserSessionRepository userSessionRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void clearTokenBlacklist() {
        userSessionRepository.deleteUSerSessionExpired();
    }
}
