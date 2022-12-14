package com.qnocks.springbootauthjwtstarter.auth.config;

import com.qnocks.springbootauthjwtstarter.auth.security.jwt.JwtSecurityConfigurer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.validation.constraints.NotNull;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final Logger LOGGER = LogManager.getLogger(SecurityConfiguration.class);
    private static final String AUTH_ENDPOINT = "/auth/**";
    private static final String ADMIN_ENDPOINT = "/admin/**";
    private final JwtSecurityConfigurer jwtSecurityConfigurer;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() {
        try {
            return super.authenticationManager();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
            return null;
        }
    }

    @SneakyThrows
    @Override
    protected void configure(@NotNull HttpSecurity http) {
        http.cors()
                    .and()
                .httpBasic()
                    .disable()
                .csrf()
                    .disable()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .authorizeRequests()
                    .antMatchers(ADMIN_ENDPOINT).authenticated()
                    .antMatchers(AUTH_ENDPOINT).permitAll()
                    .anyRequest().permitAll()
                    .and()
                .exceptionHandling()
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .and()
                .apply(jwtSecurityConfigurer);
    }
}
