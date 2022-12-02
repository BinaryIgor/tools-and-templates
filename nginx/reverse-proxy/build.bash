#!/bin/bash
set -eu

rm -f -r target
mkdir target
mkdir target/conf

export APP_URL="${APP_URL:-http://0.0.0.0:9999}"

envsubst '${HTTP_PORT} ${APP_URL}' < template_nginx.conf > target/conf/default.conf
envsubst '${HTTP_PORT}' < template_nginx.conf > target/template_nginx_app.conf

# cp -r ../../config/fake-certs target/fake-certs
export nginx_container="nginx-reverse-proxy"
# check if both proxying and proxied app are working properly
export app_health_check_url="http://0.0.0.0:${HTTPS_PORT}/health-check"

envsubst '${nginx_container} ${app_health_check_url}' < template_update_app_url.bash > target/update_app_url.bash
cp update_app_url_pre_start.bash target/update_app_url_pre_start.bash
envsubst '${app_health_check_url}' < template_check_proxied_app.bash > target/check_proxied_app.bash

cp -r target/* ${CI_PACKAGE_TARGET}
