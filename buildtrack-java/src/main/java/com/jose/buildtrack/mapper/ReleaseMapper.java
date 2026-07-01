package com.jose.buildtrack.mapper;

import java.util.List;

import com.jose.buildtrack.domain.Issue;
import com.jose.buildtrack.domain.Release;
import com.jose.buildtrack.dto.BuildResponseDTO;
import com.jose.buildtrack.dto.IssueResponseDTO;
import com.jose.buildtrack.dto.ReleaseResponseDTO;

public class ReleaseMapper {

    public ReleaseResponseDTO toReleaseResponseDTO(Release release) {
        return new ReleaseResponseDTO(
                release.getId(),
                release.getName(),
                release.getStatus().name(),
                release.getBuilds().stream()
                        .map(build -> new BuildResponseDTO(
                                build.getId(),
                                build.getVersion().getValue(),
                                build.getPlatform().name(),
                                build.getStatus().name(),
                                toIssueResponseDTOList(build.getIssues())
                        ))
                        .toList()
        );
    }

    public IssueResponseDTO toIssueResponseDTO(Issue issue) {
        return new IssueResponseDTO(
                issue.getId(),
                issue.getTitle(),
                issue.getSeverity().name(),
                issue.getStatus().name()
        );
    }


    public List<IssueResponseDTO> toIssueResponseDTOList(List<Issue> issues) {
        return issues.stream()
                .map(this::toIssueResponseDTO)
                .toList();
    }

}
