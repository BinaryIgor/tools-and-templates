#!/bin/bash
echo "About to migrate it all.."

schemas=$(cat schemas_order.txt)

for schema in $schemas
do
    echo "Migrating $schema schema.."
    docker rm db-migration
    docker run --rm -v "$PWD/schemas/$schema:/flyway/sql" \
        --name db-migration \
        flyway/flyway:9-alpine -url="jdbc:postgresql://$DB_HOST:$DB_PORT/$DB_NAME" \
        -schemas="$schema" \
        -user="system-db-user" \
        -password="$DB_PASSWORD" \
        migrate
done