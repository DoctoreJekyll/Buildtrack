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
import org.springframework.test.web.servlet.MockMvc;

import com.jose.buildtrack.domain.Build;
import com.jose.buildtrack.domain.BuildVersion;
import com.jose.buildtrack.domain.Platform;
import com.jose.buildtrack.repository.BuildRepository;
import com.jose.buildtrack.repository.ReleaseRepository;

@SpringBootTest
@AutoConfigureMockMvc
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
                createBuild("B-001", "1.0.0"),
                createBuild("B-002", "1.1.0"),
                createBuild("B-003", "1.2.0"),
                createBuild("B-004", "2.0.0"),
                createBuild("B-005", "2.1.0")
        ));
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
            String version
    ) {
        return new Build(
                id,
                new BuildVersion(version),
                Platform.WINDOWS
        );
    }
}