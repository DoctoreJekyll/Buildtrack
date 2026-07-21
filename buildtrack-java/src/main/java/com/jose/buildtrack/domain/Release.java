package com.jose.buildtrack.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "releases")
@EntityListeners(AuditingEntityListener.class)
public class Release {

    @Id
    private String id;

    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "release_builds",
            joinColumns = @JoinColumn(name = "release_id"),
            inverseJoinColumns = @JoinColumn(name = "build_id")
    )
    private List<Build> builds = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private ReleaseStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private Instant updatedAt;

    @Column(name = "published_at")
    private Instant publishedAt;

    protected Release() {
        // Default constructor for JPA
    }

    public Release(String id, String name) {
        validateRequiredText(id, "ID");
        validateRequiredText(name, "Name");

        this.id = id;
        this.name = name;
        this.builds = new ArrayList<>();
        this.status = ReleaseStatus.DRAFT;
    }

    public Release(String id, String name, List<Build> builds) {
        validateRequiredText(id, "ID");
        validateRequiredText(name, "Name");
        validateRequiredBuilds(builds);

        this.id = id;
        this.name = name;
        this.builds = new ArrayList<>(builds);
        this.status = ReleaseStatus.DRAFT;
    }

    private static void validateRequiredText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be null, empty, or blank"
            );
        }
    }

    private static void validateRequiredBuilds(List<Build> builds) {
        if (builds == null) {
            throw new IllegalArgumentException("Builds cannot be null");
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Build> getBuilds() {
        return List.copyOf(builds);
    }

    public ReleaseStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    
    public void addBuild(Build build) {
        if (build == null) {
            throw new IllegalArgumentException("Build cannot be null");
        }

        if (status != ReleaseStatus.DRAFT) {
            throw new IllegalStateException("Builds can only be added when release is in DRAFT status");
        }

        verifyUniqueBuild(build);

        builds.add(build);
    }

    public void startPreparation() {
        if (status != ReleaseStatus.DRAFT) {
            throw new IllegalStateException("Release must be in DRAFT status");
        }

        ensureHasBuilds();

        if (!allBuildsApproved() || hasAnyBlockingIssues()) {
            throw new IllegalStateException(
                    "Cannot move to READY: builds not approved or have blocking issues"
            );
        }

        this.status = ReleaseStatus.READY;
    }

    public void publish() {
        if (status != ReleaseStatus.READY) {
            throw new IllegalStateException("Release must be in READY status to publish");
        }

        ensureHasBuilds();

        if (!allBuildsApproved() || hasAnyBlockingIssues()) {
            throw new IllegalStateException("Release cannot be published due to validation rules");
        }

        this.status = ReleaseStatus.PUBLISHED;
        this.publishedAt = Instant.now();
    }

    private void verifyUniqueBuild(Build buildToAdd) {
        for (Build build : builds) {
            if (build.getId().equals(buildToAdd.getId())) {
                throw new IllegalArgumentException("Build is already added to this release");
            }
        }
    }

    private void ensureHasBuilds() {
        if (builds.isEmpty()) {
            throw new IllegalStateException("Release must contain at least one build");
        }
    }

    private boolean hasAnyBlockingIssues() {
        for (Build build : builds) {
            if (build.hasOpenBlockerIssues()) {
                return true;
            }
        }
        return false;
    }

    private boolean allBuildsApproved() {
        for (Build build : builds) {
            if (build.getStatus() != BuildStatus.APPROVED) {
                return false;
            }
        }
        return true;
    }
}