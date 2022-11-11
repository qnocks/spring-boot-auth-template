package com.qnocks.springbootauthjwtstarter.auth.service;

import com.qnocks.springbootauthjwtstarter.auth.entity.RefreshToken;
import com.qnocks.springbootauthjwtstarter.auth.entity.User;

public interface RefreshTokenService {

    RefreshToken getByToken(String token);

    RefreshToken createRefreshToken(User user);

    Boolean verifyAndDeleteExpired(RefreshToken token);

    void resetExpiration(RefreshToken token);
}
