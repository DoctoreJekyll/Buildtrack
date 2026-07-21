package com.jose.buildtrack.domain;

import java.time.Instant;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "issues")
@EntityListeners(AuditingEntityListener.class)
public class Issue {
    @Id
    private String id;
    private String title;
    @Enumerated(EnumType.STRING)
    private IssueSeverity severity;
    @Enumerated(EnumType.STRING)
    private IssueStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "build_id")
    private Build build;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private Instant updatedAt;

    protected Issue() {
        // Default constructor for JPA
    }

    public Issue(String id, String title, IssueSeverity severity) {
        validateRequiredText(id, "ID");
        validateRequiredText(title, "Title");
        validateRequiredSeverity(severity);

        this.id = id;
        this.title = title;
        this.severity = severity;
        this.status = IssueStatus.OPEN;
    }

    private static void validateRequiredText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be null, empty, or blank"
            );
        }
    }

    private static void validateRequiredSeverity(IssueSeverity severity) {
        if (severity == null) {
            throw new IllegalArgumentException("Severity cannot be null");
        }
    }


    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public IssueSeverity getSeverity() {
        return severity;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void resolve() {
        if (status != IssueStatus.OPEN) {
            throw new IllegalStateException("Only OPEN issues can be resolved");
        }
        this.status = IssueStatus.RESOLVED;
    }

    public void assignToBuild(Build build) {
        if (build == null) {
            throw new IllegalArgumentException("Build cannot be null");
        }
        this.build = build;
    }
    
}
