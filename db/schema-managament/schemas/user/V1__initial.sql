CREATE TABLE "user" (
    id bigserial primary key,
    name text not null,
    email text not null unique
);