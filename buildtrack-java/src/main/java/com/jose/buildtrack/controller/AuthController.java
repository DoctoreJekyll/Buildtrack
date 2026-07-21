package com.jose.buildtrack.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.jose.buildtrack.domain.AppUser;
import com.jose.buildtrack.dto.LoginRequestDTO;
import com.jose.buildtrack.dto.LoginResponseDTO;
import com.jose.buildtrack.dto.RegisterRequestDTO;
import com.jose.buildtrack.dto.UserResponseDTO;
import com.jose.buildtrack.mapper.AppUserMapper;
import com.jose.buildtrack.service.AppUserService;
import com.jose.buildtrack.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AppUserService appUserService;
    private final AppUserMapper appUserMapper;
    private final AuthService authService;

    public AuthController(
            AppUserService appUserService,
            AppUserMapper appUserMapper,
            AuthService authService
    ) {
        this.appUserService = appUserService;
        this.appUserMapper = appUserMapper;
        this.authService = authService;
    }

    @PostMapping("/register")
    @SuppressWarnings("null")
    public ResponseEntity<UserResponseDTO> register(
            @Valid @RequestBody RegisterRequestDTO request
    ) {
        AppUser user = appUserService.register(
                request.username(),
                request.password()
        );

        UserResponseDTO response =
                appUserMapper.toUserResponseDTO(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/users/{id}")
                .buildAndExpand(user.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @PostMapping("/login")
    public LoginResponseDTO login(
            @Valid @RequestBody LoginRequestDTO request
    ) {
        return authService.login(
                request.username(),
                request.password()
        );
    }
}