package com.jose.buildtrack.security;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.jose.buildtrack.repository.BuildRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BuildRepository buildRepository;

    @BeforeEach
    void setUp() {
        buildRepository.deleteAll();
    }

    @Test
    void shouldRejectBuildListingWithoutToken()
            throws Exception {
        mockMvc.perform(
                        get("/builds")
                )
                .andExpect(status().isUnauthorized());
    }

    @SuppressWarnings("null")
    @Test
    void shouldAllowUserToListBuilds()
            throws Exception {
        mockMvc.perform(
                        get("/builds")
                                .with(
                                        jwt().authorities(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_USER"
                                                )
                                        )
                                )
                )
                .andExpect(status().isOk());
    }

    @SuppressWarnings("null")
    @Test
    void shouldAllowAdminToListBuilds()
            throws Exception {
        mockMvc.perform(
                        get("/builds")
                                .with(
                                        jwt().authorities(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_ADMIN"
                                                )
                                        )
                                )
                )
                .andExpect(status().isOk());
    }

    @SuppressWarnings("null")
    @Test
    void shouldRejectBuildCreationForUser()
            throws Exception {
        mockMvc.perform(
                        post("/builds")
                                .with(
                                        jwt().authorities(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_USER"
                                                )
                                        )
                                )
                                .contentType(APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "id": "B-SEC-001",
                                          "version": "1.0.0",
                                          "platform": "WINDOWS"
                                        }
                                        """
                                )
                )
                .andExpect(status().isForbidden());
    }

    @SuppressWarnings("null")
    @Test
    void shouldAllowBuildCreationForAdmin()
            throws Exception {
        mockMvc.perform(
                        post("/builds")
                                .with(
                                        jwt().authorities(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_ADMIN"
                                                )
                                        )
                                )
                                .contentType(APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "id": "B-SEC-002",
                                          "version": "1.0.0",
                                          "platform": "WINDOWS"
                                        }
                                        """
                                )
                )
                .andExpect(status().isCreated());
    }

    @SuppressWarnings("null")
    @Test
    void shouldAllowAuthEndpointsWithoutToken()
            throws Exception {
        mockMvc.perform(
                        post("/auth/register")
                                .contentType(APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "username": "security-test-user",
                                          "password": "securePassword123"
                                        }
                                        """
                                )
                )
                .andExpect(status().isCreated());
    }
}