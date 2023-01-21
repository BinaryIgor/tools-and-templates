#!/bin/bash

local_port=${LOCAL_PORT:-9090}
remote_port=${REMOTE_PORT:-9090}
host=${HOST:-'monitor-droplet'}
remote_user=${REMOTE_USER:-'monitor'}

ssh -o StrictHostKeyChecking=accept-new -N -L $local_port:0.0.0.0:$remote_port $remote_user@$host