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