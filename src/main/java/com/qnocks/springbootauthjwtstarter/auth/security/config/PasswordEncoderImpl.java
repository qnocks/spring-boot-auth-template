package com.qnocks.springbootauthjwtstarter.auth.security.config;

import com.qnocks.springbootauthjwtstarter.auth.crypto.Encoder;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordEncoderImpl implements PasswordEncoder {

    private static final String SHA_PREFIX = "{SHA}";
    private final Encoder encoder;

    @Override
    public String encode(CharSequence rawPassword) {
        return SHA_PREFIX + encoder.encode(String.valueOf(rawPassword));
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        val password = encoder.encode(String.valueOf(rawPassword));
        return password.equals(encodedPassword);
    }
}
