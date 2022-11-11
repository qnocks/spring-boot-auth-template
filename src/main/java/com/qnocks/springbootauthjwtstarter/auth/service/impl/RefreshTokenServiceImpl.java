package com.qnocks.springbootauthjwtstarter.auth.service.impl;

import com.qnocks.springbootauthjwtstarter.auth.entity.RefreshToken;
import com.qnocks.springbootauthjwtstarter.auth.entity.User;
import com.qnocks.springbootauthjwtstarter.auth.repository.RefreshTokenRepository;
import com.qnocks.springbootauthjwtstarter.auth.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${app.auth.jwt.token.refresh.expired}")
    private long expired;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshToken getByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException(String.format("auth.token.refresh.expired: %s", token)));
    }

    @Override
    public RefreshToken createRefreshToken(User user) {
        return refreshTokenRepository.save(RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expired(LocalDateTime.now().plusSeconds(expired))
                .user(user)
                .build());
    }

    @Override
    public Boolean verifyAndDeleteExpired(@NotNull RefreshToken token) {
        if (token.getExpired().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.deleteById(token.getId());
            return false;
        }

        return true;
    }

    @Override
    public void resetExpiration(@NotNull RefreshToken token) {
        token.setExpired(LocalDateTime.now().plusSeconds(expired));
    }

    @Scheduled(cron = "${app.auth.jwt.token.refresh.cron}")
    public void clearExpiredTokens() {
        refreshTokenRepository.deleteAll(refreshTokenRepository.findAll().stream()
                .filter(token -> token.getExpired().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList()));
    }
}
