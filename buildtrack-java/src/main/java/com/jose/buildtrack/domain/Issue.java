package com.jose.buildtrack.domain;

public class Issue {
    private final String id;
    private final String title;
    private final IssueSeverity severity;
    private IssueStatus status;

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

    public void resolve() {
        if (status != IssueStatus.OPEN) {
            throw new IllegalStateException("Only OPEN issues can be resolved");
        }
        this.status = IssueStatus.RESOLVED;
    }
    
}
