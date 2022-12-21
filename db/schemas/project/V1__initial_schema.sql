CREATE TABLE project (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL REFERENCES "user".user(id) ON DELETE CASCADE,
    name TEXT NOT NULL UNIQUE,
    version BIGSERIAL NOT NULL
);

CREATE TABLE project_user (
    project_id UUID NOT NULL REFERENCES project(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES "user".user(id) ON DELETE CASCADE,
    PRIMARY KEY(project_id, user_id)
);

CREATE TABLE task (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES project(id) ON DELETE CASCADE,
    creator_id UUID NOT NULL REFERENCES "user".user(id) ON DELETE CASCADE,
    assignee_id UUID NOT NULL REFERENCES "user".user(id) ON DELETE CASCADE,
    name TEXT NOT NULL UNIQUE,
    description TEXT,
    version BIGSERIAL NOT NULL
);