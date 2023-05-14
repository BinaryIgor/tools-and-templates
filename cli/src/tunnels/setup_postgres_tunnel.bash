#!/bin/bash

export LOCAL_PORT=5555
export REMOTE_PORT=25060
export REMOTE_HOST="private-system-template-postgres-do-user-12816929-0.b.db.ondigitalocean.com"

echo "Tunneling postgres on a port: $LOCAL_PORT!"

exec bash setup_tunnel.bash