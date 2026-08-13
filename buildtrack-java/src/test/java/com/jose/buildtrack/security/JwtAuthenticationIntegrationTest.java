package com.jose.buildtrack.security;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jose.buildtrack.repository.AppUserRepository;
import com.jose.buildtrack.repository.BuildRepository;
import com.jose.buildtrack.repository.ReleaseRepository;
import com.jose.buildtrack.service.AppUserService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class JwtAuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private BuildRepository buildRepository;

    @Autowired
    private ReleaseRepository releaseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        releaseRepository.deleteAll();
        buildRepository.deleteAll();
        appUserRepository.deleteAll();

        appUserService.register(
                "Jose",
                "securePassword123");
    }

    @Test
    void shouldLoginAndAccessProtectedEndpointWithRealJwt()
            throws Exception {

        String responseBody = mockMvc.perform(
                post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(
                                """
                                        {
                                          "username": "JOSE",
                                          "password": "securePassword123"
                                        }
                                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.username").value("jose"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode response = objectMapper.readTree(responseBody);

        String accessToken = response.get("accessToken").asText();

        mockMvc.perform(
                get("/builds")
                        .header(
                                "Authorization",
                                "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnUnauthorizedForWrongPassword()
            throws Exception {

        mockMvc.perform(
                post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(
                                """
                                        {
                                          "username": "jose",
                                          "password": "wrongPassword"
                                        }
                                        """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(
                        jsonPath("$.error")
                                .value("Unauthorized"))
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Invalid username or password"));
    }
}