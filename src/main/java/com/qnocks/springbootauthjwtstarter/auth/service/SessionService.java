package com.qnocks.springbootauthjwtstarter.auth.service;

import com.qnocks.springbootauthjwtstarter.auth.dto.TokenPayload;
import com.qnocks.springbootauthjwtstarter.auth.entity.User;

public interface SessionService {

    void createOrUpdate(User user, TokenPayload tokenPayload);

    void clearExpiredSessions();

    void removeByUserId(Long id);

    Boolean existsByUserId(Long id);
}
