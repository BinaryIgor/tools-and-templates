#!/bin/bash

docker build . -t system-template-postgres-db

docker rm system-template-postgres-db

exec docker run -p "5678:5432" --name system-template-postgres-db system-template-postgres-db