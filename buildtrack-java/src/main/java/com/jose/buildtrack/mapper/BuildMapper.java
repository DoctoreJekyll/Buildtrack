package com.jose.buildtrack.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.jose.buildtrack.domain.Build;
import com.jose.buildtrack.domain.Issue;
import com.jose.buildtrack.domain.IssueSeverity;
import com.jose.buildtrack.domain.Platform;
import com.jose.buildtrack.dto.BuildResponseDTO;
import com.jose.buildtrack.dto.IssueResponseDTO;
import com.jose.buildtrack.exceptions.InvalidIssueSeverityException;
import com.jose.buildtrack.exceptions.InvalidPlatformException;

@Component
public class BuildMapper {

    public BuildResponseDTO toBuildResponseDTO(Build build) {
        return new BuildResponseDTO(
                build.getId(),
                build.getVersion().getValue(),
                build.getPlatform().name(),
                build.getStatus().name(),
                toIssueResponseDTOList(build.getIssues())
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

    public Platform toPlatform(String platform) {
        try {
            return Platform.valueOf(platform.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new InvalidPlatformException(platform);
        }
    }

    public IssueSeverity toIssueSeverity(String severity) {
        try {
            return IssueSeverity.valueOf(severity.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new InvalidIssueSeverityException(severity);
        }
    }
}