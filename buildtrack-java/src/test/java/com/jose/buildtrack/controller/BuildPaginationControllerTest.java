package com.jose.buildtrack.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.jose.buildtrack.domain.Build;
import com.jose.buildtrack.domain.BuildVersion;
import com.jose.buildtrack.domain.Platform;
import com.jose.buildtrack.repository.BuildRepository;
import com.jose.buildtrack.repository.ReleaseRepository;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
class BuildPaginationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BuildRepository buildRepository;

    @Autowired
    private ReleaseRepository releaseRepository;

    @SuppressWarnings("null")
    @BeforeEach
    void setUp() {
        /*
         * Las releases se eliminan primero porque la tabla release_builds
         * puede mantener referencias a builds.
         */
        releaseRepository.deleteAll();
        buildRepository.deleteAll();

        buildRepository.saveAll(List.of(
                createBuild(
                        "B-001",
                        "1.0.0",
                        Platform.WINDOWS),
                createBuild(
                        "B-002",
                        "1.1.0",
                        Platform.LINUX),
                createValidatingBuild(
                        "B-003",
                        "1.2.0",
                        Platform.WINDOWS),
                createApprovedBuild(
                        "B-004",
                        "2.0.0",
                        Platform.WINDOWS),
                createApprovedBuild(
                        "B-005",
                        "2.1.0",
                        Platform.LINUX)));
    }

    @Test
    void shouldFilterBuildsByStatus() throws Exception {
        mockMvc.perform(
                get("/builds")
                        .param(
                                "status",
                                "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.content.length()")
                                .value(2))
                .andExpect(
                        jsonPath("$.content[0].id")
                                .value("B-004"))
                .andExpect(
                        jsonPath("$.content[1].id")
                                .value("B-005"))
                .andExpect(
                        jsonPath("$.totalElements")
                                .value(2));
    }

    @Test
    void shouldFilterBuildsByPlatform() throws Exception {
        mockMvc.perform(
                get("/builds")
                        .param(
                                "platform",
                                "WINDOWS"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.content.length()")
                                .value(3))
                .andExpect(
                        jsonPath("$.totalElements")
                                .value(3));
    }

    @Test
    void shouldFilterBuildsByStatusAndPlatform()
            throws Exception {

        mockMvc.perform(
                get("/builds")
                        .param(
                                "status",
                                "APPROVED")
                        .param(
                                "platform",
                                "LINUX"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.content.length()")
                                .value(1))
                .andExpect(
                        jsonPath("$.content[0].id")
                                .value("B-005"))
                .andExpect(
                        jsonPath("$.totalElements")
                                .value(1));
    }

    @Test
    void shouldReturnFirstPageOfBuilds() throws Exception {
        mockMvc.perform(get("/builds")
                .param("page", "0")
                .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value("B-001"))
                .andExpect(jsonPath("$.content[1].id").value("B-002"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false));
    }

    @Test
    void shouldReturnSecondPageOfBuilds() throws Exception {
        mockMvc.perform(get("/builds")
                .param("page", "1")
                .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value("B-003"))
                .andExpect(jsonPath("$.content[1].id").value("B-004"))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(false));
    }

    @Test
    void shouldReturnEmptyContentWhenPageIsOutsideRange() throws Exception {
        mockMvc.perform(get("/builds")
                .param("page", "50")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.page").value(50))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    void shouldUseDefaultPaginationValues() throws Exception {
        mockMvc.perform(get("/builds"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    void shouldRejectNegativePageNumber() throws Exception {
        mockMvc.perform(get("/builds")
                .param("page", "-1")
                .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectPageSizeEqualToZero() throws Exception {
        mockMvc.perform(get("/builds")
                .param("page", "0")
                .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectPageSizeGreaterThanMaximum() throws Exception {
        mockMvc.perform(get("/builds")
                .param("page", "0")
                .param("size", "101"))
                .andExpect(status().isBadRequest());
    }

    private Build createBuild(
            String id,
            String version,
            Platform platform) {
        return new Build(
                id,
                new BuildVersion(version),
                platform);
    }

    private Build createValidatingBuild(
            String id,
            String version,
            Platform platform) {
        Build build = createBuild(
                id,
                version,
                platform);

        build.startValidation();

        return build;
    }

    private Build createApprovedBuild(
            String id,
            String version,
            Platform platform) {
        Build build = createBuild(
                id,
                version,
                platform);

        build.startValidation();
        build.approve();

        return build;
    }
}