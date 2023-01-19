#!/bin/bash

echo "Checking proxied app connection.."
curl --fail-with-body --retry-connrefused --retry 10 --retry-delay 1 ${app_health_check_url} -k
echo

echo "Proxied app is healthy!"