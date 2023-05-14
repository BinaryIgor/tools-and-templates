#!/bin/bash
set -e

export COTURN_CLI_PASSWORD="${COTURN_CLI_PASSWORD:-cli-admin}"
export COTURN_OLD_AUTH_SECRET="${COTURN_OLD_AUTH_SECRET:-fake-old-auth-secret}"
export COTURN_AUTH_SECRET="${COTURN_AUTH_SECRET:-fake-auth-secret}"

rm -f -r target
mkdir target

envsubst '${COTURN_CLI_PASSWORD} ${COTURN_OLD_AUTH_SECRET} ${COTURN_AUTH_SECRET}' < template_coturn.conf > target/no_realm_coturn.conf
cp set_coturn_realm.bash target/set_coturn_realm.bash

cp -r target/* ${CI_PACKAGE_TARGET}