package com.jose.buildtrack.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.jose.buildtrack.domain.AppUser;
import com.jose.buildtrack.domain.UserRole;
import com.jose.buildtrack.exceptions.UsernameAlreadyExistsException;
import com.jose.buildtrack.repository.AppUserRepository;

@SpringBootTest
@Transactional
class AppUserServiceTest {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        appUserRepository.deleteAll();
    }

    @Test
    void shouldRegisterUserWithEncodedPassword() {
        String rawPassword = "securePassword123";

        AppUser user = appUserService.register(
                "Jose",
                rawPassword
        );

        assertEquals("jose", user.getUsername());
        assertEquals(UserRole.USER, user.getRole());
        assertTrue(user.isEnabled());

        assertNotEquals(
                rawPassword,
                user.getPasswordHash()
        );

        assertTrue(
                passwordEncoder.matches(
                        rawPassword,
                        user.getPasswordHash()
                )
        );
    }

    @Test
    void shouldRejectDuplicatedNormalizedUsername() {
        appUserService.register(
                "Jose",
                "securePassword123"
        );

        assertThrows(
                UsernameAlreadyExistsException.class,
                () -> appUserService.register(
                        "JOSE",
                        "differentPassword123"
                )
        );
    }

    @Test
    void shouldNotStoreRawPassword() {
        AppUser user = appUserService.register(
                "test-user",
                "securePassword123"
        );

        AppUser storedUser = appUserRepository
                .findByUsername("test-user")
                .orElseThrow();

        assertFalse(
                storedUser.getPasswordHash()
                        .contains("securePassword123")
        );

        assertEquals(
                user.getPasswordHash(),
                storedUser.getPasswordHash()
        );
    }
}