package com.qnocks.springbootauthjwtstarter.auth.service.impl;

import com.qnocks.springbootauthjwtstarter.auth.dto.TokenPayload;
import com.qnocks.springbootauthjwtstarter.auth.entity.Session;
import com.qnocks.springbootauthjwtstarter.auth.entity.User;
import com.qnocks.springbootauthjwtstarter.auth.repository.SessionRepository;
import com.qnocks.springbootauthjwtstarter.auth.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;

    @Override
    public void createOrUpdate(@NotNull User user, TokenPayload tokenPayload) {
        val isSessionExist = existsByUserId(user.getId());
        if (isSessionExist) {
            updateSession(user, tokenPayload);
        } else {
            createSession(user, tokenPayload);
        }
    }

    @Scheduled(cron = "${app.auth.session.cron}")
    @Override
    public void clearExpiredSessions() {
        sessionRepository.deleteAll(sessionRepository.findAll().stream()
                .filter(session -> session.getExpired().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList()));
    }

    @Override
    public void removeByUserId(Long id) {
        sessionRepository.deleteByUserId(id);
    }

    @Override
    public Boolean existsByUserId(Long id) {
        return sessionRepository.existsByUserId(id);
    }

    private void createSession(User user, @NotNull TokenPayload tokenPayload) {
        sessionRepository.save(Session.builder()
                .user(user)
                .token(tokenPayload.getToken())
                .expired(tokenPayload.getExpiration())
                .build());
    }

    private void updateSession(@NotNull User user, @NotNull TokenPayload tokenPayload) {
        sessionRepository.findByUserId(user.getId()).ifPresent(session -> {
            session.setToken(tokenPayload.getToken());
            session.setExpired(tokenPayload.getExpiration());
            sessionRepository.save(session);
        });
    }
}
