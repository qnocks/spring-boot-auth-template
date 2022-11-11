package com.qnocks.springbootauthjwtstarter.auth.security.crypto;

public interface CredentialsEncoder {

    String encode(String plainString);
}
