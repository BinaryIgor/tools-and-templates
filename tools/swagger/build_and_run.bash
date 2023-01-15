#!/bin/bash
set -e

SWAGGER_PORT=${SWAGGER_PORT:-9999}

echo 
echo "About to run swagger on $SWAGGER_PORT..."
echo

rm -r -f target/
mkdir target/

cp ../../code/docs/swagger.json target/swagger.json

docker build . -t custom-swagger-ui

docker rm custom-swagger-ui | true
exec docker run -p "9999:8080" -e "SWAGGER_JSON=/docs/swagger.json" -v "${PWD}/target:/docs" --name custom-swagger-ui custom-swagger-ui