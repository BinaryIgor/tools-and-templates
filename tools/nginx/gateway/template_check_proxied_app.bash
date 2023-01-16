#!/bin/bash
set -e

echo "Checking proxied app connection.."
curl --silent --retry-connrefused --retry 15 --retry-delay 1 --fail ${app_health_check_url}
echo

echo "Proxied app is healthy!"