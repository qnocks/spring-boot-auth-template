package com.qnocks.springbootauthjwtstarter.auth.service.impl;

import com.qnocks.springbootauthjwtstarter.auth.crypto.Encoder;
import com.qnocks.springbootauthjwtstarter.auth.dto.LoginRequest;
import com.qnocks.springbootauthjwtstarter.auth.dto.LoginResponse;
import com.qnocks.springbootauthjwtstarter.auth.dto.LogoutRequest;
import com.qnocks.springbootauthjwtstarter.auth.dto.RefreshTokenRequest;
import com.qnocks.springbootauthjwtstarter.auth.dto.RefreshTokenResponse;
import com.qnocks.springbootauthjwtstarter.auth.entity.RefreshToken;
import com.qnocks.springbootauthjwtstarter.auth.entity.User;
import com.qnocks.springbootauthjwtstarter.auth.repository.UserRepository;
import com.qnocks.springbootauthjwtstarter.auth.security.crypto.CredentialsEncoder;
import com.qnocks.springbootauthjwtstarter.auth.security.jwt.JwtTokenProvider;
import com.qnocks.springbootauthjwtstarter.auth.service.AuthService;
import com.qnocks.springbootauthjwtstarter.auth.service.RefreshTokenService;
import com.qnocks.springbootauthjwtstarter.auth.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final SessionService sessionService;
    private final RefreshTokenService refreshTokenService;
    private final CredentialsEncoder credentialsEncoder;
    private final Encoder encoder;

    @Override
    public LoginResponse login(@NotNull LoginRequest loginRequest) {
        val username = loginRequest.getUsername();
        val encodedUsername = credentialsEncoder.encode(username);

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                encodedUsername, loginRequest.getPassword()));

        val user = getUserByUsername(encodedUsername);
        verifySessionAbsence(user, loginRequest);
        val tokenPayload = jwtTokenProvider.createToken(encodedUsername, user.getRoles());
        sessionService.createOrUpdate(user, tokenPayload);

        return LoginResponse.builder()
                .username(username)
                .accessToken(tokenPayload.getToken())
                .type(JwtTokenProvider.getTokenType())
                .refreshToken(refreshTokenService.createRefreshToken(user).getToken())
                .build();
    }

    @Override
    public RefreshTokenResponse refreshToken(@NotNull RefreshTokenRequest request) {
        val refreshToken = refreshTokenService.getByToken(request.getRefreshToken());
        verifyRefreshTokenValidation(refreshToken);
        return processRefreshing(refreshToken);
    }

    @Override
    public void logout(@NotNull LogoutRequest logoutRequest) {
        val user = getUserByUsername(encoder.encode(logoutRequest.getUsername()));
        sessionService.removeByUserId(user.getId());
    }

    private void verifySessionAbsence(@NotNull User user, @NotNull LoginRequest loginRequest) {
        val isExists = sessionService.existsByUserId(user.getId());
        if (isExists) {
            throw new RuntimeException(String.format("auth.session.exists: %s", loginRequest.getUsername()));
        }
    }

    private void verifyRefreshTokenValidation(@NotNull RefreshToken refreshToken) {
        val isTokenNotValid = !refreshTokenService.verifyAndDeleteExpired(refreshToken);

        if (isTokenNotValid) {
            throw new RuntimeException(String.format("auth.token.refresh.expired: %s", refreshToken.getToken()));
        }
    }

    private RefreshTokenResponse processRefreshing(@NotNull RefreshToken refreshToken) {
        val accessToken = jwtTokenProvider.createToken(
                refreshToken.getUser().getUsername(), refreshToken.getUser().getRoles());

        refreshTokenService.resetExpiration(refreshToken);
        sessionService.createOrUpdate(refreshToken.getUser(), accessToken);

        return RefreshTokenResponse.builder()
                .accessToken(accessToken.getToken())
                .type(JwtTokenProvider.getTokenType())
                .refreshToken(refreshToken.getToken())
                .build();
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(String.format("auth.username-not-found %s", username)));
    }
}
