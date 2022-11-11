package com.qnocks.springbootauthjwtstarter.auth.security.config;

import com.qnocks.springbootauthjwtstarter.auth.crypto.Encoder;
import com.qnocks.springbootauthjwtstarter.auth.repository.UserRepository;
import com.qnocks.springbootauthjwtstarter.auth.type.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final Encoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        val user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("auth.username-not-found"));

        return UserDetailsImpl.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .enabled(user.getStatus().equals(UserStatus.ACTIVE))
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList()))
                .build();
    }
}
