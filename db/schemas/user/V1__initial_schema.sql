CREATE TABLE "user" (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    state TEXT NOT NULL DEFAULT 'CREATED',
    second_factor_auth BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX user_state on "user"(state);

CREATE TABLE role (
    user_id UUID NOT NULL REFERENCES "user" (id) ON DELETE CASCADE,
    value TEXT NOT NULL,

    PRIMARY KEY(user_id, value)
);

CREATE TABLE activation_token (
    user_id UUID NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
    token TEXT NOT NULL UNIQUE,
    status TEXT NOT NULL DEFAULT 'SENDING',
    expires_at TIMESTAMP NOT NULL,
    type TEXT NOT NULL,
    PRIMARY KEY(user_id, type)
);
CREATE INDEX activation_token_expires_at on activation_token(expires_at);

CREATE TABLE second_factor_authentication (
    user_id UUID PRIMARY KEY REFERENCES "user"(id) ON DELETE CASCADE,
    email TEXT NOT NULL UNIQUE,
    code TEXT NOT NULL,
    sent_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL
);