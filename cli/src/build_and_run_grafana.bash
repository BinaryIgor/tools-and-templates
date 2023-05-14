#!/bin/bash
set -e

cwd="$PWD"

echo "Building and running..."
cd ../../tools/grafana
bash build_and_run_grafana.bash

echo
echo "Setting up prom tunnel.."
cd $cwd
cd tunnels

bash setup_prometheus_tunnel.bash

echo
echo "Stopping grafana..."
docker stop system-template-grafana
echo
echo "Grafana stopped, tunnels closed"