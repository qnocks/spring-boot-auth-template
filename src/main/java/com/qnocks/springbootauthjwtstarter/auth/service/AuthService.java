package com.qnocks.springbootauthjwtstarter.auth.service;

import com.qnocks.springbootauthjwtstarter.auth.dto.LoginRequest;
import com.qnocks.springbootauthjwtstarter.auth.dto.LoginResponse;
import com.qnocks.springbootauthjwtstarter.auth.dto.LogoutRequest;
import com.qnocks.springbootauthjwtstarter.auth.dto.RefreshTokenRequest;
import com.qnocks.springbootauthjwtstarter.auth.dto.RefreshTokenResponse;

public interface AuthService {

    LoginResponse login(LoginRequest loginRequest);

    RefreshTokenResponse refreshToken(RefreshTokenRequest request);

    void logout(LogoutRequest logoutRequest);
}
