CREATE SCHEMA "user";

SET SEARCH_PATH="user";

CREATE TABLE "user" (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    password TEXT,
    state TEXT NOT NULL DEFAULT 'CREATED',
--    two_factor_authentication BOOLEAN NOT NULL DEFAULT false,
--    external_authentication TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE activation_token (
    id UUID REFERENCES "user"(id) ON DELETE CASCADE,
    token TEXT NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    type TEXT NOT NULL,
    PRIMARY KEY(id, type)
);
CREATE INDEX activation_token_expires_at on activation_token(expires_at);


CREATE SCHEMA project;

SET SEARCH_PATH=project;

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
    creator_id UUID NOT NULL REFERENCES "user".user(id) ON DELETE CASCADE,
    assignee_id UUID NOT NULL REFERENCES "user".user(id) ON DELETE CASCADE,
    name TEXT NOT NULL UNIQUE,
    description TEXT,
    version BIGSERIAL NOT NULL
);

CREATE TABLE project_task (
    project_id UUID NOT NULL REFERENCES project(id) ON DELETE CASCADE,
    task_id UUID NOT NULL REFERENCES task(id) ON DELETE CASCADE,
    PRIMARY KEY(project_id, task_id)
);