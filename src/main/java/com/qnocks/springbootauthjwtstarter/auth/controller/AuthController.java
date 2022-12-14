package com.qnocks.springbootauthjwtstarter.auth.controller;

import com.qnocks.springbootauthjwtstarter.auth.dto.LoginRequest;
import com.qnocks.springbootauthjwtstarter.auth.dto.LoginResponse;
import com.qnocks.springbootauthjwtstarter.auth.dto.LogoutRequest;
import com.qnocks.springbootauthjwtstarter.auth.dto.RefreshTokenRequest;
import com.qnocks.springbootauthjwtstarter.auth.dto.RefreshTokenResponse;
import com.qnocks.springbootauthjwtstarter.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Controller", description = "API to manage authentication process")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Login", description = "Authenticates user with provided credentials")
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @Operation(summary = "Refresh token", description = "Generates new access token")
    @PostMapping("/refresh")
    public RefreshTokenResponse refreshToken(@RequestBody RefreshTokenRequest request) {
        return authService.refreshToken(request);
    }

    @Operation(summary = "Logout", description = "Deleting existing session")
    @PostMapping("/logout")
    public void logout(@RequestBody LogoutRequest request) {
        authService.logout(request);
    }
}
