CREATE SCHEMA "user";

SET SEARCH_PATH="user";

CREATE TABLE "user" (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    password TEXT,
    state TEXT NOT NULL DEFAULT 'CREATED',
    two_factor_authentication BOOLEAN NOT NULL DEFAULT false,
    external_authentication TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);
