package com.jose.buildtrack.service;

import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jose.buildtrack.domain.AppUser;
import com.jose.buildtrack.domain.UserRole;
import com.jose.buildtrack.exceptions.UsernameAlreadyExistsException;
import com.jose.buildtrack.repository.AppUserRepository;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUserService(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AppUser register(
            @NonNull String username,
            @NonNull String rawPassword
    ) {
        String normalizedUsername =
                username.trim().toLowerCase();

        if (appUserRepository.existsByUsername(
                normalizedUsername
        )) {
            throw new UsernameAlreadyExistsException(
                    normalizedUsername
            );
        }

        String passwordHash =
                passwordEncoder.encode(rawPassword);

        AppUser user = new AppUser(
                normalizedUsername,
                passwordHash,
                UserRole.USER
        );

        return appUserRepository.save(user);
    }
}