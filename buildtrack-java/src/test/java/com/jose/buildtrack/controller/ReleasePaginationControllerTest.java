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
import org.springframework.transaction.annotation.Transactional;

import com.jose.buildtrack.domain.Build;
import com.jose.buildtrack.domain.BuildVersion;
import com.jose.buildtrack.domain.Platform;
import com.jose.buildtrack.domain.Release;
import com.jose.buildtrack.repository.BuildRepository;
import com.jose.buildtrack.repository.ReleaseRepository;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
@Transactional
class ReleasePaginationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReleaseRepository releaseRepository;

    @Autowired
    private BuildRepository buildRepository;

    @SuppressWarnings("null")
    @BeforeEach
    void setUp() {
        releaseRepository.deleteAll();
        buildRepository.deleteAll();

        Build approvedBuild = new Build(
                "B-RELEASE-FILTER",
                new BuildVersion("10.0.0"),
                Platform.WINDOWS);

        approvedBuild.startValidation();
        approvedBuild.approve();

        buildRepository.save(approvedBuild);

        Release draft1 = new Release(
                "R-001",
                "Release 1.0.0");

        Release draft2 = new Release(
                "R-002",
                "Release 1.1.0");

        Release draft3 = new Release(
                "R-003",
                "Release 1.2.0");

        Release ready = new Release(
                "R-004",
                "Release 2.0.0");

        ready.addBuild(approvedBuild);
        ready.startPreparation();

        Release published = new Release(
                "R-005",
                "Release 2.1.0");

        published.addBuild(approvedBuild);
        published.startPreparation();
        published.publish();

        releaseRepository.saveAll(
                List.of(
                        draft1,
                        draft2,
                        draft3,
                        ready,
                        published));
    }

    @Test
    void shouldFilterReleasesByStatus()
            throws Exception {

        mockMvc.perform(
                get("/releases")
                        .param(
                                "status",
                                "PUBLISHED"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.content.length()")
                                .value(1))
                .andExpect(
                        jsonPath("$.content[0].id")
                                .value("R-005"))
                .andExpect(
                        jsonPath("$.content[0].status")
                                .value("PUBLISHED"))
                .andExpect(
                        jsonPath("$.totalElements")
                                .value(1));
    }

    @Test
    void shouldReturnFirstPageOfReleases()
            throws Exception {

        mockMvc.perform(
                get("/releases")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.content.length()")
                                .value(2))
                .andExpect(
                        jsonPath("$.content[0].id")
                                .value("R-001"))
                .andExpect(
                        jsonPath("$.content[1].id")
                                .value("R-002"))
                .andExpect(
                        jsonPath("$.page")
                                .value(0))
                .andExpect(
                        jsonPath("$.size")
                                .value(2))
                .andExpect(
                        jsonPath("$.totalElements")
                                .value(5))
                .andExpect(
                        jsonPath("$.totalPages")
                                .value(3))
                .andExpect(
                        jsonPath("$.first")
                                .value(true))
                .andExpect(
                        jsonPath("$.last")
                                .value(false));
    }

    @Test
    void shouldReturnSecondPageOfReleases()
            throws Exception {

        mockMvc.perform(
                get("/releases")
                        .param("page", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.content.length()")
                                .value(2))
                .andExpect(
                        jsonPath("$.content[0].id")
                                .value("R-003"))
                .andExpect(
                        jsonPath("$.content[1].id")
                                .value("R-004"))
                .andExpect(
                        jsonPath("$.page")
                                .value(1))
                .andExpect(
                        jsonPath("$.size")
                                .value(2))
                .andExpect(
                        jsonPath("$.totalElements")
                                .value(5))
                .andExpect(
                        jsonPath("$.totalPages")
                                .value(3))
                .andExpect(
                        jsonPath("$.first")
                                .value(false))
                .andExpect(
                        jsonPath("$.last")
                                .value(false));
    }

    @Test
    void shouldReturnLastPageWithOneRelease()
            throws Exception {

        mockMvc.perform(
                get("/releases")
                        .param("page", "2")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.content.length()")
                                .value(1))
                .andExpect(
                        jsonPath("$.content[0].id")
                                .value("R-005"))
                .andExpect(
                        jsonPath("$.page")
                                .value(2))
                .andExpect(
                        jsonPath("$.size")
                                .value(2))
                .andExpect(
                        jsonPath("$.totalElements")
                                .value(5))
                .andExpect(
                        jsonPath("$.totalPages")
                                .value(3))
                .andExpect(
                        jsonPath("$.first")
                                .value(false))
                .andExpect(
                        jsonPath("$.last")
                                .value(true));
    }

    @Test
    void shouldReturnEmptyContentWhenPageIsOutsideRange()
            throws Exception {

        mockMvc.perform(
                get("/releases")
                        .param("page", "50")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.content")
                                .isEmpty())
                .andExpect(
                        jsonPath("$.page")
                                .value(50))
                .andExpect(
                        jsonPath("$.size")
                                .value(10))
                .andExpect(
                        jsonPath("$.totalElements")
                                .value(5))
                .andExpect(
                        jsonPath("$.totalPages")
                                .value(1))
                .andExpect(
                        jsonPath("$.first")
                                .value(false))
                .andExpect(
                        jsonPath("$.last")
                                .value(true));
    }

    @Test
    void shouldUseDefaultPaginationValues()
            throws Exception {

        mockMvc.perform(
                get("/releases"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.content.length()")
                                .value(5))
                .andExpect(
                        jsonPath("$.content[0].id")
                                .value("R-001"))
                .andExpect(
                        jsonPath("$.content[4].id")
                                .value("R-005"))
                .andExpect(
                        jsonPath("$.page")
                                .value(0))
                .andExpect(
                        jsonPath("$.size")
                                .value(10))
                .andExpect(
                        jsonPath("$.totalElements")
                                .value(5))
                .andExpect(
                        jsonPath("$.totalPages")
                                .value(1))
                .andExpect(
                        jsonPath("$.first")
                                .value(true))
                .andExpect(
                        jsonPath("$.last")
                                .value(true));
    }

    @Test
    void shouldRejectNegativePageNumber()
            throws Exception {

        mockMvc.perform(
                get("/releases")
                        .param("page", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectPageSizeEqualToZero()
            throws Exception {

        mockMvc.perform(
                get("/releases")
                        .param("page", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectPageSizeGreaterThanMaximum()
            throws Exception {

        mockMvc.perform(
                get("/releases")
                        .param("page", "0")
                        .param("size", "101"))
                .andExpect(status().isBadRequest());
    }
}