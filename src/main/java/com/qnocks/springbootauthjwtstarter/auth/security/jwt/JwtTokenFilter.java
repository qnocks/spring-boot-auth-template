package com.qnocks.springbootauthjwtstarter.auth.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    @SneakyThrows
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            @NotNull FilterChain filterChain) {

        val token = parseJwt(request);

        if (token != null) {
            processFilter(token);
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(@NotNull HttpServletRequest request) {
        val tokenPrefix = JwtTokenProvider.getTokenType() + " ";
        val token = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (token != null && token.startsWith(tokenPrefix)) {
            // TODO: find more proper way to deal with it
            return token.substring(tokenPrefix.length());
        }

        return null;
    }

    @SneakyThrows
    private void processFilter(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("auth.token.access.expired");
        }

        val authentication = createAuthentication(jwtTokenProvider.getSubject(token));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Authentication createAuthentication(String subject) {
        val userDetails = userDetailsService.loadUserByUsername(subject);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
