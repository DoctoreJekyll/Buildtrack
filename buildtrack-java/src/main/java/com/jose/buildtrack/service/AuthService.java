package com.jose.buildtrack.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jose.buildtrack.domain.AppUser;
import com.jose.buildtrack.dto.LoginResponseDTO;
import com.jose.buildtrack.repository.AppUserRepository;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AppUserRepository appUserRepository;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            AppUserRepository appUserRepository,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.appUserRepository = appUserRepository;
        this.jwtService = jwtService;
    }

    public LoginResponseDTO login(
            String username,
            String rawPassword
    ) {
        String normalizedUsername =
                username.trim().toLowerCase();

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                normalizedUsername,
                                rawPassword
                        )
                );

        AppUser user = appUserRepository
                .findByUsername(authentication.getName())
                .orElseThrow(
                        () -> new UsernameNotFoundException(
                                "User not found"
                        )
                );

        String accessToken =
                jwtService.generateToken(user);

        return new LoginResponseDTO(
                accessToken,
                "Bearer",
                jwtService.getExpirationSeconds(),
                user.getUsername(),
                user.getRole()
        );
    }
}