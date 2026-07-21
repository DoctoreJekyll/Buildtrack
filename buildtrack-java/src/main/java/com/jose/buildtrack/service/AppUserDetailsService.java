package com.jose.buildtrack.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jose.buildtrack.domain.AppUser;
import com.jose.buildtrack.repository.AppUserRepository;

@Service
public class AppUserDetailsService
        implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    public AppUserDetailsService(
            AppUserRepository appUserRepository
    ) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(
            String username
    ) throws UsernameNotFoundException {
        String normalizedUsername =
                username.trim().toLowerCase();

        AppUser appUser = appUserRepository
                .findByUsername(normalizedUsername)
                .orElseThrow(
                        () -> new UsernameNotFoundException(
                                "User not found"
                        )
                );

        return User
                .withUsername(appUser.getUsername())
                .password(appUser.getPasswordHash())
                .roles(appUser.getRole().name())
                .disabled(!appUser.isEnabled())
                .build();
    }
}