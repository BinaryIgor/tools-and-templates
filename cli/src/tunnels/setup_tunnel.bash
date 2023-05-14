#!/bin/bash
set -euo pipefail

local_port=${LOCAL_PORT:-9090}
remote_port=${REMOTE_PORT:-9090}
#TODO: generate it/add to readme
host=${HOST:-"68.183.73.126"}
remote_host=${REMOTE_HOST:-"0.0.0.0"}
remote_user=${REMOTE_USER:-'system-template'}

ssh -o StrictHostKeyChecking=accept-new -N -L $local_port:$remote_host:$remote_port $remote_user@$host