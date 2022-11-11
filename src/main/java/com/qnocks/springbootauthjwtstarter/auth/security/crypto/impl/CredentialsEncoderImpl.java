package com.qnocks.springbootauthjwtstarter.auth.security.crypto.impl;

import com.qnocks.springbootauthjwtstarter.auth.security.crypto.CredentialsEncoder;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class CredentialsEncoderImpl implements CredentialsEncoder {

    @Override
    public String encode(String plainString) {
       return DigestUtils.sha256Hex(plainString);
    }
}
