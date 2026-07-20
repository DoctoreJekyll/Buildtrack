CREATE INDEX idx_builds_status
    ON builds(status);

CREATE INDEX idx_builds_platform
    ON builds(platform);

CREATE INDEX idx_issues_build_id
    ON issues(build_id);

CREATE INDEX idx_issues_status
    ON issues(status);

CREATE INDEX idx_issues_severity
    ON issues(severity);

CREATE INDEX idx_releases_status
    ON releases(status);

CREATE INDEX idx_release_builds_build_id
    ON release_builds(build_id);