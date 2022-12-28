FROM postgres:14.3

COPY postgresql.conf /etc/postgresql/postgresql.conf
COPY extensions.sql /docker-entrypoint-initdb.d/extensions.sql

ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=postgres

ENV TZ=UTC
ENV PGTZ=UTC