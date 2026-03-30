package com.insurtech.backend.scheduler;

import com.insurtech.backend.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenCleanUpTasks {
    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "${spring.scheduler.refresh-token-clean-up}")
    @Transactional
    public void RefreshTokenCleanUp() {
        int deleted = refreshTokenRepository.deleteExpiredBefore(
                Instant.now().minus(30, ChronoUnit.DAYS));
        log.info("Token cleanup: removed {} expired tokens", deleted);
    }
}
