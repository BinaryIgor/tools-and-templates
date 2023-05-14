#!/bin/bash
set -e
export COTURN_REALM="${CI_MACHINE_NAME}.collybri.codyn.io"
envsubst < no_realm_coturn.conf > coturn.conf