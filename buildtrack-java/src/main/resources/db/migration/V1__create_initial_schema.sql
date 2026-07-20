CREATE TABLE builds (
    id VARCHAR(255) PRIMARY KEY,
    version_value VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    platform VARCHAR(50) NOT NULL
);

CREATE TABLE issues (
    id VARCHAR(255) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    severity VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    build_id VARCHAR(255) NOT NULL,

    CONSTRAINT fk_issues_build
        FOREIGN KEY (build_id)
        REFERENCES builds(id)
);

CREATE TABLE releases (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL
);

CREATE TABLE release_builds (
    release_id VARCHAR(255) NOT NULL,
    build_id VARCHAR(255) NOT NULL,

    PRIMARY KEY (release_id, build_id),

    CONSTRAINT fk_release_builds_release
        FOREIGN KEY (release_id)
        REFERENCES releases(id),

    CONSTRAINT fk_release_builds_build
        FOREIGN KEY (build_id)
        REFERENCES builds(id)
);