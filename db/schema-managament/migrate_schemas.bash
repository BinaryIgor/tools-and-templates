#!/bin/bash
echo "About to migrate it all.."

schemas="user todo"

for schema in $schemas
do
    echo "Migrating $schema schema.."
    docker run --rm -v "$PWD/schemas/$schema:/flyway/sql" \
        flyway/flyway -url="jdbc:postgresql://$DB_HOST:$DB_PORT/$DB_NAME" \
        -schemas="$schema" \
        -user="experimental-user" \
        -password="$DB_PASSWORD" \
        migrate
done