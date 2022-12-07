#!/bin/bash

docker build . -t docker-metrics-collector

docker rm docker-metrics-collector
docker run -v /var/run/docker.sock:/var/run/docker.sock \
  --network host \
  -e "METRICS_TARGET_URL=http://localhost:10101/metrics" \
  --name docker-metrics-collector docker-metrics-collector