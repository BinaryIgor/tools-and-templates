#!/bin/bash
set -e

rm -f -r target
mkdir target

export HTTP_PORT=$(shuf -i 10000-20000 -n 1)
envsubst '${HTTP_PORT}' < template_nginx.conf > target/nginx.conf

echo "http://0.0.0.0:$HTTP_PORT" > target/${APP_URL_FILE}

cp -r target/* ${CI_PACKAGE_TARGET}