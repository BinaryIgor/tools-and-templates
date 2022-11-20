#!/bin/bash

port=${PORT:-9090}
host=${HOST:-'monitor-droplet'}
remote_user=${REMOTE_USER:-'monitor'}

ssh -o StrictHostKeyChecking=accept-new -N -L $port:0.0.0.0:$port $remote_user@$host